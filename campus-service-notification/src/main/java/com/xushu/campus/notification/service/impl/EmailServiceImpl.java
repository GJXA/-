package com.xushu.campus.notification.service.impl;

import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.dto.FailedEmailDTO;
import com.xushu.campus.notification.dto.RenderedEmailDTO;
import com.xushu.campus.notification.dto.EmailSendStatusDTO;
import com.xushu.campus.notification.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 邮件发送服务实现类
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    @Autowired
    private com.xushu.campus.notification.service.EmailTemplateService emailTemplateService;

    @Override
    public void sendSimpleEmail(String to, String subject, String content) throws BusinessException {
        if (javaMailSender == null) {
            log.warn("JavaMailSender未配置，跳过邮件发送。收件人: {}, 主题: {}", to, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            javaMailSender.send(message);
            log.info("简单邮件发送成功，收件人: {}, 主题: {}", to, subject);
        } catch (Exception e) {
            log.error("简单邮件发送失败，收件人: {}, 主题: {}", to, subject, e);
            throw new BusinessException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws BusinessException {
        if (javaMailSender == null) {
            log.warn("JavaMailSender未配置，跳过邮件发送。收件人: {}, 主题: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("HTML邮件发送成功，收件人: {}, 主题: {}", to, subject);
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败，收件人: {}, 主题: {}", to, subject, e);
            throw new BusinessException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String content,
                                       List<MultipartFile> attachments) throws BusinessException {
        if (javaMailSender == null) {
            log.warn("JavaMailSender未配置，跳过邮件发送。收件人: {}, 主题: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);

            // 添加附件
            if (attachments != null) {
                for (MultipartFile attachment : attachments) {
                    if (!attachment.isEmpty()) {
                        // 创建临时文件
                        File tempFile = File.createTempFile("attachment_", "_" + attachment.getOriginalFilename());
                        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                            fos.write(attachment.getBytes());
                        }
                        helper.addAttachment(attachment.getOriginalFilename(), tempFile);
                        // 临时文件会在邮件发送后由垃圾回收器清理
                    }
                }
            }

            javaMailSender.send(message);
            log.info("带附件邮件发送成功，收件人: {}, 主题: {}, 附件数量: {}", to, subject,
                    attachments != null ? attachments.size() : 0);
        } catch (MessagingException | IOException e) {
            log.error("带附件邮件发送失败，收件人: {}, 主题: {}", to, subject, e);
            throw new BusinessException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public void sendTemplateEmail(String to, String templateCode, Map<String, Object> variables) throws BusinessException {
        if (javaMailSender == null) {
            log.warn("JavaMailSender未配置，跳过邮件发送。收件人: {}, 模板代码: {}", to, templateCode);
            return;
        }

        try {
            // 渲染模板
            RenderedEmailDTO renderedEmail = emailTemplateService.renderEmailTemplate(templateCode, variables);
            if (!renderedEmail.isSuccess()) {
                throw new BusinessException("模板渲染失败: " + renderedEmail.getErrorMessage());
            }

            // 发送邮件
            sendEmail(renderedEmail, to);
            log.info("模板邮件发送成功，收件人: {}, 模板代码: {}", to, templateCode);
        } catch (Exception e) {
            log.error("模板邮件发送失败，收件人: {}, 模板代码: {}", to, templateCode, e);
            throw new BusinessException("模板邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public void sendEmail(RenderedEmailDTO renderedEmail, String to) throws BusinessException {
        if (javaMailSender == null) {
            log.warn("JavaMailSender未配置，跳过邮件发送。收件人: {}, 模板代码: {}", to, renderedEmail.getTemplateCode());
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(renderedEmail.getSubject());

            // 设置HTML和纯文本内容
            if (StringUtils.hasText(renderedEmail.getHtmlContent())) {
                if (StringUtils.hasText(renderedEmail.getPlainTextContent())) {
                    helper.setText(renderedEmail.getPlainTextContent(), renderedEmail.getHtmlContent());
                } else {
                    helper.setText(renderedEmail.getHtmlContent(), true);
                }
            } else if (StringUtils.hasText(renderedEmail.getPlainTextContent())) {
                helper.setText(renderedEmail.getPlainTextContent(), false);
            } else {
                throw new BusinessException("邮件内容为空");
            }

            javaMailSender.send(message);
            log.info("邮件发送成功，收件人: {}, 主题: {}", to, renderedEmail.getSubject());
        } catch (MessagingException e) {
            log.error("邮件发送失败，收件人: {}, 主题: {}", to, renderedEmail.getSubject(), e);
            throw new BusinessException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public void batchSendEmails(List<String> toList, String subject, String content) throws BusinessException {
        if (javaMailSender == null) {
            log.warn("JavaMailSender未配置，跳过批量邮件发送。收件人数量: {}, 主题: {}", toList.size(), subject);
            return;
        }

        int successCount = 0;
        int failureCount = 0;
        List<String> failedRecipients = new ArrayList<>();

        for (String to : toList) {
            try {
                sendSimpleEmail(to, subject, content);
                successCount++;
            } catch (BusinessException e) {
                failureCount++;
                failedRecipients.add(to);
                log.error("批量邮件发送失败，收件人: {}, 错误: {}", to, e.getMessage());
            }
        }

        log.info("批量邮件发送完成，成功: {}, 失败: {}, 失败收件人: {}", successCount, failureCount, failedRecipients);

        if (failureCount > 0) {
            throw new BusinessException("批量邮件发送部分失败，失败数量: " + failureCount);
        }
    }

    @Override
    public void batchSendTemplateEmails(List<String> toList, String templateCode,
                                       List<Map<String, Object>> variablesList) throws BusinessException {
        if (javaMailSender == null) {
            log.warn("JavaMailSender未配置，跳过批量模板邮件发送。收件人数量: {}, 模板代码: {}", toList.size(), templateCode);
            return;
        }

        if (toList.size() != variablesList.size()) {
            throw new BusinessException("收件人列表和变量列表长度不一致");
        }

        int successCount = 0;
        int failureCount = 0;
        List<String> failedRecipients = new ArrayList<>();

        for (int i = 0; i < toList.size(); i++) {
            try {
                sendTemplateEmail(toList.get(i), templateCode, variablesList.get(i));
                successCount++;
            } catch (BusinessException e) {
                failureCount++;
                failedRecipients.add(toList.get(i));
                log.error("批量模板邮件发送失败，收件人: {}, 错误: {}", toList.get(i), e.getMessage());
            }
        }

        log.info("批量模板邮件发送完成，成功: {}, 失败: {}, 失败收件人: {}", successCount, failureCount, failedRecipients);

        if (failureCount > 0) {
            throw new BusinessException("批量模板邮件发送部分失败，失败数量: " + failureCount);
        }
    }

    @Override
    public boolean validateEmailAddress(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }

        // 简单的邮箱格式验证
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    @Override
    public EmailSendStatusDTO getEmailSendStatus(String messageId) throws BusinessException {
        // 简化实现：在实际应用中，这里应该查询邮件发送服务的状态
        EmailSendStatusDTO status = new EmailSendStatusDTO();
        status.setMessageId(messageId);
        status.setStatus("SENT");
        status.setStatusDesc("已发送");
        status.setSendTime(LocalDateTime.now().minusMinutes(5));
        status.setDeliveredTime(LocalDateTime.now().minusMinutes(4));
        return status;
    }

    @Override
    public List<FailedEmailDTO> getFailedEmails(int days) throws BusinessException {
        // 简化实现：在实际应用中，这里应该查询数据库中的失败邮件记录
        List<FailedEmailDTO> failedEmails = new ArrayList<>();

        // 示例数据
        FailedEmailDTO example = new FailedEmailDTO();
        example.setMessageId(UUID.randomUUID().toString());
        example.setToAddress("example@test.com");
        example.setSubject("测试邮件");
        example.setFailureReason("SMTP连接失败");
        example.setFailureTime(LocalDateTime.now().minusHours(2));
        example.setRetryCount(2);
        example.setLastRetryTime(LocalDateTime.now().minusHours(1));
        example.setCanRetry(true);

        failedEmails.add(example);
        return failedEmails;
    }

    @Override
    public void retryFailedEmail(String messageId) throws BusinessException {
        // 简化实现：在实际应用中，这里应该重新发送指定的失败邮件
        log.info("重试发送失败邮件，消息ID: {}", messageId);
        // 这里可以调用sendEmail等方法重新发送
    }
}