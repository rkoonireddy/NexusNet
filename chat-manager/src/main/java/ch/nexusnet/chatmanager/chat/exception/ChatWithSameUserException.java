package ch.nexusnet.chatmanager.chat.exception;

public class ChatWithSameUserException extends RuntimeException{

        public ChatWithSameUserException(String message) {
            super(message);
        }
}
