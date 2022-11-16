package ir.proprog.enrollassist.domain.section;

import ir.proprog.enrollassist.Exception.ExceptionList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

public class PresentationScheduleTest {
    private PresentationSchedule presentationSchedule;

    private static List<Arguments> hasConflictParameters() throws ExceptionList {
        return List.of(
                Arguments.of(new PresentationSchedule("Sunday", "12:00", "15:00"), false),
                Arguments.of(new PresentationSchedule("Saturday", "16:00", "18:00"), false),
                Arguments.of(new PresentationSchedule("Saturday", "8:00", "10:00"), false),
                Arguments.of(new PresentationSchedule("Saturday", "10:00", "12:00"), false),
                Arguments.of(new PresentationSchedule("Saturday", "15:00", "17:00"), false),
                Arguments.of(new PresentationSchedule("Saturday", "11:00", "13:00"), true),
                Arguments.of(new PresentationSchedule("Saturday", "13:00", "14:00"), true),
                Arguments.of(new PresentationSchedule("Saturday", "14:00", "16:00"), true),
                Arguments.of(new PresentationSchedule("Saturday", "12:00", "15:00"), true),
                Arguments.of(new PresentationSchedule("Saturday", "13:00", "15:00"), true),
                Arguments.of(new PresentationSchedule("Saturday", "12:00", "14:00"), true)
        );
    }

    @BeforeEach
    public void setup() throws ExceptionList {
        presentationSchedule = new PresentationSchedule("Saturday", "12:00", "15:00");
    }

    @ParameterizedTest
    @MethodSource("hasConflictParameters")
    public void hasConflictTest(PresentationSchedule other, boolean expected) {
        boolean actual = presentationSchedule.hasConflict(other);
        Assertions.assertEquals(expected, actual);
    }

    @AfterEach
    public void reset() {
        presentationSchedule = null;
    }
}
