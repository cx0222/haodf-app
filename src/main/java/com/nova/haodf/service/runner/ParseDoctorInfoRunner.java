package com.nova.haodf.service.runner;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.entity.Doctor;
import com.nova.haodf.service.DoctorService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ParseDoctorInfoRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseDoctorInfoRunner.class);
    private final DoctorService doctorService;
    @Value("${options.runner.parse-doctor-info}")
    private Boolean taskOn;
    @Value("${options.resources.main-dir}")
    private String mainDirectory;

    @Autowired
    public ParseDoctorInfoRunner(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @Override
    public void run(String... args) {
        if (!taskOn) {
            LOGGER.warn("Parse doctor info runner is not started.");
            return;
        }
        File directory = new File(mainDirectory);
        Collection<File> fileCollection = FileUtils.listFiles(directory, new String[]{"json"}, true);
        for (File file : fileCollection) {
            try {
                LOGGER.info("Processing file {}", file.getPath());
                List<Doctor> doctorList = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                List<String> stringList = FileUtils.readLines(file, StandardCharsets.UTF_8);
                stringList.forEach(builder::append);
                String jsonString = builder.toString();
                JSONArray jsonArray = JSONObject.parse(jsonString)
                        .getJSONArray("data");
                for (Object doctorObject : jsonArray) {
                    Long doctorId = ((JSONObject) doctorObject)
                            .getLong("doctorId");
                    doctorList.add(new Doctor(doctorId));
                }
                doctorService.saveDoctorList(doctorList);
                LOGGER.info("File {} parsed", file.getPath());
            } catch (Exception exception) {
                LOGGER.error("Fail to parse file {}", file.getPath(), exception);
            }
        }
        LOGGER.info("Parse doctor info runner run completed.");
    }
}
