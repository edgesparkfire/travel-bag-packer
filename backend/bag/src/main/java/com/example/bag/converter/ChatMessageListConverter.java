package com.example.bag.converter;

import com.example.bag.model.ChatMessage;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Converter
public class ChatMessageListConverter implements AttributeConverter<List<ChatMessage>, String> {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageListConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ChatMessage> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            logger.error("Error serializing chat history to JSON string", e);
            return "[]";
        }
    }

    @Override
    public List<ChatMessage> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty() || dbData.trim().equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<ChatMessage>>() {});
        } catch (Exception e) {
            logger.error("Error deserializing chat history JSON: {}", dbData, e);
            return new ArrayList<>();
        }
    }
}
