package example.api.controller;

import example.api.config.ConsoleFormatter;
import example.api.config.JSonManager;
import example.api.config.JwtTokenProvider;
import example.api.model.Status;
import example.api.model.Student;
import example.api.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.logging.Logger;

@RestController
public class StudentController {
    @Autowired
    private StudentService studentService;

    //Beans injections
    @Autowired
    private final Logger log;
    @Autowired
    private final ConsoleFormatter console;
    @Autowired
    private final JSonManager jSonManager;

    //Constructor for bean injection
    public StudentController(
            Logger log,
            ConsoleFormatter consoleFormat,
            JSonManager jSonManager
    ) {
        this.log = log;
        this.console = consoleFormat;
        this.jSonManager = jSonManager;
    }

    public void checkToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            log.info(token);
        }
    }

    private HttpHeaders createCORSHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Headers", "Content-Type, Origin, X-Requested-With, Accept, Content, Authorization");
        return headers;
    }

    @RequestMapping(value = "/save", method = {RequestMethod.OPTIONS, RequestMethod.POST})
    public ResponseEntity<Student> handleOptionsSave() {
        try {
            HttpHeaders headers = createCORSHeaders();
            return new ResponseEntity<>(headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.OPTIONS, RequestMethod.DELETE})
    public ResponseEntity<Student> handleOptionsDelete() {
        try {
            HttpHeaders headers = createCORSHeaders();
            return new ResponseEntity<>(headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Niveau d'autorisation : Tout le monde
    @GetMapping("/student/list")
    public Iterable<Student> getStudents(HttpServletRequest request) {
        log.info(console.format(Status.METHOD_TYPE, "METHOD STUDENT LIST CALLED"));
        Iterable<Student> studentList;

        checkToken(request);

        try {
            studentList = studentService.getStudents();
            log.info(console.format(Status.SUCCESS,
                    "[GET STUDENT LIST - DONE]  -- STATUS : " + HttpStatus.OK));
            return studentList;
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[GET STUDENT LIST - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            throw new RuntimeException(e);
        }
    }

    //Niveau d'autorisation : User
    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @GetMapping("/student/")
    public Optional<Student> getStudent(final Long id, HttpServletRequest r) {

        Optional<Student> student;

        checkToken(r);

        try {
            student = studentService.getStudent(id);
            log.info(console.format(Status.SUCCESS,
                    "[Get Student - DONE]  -- Status : " + HttpStatus.OK));
            return student;
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[Get Student - KO]  -- Status : " + HttpStatus.INTERNAL_SERVER_ERROR));
            throw new RuntimeException(e);
        }
    }

    //Niveau d'autorisation : Admin
    //@PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/student/delete")
    public ResponseEntity<String> deleteStudent (@RequestParam final Long id) {
        log.info(console.format(Status.METHOD_TYPE, "DELETE STUDENT METHOD CALLED"));
        try {
            studentService.deleteStudent(id);
            log.info(console.format(Status.IN_METHOD, "STUDENT DELETED"));
            log.info(console.format(Status.SUCCESS,
                    "[DELETE STUDENT - DONE]  -- STATUS : " + HttpStatus.OK));
            return new ResponseEntity<>(jSonManager.addLine("Status", "200").addLine("Method", "Delete student").build(),
                    createCORSHeaders() , HttpStatus.OK);
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[DELETE STUDENT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            return new ResponseEntity<>(jSonManager.addLine("Status", "200").addLine("Method", "Delete student").build(),
                    createCORSHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //Niveau d'autorisation : Admin
    //@PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/student/clear")
    public ResponseEntity<String> deleteAllStudents() {
        log.info(console.format(Status.METHOD_TYPE, "CLEAR STUDENT METHOD CALLED"));
        try {
            studentService.clearDB();
            log.info(console.format(Status.SUCCESS,
                    "[BASE CLEAR - DONE]  -- STATUS : " + HttpStatus.OK));
            return new ResponseEntity<>(jSonManager.addLine("Status", "200").addLine("Method", "Base clear").build(),
                    createCORSHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[BASE CLEAR - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            return new ResponseEntity<>(jSonManager.addLine("Status", "Error"). addLine("Method", "Base clear").build(),
                    createCORSHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Niveau d'autorisation : User
    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PostMapping("/student/save")
    public ResponseEntity<Student> saveStudent (@RequestBody Student student) {
        log.info(console.format(Status.METHOD_TYPE, "SAVE STUDENT METHOD CALLED"));
        Student newStudent = null;
        try {
            newStudent = studentService.saveStudent(student);
            log.info(console.format(Status.SUCCESS, "[SAVE STUDENT - DONE]  -- STATUS : " + HttpStatus.OK));
            return new ResponseEntity<>(newStudent, createCORSHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[SAVE STUDENT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            return new ResponseEntity<>(newStudent, createCORSHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/student/validate/")
    public ResponseEntity<String> validateStudent (@RequestParam final Long id) {
        log.info(console.format(Status.METHOD_TYPE, "VALIDATION METHOD CALLED"));
        try {
            Optional<Student> optionalStudent = studentService.getStudent(id);
            Student studentToValidate = optionalStudent.orElse(null); //Incroyable la syntax merci aux parents
            if (studentToValidate != null) {
                log.info(console.format(Status.IN_METHOD, "STUDENT FOUND"));
                studentToValidate.setValidated(true);
                log.info(console.format(Status.IN_METHOD, "VALIDATION SET"));
                studentService.saveStudent(studentToValidate);
                log.info(console.format(Status.IN_METHOD, "STUDENT UPDATED"));

                log.info(console.format(Status.SUCCESS, "[VALIDATE STUDENT] -- STATUS : " + HttpStatus.OK));
                return new ResponseEntity<>(jSonManager.addLine("Method", "Validation").addLine("Status", "Ok").build(),
                        createCORSHeaders(), HttpStatus.OK);
            } else {
                log.severe(console.format(Status.ERROR,
                        "[SAVE STUDENT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
                return new ResponseEntity<>(jSonManager.addLine("Method", "Validation").addLine("Status", "Error")
                        .addLine("Problem", "Student doesn't exist").build(), createCORSHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[SAVE STUDENT - KO]  -- STATUS : " + HttpStatus.INTERNAL_SERVER_ERROR));
            return new ResponseEntity<>(jSonManager.addLine("Method", "Validation").addLine("Status", "Error").build(),
                    createCORSHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Test only method
    @PutMapping("/student/invalidate/")
    public ResponseEntity<String> invalidateStudent (@RequestParam final Long id) {
        Student studentToInvalidate = studentService.getStudent(id).orElse(null);
        if (studentToInvalidate != null) {
            studentToInvalidate.setValidated(false);
            studentService.saveStudent(studentToInvalidate);
        } else {
            return new ResponseEntity<>(jSonManager.addLine("Method", "Invalidate").addLine("Status", "Error").build(), createCORSHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(jSonManager.addLine("Method", "Invalidate").addLine("Status", "Done").build(), createCORSHeaders(), HttpStatus.OK);
    }

    //Method for editing student
    //@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @PutMapping("/student/edit")
    public ResponseEntity<Student> editStudent (@RequestBody Student student) {
        log.info(console.format(Status.METHOD_TYPE, "EDIT STUDENT METHOD CALLED"));
        try {
            Optional<Student> existingStudent = studentService.getStudent(student.getId());

            if (existingStudent.isPresent()) {

                Student newStudent = studentService.saveStudent(student);
                log.info(console.format(Status.SUCCESS, "[EDIT STUDENT - DONE]  -- STATUS : " + HttpStatus.OK));
                return new ResponseEntity<>(newStudent, createCORSHeaders(), HttpStatus.OK);

            } else {

                log.severe(console.format(Status.ERROR, "[EDIT STUDENT - KO]  -- STATUS : " + HttpStatus.NOT_FOUND));
                return new ResponseEntity<>(createCORSHeaders(), HttpStatus.NOT_FOUND);

            }

        } catch ( Exception e) {
            log.severe(console.format(Status.ERROR,
                    "[Edit Student - KO]  -- Status : " + HttpStatus.INTERNAL_SERVER_ERROR + e.getMessage()));
            // e.printStackTrace();
            return new ResponseEntity<>(createCORSHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}