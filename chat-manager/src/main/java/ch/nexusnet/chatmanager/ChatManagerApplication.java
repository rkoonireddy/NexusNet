package ch.nexusnet.chatmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;

@SpringBootApplication(exclude = JpaRepositoriesAutoConfiguration.class)
public class ChatManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatManagerApplication.class, args);
	}

}
