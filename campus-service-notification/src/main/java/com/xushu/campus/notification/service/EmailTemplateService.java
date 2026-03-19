package com.xushu.campus.notification.service;

import com.xushu.campus.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.notification.dto.CreateEmailTemplateRequest;
import com.xushu.campus.notification.dto.EmailTemplateDTO;
import com.xushu.campus.notification.dto.RenderedEmailDTO;
import com.xushu.campus.notification.dto.UpdateEmailTemplateRequest;
import com.xushu.campus.notification.entity.EmailTemplate;

import java.util.List;

/**
 * 邮件模板服务接口
 */
public interface EmailTemplateService {

    /**
     * 创建邮件模板
     */
    EmailTemplateDTO createEmailTemplate(CreateEmailTemplateRequest request) throws BusinessException;

    /**
     * 更新邮件模板
     */
    EmailTemplateDTO updateEmailTemplate(Long id, UpdateEmailTemplateRequest request) throws BusinessException;

    /**
     * 删除邮件模板（逻辑删除）
     */
    void deleteEmailTemplate(Long id) throws BusinessException;

    /**
     * 根据ID获取邮件模板详情
     */
    EmailTemplateDTO getEmailTemplateById(Long id) throws BusinessException;

    /**
     * 根据模板代码获取邮件模板
     */
    EmailTemplateDTO getEmailTemplateByCode(String templateCode) throws BusinessException;

    /**
     * 获取邮件模板列表（分页）
     */
    IPage<EmailTemplateDTO> getEmailTemplateList(Integer page, Integer size, String sortField, String sortDirection);

    /**
     * 根据模板类型获取邮件模板列表
     */
    List<EmailTemplateDTO> getEmailTemplatesByType(String templateType);

    /**
     * 获取所有启用的邮件模板
     */
    List<EmailTemplateDTO> getAllEnabledEmailTemplates();

    /**
     * 搜索邮件模板
     */
    IPage<EmailTemplateDTO> searchEmailTemplates(String keyword, Integer page, Integer size);

    /**
     * 启用/禁用邮件模板
     */
    void toggleEmailTemplateStatus(Long id, boolean enabled) throws BusinessException;

    /**
     * 检查模板代码是否已存在
     */
    boolean checkTemplateCodeExists(String templateCode, Long excludeId);

    /**
     * 渲染邮件模板（使用变量替换）
     */
    RenderedEmailDTO renderEmailTemplate(String templateCode, java.util.Map<String, Object> variables) throws BusinessException;

    /**
     * 验证模板变量是否有效
     */
    boolean validateTemplateVariables(String templateCode, java.util.Map<String, Object> variables) throws BusinessException;

    /**
     * 获取支持的模板变量说明
     */
    String getTemplateVariablesDescription(String templateCode) throws BusinessException;

    /**
     * 批量创建邮件模板
     */
    List<EmailTemplateDTO> batchCreateEmailTemplates(List<CreateEmailTemplateRequest> requests) throws BusinessException;
}