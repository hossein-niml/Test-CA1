package ir.proprog.enrollassist.course;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.proprog.enrollassist.Exception.ExceptionList;
import ir.proprog.enrollassist.controller.course.CourseMajorView;
import ir.proprog.enrollassist.domain.GraduateLevel;
import ir.proprog.enrollassist.domain.course.Course;
import ir.proprog.enrollassist.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class CourseControllerTest {
    private static final String entryPoint = "/courses/";
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private MockMvc mvc;

    public static List<Course> courses() throws ExceptionList {
        return List.of(
                new Course("1111111", "cm1", 2, GraduateLevel.Undergraduate.name()),
                new Course("2222222", "cm2", 2, GraduateLevel.Masters.name()),
                new Course("3333333", "cm3", 2, GraduateLevel.PHD.name())
        );
    }

    @BeforeEach
    public void setup() throws ExceptionList {
        courseRepository.saveAll(courses());
    }

    @Test
    public void allTest() throws Exception {
        ResultActions resultActions = mvc.perform(get(entryPoint))
                .andExpect(status().isOk());
        for (int i = 0; i < courses().size(); i++) {
            String courseNumberPath = "$[" + i + "].courseNumber.courseNumber";
            String courseTitlePath = "$[" + i + "].courseTitle";
            resultActions.andExpect(jsonPath(courseNumberPath).value(courses().get(i).getCourseNumber().getCourseNumber()))
                    .andExpect(jsonPath(courseTitlePath).value(courses().get(i).getTitle()));
        }
    }

    @Test
    public void oneTestWithExistentId() throws Exception {
        List<Course> coursesFromDatabase = (List<Course>) courseRepository.findAll();
        for (var course : coursesFromDatabase) {
            String url = entryPoint + course.getId();
            mvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.courseNumber.courseNumber").value(course.getCourseNumber().getCourseNumber()))
                    .andExpect(jsonPath("$.courseTitle").value(course.getTitle()));
        }
    }

    @Test
    public void oneTestWithNotExistentId() throws Exception {
        String url = entryPoint + (new Random().nextLong());
        mvc.perform(get(url))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Course not found"));
    }

    @Test
    public void addNewCourseTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Course newCourse = new Course("9999999", "cm9", 2, GraduateLevel.Undergraduate.name());
        CourseMajorView newCourseMajorView = new CourseMajorView(newCourse, new HashSet<>(), new HashSet<>());
        String newCourseJson = objectMapper.writeValueAsString(newCourseMajorView);
        mvc.perform(post(entryPoint)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(newCourseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseNumber.courseNumber").value(newCourse.getCourseNumber().getCourseNumber()))
                .andExpect(jsonPath("$.courseTitle").value(newCourse.getTitle()));

        Course existentCourse = courses().get(0);
        CourseMajorView existentCourseMajorView = new CourseMajorView(existentCourse, new HashSet<>(), new HashSet<>());
        String existentCourseJson = objectMapper.writeValueAsString(existentCourseMajorView);
        mvc.perform(post(entryPoint)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(existentCourseJson))
                .andExpect(status().isBadRequest());
    }

    @AfterEach
    public void tearDown() {
        courseRepository.deleteAll();
    }
}
