package ch.nexusnet.chatmanager.chat;

import ch.nexusnet.chatmanager.chat.exception.ChatNotFoundException;
import ch.nexusnet.chatmanager.chat.repository.ChatRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatServiceImplTest {

    private ChatRepositoryPort chatRepository;

    ChatServiceImpl chatService;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepositoryPort.class);
        chatService = new ChatServiceImpl(chatRepository);
    }

    @Test
    void Get_chat() {
        String userId1 = "user1";
        String userId2 = "user2";
        String chatId = "1";

        Chat expectedChat = new Chat();
        expectedChat.setParticipant1(userId1);
        expectedChat.setParticipant2(userId2);

        when(chatRepository.findChat(userId1, userId2)).thenReturn(Optional.of(expectedChat));

        Chat chat = chatService.getChat(userId1, userId2);

        assertEquals(chat, expectedChat);
    }

    @Test
    void Get_chat_not_found() {
        String userId1 = "user1";
        String userId2 = "user2";

        when(chatRepository.findChat(userId1, userId2)).thenReturn(Optional.empty());

        assertThrows(ChatNotFoundException.class, () -> chatService.getChat(userId1, userId2));
    }
}