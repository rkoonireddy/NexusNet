package ch.nexusnet.chatmanager.message;

import ch.nexusnet.chatmanager.chat.Chat;
import ch.nexusnet.chatmanager.chat.repository.ChatRepositoryPort;
import ch.nexusnet.chatmanager.chat.exception.ChatWithSameUserException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService{

    private final ChatRepositoryPort chatRepository;

    public MessageServiceImpl(ChatRepositoryPort chatRepository) {
        this.chatRepository = chatRepository;
    }

    /**
     * Sends a message from one user to another. If a chat between the two users does not exist, a new chat is created.
     * The message is then added to the chat and the chat is saved in the repository.
     * If the sender and receiver are the same, a ChatWithSameUserException is thrown.
     *
     * @param message the message to be sent
     * @return the chat to which the message was added
     * @throws ChatWithSameUserException if the sender and receiver of the message are the same
     */
    @Override
    public Chat sendMessage(Message message) {
        if (Objects.equals(message.getSender(), message.getReceiver())) {
            throw new ChatWithSameUserException("Message cannot be sent to self. Sender: " + message.getSender());
        }

        message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setId(UUID.randomUUID().toString());

        Optional<Chat> chat = chatRepository.findChat(message.getSender(), message.getReceiver());
        if (chat.isPresent()) {
            return addMessageToChat(message, chat.get());
        } else {
            return createNewChat(message);
        }
    }

    private Chat addMessageToChat(Message message, Chat chat) {
        chat.addMessage(message);
        return chatRepository.saveChat(chat);
    }

    private Chat createNewChat(Message message) {
        String participant1 = message.getSender();
        String participant2 = message.getReceiver();
        if (participant1.compareTo(participant2) > 0) {
            String temp = participant1;
            participant1 = participant2;
            participant2 = temp;
        }

        Chat newChat = new Chat();
        newChat.setParticipant1(participant1);
        newChat.setParticipant2(participant2);
        newChat.addMessage(message);
        return chatRepository.saveChat(newChat);
    }
}
