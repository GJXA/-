package com.xushu.campus.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xushu.campus.notification.entity.EmailTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 邮件模板Mapper接口
 */
@Mapper
public interface EmailTemplateMapper extends BaseMapper<EmailTemplate> {

    /**
     * 根据模板代码查询邮件模板
     */
    @Select("SELECT * FROM email_templates WHERE template_code = #{templateCode} AND deleted = 0")
    EmailTemplate selectByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 根据模板类型查询邮件模板列表
     */
    @Select("SELECT * FROM email_templates WHERE template_type = #{templateType} AND deleted = 0 AND enabled = 1 ORDER BY id DESC")
    List<EmailTemplate> selectByTemplateType(@Param("templateType") String templateType);

    /**
     * 查询所有启用的邮件模板
     */
    @Select("SELECT * FROM email_templates WHERE enabled = 1 AND deleted = 0 ORDER BY template_type, id DESC")
    List<EmailTemplate> selectAllEnabled();

    /**
     * 根据模板名称模糊查询
     */
    @Select("SELECT * FROM email_templates WHERE template_name LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0 ORDER BY id DESC")
    List<EmailTemplate> searchByTemplateName(@Param("keyword") String keyword);

    /**
     * 更新邮件模板启用状态
     */
    @Select("UPDATE email_templates SET enabled = #{enabled}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateEnabledStatus(@Param("id") Long id, @Param("enabled") Integer enabled);

    /**
     * 根据模板代码检查是否存在（不包含已删除的）
     */
    @Select("SELECT COUNT(*) FROM email_templates WHERE template_code = #{templateCode} AND deleted = 0")
    int countByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 检查模板代码是否存在（用于更新时排除自身）
     */
    @Select("SELECT COUNT(*) FROM email_templates WHERE template_code = #{templateCode} AND id != #{excludeId} AND deleted = 0")
    int countByTemplateCodeExcludeSelf(@Param("templateCode") String templateCode, @Param("excludeId") Long excludeId);
}