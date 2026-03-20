package com.xushu.campus.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.constant.NotificationConstants;
import com.xushu.campus.notification.dto.*;
import com.xushu.campus.notification.entity.EmailTemplate;
import com.xushu.campus.notification.mapper.EmailTemplateMapper;
import com.xushu.campus.notification.service.EmailTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 邮件模板服务实现类
 */
@Slf4j
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    @Autowired
    private EmailTemplateMapper emailTemplateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailTemplateDTO createEmailTemplate(CreateEmailTemplateRequest request) throws BusinessException {
        // 验证请求数据
        if (!request.isValid()) {
            throw new BusinessException("邮件模板请求数据无效");
        }

        // 检查模板代码是否已存在
        if (checkTemplateCodeExists(request.getTemplateCode(), null)) {
            throw new BusinessException("模板代码已存在");
        }

        // 创建邮件模板实体
        EmailTemplate emailTemplate = new EmailTemplate();
        BeanUtils.copyProperties(request, emailTemplate);

        // 保存到数据库
        emailTemplateMapper.insert(emailTemplate);

        return convertToDTO(emailTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailTemplateDTO updateEmailTemplate(Long id, UpdateEmailTemplateRequest request) throws BusinessException {
        // 验证请求数据
        if (!request.hasUpdateFields()) {
            throw new BusinessException("没有提供更新字段");
        }
        if (!request.isValid()) {
            throw new BusinessException("更新数据无效");
        }

        // 获取现有模板
        EmailTemplate emailTemplate = emailTemplateMapper.selectById(id);
        if (emailTemplate == null || emailTemplate.getDeleted() == 1) {
            throw new BusinessException("邮件模板不存在");
        }

        // 检查模板代码是否已存在（排除自身）
        if (request.getTemplateCode() != null && checkTemplateCodeExists(request.getTemplateCode(), id)) {
            throw new BusinessException("模板代码已存在");
        }

        // 更新字段
        if (request.getTemplateName() != null) {
            emailTemplate.setTemplateName(request.getTemplateName());
        }
        if (request.getSubject() != null) {
            emailTemplate.setSubject(request.getSubject());
        }
        if (request.getContent() != null) {
            emailTemplate.setContent(request.getContent());
        }
        if (request.getPlainTextContent() != null) {
            emailTemplate.setPlainTextContent(request.getPlainTextContent());
        }
        if (request.getVariables() != null) {
            emailTemplate.setVariables(request.getVariables());
        }
        if (request.getTemplateType() != null) {
            emailTemplate.setTemplateType(request.getTemplateType());
        }
        if (request.getEnabled() != null) {
            emailTemplate.setEnabled(request.getEnabled());
        }
        if (request.getRemark() != null) {
            emailTemplate.setRemark(request.getRemark());
        }

        // 更新到数据库
        emailTemplateMapper.updateById(emailTemplate);

        return convertToDTO(emailTemplate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmailTemplate(Long id) throws BusinessException {
        EmailTemplate emailTemplate = emailTemplateMapper.selectById(id);
        if (emailTemplate == null || emailTemplate.getDeleted() == 1) {
            throw new BusinessException("邮件模板不存在");
        }

        emailTemplate.setDeleted(1);
        emailTemplateMapper.updateById(emailTemplate);
    }

    @Override
    public EmailTemplateDTO getEmailTemplateById(Long id) throws BusinessException {
        EmailTemplate emailTemplate = emailTemplateMapper.selectById(id);
        if (emailTemplate == null || emailTemplate.getDeleted() == 1) {
            throw new BusinessException("邮件模板不存在");
        }
        return convertToDTO(emailTemplate);
    }

    @Override
    public EmailTemplateDTO getEmailTemplateByCode(String templateCode) throws BusinessException {
        EmailTemplate emailTemplate = emailTemplateMapper.selectByTemplateCode(templateCode);
        if (emailTemplate == null || emailTemplate.getDeleted() == 1) {
            throw new BusinessException("邮件模板不存在");
        }
        return convertToDTO(emailTemplate);
    }

    @Override
    public IPage<EmailTemplateDTO> getEmailTemplateList(Integer page, Integer size, String sortField, String sortDirection) {
        Page<EmailTemplate> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<EmailTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(EmailTemplate::getDeleted, 0)
               .orderByDesc(EmailTemplate::getCreateTime);

        IPage<EmailTemplate> result = emailTemplateMapper.selectPage(pageParam, wrapper);

        List<EmailTemplateDTO> dtos = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<EmailTemplateDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(result, resultPage, "records");
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public List<EmailTemplateDTO> getEmailTemplatesByType(String templateType) {
        List<EmailTemplate> templates = emailTemplateMapper.selectByTemplateType(templateType);
        return templates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailTemplateDTO> getAllEnabledEmailTemplates() {
        List<EmailTemplate> templates = emailTemplateMapper.selectAllEnabled();
        return templates.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<EmailTemplateDTO> searchEmailTemplates(String keyword, Integer page, Integer size) {
        Page<EmailTemplate> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<EmailTemplate> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(EmailTemplate::getDeleted, 0)
               .like(StringUtils.hasText(keyword), EmailTemplate::getTemplateName, keyword)
               .orderByDesc(EmailTemplate::getCreateTime);

        IPage<EmailTemplate> result = emailTemplateMapper.selectPage(pageParam, wrapper);

        List<EmailTemplateDTO> dtos = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<EmailTemplateDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(result, resultPage, "records");
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleEmailTemplateStatus(Long id, boolean enabled) throws BusinessException {
        EmailTemplate emailTemplate = emailTemplateMapper.selectById(id);
        if (emailTemplate == null || emailTemplate.getDeleted() == 1) {
            throw new BusinessException("邮件模板不存在");
        }

        emailTemplate.setEnabled(enabled ? 1 : 0);
        emailTemplateMapper.updateById(emailTemplate);
    }

    @Override
    public boolean checkTemplateCodeExists(String templateCode, Long excludeId) {
        if (excludeId == null) {
            return emailTemplateMapper.countByTemplateCode(templateCode) > 0;
        } else {
            return emailTemplateMapper.countByTemplateCodeExcludeSelf(templateCode, excludeId) > 0;
        }
    }

    @Override
    public RenderedEmailDTO renderEmailTemplate(String templateCode, Map<String, Object> variables) throws BusinessException {
        EmailTemplateDTO template = getEmailTemplateByCode(templateCode);

        RenderedEmailDTO renderedEmail = new RenderedEmailDTO();
        renderedEmail.setTemplateCode(templateCode);
        renderedEmail.setTemplateName(template.getTemplateName());
        renderedEmail.setVariables(variables);

        try {
            // 渲染邮件主题
            String subject = renderTemplate(template.getSubject(), variables);
            renderedEmail.setSubject(subject);

            // 渲染HTML内容
            String htmlContent = renderTemplate(template.getContent(), variables);
            renderedEmail.setHtmlContent(htmlContent);

            // 渲染纯文本内容
            if (StringUtils.hasText(template.getPlainTextContent())) {
                String plainTextContent = renderTemplate(template.getPlainTextContent(), variables);
                renderedEmail.setPlainTextContent(plainTextContent);
            } else {
                // 如果没有提供纯文本内容，从HTML中提取
                renderedEmail.setPlainTextContent(extractPlainTextFromHtml(htmlContent));
            }

        } catch (Exception e) {
            log.error("渲染邮件模板失败，模板代码: {}", templateCode, e);
            renderedEmail.setSuccess(false);
            renderedEmail.setErrorMessage("渲染失败: " + e.getMessage());
        }

        return renderedEmail;
    }

    @Override
    public boolean validateTemplateVariables(String templateCode, Map<String, Object> variables) throws BusinessException {
        // 简化实现：检查变量是否为空
        if (variables == null || variables.isEmpty()) {
            return false;
        }

        // 这里可以添加更复杂的验证逻辑，比如检查必需的变量等
        return true;
    }

    @Override
    public String getTemplateVariablesDescription(String templateCode) throws BusinessException {
        EmailTemplateDTO template = getEmailTemplateByCode(templateCode);
        return template.getVariables();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<EmailTemplateDTO> batchCreateEmailTemplates(List<CreateEmailTemplateRequest> requests) throws BusinessException {
        List<EmailTemplateDTO> results = new ArrayList<>();

        for (CreateEmailTemplateRequest request : requests) {
            try {
                EmailTemplateDTO created = createEmailTemplate(request);
                results.add(created);
            } catch (Exception e) {
                log.error("批量创建邮件模板失败，模板代码: {}", request.getTemplateCode(), e);
                // 可以记录错误，但继续处理其他请求
            }
        }

        return results;
    }

    /**
     * 渲染模板（简单的变量替换）
     */
    private String renderTemplate(String template, Map<String, Object> variables) {
        if (template == null || variables == null) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = "\\$\\{" + entry.getKey() + "\\}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replaceAll(key, value);
        }

        return result;
    }

    /**
     * 从HTML中提取纯文本
     */
    private String extractPlainTextFromHtml(String html) {
        if (html == null) {
            return "";
        }

        // 简单实现：移除HTML标签
        return html.replaceAll("<[^>]*>", "")
                   .replaceAll("\\s+", " ")
                   .trim();
    }

    /**
     * 将EmailTemplate实体转换为EmailTemplateDTO
     */
    private EmailTemplateDTO convertToDTO(EmailTemplate emailTemplate) {
        if (emailTemplate == null) {
            return null;
        }

        EmailTemplateDTO dto = new EmailTemplateDTO();
        BeanUtils.copyProperties(emailTemplate, dto);
        dto.calculateDesc();
        return dto;
    }
}