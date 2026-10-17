package com.example.bag.converter;

import com.example.bag.model.Item;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Converter
public class ItemListConverter implements AttributeConverter<List<Item>, String> {

    private static final Logger logger = LoggerFactory.getLogger(ItemListConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Item> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            logger.error("Error serializing packing list to JSON string", e);
            return "[]";
        }
    }

    @Override
    public List<Item> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty() || dbData.trim().equals("[]")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Item>>() {});
        } catch (Exception e) {
            logger.error("Error deserializing packing list JSON: {}", dbData, e);
            return new ArrayList<>();
        }
    }
}
