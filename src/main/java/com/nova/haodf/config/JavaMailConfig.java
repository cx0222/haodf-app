package com.nova.haodf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JavaMailConfig {
    @Value("${spring.mail.host}")
    public String mailHost;
    @Value("${spring.mail.port}")
    public Integer mailPort;
    @Value("${spring.mail.username}")
    public String mailUsername;
    @Value("${spring.mail.password}")
    public String mailPassword;
    @Value("${spring.mail.default-encoding}")
    public String mailDefaultEncoding;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        mailSender.setDefaultEncoding(mailDefaultEncoding);
        return mailSender;
    }
}
