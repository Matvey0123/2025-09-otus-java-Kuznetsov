package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.util.Map;

public class FileSerializer implements Serializer {

    private final ObjectMapper objectMapper;

    private final String fileName;

    public FileSerializer(String fileName) {
        this.objectMapper = JsonMapper.builder()
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .build();
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        // формирует результирующий json и сохраняет его в файл
        try {
            var file = new File(fileName);
            objectMapper.writeValue(file, data);
        } catch (Exception e) {
            throw new FileProcessException(e);
        }
    }
}
