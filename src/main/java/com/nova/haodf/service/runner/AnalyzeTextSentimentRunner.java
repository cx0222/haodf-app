package com.nova.haodf.service.runner;

import com.nova.haodf.util.EntityStatus;
import com.nova.haodf.entity.DoctorOnlineReview;
import com.nova.haodf.service.DoctorOnlineReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class AnalyzeTextSentimentRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeTextSentimentRunner.class);
    private static final Set<String> POSITIVE_MAP;
    private static final Set<String> NEGATIVE_MAP;

    static {
        POSITIVE_MAP = Set.of("消除了我的疑惑", "态度好很亲切", "建议很有帮助", "有经验很专业", "解答清晰明了", "非常有耐心", "回复很及时");
        NEGATIVE_MAP = Set.of("还有问题没回复", "敷衍了事", "缺少解释说明", "对我没有帮助", "答非所问", "没有实质性建议");
    }

    private final DoctorOnlineReviewService doctorOnlineReviewService;
    @Value("${options.runner.analyze-text-sentiment}")
    private Boolean taskOn;

    @Autowired
    public AnalyzeTextSentimentRunner(DoctorOnlineReviewService doctorOnlineReviewService) {
        this.doctorOnlineReviewService = doctorOnlineReviewService;
    }

    private Integer evaluateTraitSatisfaction(String trait) {
        if (trait == null || trait.isEmpty()) {
            return 0;
        }
        String[] traits = trait.split(" ");
        int traitSatisfaction = 0;
        for (String traitItem : traits) {
            if (POSITIVE_MAP.contains(traitItem)) {
                ++traitSatisfaction;
            } else {
                --traitSatisfaction;
            }
        }
        return Integer.compare(traitSatisfaction, 0);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!taskOn) {
            LOGGER.warn("Analyze text sentiment runner is not started.");
            return;
        }
        List<DoctorOnlineReview> doctorOnlineReviewList = doctorOnlineReviewService.getDoctorOnlineReviewsByStatusAndLimit(EntityStatus.OK, Integer.MAX_VALUE);
        Map<UUID, Integer> traitSatisfactionMap = new HashMap<>();
        for (DoctorOnlineReview doctorOnlineReview : doctorOnlineReviewList) {
            UUID onlineReviewId = doctorOnlineReview.getOnlineReviewId();
            String trait = doctorOnlineReview.getTrait();
            int traitSatisfaction = evaluateTraitSatisfaction(trait);
            traitSatisfactionMap.put(onlineReviewId, traitSatisfaction);
        }
        doctorOnlineReviewService.updateTraitSatisfactionByOnlineReviewIds(traitSatisfactionMap);
    }
}
