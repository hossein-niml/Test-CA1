package ir.proprog.enrollassist.domain.course;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.course.CourseMajorView;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.repository.CourseRepository;
import ir.proprog.enrollassist.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddCourseServiceTest {
    private AddCourseService addCourseService;
    @Mock
    public CourseRepository courseRepository;
    @Mock
    public ProgramRepository programRepository;

    private Course course1;
    private Course course2;
    private Course course3;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        course1 = new Course("1111111", "cm1", 2, GraduateLevel.Masters.name());
        course2 = new Course("2222222", "cm2", 2, GraduateLevel.Masters.name());
        course3 = new Course("3333333", "cm3", 2, GraduateLevel.Masters.name());
        course1.setPrerequisites(Set.of(course2));
        course2.setPrerequisites(Set.of(course1));
        course3.setPrerequisites(Set.of(course1));
        when(courseRepository.findById(course1.getId())).thenReturn(Optional.of(course1));
        when(courseRepository.findById(course2.getId())).thenReturn(Optional.of(course2));
        when(courseRepository.findById(course3.getId())).thenReturn(Optional.of(course3));
        addCourseService = new AddCourseService(courseRepository, programRepository);
    }

    @Test
    public void checkLoopTest() {
        CourseMajorView courseMajorView1 = mock(CourseMajorView.class);
        CourseMajorView courseMajorView2 = mock(CourseMajorView.class);
        CourseMajorView courseMajorView3 = mock(CourseMajorView.class);
        when(courseMajorView1.getCourseTitle()).thenReturn(course1.getTitle());
        when(courseMajorView2.getCourseTitle()).thenReturn(course2.getTitle());
        when(courseMajorView3.getCourseTitle()).thenReturn(course3.getTitle());
        when(courseMajorView1.getCourseId()).thenReturn(course1.getId());
        when(courseMajorView2.getCourseId()).thenReturn(course2.getId());
        when(courseMajorView3.getCourseId()).thenReturn(course3.getId());
        when(courseMajorView1.getCourseNumber()).thenReturn(course1.getCourseNumber());
        when(courseMajorView2.getCourseNumber()).thenReturn(course2.getCourseNumber());
        when(courseMajorView3.getCourseNumber()).thenReturn(course3.getCourseNumber());
        when(courseMajorView1.getGraduateLevel()).thenReturn(course1.getGraduateLevel());
        when(courseMajorView2.getGraduateLevel()).thenReturn(course2.getGraduateLevel());
        when(courseMajorView3.getGraduateLevel()).thenReturn(course3.getGraduateLevel());
        when(courseMajorView1.getPrerequisites()).thenReturn(course1.getPrerequisites().stream().map(Course::getId).collect(Collectors.toSet()));
        when(courseMajorView2.getPrerequisites()).thenReturn(course2.getPrerequisites().stream().map(Course::getId).collect(Collectors.toSet()));
        when(courseMajorView3.getPrerequisites()).thenReturn(course3.getPrerequisites().stream().map(Course::getId).collect(Collectors.toSet()));

        List<CourseMajorView> courseMajorViews = List.of(courseMajorView1, courseMajorView2, courseMajorView3);
        List<Exception> exceptions = new ArrayList<>();
        try {
            for (var cm : courseMajorViews) {
                addCourseService.addCourse(cm);
            }
        } catch (ExceptionList e) {
            exceptions.addAll(e.getExceptions());
        }
        assertTrue(exceptions.stream()
                .anyMatch(e -> e.getMessage().equals(course1.getTitle() + " has made a loop in prerequisites.")));
        assertFalse(exceptions.stream()
                .anyMatch(e -> e.getMessage().equals(course3.getTitle() + " has made a loop in prerequisites.")));
    }
}
