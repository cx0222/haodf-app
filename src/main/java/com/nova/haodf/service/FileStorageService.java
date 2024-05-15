package com.nova.haodf.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class FileStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);
    @Value("${options.resources.main-dir}")
    public String outputFile;

    public FileStorageService() {
    }

    public void saveToFile(String fileId, String content) throws IOException {
        File file = new File(outputFile + '/' + fileId + ".json");
        FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8, true);
    }

    public void saveToFile(Map<String, String> contentMap) throws IOException {
        for (Map.Entry<String, String> entry : contentMap.entrySet()) {
            saveToFile(entry.getKey(), entry.getValue());
        }
        LOGGER.info("Saved {} records to file", contentMap.size());
    }
}
