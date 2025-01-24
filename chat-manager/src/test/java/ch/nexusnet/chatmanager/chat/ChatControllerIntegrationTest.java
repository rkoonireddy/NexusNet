package ch.nexusnet.chatmanager.chat;

import ch.nexusnet.chatmanager.aws.dynamodb.repositories.ChatCrudRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void getChat() throws Exception {
        mockMvc.perform(get("/chats/participant1/participant2"))
                .andExpect(status().isOk());
    }

    @Test
    void getChatInverseOrder() throws Exception {
        mockMvc.perform(get("/chats/participant2/participant1"))
                .andExpect(status().isOk());
    }

    @Test
    void getChatNoSuchChat() throws Exception {
        mockMvc.perform(get("/chats/participant1/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChatSameParticipant() throws Exception {
        mockMvc.perform(get("/chats/participant1/participant1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getChatForUser() throws Exception {
        mockMvc.perform(get("/chats/participant1"))
                .andExpect(status().isOk());
    }

    @Test
    void getChatForUserOnly() throws Exception {
        Chat chat = new Chat();
        chat.setParticipant1("participant2");
        chat.setParticipant2("participant3");

        chatCrudRepository.save(chat);

        mockMvc.perform(get("/chats/participant1"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getChatForUserIsParticipant2() throws Exception {
        Chat chat = new Chat();
        chat.setParticipant1("participant2");
        chat.setParticipant2("participant3");

        chatCrudRepository.save(chat);

        mockMvc.perform(get("/chats/participant3"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getChatForUserWithoutChats() throws Exception {
        mockMvc.perform(get("/chats/no_chats"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

}