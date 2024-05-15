package com.nova.haodf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class LuceneConfig {
    @Value("${options.index.doctor-index-dir}")
    private String doctorIndexDirectory;
    @Value("${options.index.hospital-index-dir}")
    private String hospitalIndexDirectory;

    public Path getDoctorIndexPath() {
        return Paths.get(doctorIndexDirectory);
    }

    public Path getHospitalIndexPath() {
        return Paths.get(hospitalIndexDirectory);
    }
}
