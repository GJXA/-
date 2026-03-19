package com.xushu.campus.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.dto.*;
import com.xushu.campus.notification.entity.Conversation;
import com.xushu.campus.notification.entity.ConversationMember;
import com.xushu.campus.notification.mapper.ConversationMapper;
import com.xushu.campus.notification.mapper.ConversationMemberMapper;
import com.xushu.campus.notification.service.ConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会话服务实现类
 */
@Slf4j
@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMemberMapper conversationMemberMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationDTO createConversation(CreateConversationRequest request, Long creatorId) throws BusinessException {
        // 验证请求参数
        request.validate();

        // 检查权限和重复创建
        if (request.isPrivate()) {
            // 私聊检查是否已存在
            Long otherUserId = request.getMemberUserIds().get(0);
            if (hasPrivateConversation(creatorId, otherUserId)) {
                throw new BusinessException("私聊会话已存在");
            }
        }

        // 创建会话
        Conversation conversation = new Conversation();
        conversation.setConversationType(request.getConversationType());
        conversation.setTitle(request.getTitle());
        conversation.setAvatarUrl(request.getAvatarUrl());
        conversation.setCreatorId(creatorId);
        conversation.setMemberCount(1); // 创建者自己
        conversation.setUnreadCount(0);
        conversation.setLastMessageTime(LocalDateTime.now());

        int result = conversationMapper.insert(conversation);
        if (result <= 0) {
            throw new BusinessException("创建会话失败");
        }

        // 添加创建者为成员（拥有者角色）
        addConversationMember(conversation.getId(), creatorId, "OWNER", null);

        // 添加其他成员
        if (!CollectionUtils.isEmpty(request.getMemberUserIds())) {
            for (Long userId : request.getMemberUserIds()) {
                if (!userId.equals(creatorId)) {
                    addConversationMember(conversation.getId(), userId, "MEMBER", null);
                    conversation.setMemberCount(conversation.getMemberCount() + 1);
                }
            }
        }

        // 更新成员数量
        conversationMapper.updateById(conversation);

        // 发送欢迎消息（如果需要）
        if (Boolean.TRUE.equals(request.getSendWelcomeMessage())) {
            try {
                sendWelcomeMessage(conversation.getId(), creatorId, request.getWelcomeMessageContent());
            } catch (Exception e) {
                log.warn("发送欢迎消息失败: {}", e.getMessage());
            }
        }

        // 返回结果
        return convertToDTO(conversation);
    }

    @Override
    public ConversationDTO getConversationById(Long id, Long userId) throws BusinessException {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation == null || conversation.getDeleted() == 1) {
            throw new BusinessException("会话不存在或已被删除");
        }

        // 检查用户是否有权限访问
        validateUserAccess(id, userId);

        ConversationDTO dto = convertToDTO(conversation);
        // 设置当前用户的未读消息数
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(id, userId);
        if (member != null && member.getDeleted() == 0) {
            dto.setUnreadCount(member.getUnreadCount());
        }

        return dto;
    }

    @Override
    public IPage<ConversationDTO> getConversationsByUserId(Long userId, Integer page, Integer size) {
        // 设置默认分页参数
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 20;

        // 查询用户参与的会话
        List<Conversation> conversations = conversationMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(conversations)) {
            Page<ConversationDTO> emptyPage = new Page<>(page, size, 0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        // 转换为DTO并设置未读消息数
        List<ConversationDTO> dtos = conversations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 设置每个会话的未读消息数
        for (ConversationDTO dto : dtos) {
            ConversationMember member = conversationMemberMapper.selectByConversationAndUser(dto.getId(), userId);
            if (member != null && member.getDeleted() == 0) {
                dto.setUnreadCount(member.getUnreadCount());
            }
        }

        // 按最后消息时间排序
        dtos.sort((a, b) -> {
            if (a.getLastMessageTime() == null && b.getLastMessageTime() == null) return 0;
            if (a.getLastMessageTime() == null) return 1;
            if (b.getLastMessageTime() == null) return -1;
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        // 分页
        int total = dtos.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            Page<ConversationDTO> emptyPage = new Page<>(page, size, total);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        List<ConversationDTO> pageData = dtos.subList(start, end);
        Page<ConversationDTO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(pageData);
        return resultPage;
    }

    @Override
    public List<ConversationDTO> getConversationsByType(String conversationType, Integer limit) {
        if (!StringUtils.hasText(conversationType)) {
            throw new IllegalArgumentException("会话类型不能为空");
        }

        List<Conversation> conversations = conversationMapper.selectByType(conversationType);
        if (CollectionUtils.isEmpty(conversations)) {
            return Collections.emptyList();
        }

        if (limit != null && limit > 0 && conversations.size() > limit) {
            conversations = conversations.subList(0, limit);
        }

        return conversations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationDTO updateConversation(Long id, ConversationDTO updateRequest, Long operatorId) throws BusinessException {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation == null || conversation.getDeleted() == 1) {
            throw new BusinessException("会话不存在或已被删除");
        }

        // 检查操作权限
        if (!hasPermissionToManage(id, operatorId)) {
            throw new BusinessException("没有权限修改会话");
        }

        // 更新字段
        if (StringUtils.hasText(updateRequest.getTitle())) {
            conversation.setTitle(updateRequest.getTitle());
        }
        if (StringUtils.hasText(updateRequest.getAvatarUrl())) {
            conversation.setAvatarUrl(updateRequest.getAvatarUrl());
        }

        int result = conversationMapper.updateById(conversation);
        if (result <= 0) {
            throw new BusinessException("更新会话失败");
        }

        return convertToDTO(conversation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(Long id, Long operatorId) throws BusinessException {
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation == null || conversation.getDeleted() == 1) {
            throw new BusinessException("会话不存在或已被删除");
        }

        // 检查操作权限
        if (!hasPermissionToManage(id, operatorId)) {
            throw new BusinessException("没有权限删除会话");
        }

        // 逻辑删除会话
        conversation.setDeleted(1);
        conversationMapper.updateById(conversation);

        // 逻辑删除所有会话成员
        LambdaQueryWrapper<ConversationMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConversationMember::getConversationId, id);
        List<ConversationMember> members = conversationMemberMapper.selectList(wrapper);
        for (ConversationMember member : members) {
            member.setDeleted(1);
            conversationMemberMapper.updateById(member);
        }

        log.info("会话 {} 已被用户 {} 删除", id, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationMemberDTO addMemberToConversation(Long conversationId, Long userId, Long operatorId) throws BusinessException {
        // 验证会话存在
        validateConversationExists(conversationId);

        // 检查操作权限
        if (!hasPermissionToManage(conversationId, operatorId)) {
            throw new BusinessException("没有权限添加成员");
        }

        // 检查用户是否已是成员
        ConversationMember existingMember = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (existingMember != null) {
            if (existingMember.getDeleted() == 0) {
                throw new BusinessException("用户已是会话成员");
            } else {
                // 恢复已删除的成员
                existingMember.setDeleted(0);
                existingMember.setLeaveTime(null);
                existingMember.setUnreadCount(0);
                conversationMemberMapper.updateById(existingMember);
                return convertToMemberDTO(existingMember);
            }
        }

        // 添加新成员
        ConversationMember member = addConversationMember(conversationId, userId, "MEMBER", null);

        // 增加会话成员数量
        conversationMapper.increaseMemberCount(conversationId, 1);

        return convertToMemberDTO(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ConversationMemberDTO> addMembersToConversation(Long conversationId, List<Long> userIds, Long operatorId) throws BusinessException {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new BusinessException("用户ID列表不能为空");
        }

        List<ConversationMemberDTO> results = new ArrayList<>();
        for (Long userId : userIds) {
            try {
                ConversationMemberDTO dto = addMemberToConversation(conversationId, userId, operatorId);
                results.add(dto);
            } catch (Exception e) {
                log.warn("添加用户 {} 到会话 {} 失败: {}", userId, conversationId, e.getMessage());
            }
        }

        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMemberFromConversation(Long conversationId, Long userId, Long operatorId) throws BusinessException {
        // 验证会话存在
        validateConversationExists(conversationId);

        // 检查操作权限（用户可以移除自己，管理员可以移除其他人）
        boolean isSelfRemoval = userId.equals(operatorId);
        if (!isSelfRemoval && !hasPermissionToManage(conversationId, operatorId)) {
            throw new BusinessException("没有权限移除成员");
        }

        // 检查成员是否存在
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException("用户不是会话成员");
        }

        // 不能移除拥有者，除非是拥有者自己
        if ("OWNER".equals(member.getUserRole()) && !isSelfRemoval) {
            throw new BusinessException("不能移除会话拥有者");
        }

        // 逻辑删除成员
        member.setDeleted(1);
        member.setLeaveTime(LocalDateTime.now());
        conversationMemberMapper.updateById(member);

        // 减少会话成员数量
        conversationMapper.decreaseMemberCount(conversationId, 1);

        log.info("用户 {} 被用户 {} 从会话 {} 中移除", userId, operatorId, conversationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMembersFromConversation(Long conversationId, List<Long> userIds, Long operatorId) throws BusinessException {
        if (CollectionUtils.isEmpty(userIds)) {
            throw new BusinessException("用户ID列表不能为空");
        }

        for (Long userId : userIds) {
            try {
                removeMemberFromConversation(conversationId, userId, operatorId);
            } catch (Exception e) {
                log.warn("从会话 {} 中移除用户 {} 失败: {}", conversationId, userId, e.getMessage());
            }
        }
    }

    @Override
    public List<ConversationMemberDTO> getConversationMembers(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户访问权限
        validateUserAccess(conversationId, operatorId);

        List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }

        return members.stream()
                .filter(member -> member.getDeleted() == 0)
                .map(this::convertToMemberDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ConversationMemberDTO getConversationMember(Long conversationId, Long userId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户访问权限
        validateUserAccess(conversationId, operatorId);

        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException("用户不是会话成员");
        }

        return convertToMemberDTO(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationMemberDTO updateConversationMember(Long conversationId, Long userId, ConversationMemberDTO updateRequest, Long operatorId) throws BusinessException {
        // 验证会话存在
        validateConversationExists(conversationId);

        // 检查操作权限（用户只能更新自己的信息，管理员可以更新其他人）
        boolean isSelfUpdate = userId.equals(operatorId);
        if (!isSelfUpdate && !hasPermissionToManage(conversationId, operatorId)) {
            throw new BusinessException("没有权限更新成员信息");
        }

        // 检查成员是否存在
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException("用户不是会话成员");
        }

        // 更新字段
        if (StringUtils.hasText(updateRequest.getNicknameInConversation())) {
            member.setNicknameInConversation(updateRequest.getNicknameInConversation());
        }
        if (updateRequest.getIsMuted() != null) {
            member.setIsMuted(updateRequest.getIsMuted());
        }
        if (updateRequest.getUserRole() != null && !isSelfUpdate) {
            // 只有管理员可以修改角色，且不能修改拥有者的角色
            if (!"OWNER".equals(member.getUserRole()) && hasPermissionToManage(conversationId, operatorId)) {
                member.setUserRole(updateRequest.getUserRole());
            }
        }

        conversationMemberMapper.updateById(member);

        return convertToMemberDTO(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveConversation(Long conversationId, Long userId) throws BusinessException {
        removeMemberFromConversation(conversationId, userId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearConversationMessages(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和操作权限
        validateUserAccess(conversationId, operatorId);
        if (!hasPermissionToManage(conversationId, operatorId)) {
            throw new BusinessException("没有权限清空会话消息");
        }

        // 这里需要调用消息服务来清空消息
        // messageService.clearConversationMessages(conversationId);
        log.info("会话 {} 的消息已被用户 {} 清空", conversationId, operatorId);
    }

    @Override
    public void markConversationAsRead(Long conversationId, Long userId) throws BusinessException {
        // 验证会话存在和用户访问权限
        validateUserAccess(conversationId, userId);

        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException("用户不是会话成员");
        }

        // 重置未读消息数
        conversationMemberMapper.resetUnreadCount(member.getId(), member.getLastReadMessageId());

        // 减少会话的未读消息数
        if (member.getUnreadCount() != null && member.getUnreadCount() > 0) {
            conversationMapper.decreaseUnreadCount(conversationId, member.getUnreadCount());
        }

        log.debug("用户 {} 标记会话 {} 为已读", userId, conversationId);
    }

    @Override
    public void updateLastMessageInfo(Long conversationId, Long messageId, String messageContent) throws BusinessException {
        // 这里只更新会话的最后消息信息，不验证权限，由消息服务调用
        conversationMapper.updateLastMessageInfo(conversationId, messageId, messageContent, LocalDateTime.now().toString());
    }

    @Override
    public boolean isConversationMember(Long conversationId, Long userId) {
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        return member != null && member.getDeleted() == 0;
    }

    @Override
    public boolean hasPermissionToManage(Long conversationId, Long userId) {
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1) {
            return false;
        }
        return "OWNER".equals(member.getUserRole()) || "ADMIN".equals(member.getUserRole());
    }

    @Override
    public IPage<ConversationDTO> searchConversations(String keyword, Long userId, Integer page, Integer size) {
        // 简化实现：先获取用户的所有会话，然后过滤
        IPage<ConversationDTO> allConversations = getConversationsByUserId(userId, 1, Integer.MAX_VALUE);
        if (allConversations == null || CollectionUtils.isEmpty(allConversations.getRecords())) {
            Page<ConversationDTO> emptyPage = new Page<>(page, size, 0);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        List<ConversationDTO> filtered = allConversations.getRecords().stream()
                .filter(conv -> {
                    if (!StringUtils.hasText(keyword)) {
                        return true;
                    }
                    return (conv.getTitle() != null && conv.getTitle().contains(keyword)) ||
                           (conv.getLastMessageContent() != null && conv.getLastMessageContent().contains(keyword));
                })
                .collect(Collectors.toList());

        // 分页
        int total = filtered.size();
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 20;
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            Page<ConversationDTO> emptyPage = new Page<>(page, size, total);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        List<ConversationDTO> pageData = filtered.subList(start, end);
        Page<ConversationDTO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(pageData);
        return resultPage;
    }

    @Override
    public Long countUnreadConversations(Long userId) {
        List<Conversation> conversations = conversationMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(conversations)) {
            return 0L;
        }

        long count = 0;
        for (Conversation conversation : conversations) {
            ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversation.getId(), userId);
            if (member != null && member.getDeleted() == 0 && member.getUnreadCount() != null && member.getUnreadCount() > 0) {
                count++;
            }
        }

        return count;
    }

    @Override
    public ConversationStatisticsDTO getConversationStatistics(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户访问权限
        validateUserAccess(conversationId, operatorId);

        // 简化实现，实际需要从数据库统计各种数据
        ConversationStatisticsDTO statistics = new ConversationStatisticsDTO();
        statistics.setConversationId(conversationId);

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            statistics.setConversationTitle(conversation.getTitle());
            statistics.setMemberCount(conversation.getMemberCount());
        }

        // 设置当前用户的未读消息数
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, operatorId);
        if (member != null) {
            statistics.setUnreadCount(member.getUnreadCount());
        }

        // 这里可以添加更多统计数据的计算

        statistics.calculate();
        return statistics;
    }

    @Override
    public void archiveConversation(Long conversationId, Long userId) throws BusinessException {
        // 简化实现，实际需要添加归档状态
        log.info("用户 {} 归档会话 {}", userId, conversationId);
    }

    @Override
    public void restoreConversation(Long conversationId, Long userId) throws BusinessException {
        // 简化实现，实际需要移除归档状态
        log.info("用户 {} 恢复会话 {}", userId, conversationId);
    }

    @Override
    public void muteConversation(Long conversationId, Long userId, boolean mute) throws BusinessException {
        validateUserAccess(conversationId, userId);

        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException("用户不是会话成员");
        }

        member.setIsMuted(mute ? 1 : 0);
        conversationMemberMapper.updateById(member);

        log.info("用户 {} {} 会话 {}", userId, mute ? "静音" : "取消静音", conversationId);
    }

    @Override
    public void pinConversation(Long conversationId, Long userId, boolean pin) throws BusinessException {
        // 简化实现，实际需要添加置顶状态
        log.info("用户 {} {} 会话 {}", userId, pin ? "置顶" : "取消置顶", conversationId);
    }

    @Override
    public List<ConversationDTO> getPinnedConversations(Long userId) {
        // 简化实现，返回空列表
        return Collections.emptyList();
    }

    @Override
    public boolean checkConversationExists(Long conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        return conversation != null && conversation.getDeleted() == 0;
    }

    @Override
    public void validateUserAccess(Long conversationId, Long userId) throws BusinessException {
        if (!isConversationMember(conversationId, userId)) {
            throw new BusinessException("用户没有权限访问该会话");
        }
    }

    // ============= 私有辅助方法 =============

    private boolean hasPrivateConversation(Long userId1, Long userId2) {
        // 简化实现：查询两个用户是否已有私聊会话
        List<Conversation> user1Conversations = conversationMapper.selectByUserId(userId1);
        if (CollectionUtils.isEmpty(user1Conversations)) {
            return false;
        }

        for (Conversation conv : user1Conversations) {
            if ("PRIVATE".equals(conv.getConversationType())) {
                List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conv.getId());
                if (members != null && members.size() == 2) {
                    boolean hasUser1 = false;
                    boolean hasUser2 = false;
                    for (ConversationMember member : members) {
                        if (member.getUserId().equals(userId1) && member.getDeleted() == 0) {
                            hasUser1 = true;
                        }
                        if (member.getUserId().equals(userId2) && member.getDeleted() == 0) {
                            hasUser2 = true;
                        }
                    }
                    if (hasUser1 && hasUser2) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private ConversationMember addConversationMember(Long conversationId, Long userId, String role, String nickname) {
        ConversationMember member = new ConversationMember();
        member.setConversationId(conversationId);
        member.setUserId(userId);
        member.setUserRole(role);
        member.setNicknameInConversation(nickname);
        member.setIsMuted(0);
        member.setUnreadCount(0);
        member.setJoinTime(LocalDateTime.now());

        conversationMemberMapper.insert(member);
        return member;
    }

    private void sendWelcomeMessage(Long conversationId, Long senderId, String content) {
        // 这里需要调用消息服务发送欢迎消息
        // 简化实现，只记录日志
        String messageContent = content != null ? content : "欢迎加入会话！";
        log.info("发送欢迎消息到会话 {}: {}", conversationId, messageContent);
    }

    private void validateConversationExists(Long conversationId) throws BusinessException {
        if (!checkConversationExists(conversationId)) {
            throw new BusinessException("会话不存在或已被删除");
        }
    }

    private ConversationDTO convertToDTO(Conversation conversation) {
        if (conversation == null) {
            return null;
        }
        ConversationDTO dto = new ConversationDTO();
        BeanUtils.copyProperties(conversation, dto);
        dto.calculateDesc();
        return dto;
    }

    private ConversationMemberDTO convertToMemberDTO(ConversationMember member) {
        if (member == null) {
            return null;
        }
        ConversationMemberDTO dto = new ConversationMemberDTO();
        BeanUtils.copyProperties(member, dto);
        dto.calculateDesc();
        return dto;
    }
}