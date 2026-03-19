package com.xushu.campus.notification.service;

import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.dto.RenderedEmailDTO;
import com.xushu.campus.notification.dto.EmailSendStatusDTO;
import com.xushu.campus.notification.dto.FailedEmailDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 邮件发送服务接口
 */
public interface EmailService {

    /**
     * 发送简单邮件
     */
    void sendSimpleEmail(String to, String subject, String content) throws BusinessException;

    /**
     * 发送HTML邮件
     */
    void sendHtmlEmail(String to, String subject, String htmlContent) throws BusinessException;

    /**
     * 发送带附件的邮件
     */
    void sendEmailWithAttachment(String to, String subject, String content,
                                List<MultipartFile> attachments) throws BusinessException;

    /**
     * 发送模板邮件
     */
    void sendTemplateEmail(String to, String templateCode,
                          java.util.Map<String, Object> variables) throws BusinessException;

    /**
     * 发送邮件（使用RenderedEmailDTO）
     */
    void sendEmail(RenderedEmailDTO renderedEmail, String to) throws BusinessException;

    /**
     * 批量发送邮件
     */
    void batchSendEmails(List<String> toList, String subject, String content) throws BusinessException;

    /**
     * 批量发送模板邮件
     */
    void batchSendTemplateEmails(List<String> toList, String templateCode,
                                List<java.util.Map<String, Object>> variablesList) throws BusinessException;

    /**
     * 验证邮件地址是否有效
     */
    boolean validateEmailAddress(String email);

    /**
     * 获取邮件发送状态
     */
    EmailSendStatusDTO getEmailSendStatus(String messageId) throws BusinessException;

    /**
     * 获取失败邮件详情
     */
    List<FailedEmailDTO> getFailedEmails(int days) throws BusinessException;

    /**
     * 重新发送失败邮件
     */
    void retryFailedEmail(String messageId) throws BusinessException;
}