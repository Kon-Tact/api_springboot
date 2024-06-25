package example.api.service;

import example.api.model.Student;
import example.api.repository.StudentRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@NoArgsConstructor
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public Optional<Student> getStudent(final Long id) {
        System.out.println("I'm in get student");
        return studentRepository.findById(id); }

    public Iterable<Student> getStudents() {
        return studentRepository.findAll();
    }

    public void deleteStudent (final Long id) {
        studentRepository.deleteById(id);
    }

    public Student saveStudent (Student student) { return studentRepository.save(student); }

    public void clearDB () {
        studentRepository.deleteAll();
    }

    public void validateAll() {
        Iterable<Student> studentList = getStudents();
        for (Student student: studentList) {
            student.setValidated(true);
            saveStudent(student);
        }
        System.out.println("validate all done");
    }

}
