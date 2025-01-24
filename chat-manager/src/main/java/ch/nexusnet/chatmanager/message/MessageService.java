package ch.nexusnet.chatmanager.message;

import ch.nexusnet.chatmanager.chat.Chat;

public interface MessageService {

    Chat sendMessage(Message message);

}
