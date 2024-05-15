package com.nova.haodf.service;

import com.nova.haodf.entity.DoctorOfflineReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MailSenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSenderService.class);
    private final JavaMailSender javaMailSender;
    private final DoctorOfflineReviewService doctorOfflineReviewService;

    @Autowired
    public MailSenderService(JavaMailSender javaMailSender, DoctorOfflineReviewService doctorOfflineReviewService) {
        this.javaMailSender = javaMailSender;
        this.doctorOfflineReviewService = doctorOfflineReviewService;
    }

    @Async
    public void sendProgressNotification(String mailFrom, String mailTo, String content) throws MailException {
        LOGGER.info("Mail from = {}, to = {}", mailFrom, mailTo);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(mailTo);
        message.setSubject("[爬虫自动提醒]");
        message.setText(content);
        javaMailSender.send(message);
    }

    @Scheduled(fixedDelay = 600000)
    public void sendProgressNotification() {
        try {
            List<DoctorOfflineReview> doctorOfflineReviewList = doctorOfflineReviewService
                    .getDoctorOfflineReviewsByStatusAndLimit(6, 1);
            LocalDateTime localDateTime = doctorOfflineReviewList.get(0).getRealTime();
            String content = String.format("当前未爬取的最近的文本日期为：%s\n陈  玄\n%s",
                    localDateTime.toLocalDate().toString(),
                    LocalDate.now()
            );
            sendProgressNotification("cx0222@vip.qq.com", "1929842479@qq.com", content);
        } catch (Exception exception) {
            LOGGER.error("Could not send email", exception);
        }
    }
}
