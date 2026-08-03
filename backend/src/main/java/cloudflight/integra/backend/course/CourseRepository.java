package cloudflight.integra.backend.course;

import cloudflight.integra.backend.course.model.Course;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CourseRepository {

    private final Map<UUID, Course> courses = new HashMap<>();

    public List<Course> findAll() {
        return new ArrayList<>(courses.values());
    }

    public Optional<Course> findById(UUID id) {
        return Optional.ofNullable(courses.get(id));
    }

    public Course save(Course course) {

        if (course.getId() == null) {
            course.setId(UUID.randomUUID());
        }

        courses.put(course.getId(), course);
        return course;
    }

    public void deleteById(UUID id) {
        courses.remove(id);
    }
}
