package ch.nexusnet.chatmanager.message;

import ch.nexusnet.chatmanager.chat.Chat;
import ch.nexusnet.chatmanager.chat.repository.ChatRepositoryPort;
import ch.nexusnet.chatmanager.chat.exception.ChatWithSameUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MessageServiceImplTest {


    private ChatRepositoryPort chatRepository;

    MessageServiceImpl messageService;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepositoryPort.class);
        messageService = new MessageServiceImpl(chatRepository);
    }

    @Test
    void Send_message_to_existing_chat(){
        String userId1 = "user1";
        String userId2 = "user2";

        Chat expectedChat = new Chat();
        expectedChat.setParticipant1(userId1);
        expectedChat.setParticipant2(userId2);

        when(chatRepository.findChat(userId1, userId2)).thenReturn(Optional.of(expectedChat));
        when(chatRepository.saveChat(any(Chat.class))).thenReturn(expectedChat);

        Message message = new Message();
        message.setReceiver(userId1);
        message.setSender(userId2);
        message.setContent("Hello");

        expectedChat.setMessages(new ArrayList<>());
        expectedChat.addMessage(message);

        Chat chat = messageService.sendMessage(message);

        assertEquals(userId1+":"+userId2, chat.getId());
        assertEquals(userId1, chat.getParticipant1());
        assertEquals(userId2, chat.getParticipant2());
        assertEquals(1, chat.getMessages().size());

        Message savedMessage = chat.getMessages().get(0);
        assertEquals(message.getContent(), savedMessage.getContent());
        assertEquals(message.getSender(), savedMessage.getSender());
        assertEquals(message.getReceiver(), savedMessage.getReceiver());
        assertNotNull(savedMessage.getTimestamp());
        assertNotNull(savedMessage.getId());
    }

    @Test
    void Send_message_to_nonexistent_chat(){
        String userId1 = "user1";
        String userId2 = "user2";

        Chat expectedChat = new Chat();
        expectedChat.setParticipant1(userId1);
        expectedChat.setParticipant2(userId2);

        when(chatRepository.findChat(userId1, userId2)).thenReturn(Optional.empty());
        when(chatRepository.saveChat(any(Chat.class))).thenReturn(expectedChat);

        Message message = new Message();
        message.setReceiver(userId1);
        message.setSender(userId2);
        message.setContent("Hello");

        expectedChat.setMessages(new ArrayList<>());
        expectedChat.addMessage(message);

        Chat chat = messageService.sendMessage(message);

        assertEquals(userId1+":"+userId2, chat.getId());
        assertEquals(userId1, chat.getParticipant1());
        assertEquals(userId2, chat.getParticipant2());
        assertEquals(1, chat.getMessages().size());

        Message savedMessage = chat.getMessages().get(0);
        assertEquals(message.getContent(), savedMessage.getContent());
        assertEquals(message.getSender(), savedMessage.getSender());
        assertEquals(message.getReceiver(), savedMessage.getReceiver());
        assertNotNull(savedMessage.getTimestamp());
        assertNotNull(savedMessage.getId());
    }

    @Test
    void Send_message_to_self(){
        Message message = new Message();
        message.setReceiver("user1");
        message.setSender("user1");

        assertThrows(ChatWithSameUserException.class, () -> messageService.sendMessage(message));
    }

}