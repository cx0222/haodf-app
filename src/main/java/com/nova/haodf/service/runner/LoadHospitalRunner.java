package com.nova.haodf.service.runner;

import com.nova.haodf.entity.Hospital;
import com.nova.haodf.service.HospitalService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class LoadHospitalRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadHospitalRunner.class);
    private final HospitalService hospitalService;
    @Value("${options.resources.hospital-list}")
    public String inputFile;
    @Value("${options.runner.load-hospital}")
    private Boolean taskOn;

    @Autowired
    public LoadHospitalRunner(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Load hospital runner is not started.");
            return;
        }
        File file = new File(inputFile + ".txt");
        List<Hospital> hospitalList = FileUtils.readLines(file, StandardCharsets.UTF_8)
                .stream()
                .mapToLong(Long::parseLong)
                .mapToObj(Hospital::new)
                .toList();
        hospitalService.saveHospitalList(hospitalList);
        LOGGER.info("Load hospital runner run completed.");
    }
}
