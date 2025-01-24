package ch.nexusnet.chatmanager.chat.util;

import ch.nexusnet.chatmanager.message.Message;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class MessageListConverter implements DynamoDBTypeConverter<String, List<Message>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(List<Message> object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert message list to JSON", e);
        }
    }

    @Override
    public List<Message> unconvert(String object) {
        try {
            return objectMapper.readValue(object, new TypeReference<List<Message>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Unable to read message list from JSON", e);
        }
    }
}