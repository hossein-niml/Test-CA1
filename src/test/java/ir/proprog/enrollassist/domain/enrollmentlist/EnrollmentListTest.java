package ir.proprog.enrollassist.domain.enrollmentlist;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.EnrollmentRuleViolation;
import ir.proprog.enrollassist.domain.EnrollmentRules.MaxCreditsLimitExceeded;
import ir.proprog.enrollassist.domain.EnrollmentRules.MinCreditsRequiredNotMet;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.major.Faculty;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.domain.program.ProgramType;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.studyRecord.Term;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EnrollmentListTest {
    private EnrollmentList enrollmentList;
    private Student student;

    private static List<Course> courses() throws ExceptionList {
        Course c0 = new Course("0000000", "c0", 4, GraduateLevel.Undergraduate.name());
        Course c1 = new Course("1111111", "c1", 4, GraduateLevel.Undergraduate.name());
        Course c2 = new Course("2222222", "c2", 4, GraduateLevel.Undergraduate.name());
        Course c3 = new Course("3333333", "c3", 4, GraduateLevel.Undergraduate.name());
        Course c4 = new Course("4444444", "c4", 4, GraduateLevel.Undergraduate.name());
        Course c5 = new Course("5555555", "c5", 4, GraduateLevel.Undergraduate.name());
        return List.of(c0, c1, c2, c3, c4, c5);
    }

    private static Section getSection(int courseNum) throws ExceptionList {
        return new Section(courses().get(courseNum), String.valueOf(courseNum));
    }

    @Test
    public void minCreditsRequiredNotMetTest() throws ExceptionList {
        Collection<Section> sections = List.of(getSection(0));
        validateSingleViolation(sections,
                new MinCreditsRequiredNotMet(enrollmentList.getOwner().getGraduateLevel().getMinValidTermCredit()));
    }

    @Test
    public void maxCreditsLimitExceededTestWithZeroGpa() throws ExceptionList {
        Collection<Section> sections = new ArrayList<>();
        for (int i = 0; i < courses().size(); i++) {
            sections.add(getSection(i));
        }
        validateSingleViolation(sections, new MaxCreditsLimitExceeded(20));
    }

    @Test
    public void maxCreditsLimitExceededTestWithLessThan12Gpa() throws ExceptionList {
        student.setGrade(new Term().getTermCode(), courses().get(0), 10);
        Collection<Section> sections = new ArrayList<>();
        for (int i = 1; i < courses().size(); i++) {
            sections.add(getSection(i));
        }
        validateSingleViolation(sections, new MaxCreditsLimitExceeded(14));
    }

    private <V extends EnrollmentRuleViolation> void validateSingleViolation(Collection<Section> sections,
                                                                             V expectedViolation) {
        sections.forEach(section -> enrollmentList.addSection(section));
        List<EnrollmentRuleViolation> actualViolations = enrollmentList.checkEnrollmentRules();
        assertThat(actualViolations.size()).isEqualTo(1);
        assertThat(actualViolations.get(0).toString()).isEqualTo(expectedViolation.toString());
    }

    @BeforeEach
    public void setup() throws Exception {
        student = new Student("810197580", GraduateLevel.Undergraduate.name());
        Program program = new Program(
                new Major("1", "m", Faculty.Engineering.name()),
                GraduateLevel.Undergraduate.name(),
                6,
                Integer.MAX_VALUE,
                ProgramType.Major.name()
        );
        for (var c : courses()) {
            program.addCourse(c);
        }
        student.addProgram(program);
        enrollmentList = new EnrollmentList("list", student);
    }

    @AfterEach
    public void reset() {
        student = null;
        enrollmentList = null;
    }
}
