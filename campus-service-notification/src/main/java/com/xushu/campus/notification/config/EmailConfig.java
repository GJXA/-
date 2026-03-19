package com.xushu.campus.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 邮件配置类
 */
@Configuration
public class EmailConfig {

    /**
     * 配置JavaMailSender
     * 注意：在实际应用中，这些配置应该从配置中心或配置文件中读取
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 基础配置（示例值，实际使用时需要从配置文件读取）
        mailSender.setHost("smtp.example.com");
        mailSender.setPort(587);
        mailSender.setUsername("noreply@example.com");
        mailSender.setPassword("your-password");

        // JavaMail属性配置
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false"); // 生产环境应该设为false
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "3000");
        props.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }

    /**
     * 邮件模板配置
     */
    @Bean
    public EmailTemplateConfig emailTemplateConfig() {
        return new EmailTemplateConfig();
    }

    /**
     * 邮件模板配置类
     */
    public static class EmailTemplateConfig {
        private String defaultFrom = "noreply@example.com";
        private String defaultFromName = "校园二手交易与兼职平台";
        private boolean enabled = true;
        private int maxRetryCount = 3;
        private int retryIntervalSeconds = 60;

        // getters and setters
        public String getDefaultFrom() {
            return defaultFrom;
        }

        public void setDefaultFrom(String defaultFrom) {
            this.defaultFrom = defaultFrom;
        }

        public String getDefaultFromName() {
            return defaultFromName;
        }

        public void setDefaultFromName(String defaultFromName) {
            this.defaultFromName = defaultFromName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxRetryCount() {
            return maxRetryCount;
        }

        public void setMaxRetryCount(int maxRetryCount) {
            this.maxRetryCount = maxRetryCount;
        }

        public int getRetryIntervalSeconds() {
            return retryIntervalSeconds;
        }

        public void setRetryIntervalSeconds(int retryIntervalSeconds) {
            this.retryIntervalSeconds = retryIntervalSeconds;
        }
    }
}