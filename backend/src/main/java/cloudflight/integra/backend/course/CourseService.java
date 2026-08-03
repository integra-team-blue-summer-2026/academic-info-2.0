package cloudflight.integra.backend.course;

import cloudflight.integra.backend.course.model.Course;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository repository;

    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }

    public List<Course> getAll() {
        return repository.findAll();
    }

    public Optional<Course> getById(UUID id) {
        return repository.findById(id);
    }

    public Course create(Course course) {
        return repository.save(course);
    }

    public Optional<Course> update(UUID id, Course course) {

        return repository.findById(id).map(existing -> {
            course.setId(id);
            return repository.save(course);
        });
    }

    public boolean delete(UUID id) {

        return repository.findById(id)
            .map(existing -> {
                repository.deleteById(id);
                return true;
            })
            .orElse(false);
    }
}
