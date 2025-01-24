package ch.nexusnet.chatmanager.chat.repository;

import ch.nexusnet.chatmanager.aws.dynamodb.repositories.ChatCrudRepository;
import ch.nexusnet.chatmanager.chat.Chat;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatRepositoryAdapter implements ChatRepositoryPort {

    private final ChatCrudRepository chatCrudRepository;

    public ChatRepositoryAdapter(ChatCrudRepository chatCrudRepository) {
        this.chatCrudRepository = chatCrudRepository;
    }

    @Override
    public Chat saveChat(Chat chat) {
        return chatCrudRepository.save(chat);
    }

    @Override
    public Optional<Chat> findChat(String participant1, String participant2) {
        if (participant1.compareTo(participant2) > 0) {
            String temp = participant1;
            participant1 = participant2;
            participant2 = temp;
        }

        return chatCrudRepository.findByParticipant1AndParticipant2(participant1, participant2);
    }

    @Override
    public List<Chat> getChatsForParticipant(String participant1) {
        return chatCrudRepository.findByIdContaining(participant1);
    }

}
