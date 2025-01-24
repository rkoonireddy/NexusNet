package ch.nexusnet.chatmanager.chat.repository;

import ch.nexusnet.chatmanager.chat.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatRepositoryPort {

    Chat saveChat(Chat chat);

    Optional<Chat> findChat(String participant1, String participant2);

    List<Chat> getChatsForParticipant(String participant1);
}
