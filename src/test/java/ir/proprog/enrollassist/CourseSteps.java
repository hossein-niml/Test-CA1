package ir.proprog.enrollassist;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import lombok.RequiredArgsConstructor;
import org.junit.platform.commons.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class CourseSteps {
    private ExceptionList exceptionList = new ExceptionList();

    @When("user adds a new course with empty title")
    public void user_adds_a_new_course_with_empty_title() {
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

    private void addCourseWithErrorChecking(String courseNumber, String title, int credits, String graduateLevel) {
        try {
            new Course(courseNumber, title, credits, graduateLevel);
        } catch (ExceptionList el) {
            exceptionList = el;
        }
    }

    @Then("user gets an error with message={string}")
    public void checkException(String expectedErrorMessage) {
        if (StringUtils.isNotBlank(expectedErrorMessage)) {
            Exception actualException = exceptionList.getExceptions().stream()
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Bad input for test"));
            assertThat(actualException.getMessage()).isEqualTo(expectedErrorMessage);
        }
    }
}
