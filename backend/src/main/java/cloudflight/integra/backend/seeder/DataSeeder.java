package cloudflight.integra.backend.seeder;

import cloudflight.integra.backend.auth.AuthService;
import cloudflight.integra.backend.auth.AppUserRepository;
import cloudflight.integra.backend.course.CourseRepository;
import cloudflight.integra.backend.course.model.Course;
import cloudflight.integra.backend.student.StudentRepository;
import cloudflight.integra.backend.student.model.Student;
import cloudflight.integra.backend.teacher.TeacherRepository;
import cloudflight.integra.backend.teacher.model.Teacher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AuthService authService;
    private final AppUserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public DataSeeder(AuthService authService,
                      AppUserRepository userRepository,
                      TeacherRepository teacherRepository,
                      StudentRepository studentRepository,
                      CourseRepository courseRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Prevent re-seeding if data already exists
        if (userRepository.count() > 0) {
            System.out.println("Data already seeded. Skipping...");
            return;
        }

        System.out.println("Seeding initial database with dummy data...");

        // 1. Create a teacher
        authService.register("teacher@test.com", "Password123!");
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setTitle("Prof.");
        teacher.setDepartment("Computer Science");
        teacher = teacherRepository.save(teacher);

        // 2. Create a student
        authService.register("student@test.com", "Password123!");
        Student student = new Student();
        student.setFirstName("Alice");
        student.setLastName("Smith");
        student.setNationalId("1234567890123");
        student.setDateOfBirth("2000-01-01");
        student.setEmail("student@test.com");
        student.setGroup("343");
        student = studentRepository.save(student);

        // 3. Create a course
        Course course = new Course(null, teacher.getId(), "Introduction to Spring Boot", "Learn Spring Boot from scratch", 6, "A beginner friendly course");
        course = courseRepository.save(course);

        System.out.println("Seeding completed successfully.");
    }
}
