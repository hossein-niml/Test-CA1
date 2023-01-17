package ir.proprog.enrollassist;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.student.Student;
import lombok.RequiredArgsConstructor;
import org.junit.platform.commons.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class CourseStepDefinitions {
    private final Map<String, Course> courseMap = new HashMap<>();
    private final Map<String, Student> studentMap = new HashMap<>();
    private ExceptionList exceptionList = new ExceptionList();
    private List<EnrollmentRuleViolation> violations = new ArrayList<>();

    @Before
    public void setup() {
        exceptionList = new ExceptionList();
        violations.clear();
    }

    @When("user adds a new course with correct values")
    public void addCourse() {
        addCourseWithErrorChecking("1234567", "c3", 2, GraduateLevel.Undergraduate.toString());
    }

    @When("user adds a new course with empty title")
    public void addCourseWithEmptyTitle() {
        addCourseWithErrorChecking("1234567", "", 2, GraduateLevel.Undergraduate.toString());
    }

    @When("user adds a new course with number={string}")
    public void addCourseWithInvalidNumber(String courseNumber) {
        addCourseWithErrorChecking(courseNumber, "c1", 2, GraduateLevel.Undergraduate.toString());
    }

    @When("user adds a new course with credits={int}")
    public void addCourseWithInvalidCredits(Integer credits) {
        addCourseWithErrorChecking("1234567", "c2", credits, GraduateLevel.Undergraduate.toString());
    }

    @When("user adds a new course with invalid graduate level")
    public void addCourseWithInvalidGraduateLevel() {
        addCourseWithErrorChecking("1234567", "c3", 2, "Invalid GL");
    }

    @Given("course of {string} with {string} prerequisites")
    public void setCourse(String title, String prerequisiteTitlesStr) {
        Course course = Optional.ofNullable(addCourseWithErrorChecking("1234567",
                        title,
                        2,
                        GraduateLevel.Undergraduate.toString()))
                .orElseThrow(() -> new RuntimeException("Bad input for test"));
        List<String> prerequisiteTitles = parse(prerequisiteTitlesStr);
        if (prerequisiteTitles != null) {
            Set<Course> prerequisites = prerequisiteTitles.stream().map(courseMap::get).collect(Collectors.toSet());
            course.setPrerequisites(prerequisites);
        }
        courseMap.put(title, course);
    }

    @Given("student of {string} that has passed {string} courses")
    public void setStudent(String studentName, String courseTitlesStr) throws ExceptionList {
        List<String> courseTitles = parse(courseTitlesStr);
        Student student = new Student("810197580");
        if (courseTitles != null) {
            List<Course> courses = courseTitles.stream().map(courseMap::get).toList();
            for (var course : courses) {
                student.setGrade("40001", course, 18);
            }
        }
        studentMap.put(studentName, student);
    }


    @Then("user gets an error with message={string}")
    public void checkException(String expectedErrorMessage) {
        Exception actualException = exceptionList.getExceptions().stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("Bad input for test"));
        assertThat(actualException.getMessage()).isEqualTo(expectedErrorMessage);
    }

    @Then("user does not get any error")
    public void checkException() {
        assertThat(exceptionList.getExceptions()).isEmpty();
    }

    @When("student of {string} wants to take course of {string}")
    public void takeCourse(String studentName, String courseName) {
        Student student = studentMap.get(studentName);
        Course course = courseMap.get(courseName);
        violations = course.canBeTakenBy(student);
    }

    @Then("student gets an error with message={string}")
    public void checkViolation(String expectedErrorMessage) {
        EnrollmentRuleViolation violation = violations.stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("Bad input for test"));
        assertThat(violation.toString()).isEqualTo(expectedErrorMessage);
    }

    private Course addCourseWithErrorChecking(String courseNumber, String title, int credits, String graduateLevel) {
        try {
            return new Course(courseNumber, title, credits, graduateLevel);
        } catch (ExceptionList el) {
            exceptionList = el;
            return null;
        }
    }

    private List<String> parse(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return List.of(",".split(str));
    }
}
