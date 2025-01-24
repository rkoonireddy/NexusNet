package ch.nexusnet.postmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;

@SpringBootApplication(exclude = JpaRepositoriesAutoConfiguration.class)

public class PostManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostManagerApplication.class, args);
	}

}
