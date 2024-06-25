package example.api;

import example.api.service.AccountService;
import example.api.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentsApiApplication implements CommandLineRunner {

	@Autowired
	private AccountService accountService;
	@Autowired
	private StudentService studentService;

	public static void main(String[] args) {
		SpringApplication.run(StudentsApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		accountService.superUserManagement();
	}

	

}
