package com.nova.haodf.service.runner;

import com.nova.haodf.entity.Hospital;
import com.nova.haodf.service.AmapService;
import com.nova.haodf.service.HospitalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetHospitalGeocodeRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadDoctorAndHospitalIdRunner.class);
    private final AmapService amapService;
    private final HospitalService hospitalService;
    @Value("${options.runner.get-hospital-geocode}")
    private Boolean taskOn;

    @Autowired
    public GetHospitalGeocodeRunner(AmapService amapService, HospitalService hospitalService) {
        this.amapService = amapService;
        this.hospitalService = hospitalService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Get hospital geocode runner is not started.");
            return;
        }
        List<Hospital> hospitalList = hospitalService
                .getHospitalListByStatusAndLimit(9, 1000);
        while (!hospitalList.isEmpty()) {
            int count = hospitalList.size();
            LOGGER.info("Found {} get hospital geocode tasks", count);
            for (int i = 0; i < count; ++i) {
                Hospital hospital = hospitalList.get(i);
                Long hospitalId = hospital.getHospitalId();
                try {
                    String name = hospital.getName();
                    String geocode = amapService.getGeocode(name, null);
                    if (geocode == null) {
                        LOGGER.warn("Failed to get hospital geocode, trying address geocode");
                        String address = hospital.getAddress();
                        geocode = amapService.getGeocode(address, null);
                    }
                    if (geocode != null) {
                        LOGGER.info("The final decided geocode = {}", geocode);
                        hospital.setLocation(geocode);
                        hospital.setStatus(10);
                    }
                } catch (Exception exception) {
                    LOGGER.warn("Failed to get geocode for hospital id = {}", hospitalId, exception);
                }
                LOGGER.info("Current progress {}/{}", i + 1, count);
            }
            hospitalService.saveHospitalList(hospitalList);
            hospitalList = hospitalService
                    .getHospitalListByStatusAndLimit(9, 1000);
        }
    }
}
