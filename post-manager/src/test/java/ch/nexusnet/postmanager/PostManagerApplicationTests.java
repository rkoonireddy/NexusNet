package ch.nexusnet.postmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PostManagerApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;


	@Test
	void contextLoads() {
        assertNotNull(applicationContext, "The application context should have loaded.");
	}

}
