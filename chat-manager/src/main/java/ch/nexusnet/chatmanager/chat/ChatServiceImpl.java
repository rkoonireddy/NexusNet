package ch.nexusnet.chatmanager.chat;

import ch.nexusnet.chatmanager.chat.exception.ChatNotFoundException;
import ch.nexusnet.chatmanager.chat.exception.ChatWithSameUserException;
import ch.nexusnet.chatmanager.chat.repository.ChatRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepositoryPort chatRepository;

    public ChatServiceImpl(ChatRepositoryPort chatRepository) {
        this.chatRepository = chatRepository;
    }

    /**
     * Retrieves a chat between two different participants. Participant order does not matter
     * @param participant1 the first participant's identifier
     * @param participant2 the second participant's identifier
     * @return the chat between the specified participants
     * @throws ChatWithSameUserException if the two participant identifiers are the same
     * @throws ChatNotFoundException if no chat is found for the given participants
     */
    @Override
    public Chat getChat(String participant1, String participant2) {
        if (Objects.equals(participant1, participant2)) {
            throw new ChatWithSameUserException("Participants must be different. Provided: " + participant1);
        }
        return chatRepository.findChat(participant1, participant2).orElseThrow(() -> new ChatNotFoundException("Chat not found for participants: " + participant1 + " and " + participant2));
    }

    /**
     * Retrieves all chats for a given participant.
     *
     * @param participant1 the participant's identifier
     * @return a list of chats where the specified participant is involved
     */
    @Override
    public List<Chat> getChatsForParticipant(String participant1) {
        return chatRepository.getChatsForParticipant(participant1);
    }
}
