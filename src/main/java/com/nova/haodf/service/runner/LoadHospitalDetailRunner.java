package com.nova.haodf.service.runner;

import com.alibaba.fastjson2.JSONObject;
import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.entity.Hospital;
import com.nova.haodf.service.HospitalService;
import com.nova.haodf.util.JsonInstruction;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class LoadHospitalDetailRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadHospitalDetailRunner.class);
    private static final Map<String, JsonInstruction> JSON_INSTRUCTION_MAP = new HashMap<>();

    static {
        JSON_INSTRUCTION_MAP.put("setHospitalId", new JsonInstruction("hospitalIndex:hosHeadInfo:id", Long.class, JsonInstruction.INTEGER_TO_LONG_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setName", new JsonInstruction("hospitalIndex:hosHeadInfo:name", String.class));
        JSON_INSTRUCTION_MAP.put("setAreaCode", new JsonInstruction("hospitalIndex:hosHeadInfo:areaCode", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setPhone", new JsonInstruction("hospitalIndex:hosHeadInfo:phone", String.class));
        JSON_INSTRUCTION_MAP.put("setGrade", new JsonInstruction("hospitalIndex:hosHeadInfo:grade", Integer.class));
        JSON_INSTRUCTION_MAP.put("setGradeDescription", new JsonInstruction("hospitalIndex:hosHeadInfo:gradeDesc", String.class));
        JSON_INSTRUCTION_MAP.put("setCategory", new JsonInstruction("hospitalIndex:hosHeadInfo:category", Integer.class));
        JSON_INSTRUCTION_MAP.put("setCategoryDescription", new JsonInstruction("hospitalIndex:hosHeadInfo:categoryDesc", String.class));
        JSON_INSTRUCTION_MAP.put("setProperty", new JsonInstruction("hospitalIndex:hosHeadInfo:character", Integer.class));
        JSON_INSTRUCTION_MAP.put("setPropertyDescription", new JsonInstruction("hospitalIndex:hosHeadInfo:characterDesc", String.class));
        JSON_INSTRUCTION_MAP.put("setIntroduction", new JsonInstruction("hospitalIndex:hosHeadInfo:introTrim", String.class));
        JSON_INSTRUCTION_MAP.put("setAddress", new JsonInstruction("hospitalIndex:hosHeadInfo:address", String.class));
        JSON_INSTRUCTION_MAP.put("setAddressDetail", new JsonInstruction("hospitalIndex:hosHeadInfo:addressDetail", String.class));
        JSON_INSTRUCTION_MAP.put("setLocation", new JsonInstruction("hospitalIndex:hosHeadInfo:location", String.class));
        JSON_INSTRUCTION_MAP.put("setTotalFacultyCount", new JsonInstruction("hospitalIndex:hosHeadInfo:totalFacultyCnt", Integer.class));
        JSON_INSTRUCTION_MAP.put("setTotalDoctorCount", new JsonInstruction("hospitalIndex:hosHeadInfo:totalDoctorCnt", Integer.class));
        JSON_INSTRUCTION_MAP.put("setTotalCommentCount", new JsonInstruction("hospitalIndex:hosHeadInfo:totalCommentCnt", Integer.class));
        JSON_INSTRUCTION_MAP.put("setTotalDiseaseCount", new JsonInstruction("hospitalIndex:hosHeadInfo:diseaseCnt", Integer.class));
        JSON_INSTRUCTION_MAP.put("setProvinceName", new JsonInstruction("hospitalIndex:rankingInfo:ranking:rankingOfConcerns:province:name", String.class));
        JSON_INSTRUCTION_MAP.put("setProvinceRank", new JsonInstruction("hospitalIndex:rankingInfo:ranking:rankingOfConcerns:province:rank", Integer.class));
        JSON_INSTRUCTION_MAP.put("setCountryName", new JsonInstruction("hospitalIndex:rankingInfo:ranking:rankingOfConcerns:country:name", String.class));
        JSON_INSTRUCTION_MAP.put("setCountryRank", new JsonInstruction("hospitalIndex:rankingInfo:ranking:rankingOfConcerns:country:rank", Integer.class));
        JSON_INSTRUCTION_MAP.put("setTotalSpaceHits", new JsonInstruction("hospitalIndex:rankingInfo:ranking:totalSpaceHits", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setServicePatientCount", new JsonInstruction("hospitalIndex:rankingInfo:ranking:servicePatientCnt", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setUpVoteCount2Years", new JsonInstruction("hospitalIndex:rankingInfo:ranking:goodVoteCnt2Years", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setDownVoteCount2Years", new JsonInstruction("hospitalIndex:rankingInfo:ranking:badVoteCnt2Years", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setPatientSatisfaction", new JsonInstruction("hospitalIndex:rankingInfo:ranking:patientSatisfaction", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setArticleCount", new JsonInstruction("hospitalIndex:rankingInfo:ranking:articleCnt", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setLiveCount", new JsonInstruction("hospitalIndex:rankingInfo:ranking:liveCnt", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
        JSON_INSTRUCTION_MAP.put("setYearHaodfCount", new JsonInstruction("hospitalIndex:rankingInfo:rankDoctCnt", Integer.class, JsonInstruction.STRING_TO_INTEGER_ADAPTER_FUNCTION));
    }

    private final HospitalService hospitalService;
    @Value("${options.runner.load-hospital-detail}")
    private Boolean taskOn;
    @Value("${options.resources.hospital-detail-dir}")
    private String mainDirectory;

    @Autowired
    public LoadHospitalDetailRunner(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    private Hospital parseDetail(String htmlString) {
        Element scriptElement = Jsoup.parse(htmlString)
                .selectXpath("/html/body/script")
                .first();
        if (scriptElement == null) {
            LOGGER.warn("Hospital detail is null");
            return null;
        }
        JSONObject jsonObject;
        try {
            String scriptText = scriptElement.html();
            int jsonStartIndex = scriptText.indexOf("{");
            int jsonEndIndex = scriptText.lastIndexOf("};") + 1;
            String jsonString = scriptText.substring(jsonStartIndex, jsonEndIndex);
            jsonObject = JSONObject.parseObject(jsonString);
            Objects.requireNonNull(jsonObject);
        } catch (Exception exception) {
            LOGGER.warn("Could not parse meta data");
            return null;
        }
        Hospital hospital = new Hospital();
        for (Map.Entry<String, JsonInstruction> entry : JSON_INSTRUCTION_MAP.entrySet()) {
            String methodName = entry.getKey();
            JsonInstruction jsonInstruction = entry.getValue();
            Object fieldData = JsonInstruction.getFieldDataFromJson(jsonObject, jsonInstruction.getPath());
            fieldData = jsonInstruction.applyTransform(fieldData);
            try {
                Hospital.class
                        .getDeclaredMethod(methodName, jsonInstruction.getClazz())
                        .invoke(hospital, fieldData);
            } catch (Exception exception) {
                LOGGER.warn("Exception occurred, method name = {}", methodName, exception);
            }
        }
        hospital.setHospitalInfoStatus(EntityStatus.OK);
        return hospital;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Load hospital detail runner is not started.");
            return;
        }
        File directory = new File(mainDirectory);
        Collection<File> fileCollection = FileUtils.listFiles(directory, new String[]{"json"}, true);
        List<Hospital> hospitalList = new ArrayList<>();
        for (File file : fileCollection) {
            try {
                LOGGER.info("Processing file {}", file.getPath());
                StringBuilder builder = new StringBuilder();
                List<String> stringList = FileUtils.readLines(file, StandardCharsets.UTF_8);
                stringList.forEach(builder::append);
                String htmlString = builder.toString();
                Hospital hospital = parseDetail(htmlString);
                if (hospital != null) {
                    hospitalList.add(hospital);
                }
                LOGGER.info("File {} parsed", file.getPath());
            } catch (Exception exception) {
                LOGGER.error("Fail to parse file {}", file.getPath(), exception);
            }
        }
        hospitalService.saveHospitalDetailList(hospitalList);
        LOGGER.info("Load hospital detail runner run completed.");
    }
}
