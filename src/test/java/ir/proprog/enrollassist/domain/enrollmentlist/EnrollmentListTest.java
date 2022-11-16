package ir.proprog.enrollassist.domain.enrollmentlist;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.EnrollmentRules.*;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.enrollmentList.EnrollmentList;
import ir.proprog.enrollassist.domain.major.Faculty;
import ir.proprog.enrollassist.domain.major.Major;
import ir.proprog.enrollassist.domain.program.Program;
import ir.proprog.enrollassist.domain.program.ProgramType;
import ir.proprog.enrollassist.domain.section.ExamTime;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.studyRecord.Term;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnrollmentListTest {
    private Student student;
    private EnrollmentList enrollmentList;

    private static List<Arguments> checkEnrollmentRulesParameters() throws Exception {
        return List.of(
                Arguments.of(
                        List.of(getSection(0), getSection(3), getSection(4)),
                        Set.of(PrerequisiteNotTaken.class)
                ),
                Arguments.of(
                        List.of(getSection(0), getSection(1), getSection(5)),
                        Set.of(RequestedCourseAlreadyPassed.class)
                ),
                Arguments.of(
                        List.of(getSection(0), getSection(1), getSection(1)),
                        Set.of(CourseRequestedTwice.class)
                ),
                Arguments.of(
                        List.of(getSection(0)),
                        Set.of(MinCreditsRequiredNotMet.class)
                ),
                Arguments.of(
                        List.of(
                                getSection(0, new ExamTime("2022-01-01T10:00", "2022-01-01T11:00")),
                                getSection(1, new ExamTime("2022-01-01T10:30", "2022-01-01T12:00")),
                                getSection(2, new ExamTime("2022-01-01T13:00", "2022-01-01T14:00"))
                        ),
                        Set.of(ExamTimeCollision.class)
                ),
                Arguments.of(
                        List.of(
                                getSection(0, "Sunday", "16:00", "18:00"),
                                getSection(1, "Saturday", "10:00", "12:00"),
                                getSection(2, "Saturday", "11:00", "13:00")
                        ),
                        Set.of(ConflictOfClassSchedule.class)
                )
        );
    }

    private static List<Course> courses() throws ExceptionList {
        Course c0 = new Course("0000000", "c0", 4, GraduateLevel.Undergraduate.name());
        Course c1 = new Course("1111111", "c1", 4, GraduateLevel.Undergraduate.name());
        Course c2 = new Course("2222222", "c2", 4, GraduateLevel.Undergraduate.name());
        Course c3 = new Course("3333333", "c3", 4, GraduateLevel.Undergraduate.name())
                .withPre(c1, c2);
        Course c4 = new Course("4444444", "c4", 4, GraduateLevel.Undergraduate.name());
        Course c5 = new Course("5555555", "c5", 4, GraduateLevel.Undergraduate.name());
        return List.of(c0, c1, c2, c3, c4, c5);
    }

    private static Section getSection(int courseNum) throws ExceptionList {
        return new Section(courses().get(courseNum), String.valueOf(courseNum));
    }

    private static Section getSection(int courseNum, ExamTime examTime) throws ExceptionList {
        Section section = getSection(courseNum);
        section.setExamTime(examTime);
        return section;
    }

    private static Section getSection(int courseNum, String dayOfWeek, String start, String end) throws ExceptionList {
        Section section = getSection(courseNum);
        section.setPresentationSchedule(Set.of(new PresentationSchedule(dayOfWeek, start, end)));
        return section;
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
        student.setGrade(new Term().getTermCode(), courses().get(5), 20);
        enrollmentList = new EnrollmentList("list", student);
    }

    @ParameterizedTest
    @MethodSource("checkEnrollmentRulesParameters")
    public <V extends EnrollmentRuleViolation> void checkEnrollmentRules(List<Section> sections,
                                                                         Set<Class<V>> expectedViolationTypes) {
        sections.forEach(enrollmentList::addSection);
        Set<Class<? extends EnrollmentRuleViolation>> actualViolationsTypes = enrollmentList.checkEnrollmentRules()
                .stream()
                .map(EnrollmentRuleViolation::getClass)
                .collect(Collectors.toSet());
        Assertions.assertEquals(expectedViolationTypes, actualViolationsTypes);
    }

    @AfterEach
    public void reset() {
        student = null;
        enrollmentList = null;
    }
}
