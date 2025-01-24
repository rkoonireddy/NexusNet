package ch.nexusnet.chatmanager.message;

import ch.nexusnet.chatmanager.aws.dynamodb.repositories.ChatCrudRepository;
import ch.nexusnet.chatmanager.chat.Chat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MessageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatCrudRepository chatCrudRepository;

    @BeforeEach
    public void setup() {
        chatCrudRepository.deleteAll();

        Chat chat = new Chat();
        chat.setParticipant1("participant1");
        chat.setParticipant2("participant2");

        chatCrudRepository.save(chat);
    }

    @AfterEach
    public void tearDown() {
        chatCrudRepository.deleteAll();
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void sendMessageNoExistingChat() throws Exception {
        Message message = new Message();
        message.setSender("sender");
        message.setReceiver("receiver");
        message.setContent("Hello");

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(message)))
                .andExpect(status().isCreated());
    }

    @Test
    void sendMessageExistingChat() throws Exception {
        Message message = new Message();
        message.setSender("participant1");
        message.setReceiver("participant2");
        message.setContent("Hello");

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(message)))
                .andExpect(status().isCreated());
    }

    @Test
    void sendMessageSameParticipant() throws Exception {
        Message message = new Message();
        message.setSender("participant1");
        message.setReceiver("participant1");
        message.setContent("Hello");

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(message)))
                .andExpect(status().isBadRequest());
    }
}
