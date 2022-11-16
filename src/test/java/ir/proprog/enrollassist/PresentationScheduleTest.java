package ir.proprog.enrollassist;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.domain.section.PresentationSchedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresentationScheduleTest {
    private PresentationSchedule presentationSchedule;

    @BeforeEach
    public void setup() throws ExceptionList {
        presentationSchedule = new PresentationSchedule("Saturday", "12:00", "15:00");
    }

    @Test
    public void hasConflictTest() throws ExceptionList {
        Map<PresentationSchedule, Boolean> parameterToExpectedMap = hasConflictParameters();
        for (var entry : parameterToExpectedMap.entrySet()) {
            PresentationSchedule other = entry.getKey();
            Boolean expected = entry.getValue();
            Boolean actual = presentationSchedule.hasConflict(other);
            Assertions.assertEquals(expected, actual);
        }
    }

    private Map<PresentationSchedule, Boolean> hasConflictParameters() throws ExceptionList {
        Map<PresentationSchedule, Boolean> parameterToExpectedMap = new HashMap<>();
        List<PresentationSchedule> parametersWithoutConflict = List.of(
                new PresentationSchedule("Sunday", "12:00", "15:00"),
                new PresentationSchedule("Saturday", "16:00", "18:00"),
                new PresentationSchedule("Saturday", "8:00", "10:00"),
                new PresentationSchedule("Saturday", "10:00", "12:00"),
                new PresentationSchedule("Saturday", "15:00", "17:00")
        );
        List<PresentationSchedule> parametersWithConflict = List.of(
                new PresentationSchedule("Saturday", "11:00", "13:00"),
                new PresentationSchedule("Saturday", "13:00", "14:00"),
                new PresentationSchedule("Saturday", "14:00", "16:00"),
                new PresentationSchedule("Saturday", "12:00", "15:00"),
                new PresentationSchedule("Saturday", "13:00", "15:00"),
                new PresentationSchedule("Saturday", "12:00", "14:00")
        );
        parametersWithoutConflict.forEach(p -> parameterToExpectedMap.put(p, false));
        parametersWithConflict.forEach(p -> parameterToExpectedMap.put(p, true));
        return parameterToExpectedMap;
    }
}
