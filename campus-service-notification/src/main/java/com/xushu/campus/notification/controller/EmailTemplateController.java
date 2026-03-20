package com.xushu.campus.notification.controller;

import com.xushu.campus.common.annotation.LoginRequired;
import com.xushu.campus.common.annotation.RoleRequired;
import com.xushu.campus.common.exception.BusinessException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xushu.campus.common.model.Result;
import com.xushu.campus.notification.dto.*;
import com.xushu.campus.notification.service.EmailTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 邮件模板管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/email-templates")
@Tag(name = "邮件模板管理", description = "邮件模板的创建、查询、管理等接口")
public class EmailTemplateController {

    @Autowired
    private EmailTemplateService emailTemplateService;

    @PostMapping
    @Operation(summary = "创建邮件模板", description = "创建新的邮件模板")
    @RoleRequired("ADMIN")
    public Result<EmailTemplateDTO> createEmailTemplate(@Valid @RequestBody CreateEmailTemplateRequest request) {
        try {
            EmailTemplateDTO result = emailTemplateService.createEmailTemplate(request);
            return Result.success("邮件模板创建成功", result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新邮件模板", description = "更新指定的邮件模板")
    @RoleRequired("ADMIN")
    public Result<EmailTemplateDTO> updateEmailTemplate(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmailTemplateRequest request) {
        try {
            EmailTemplateDTO result = emailTemplateService.updateEmailTemplate(id, request);
            return Result.success("邮件模板更新成功", result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除邮件模板", description = "删除指定的邮件模板（逻辑删除）")
    @RoleRequired("ADMIN")
    public Result<Void> deleteEmailTemplate(@PathVariable Long id) {
        try {
            emailTemplateService.deleteEmailTemplate(id);
            return Result.success();
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取邮件模板详情", description = "根据ID获取邮件模板详情")
    @RoleRequired("ADMIN")
    public Result<EmailTemplateDTO> getEmailTemplateById(@PathVariable Long id) {
        try {
            EmailTemplateDTO result = emailTemplateService.getEmailTemplateById(id);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/code/{templateCode}")
    @Operation(summary = "根据模板代码获取邮件模板", description = "根据模板代码获取邮件模板详情")
    @RoleRequired("ADMIN")
    public Result<EmailTemplateDTO> getEmailTemplateByCode(@PathVariable String templateCode) {
        try {
            EmailTemplateDTO result = emailTemplateService.getEmailTemplateByCode(templateCode);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "获取邮件模板列表", description = "获取邮件模板列表（分页）")
    @RoleRequired("ADMIN")
    public Result<IPage<EmailTemplateDTO>> getEmailTemplateList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(defaultValue = "create_time") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        IPage<EmailTemplateDTO> result = emailTemplateService.getEmailTemplateList(page, size, sortField, sortDirection);
        return Result.success(result);
    }

    @GetMapping("/type/{templateType}")
    @Operation(summary = "根据类型获取邮件模板", description = "根据模板类型获取邮件模板列表")
    @RoleRequired("ADMIN")
    public Result<List<EmailTemplateDTO>> getEmailTemplatesByType(@PathVariable String templateType) {
        List<EmailTemplateDTO> result = emailTemplateService.getEmailTemplatesByType(templateType);
        return Result.success(result);
    }

    @GetMapping("/enabled")
    @Operation(summary = "获取所有启用的邮件模板", description = "获取所有启用的邮件模板列表")
    @LoginRequired
    public Result<List<EmailTemplateDTO>> getAllEnabledEmailTemplates() {
        List<EmailTemplateDTO> result = emailTemplateService.getAllEnabledEmailTemplates();
        return Result.success(result);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索邮件模板", description = "根据关键词搜索邮件模板（分页）")
    @RoleRequired("ADMIN")
    public Result<IPage<EmailTemplateDTO>> searchEmailTemplates(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<EmailTemplateDTO> result = emailTemplateService.searchEmailTemplates(keyword, page, size);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用邮件模板", description = "启用或禁用指定的邮件模板")
    @RoleRequired("ADMIN")
    public Result<Void> toggleEmailTemplateStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        try {
            emailTemplateService.toggleEmailTemplateStatus(id, enabled);
            String message = enabled ? "启用成功" : "禁用成功";
            return Result.success(message, null);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/render")
    @Operation(summary = "渲染邮件模板", description = "使用变量渲染邮件模板")
    @LoginRequired
    public Result<RenderedEmailDTO> renderEmailTemplate(
            @RequestParam String templateCode,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            RenderedEmailDTO result = emailTemplateService.renderEmailTemplate(templateCode, variables);
            if (result.isSuccess()) {
                return Result.success("模板渲染成功", result);
            } else {
                return Result.error("模板渲染失败: " + result.getErrorMessage());
            }
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "验证模板变量", description = "验证邮件模板变量是否有效")
    @LoginRequired
    public Result<Boolean> validateTemplateVariables(
            @RequestParam String templateCode,
            @RequestBody(required = false) Map<String, Object> variables) {
        try {
            boolean result = emailTemplateService.validateTemplateVariables(templateCode, variables);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{templateCode}/variables")
    @Operation(summary = "获取模板变量说明", description = "获取邮件模板的变量说明")
    @LoginRequired
    public Result<String> getTemplateVariablesDescription(@PathVariable String templateCode) {
        try {
            String result = emailTemplateService.getTemplateVariablesDescription(templateCode);
            return Result.success(result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/batch")
    @Operation(summary = "批量创建邮件模板", description = "批量创建邮件模板")
    @RoleRequired("ADMIN")
    public Result<List<EmailTemplateDTO>> batchCreateEmailTemplates(@Valid @RequestBody List<CreateEmailTemplateRequest> requests) {
        try {
            List<EmailTemplateDTO> result = emailTemplateService.batchCreateEmailTemplates(requests);
            return Result.success("批量创建成功", result);
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/check-code/{templateCode}")
    @Operation(summary = "检查模板代码是否存在", description = "检查模板代码是否已存在")
    @RoleRequired("ADMIN")
    public Result<Boolean> checkTemplateCodeExists(
            @PathVariable String templateCode,
            @RequestParam(required = false) Long excludeId) {
        boolean result = emailTemplateService.checkTemplateCodeExists(templateCode, excludeId);
        return Result.success(result);
    }
}