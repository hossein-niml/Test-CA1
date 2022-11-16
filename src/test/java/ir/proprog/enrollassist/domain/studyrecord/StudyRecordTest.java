package ir.proprog.enrollassist.domain.studyrecord;

import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.studyRecord.StudyRecord;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

public class StudyRecordTest {

    private static List<IsPassedParameter> passedParameters() throws Exception {
        return List.of(
                new IsPassedParameter(GraduateLevel.Undergraduate, GraduateLevel.Undergraduate, 12),
                new IsPassedParameter(GraduateLevel.Undergraduate, GraduateLevel.Masters, 11),
                new IsPassedParameter(GraduateLevel.Undergraduate, GraduateLevel.Masters, 18),
                new IsPassedParameter(GraduateLevel.Undergraduate, GraduateLevel.PHD, 13),
                new IsPassedParameter(GraduateLevel.Undergraduate, GraduateLevel.PHD, 17),
                new IsPassedParameter(GraduateLevel.Masters, GraduateLevel.Undergraduate, 11),
                new IsPassedParameter(GraduateLevel.Masters, GraduateLevel.Undergraduate, 18),
                new IsPassedParameter(GraduateLevel.Masters, GraduateLevel.Masters, 16),
                new IsPassedParameter(GraduateLevel.Masters, GraduateLevel.PHD, 13),
                new IsPassedParameter(GraduateLevel.Masters, GraduateLevel.PHD, 18),
                new IsPassedParameter(GraduateLevel.PHD, GraduateLevel.Undergraduate, 13),
                new IsPassedParameter(GraduateLevel.PHD, GraduateLevel.Undergraduate, 17),
                new IsPassedParameter(GraduateLevel.PHD, GraduateLevel.Masters, 13),
                new IsPassedParameter(GraduateLevel.PHD, GraduateLevel.Masters, 18),
                new IsPassedParameter(GraduateLevel.PHD, GraduateLevel.PHD, 19)
        );
    }

    private static List<IsPassedParameter> failedParameters() throws Exception {
        return List.of(
                new IsPassedParameter(GraduateLevel.Undergraduate, GraduateLevel.Undergraduate, 8),
                new IsPassedParameter(GraduateLevel.Undergraduate, GraduateLevel.Masters, 9),
                new IsPassedParameter(GraduateLevel.Undergraduate, GraduateLevel.PHD, 7),
                new IsPassedParameter(GraduateLevel.Masters, GraduateLevel.Undergraduate, 8),
                new IsPassedParameter(GraduateLevel.Masters, GraduateLevel.Masters, 10),
                new IsPassedParameter(GraduateLevel.Masters, GraduateLevel.PHD, 11),
                new IsPassedParameter(GraduateLevel.PHD, GraduateLevel.Undergraduate, 5),
                new IsPassedParameter(GraduateLevel.PHD, GraduateLevel.Masters, 8),
                new IsPassedParameter(GraduateLevel.PHD, GraduateLevel.PHD, 13)
        );
    }

    @ParameterizedTest
    @MethodSource("passedParameters")
    public void isPassedTests(IsPassedParameter parameter) throws Exception {
        StudyRecord record = new StudyRecord("10002", parameter.getCourse(), parameter.getGrade());
        boolean actual = record.isPassed(parameter.getGraduateLevel());
        Assertions.assertTrue(actual);
    }

    @ParameterizedTest
    @MethodSource("failedParameters")
    public void isFailedTests(IsPassedParameter parameter) throws Exception {
        StudyRecord record = new StudyRecord("10002", parameter.getCourse(), parameter.getGrade());
        boolean actual = record.isPassed(parameter.getGraduateLevel());
        Assertions.assertFalse(actual);
    }

    @Getter
    private static final class IsPassedParameter {
        private final Course course;
        private final Double grade;
        private final GraduateLevel graduateLevel;

        public IsPassedParameter(GraduateLevel courseGraduateLevel,
                                 GraduateLevel studentGraduateLevel,
                                 double studentGrade)
                throws Exception {
            this.course = new Course("1234567", "title", 3, courseGraduateLevel.name());
            this.grade = studentGrade;
            this.graduateLevel = studentGraduateLevel;
        }
    }
}
