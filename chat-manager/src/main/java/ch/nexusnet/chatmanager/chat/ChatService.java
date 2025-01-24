package ch.nexusnet.chatmanager.chat;


import java.util.List;

public interface ChatService {

    Chat getChat(String participant1, String participant2);

    List<Chat> getChatsForParticipant(String participant1);
}
