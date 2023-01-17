package ir.proprog.enrollassist;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.EnrollmentRules.PrerequisiteNotTaken;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.student.Student;
import lombok.RequiredArgsConstructor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
public class CourseStepDefinitions {
    private final Map<String, Course> courseMap = new HashMap<>();
    private ExceptionList exceptionList = new ExceptionList();
    private List<EnrollmentRuleViolation> violations = new ArrayList<>();

    @Mock
    private Student student;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        exceptionList = new ExceptionList();
        violations.clear();
        Course course = Optional.ofNullable(addCourseWithErrorChecking("1234567",
                        "c1",
                        2,
                        GraduateLevel.Undergraduate.toString()))
                .orElseThrow(() -> new RuntimeException("Bad input for test"));
        when(student.hasPassed(course)).thenReturn(true);
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
        String[] prerequisiteTitles = prerequisiteTitlesStr.split(",");
        if (prerequisiteTitles.length != 0) {
            Set<Course> prerequisites = Arrays.stream(prerequisiteTitles).map(courseMap::get).collect(Collectors.toSet());
            course.setPrerequisites(prerequisites);
        }
        courseMap.put(title, course);
    }

    @Given("student has passed {string} course")
    public void addStudent(String courseTitlesStr) {
        Course course = courseMap.get(courseTitlesStr);
        when(student.hasPassed(course)).thenReturn(true);
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

    @When("student wants to take course of {string}")
    public void takeCourseByStudent(String courseName) {
        Course course = courseMap.get(courseName);
        violations = course.canBeTakenBy(student);
    }

    @Then("student gets PrerequisiteNotTaken error")
    public void checkViolation() {
        EnrollmentRuleViolation violation = violations.stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("Bad input for test"));
        assertThat(violation.getClass()).isEqualTo(PrerequisiteNotTaken.class);
    }

    @Then("student gets no error")
    public void checkNoViolation() {
        assertThat(violations).isEmpty();
    }

    private Course addCourseWithErrorChecking(String courseNumber, String title, int credits, String graduateLevel) {
        try {
            return new Course(courseNumber, title, credits, graduateLevel);
        } catch (ExceptionList el) {
            exceptionList = el;
            return null;
        }
    }
}
