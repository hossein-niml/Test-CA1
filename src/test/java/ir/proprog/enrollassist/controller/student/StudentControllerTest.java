package ir.proprog.enrollassist.controller.student;

import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.section.SectionView;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.domain.section.Section;
import ir.proprog.enrollassist.domain.student.Student;
import ir.proprog.enrollassist.domain.student.StudentNumber;
import ir.proprog.enrollassist.domain.user.User;
import ir.proprog.enrollassist.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StudentControllerTest {
    private StudentController controller;
    private Student s1;
    private Student s2;
    private Student s3;
    private User mainUser;
    private User notExistUser;

    @Mock
    public StudentRepository studentRepository;
    @Mock
    public CourseRepository courseRepository;
    @Mock
    public SectionRepository sectionRepository;
    @Mock
    public UserRepository userRepository;
    @Mock
    public EnrollmentListRepository enrollmentListRepository;

    @BeforeEach
    public void setup() throws ExceptionList {
        MockitoAnnotations.openMocks(this);
        s1 = new Student("111111111", GraduateLevel.Undergraduate.name());
        s2 = new Student("222222222", GraduateLevel.Undergraduate.name());
        s3 = new Student("333333333", GraduateLevel.PHD.name());
        mainUser = new User("u", "11");
        notExistUser = new User("n", "22");
        controller = new StudentController(studentRepository,
                courseRepository,
                sectionRepository,
                enrollmentListRepository,
                userRepository);
    }

    // Test Model : Dummy, Stub
    // Validation : State
    // Approach : Mockisty
    @Test
    public void allTest() {
        when(studentRepository.findAll()).thenReturn(List.of(s1, s2, s3));
        Set<StudentNumber> actual = ((List<StudentView>) controller.all()).stream()
                .map(StudentView::getStudentNo)
                .collect(Collectors.toSet());
        Set<StudentNumber> expected = ((List<Student>) studentRepository.findAll()).stream()
                .map(Student::getStudentNumber)
                .collect(Collectors.toSet());
        assertEquals(expected, actual);
    }

    // Test Model : Dummy, Stub
    // Validation : State
    // Approach : Mockisty
    @Test
    public void oneTest() {
        when(studentRepository.findByStudentNumber(s1.getStudentNumber())).thenReturn(Optional.of(s1));
        when(studentRepository.findByStudentNumber(s2.getStudentNumber())).thenReturn(Optional.of(s2));
        when(studentRepository.findByStudentNumber(s3.getStudentNumber())).thenReturn(Optional.of(s3));
        assertEquals(s1.getStudentNumber(), controller.one(s1.getStudentNumber().getNumber()).getStudentNo());
        assertEquals(s2.getStudentNumber(), controller.one(s2.getStudentNumber().getNumber()).getStudentNo());
        assertEquals(s3.getStudentNumber(), controller.one(s3.getStudentNumber().getNumber()).getStudentNo());
        ResponseStatusException studentNotFound = assertThrows(ResponseStatusException.class,
                () -> controller.one("999999999"));
        assertEquals("Student not found", studentNotFound.getReason());
    }

    // Test Model : Dummy, Stub
    // Validation : State
    // Approach : Mockisty
    @Test
    public void addStudentTest() {
        when(studentRepository.findByStudentNumber(s3.getStudentNumber())).thenReturn(Optional.of(s3));
        when(userRepository.findByUserId(mainUser.getUserId())).thenReturn(Optional.of(mainUser));
        StudentView studentView1 = new StudentView(s1);
        studentView1.setUserId(mainUser.getUserId());
        StudentView studentView2 = new StudentView(s2);
        studentView2.setUserId(notExistUser.getUserId());
        StudentView studentView3 = new StudentView(s3);
        studentView3.setUserId(mainUser.getUserId());
        StudentView response = controller.addStudent(studentView1);
        assertEquals(s1.getStudentNumber(), response.getStudentNo());
        assertEquals(mainUser.getUserId(), response.getUserId());
        ResponseStatusException userNotFound = assertThrows(ResponseStatusException.class,
                () -> controller.addStudent(studentView2));
        ResponseStatusException studentAlreadyExists = assertThrows(ResponseStatusException.class,
                () -> controller.addStudent(studentView3));
        assertEquals("User with id: " + studentView2.getUserId() + " was not found.", userNotFound.getReason());
        assertEquals("This student already exists.", studentAlreadyExists.getReason());
    }

    // Test Model : Dummy, Stub
    // Validation : State
    // Approach : Mockisty
    @Test
    public void findTakeableSectionsByMajorTest() throws ExceptionList {
        Student student = mock(Student.class);
        Course course = mock(Course.class);
        Section takeableSection = new Section(course, "100");
        Section notTakeableSection = new Section(course, "200");
        List<Section> sections = List.of(takeableSection, notTakeableSection);
        when(sectionRepository.findAll()).thenReturn(sections);
        when(student.getStudentNumber()).thenReturn(new StudentNumber("666666666"));
        when(student.getTakeableSections(sections)).thenReturn(List.of(takeableSection));
        when(studentRepository.findByStudentNumber(student.getStudentNumber())).thenReturn(Optional.of(student));
        List<SectionView> actual =
                (List<SectionView>) controller.findTakeableSectionsByMajor(student.getStudentNumber().getNumber());
        assertEquals(1, actual.size());
        assertEquals(takeableSection.getSectionNo(), actual.get(0).getSectionNo());
        ResponseStatusException studentNotFound = assertThrows(ResponseStatusException.class,
                () -> controller.findTakeableSectionsByMajor("121212121"));
        assertEquals("Student not found.", studentNotFound.getReason());
    }
}
