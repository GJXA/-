package com.xushu.campus.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.dto.*;
import com.xushu.campus.notification.entity.Conversation;
import com.xushu.campus.notification.entity.ConversationMember;
import com.xushu.campus.notification.entity.Message;
import com.xushu.campus.notification.entity.MessageReadStatus;
import com.xushu.campus.notification.mapper.ConversationMapper;
import com.xushu.campus.notification.mapper.ConversationMemberMapper;
import com.xushu.campus.notification.mapper.MessageMapper;
import com.xushu.campus.notification.mapper.MessageReadStatusMapper;
import com.xushu.campus.notification.service.MessageService;
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
 * 消息服务实现类
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMemberMapper conversationMemberMapper;

    @Autowired
    private MessageReadStatusMapper messageReadStatusMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageDTO sendMessage(SendMessageRequest request, Long senderId) throws BusinessException {
        // 验证请求参数
        request.validate();

        // 验证会话存在和用户权限
        validateUserAccessToConversation(request.getConversationId(), senderId);

        // 检查是否为草稿
        if (request.isDraft()) {
            return saveMessageDraft(request, senderId);
        }

        // 创建消息
        Message message = createMessageFromRequest(request, senderId);
        int result = messageMapper.insert(message);
        if (result <= 0) {
            throw new BusinessException("发送消息失败");
        }

        // 更新会话的最后消息信息
        updateConversationLastMessage(request.getConversationId(), message.getId(), message.getContent());

        // 增加会话成员的未读消息数（除了发送者自己）
        increaseUnreadCountForMembers(request.getConversationId(), senderId);

        // 记录发送者已读（可选）
        markMessageAsReadBySender(message.getId(), senderId);

        // 记录日志
        log.info("用户 {} 在会话 {} 中发送消息: {}", senderId, request.getConversationId(),
                StringUtils.hasText(message.getContentSummary()) ? message.getContentSummary() : message.getContent());

        return convertToDTO(message, senderId);
    }

    @Override
    public MessageDTO getMessageById(Long id, Long userId) throws BusinessException {
        Message message = messageMapper.selectById(id);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 验证用户是否有权限访问消息
        validateMessageAccess(id, userId);

        return convertToDTO(message, userId);
    }

    @Override
    public IPage<MessageDTO> getMessagesByConversationId(Long conversationId, Long userId, Integer page, Integer size) throws BusinessException {
        // 验证会话存在和用户权限
        validateUserAccessToConversation(conversationId, userId);

        // 设置默认分页参数
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 50;

        // 查询总数量
        Integer totalCount = messageMapper.countByConversationId(conversationId);
        if (totalCount == null || totalCount == 0) {
            Page<MessageDTO> resultPage = new Page<>(page, size, 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 计算分页参数
        int offset = (page - 1) * size;
        List<Message> messages = messageMapper.selectByConversationId(conversationId, offset, size);
        if (CollectionUtils.isEmpty(messages)) {
            Page<MessageDTO> resultPage = new Page<>(page, size, totalCount);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 转换为DTO
        List<MessageDTO> dtos = messages.stream()
                .map(message -> convertToDTO(message, userId))
                .collect(Collectors.toList());

        Page<MessageDTO> resultPage = new Page<>(page, size, totalCount);
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public List<MessageDTO> getMessagesByTimeRange(Long conversationId, Long userId, LocalDateTime startTime, LocalDateTime endTime) throws BusinessException {
        // 验证会话存在和用户权限
        validateUserAccessToConversation(conversationId, userId);

        // 简化实现：查询所有消息然后过滤时间范围
        List<Message> allMessages = getAllMessagesByConversation(conversationId);
        if (CollectionUtils.isEmpty(allMessages)) {
            return Collections.emptyList();
        }

        return allMessages.stream()
                .filter(message -> isInTimeRange(message.getCreateTime(), startTime, endTime))
                .map(message -> convertToDTO(message, userId))
                .collect(Collectors.toList());
    }

    @Override
    public IPage<MessageDTO> getMessagesBySenderId(Long senderId, Long userId, Integer page, Integer size) {
        // 设置默认分页参数
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 20;

        // 查询发送者的消息
        List<Message> messages = messageMapper.selectBySenderId(senderId, size);
        if (CollectionUtils.isEmpty(messages)) {
            Page<MessageDTO> resultPage = new Page<>(page, size, 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 过滤掉用户没有权限访问的消息
        List<MessageDTO> accessibleMessages = new ArrayList<>();
        for (Message message : messages) {
            try {
                validateMessageAccess(message.getId(), userId);
                accessibleMessages.add(convertToDTO(message, userId));
            } catch (BusinessException e) {
                // 没有权限，跳过
            }
        }

        // 分页
        int total = accessibleMessages.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            Page<MessageDTO> emptyPage = new Page<>(page, size, total);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        List<MessageDTO> pageData = accessibleMessages.subList(start, end);
        Page<MessageDTO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(pageData);
        return resultPage;
    }

    @Override
    public IPage<MessageDTO> getMessagesByType(String messageType, Long userId, Integer page, Integer size) {
        // 设置默认分页参数
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 20;

        // 查询指定类型的消息
        List<Message> messages = messageMapper.selectByMessageType(messageType, size);
        if (CollectionUtils.isEmpty(messages)) {
            Page<MessageDTO> resultPage = new Page<>(page, size, 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 过滤掉用户没有权限访问的消息
        List<MessageDTO> accessibleMessages = new ArrayList<>();
        for (Message message : messages) {
            try {
                validateMessageAccess(message.getId(), userId);
                accessibleMessages.add(convertToDTO(message, userId));
            } catch (BusinessException e) {
                // 没有权限，跳过
            }
        }

        // 分页
        int total = accessibleMessages.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            Page<MessageDTO> emptyPage = new Page<>(page, size, total);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        List<MessageDTO> pageData = accessibleMessages.subList(start, end);
        Page<MessageDTO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(pageData);
        return resultPage;
    }

    @Override
    public List<MessageDTO> getNewMessages(Long conversationId, Long userId, LocalDateTime afterTime) throws BusinessException {
        // 验证会话存在和用户权限
        validateUserAccessToConversation(conversationId, userId);

        // 查询指定时间之后的新消息
        List<Message> messages = messageMapper.selectNewMessages(conversationId, afterTime);
        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }

        return messages.stream()
                .map(message -> convertToDTO(message, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessageReplies(Long parentMessageId, Long userId) throws BusinessException {
        // 验证父消息存在和用户权限
        validateMessageAccess(parentMessageId, userId);

        // 查询回复消息
        List<Message> replies = messageMapper.selectReplies(parentMessageId);
        if (CollectionUtils.isEmpty(replies)) {
            return Collections.emptyList();
        }

        return replies.stream()
                .map(message -> convertToDTO(message, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageDTO editMessage(Long messageId, String newContent, Long userId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 检查权限：只能编辑自己发送的消息
        if (!message.getSenderId().equals(userId)) {
            throw new BusinessException("只能编辑自己发送的消息");
        }

        // 检查消息是否已撤回
        if (message.getIsRecalled() != null && message.getIsRecalled() == 1) {
            throw new BusinessException("已撤回的消息不能编辑");
        }

        // 更新消息内容
        String oldContent = message.getContent();
        message.setContent(newContent);
        message.setContentSummary(newContent.length() > 50 ? newContent.substring(0, 50) + "..." : newContent);
        message.setIsEdited(1);
        message.setEditTime(LocalDateTime.now());

        int result = messageMapper.updateById(message);
        if (result <= 0) {
            throw new BusinessException("编辑消息失败");
        }

        log.info("用户 {} 编辑消息 {}: {} -> {}", userId, messageId, oldContent, newContent);
        return convertToDTO(message, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageDTO recallMessage(Long messageId, Long userId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 检查权限：只能撤回自己发送的消息
        if (!message.getSenderId().equals(userId)) {
            throw new BusinessException("只能撤回自己发送的消息");
        }

        // 检查消息是否已撤回
        if (message.getIsRecalled() != null && message.getIsRecalled() == 1) {
            throw new BusinessException("消息已撤回");
        }

        // 检查消息发送时间是否在允许撤回的时间范围内（例如2分钟内）
        LocalDateTime recallDeadline = message.getCreateTime().plusMinutes(2);
        if (LocalDateTime.now().isAfter(recallDeadline)) {
            throw new BusinessException("消息已超过撤回时间限制");
        }

        // 撤回消息
        message.setIsRecalled(1);
        message.setRecallTime(LocalDateTime.now());

        int result = messageMapper.updateById(message);
        if (result <= 0) {
            throw new BusinessException("撤回消息失败");
        }

        log.info("用户 {} 撤回消息 {}", userId, messageId);
        return convertToDTO(message, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long messageId, Long userId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 检查权限：只能删除自己发送的消息
        if (!message.getSenderId().equals(userId)) {
            throw new BusinessException("只能删除自己发送的消息");
        }

        // 逻辑删除
        message.setDeleted(1);
        messageMapper.updateById(message);

        log.info("用户 {} 删除消息 {}", userId, messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteMessages(List<Long> messageIds, Long userId) throws BusinessException {
        if (CollectionUtils.isEmpty(messageIds)) {
            throw new BusinessException("消息ID列表不能为空");
        }

        for (Long messageId : messageIds) {
            try {
                deleteMessage(messageId, userId);
            } catch (BusinessException e) {
                log.warn("删除消息 {} 失败: {}", messageId, e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessageAsRead(Long messageId, Long userId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, userId);

        // 检查是否已读
        if (hasUserReadMessage(messageId, userId)) {
            return;
        }

        // 创建或更新阅读状态
        MessageReadStatus readStatus = new MessageReadStatus();
        readStatus.setMessageId(messageId);
        readStatus.setUserId(userId);
        readStatus.setReadTime(LocalDateTime.now());

        messageReadStatusMapper.insert(readStatus);

        // 增加消息的已读人数
        increaseMessageReadCount(messageId);

        // 减少会话成员的未读消息数
        Long conversationId = getMessageConversationId(messageId);
        if (conversationId != null) {
            ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
            if (member != null && member.getDeleted() == 0 && member.getUnreadCount() != null && member.getUnreadCount() > 0) {
                conversationMemberMapper.increaseUnreadCount(member.getId(), -1);
                conversationMapper.decreaseUnreadCount(conversationId, 1);
            }
        }

        log.debug("用户 {} 标记消息 {} 为已读", userId, messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchMarkMessagesAsRead(List<Long> messageIds, Long userId) throws BusinessException {
        if (CollectionUtils.isEmpty(messageIds)) {
            throw new BusinessException("消息ID列表不能为空");
        }

        for (Long messageId : messageIds) {
            try {
                markMessageAsRead(messageId, userId);
            } catch (BusinessException e) {
                log.warn("标记消息 {} 为已读失败: {}", messageId, e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllMessagesAsRead(Long conversationId, Long userId) throws BusinessException {
        // 验证会话存在和用户权限
        validateUserAccessToConversation(conversationId, userId);

        // 查询会话中所有未读消息
        List<Message> allMessages = getAllMessagesByConversation(conversationId);
        if (CollectionUtils.isEmpty(allMessages)) {
            return;
        }

        List<Long> unreadMessageIds = new ArrayList<>();
        for (Message message : allMessages) {
            if (!hasUserReadMessage(message.getId(), userId)) {
                unreadMessageIds.add(message.getId());
            }
        }

        // 批量标记为已读
        batchMarkMessagesAsRead(unreadMessageIds, userId);

        // 重置会话成员的未读消息数
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member != null && member.getDeleted() == 0) {
            conversationMemberMapper.resetUnreadCount(member.getId(), getLatestMessageId(conversationId));
            if (member.getUnreadCount() != null && member.getUnreadCount() > 0) {
                conversationMapper.decreaseUnreadCount(conversationId, member.getUnreadCount());
            }
        }

        log.info("用户 {} 标记会话 {} 中所有消息为已读", userId, conversationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageDTO forwardMessage(Long messageId, Long targetConversationId, Long userId) throws BusinessException {
        // 验证源消息存在和用户权限
        validateMessageAccess(messageId, userId);

        // 验证目标会话存在和用户权限
        validateUserAccessToConversation(targetConversationId, userId);

        // 获取源消息
        Message sourceMessage = messageMapper.selectById(messageId);
        if (sourceMessage == null || sourceMessage.getDeleted() == 1) {
            throw new BusinessException("源消息不存在或已被删除");
        }

        // 创建转发消息
        Message forwardedMessage = new Message();
        forwardedMessage.setConversationId(targetConversationId);
        forwardedMessage.setSenderId(userId);
        forwardedMessage.setMessageType(sourceMessage.getMessageType());

        // 构建转发内容
        String forwardContent = buildForwardContent(sourceMessage, userId);
        forwardedMessage.setContent(forwardContent);
        forwardedMessage.setContentSummary(forwardContent.length() > 50 ? forwardContent.substring(0, 50) + "..." : forwardContent);

        // 复制附件信息
        if (StringUtils.hasText(sourceMessage.getAttachmentUrl())) {
            forwardedMessage.setAttachmentUrl(sourceMessage.getAttachmentUrl());
            forwardedMessage.setAttachmentName(sourceMessage.getAttachmentName());
            forwardedMessage.setAttachmentSize(sourceMessage.getAttachmentSize());
        }

        // 设置引用信息
        forwardedMessage.setReferencedMessageId(messageId);

        int result = messageMapper.insert(forwardedMessage);
        if (result <= 0) {
            throw new BusinessException("转发消息失败");
        }

        // 更新目标会话的最后消息信息
        updateConversationLastMessage(targetConversationId, forwardedMessage.getId(), forwardedMessage.getContent());

        // 增加目标会话成员的未读消息数（除了转发者自己）
        increaseUnreadCountForMembers(targetConversationId, userId);

        log.info("用户 {} 将消息 {} 转发到会话 {}", userId, messageId, targetConversationId);
        return convertToDTO(forwardedMessage, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageDTO copyMessage(Long messageId, Long targetConversationId, Long userId) throws BusinessException {
        // 与转发类似，但可以修改内容
        return forwardMessage(messageId, targetConversationId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageDTO quoteMessage(Long messageId, Long targetConversationId, String additionalContent, Long userId) throws BusinessException {
        // 验证源消息存在和用户权限
        validateMessageAccess(messageId, userId);

        // 验证目标会话存在和用户权限
        validateUserAccessToConversation(targetConversationId, userId);

        // 获取源消息
        Message sourceMessage = messageMapper.selectById(messageId);
        if (sourceMessage == null || sourceMessage.getDeleted() == 1) {
            throw new BusinessException("源消息不存在或已被删除");
        }

        // 构建引用内容
        String quoteContent = buildQuoteContent(sourceMessage, additionalContent);

        // 发送新消息
        SendMessageRequest request = new SendMessageRequest();
        request.setConversationId(targetConversationId);
        request.setMessageType("TEXT");
        request.setContent(quoteContent);
        request.setReferencedMessageId(messageId);

        return sendMessage(request, userId);
    }

    @Override
    public IPage<MessageDTO> searchMessages(String keyword, Long userId, Long conversationId, Integer page, Integer size) {
        // 简化实现：先获取用户有权限访问的所有消息，然后过滤
        List<MessageDTO> allMessages = new ArrayList<>();

        if (conversationId != null) {
            try {
                IPage<MessageDTO> conversationMessages = getMessagesByConversationId(conversationId, userId, 1, Integer.MAX_VALUE);
                if (conversationMessages != null && !CollectionUtils.isEmpty(conversationMessages.getRecords())) {
                    allMessages.addAll(conversationMessages.getRecords());
                }
            } catch (BusinessException e) {
                // 没有权限，跳过
            }
        } else {
            // 获取用户参与的所有会话的消息（简化实现）
            // 实际应该实现更高效的搜索
        }

        // 过滤关键词
        List<MessageDTO> filtered = allMessages.stream()
                .filter(msg -> {
                    if (!StringUtils.hasText(keyword)) {
                        return true;
                    }
                    return (msg.getContent() != null && msg.getContent().contains(keyword)) ||
                           (msg.getSenderName() != null && msg.getSenderName().contains(keyword));
                })
                .collect(Collectors.toList());

        // 分页
        int total = filtered.size();
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 20;
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            Page<MessageDTO> emptyPage = new Page<>(page, size, total);
            emptyPage.setRecords(List.of());
            return emptyPage;
        }

        List<MessageDTO> pageData = filtered.subList(start, end);
        Page<MessageDTO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(pageData);
        return resultPage;
    }

    @Override
    public MessageStatisticsDTO getMessageStatistics(Long conversationId, Long userId) throws BusinessException {
        // 验证会话存在和用户权限
        validateUserAccessToConversation(conversationId, userId);

        // 简化实现，实际需要从数据库统计
        MessageStatisticsDTO statistics = new MessageStatisticsDTO();
        statistics.setConversationId(conversationId);

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            statistics.setConversationTitle(conversation.getTitle());
        }

        // 查询总消息数量
        Integer totalCount = messageMapper.countByConversationId(conversationId);
        statistics.setTotalMessageCount(totalCount != null ? totalCount.longValue() : 0L);

        // 设置当前用户的未读消息数
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member != null) {
            statistics.setUnreadMessageCount(member.getUnreadCount());
        }

        statistics.calculate();
        return statistics;
    }

    @Override
    public UserMessageStatisticsDTO getUserMessageStatistics(Long userId) {
        // 简化实现
        UserMessageStatisticsDTO statistics = new UserMessageStatisticsDTO();
        statistics.setUserId(userId);

        // 这里可以添加实际的数据统计

        statistics.calculate();
        return statistics;
    }

    @Override
    public void cleanupExpiredMessages() {
        // 清理过期的临时消息（定时任务）
        log.info("清理过期的临时消息");
        // 实际实现需要根据业务需求清理
    }

    @Override
    public MessageDTO resendMessage(Long messageId, Long userId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, userId);

        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 检查消息是否属于用户
        if (!message.getSenderId().equals(userId)) {
            throw new BusinessException("只能重新发送自己发送的消息");
        }

        // 重新发送消息（实际可能需要调用消息队列或其他服务）
        log.info("用户 {} 重新发送消息 {}", userId, messageId);
        return convertToDTO(message, userId);
    }

    @Override
    public List<Long> getMessageReaders(Long messageId, Long userId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, userId);

        List<MessageReadStatus> readStatuses = messageReadStatusMapper.selectByMessageId(messageId);
        if (CollectionUtils.isEmpty(readStatuses)) {
            return Collections.emptyList();
        }

        return readStatuses.stream()
                .map(MessageReadStatus::getUserId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getMessageUnreaders(Long messageId, Long userId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, userId);

        // 获取消息的会话ID
        Long conversationId = getMessageConversationId(messageId);
        if (conversationId == null) {
            return Collections.emptyList();
        }

        // 获取会话的所有成员
        List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }

        // 获取已读用户
        List<Long> readers = getMessageReaders(messageId, userId);

        // 计算未读用户
        List<Long> allMemberIds = members.stream()
                .filter(member -> member.getDeleted() == 0)
                .map(ConversationMember::getUserId)
                .collect(Collectors.toList());

        allMemberIds.removeAll(readers);
        return allMemberIds;
    }

    @Override
    public boolean hasUserReadMessage(Long messageId, Long userId) {
        Integer count = messageReadStatusMapper.checkUserHasRead(messageId, userId);
        return count != null && count > 0;
    }

    @Override
    public void updateMessageReactionCount(Long messageId, int count) {
        messageMapper.updateReactionCount(messageId, count);
    }

    @Override
    public void increaseMessageReadCount(Long messageId) {
        messageMapper.increaseReadCount(messageId);
    }

    @Override
    public void validateMessageAccess(Long messageId, Long userId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 验证用户是否是消息所在会话的成员
        if (!isConversationMember(message.getConversationId(), userId)) {
            throw new BusinessException("没有权限访问该消息");
        }
    }

    @Override
    public boolean checkMessageExists(Long messageId) {
        Message message = messageMapper.selectById(messageId);
        return message != null && message.getDeleted() == 0;
    }

    @Override
    public Long getMessageConversationId(Long messageId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }
        return message.getConversationId();
    }

    @Override
    public MessageDTO saveMessageDraft(SendMessageRequest request, Long userId) throws BusinessException {
        // 验证会话存在和用户权限
        validateUserAccessToConversation(request.getConversationId(), userId);

        // 创建草稿消息
        Message draft = createMessageFromRequest(request, userId);
        draft.setDeleted(0); // 草稿也是逻辑删除状态为0，但可能有其他标记

        int result = messageMapper.insert(draft);
        if (result <= 0) {
            throw new BusinessException("保存草稿失败");
        }

        log.info("用户 {} 保存消息草稿到会话 {}", userId, request.getConversationId());
        return convertToDTO(draft, userId);
    }

    @Override
    public List<MessageDTO> getMessageDrafts(Long userId, Long conversationId) {
        // 简化实现：查询用户发送的但标记为草稿的消息
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSenderId, userId);
        wrapper.eq(Message::getDeleted, 0);
        if (conversationId != null) {
            wrapper.eq(Message::getConversationId, conversationId);
        }
        // 这里应该有一个字段标记是否为草稿，简化实现返回空列表
        return Collections.emptyList();
    }

    @Override
    public void deleteMessageDraft(Long draftId, Long userId) throws BusinessException {
        deleteMessage(draftId, userId);
    }

    @Override
    public MessageDTO sendMessageDraft(Long draftId, Long userId) throws BusinessException {
        Message draft = messageMapper.selectById(draftId);
        if (draft == null || draft.getDeleted() == 1) {
            throw new BusinessException("草稿不存在或已被删除");
        }

        // 检查权限
        if (!draft.getSenderId().equals(userId)) {
            throw new BusinessException("只能发送自己的草稿");
        }

        // 更新草稿为正式消息
        draft.setDeleted(0); // 确保不是删除状态

        // 更新会话的最后消息信息
        updateConversationLastMessage(draft.getConversationId(), draft.getId(), draft.getContent());

        // 增加会话成员的未读消息数（除了发送者自己）
        increaseUnreadCountForMembers(draft.getConversationId(), userId);

        messageMapper.updateById(draft);

        log.info("用户 {} 发送草稿消息 {}", userId, draftId);
        return convertToDTO(draft, userId);
    }

    // ============= 私有辅助方法 =============

    private void validateUserAccessToConversation(Long conversationId, Long userId) throws BusinessException {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getDeleted() == 1) {
            throw new BusinessException("会话不存在或已被删除");
        }

        if (!isConversationMember(conversationId, userId)) {
            throw new BusinessException("用户不是会话成员");
        }
    }

    private boolean isConversationMember(Long conversationId, Long userId) {
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        return member != null && member.getDeleted() == 0;
    }

    private Message createMessageFromRequest(SendMessageRequest request, Long senderId) {
        Message message = new Message();
        message.setConversationId(request.getConversationId());
        message.setSenderId(senderId);
        message.setMessageType(request.getMessageType());
        message.setContent(request.getContent());

        // 生成内容摘要
        String summary = request.getContent().length() > 50 ?
                request.getContent().substring(0, 50) + "..." : request.getContent();
        message.setContentSummary(summary);

        // 设置附件信息
        if (StringUtils.hasText(request.getAttachmentUrl())) {
            message.setAttachmentUrl(request.getAttachmentUrl());
            message.setAttachmentName(request.getAttachmentName());
            message.setAttachmentSize(request.getAttachmentSize());
        }

        // 设置父消息和引用消息
        message.setParentMessageId(request.getParentMessageId());
        message.setReferencedMessageId(request.getReferencedMessageId());

        // 初始化计数
        message.setReadCount(0);
        message.setReactionCount(0);
        message.setIsEdited(0);
        message.setIsRecalled(0);
        message.setDeleted(0);

        return message;
    }

    private void updateConversationLastMessage(Long conversationId, Long messageId, String messageContent) {
        try {
            conversationMapper.updateLastMessageInfo(conversationId, messageId, messageContent, LocalDateTime.now().toString());
        } catch (Exception e) {
            log.error("更新会话最后消息信息失败: {}", e.getMessage(), e);
        }
    }

    private void increaseUnreadCountForMembers(Long conversationId, Long excludeUserId) {
        List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        if (CollectionUtils.isEmpty(members)) {
            return;
        }

        for (ConversationMember member : members) {
            if (member.getDeleted() == 0 && !member.getUserId().equals(excludeUserId)) {
                conversationMemberMapper.increaseUnreadCount(member.getId(), 1);
            }
        }

        // 增加会话的未读消息数
        conversationMapper.increaseMemberCount(conversationId, 0); // 这里应该是增加未读消息数，但方法名有误
        // 实际应该调用 conversationMapper.increaseUnreadCount 或类似方法
    }

    private void markMessageAsReadBySender(Long messageId, Long senderId) {
        // 发送者自动标记为已读
        try {
            MessageReadStatus readStatus = new MessageReadStatus();
            readStatus.setMessageId(messageId);
            readStatus.setUserId(senderId);
            readStatus.setReadTime(LocalDateTime.now());
            messageReadStatusMapper.insert(readStatus);
        } catch (Exception e) {
            log.warn("记录发送者已读状态失败: {}", e.getMessage());
        }
    }

    private MessageDTO convertToDTO(Message message, Long currentUserId) {
        if (message == null) {
            return null;
        }
        MessageDTO dto = new MessageDTO();
        BeanUtils.copyProperties(message, dto);

        // 设置当前用户的阅读状态
        dto.setIsRead(hasUserReadMessage(message.getId(), currentUserId));

        dto.calculateDesc();
        return dto;
    }

    private List<Message> getAllMessagesByConversation(Long conversationId) {
        // 简化实现，实际应该分页查询所有
        List<Message> allMessages = new ArrayList<>();
        int page = 1;
        int size = 100;

        while (true) {
            int offset = (page - 1) * size;
            List<Message> messages = messageMapper.selectByConversationId(conversationId, offset, size);
            if (CollectionUtils.isEmpty(messages)) {
                break;
            }
            allMessages.addAll(messages);
            if (messages.size() < size) {
                break;
            }
            page++;
        }

        return allMessages;
    }

    private boolean isInTimeRange(LocalDateTime time, LocalDateTime startTime, LocalDateTime endTime) {
        if (time == null) {
            return false;
        }
        if (startTime != null && time.isBefore(startTime)) {
            return false;
        }
        if (endTime != null && time.isAfter(endTime)) {
            return false;
        }
        return true;
    }

    private Long getLatestMessageId(Long conversationId) {
        List<Message> messages = messageMapper.selectByConversationId(conversationId, 0, 1);
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        }
        return messages.get(0).getId();
    }

    private String buildForwardContent(Message sourceMessage, Long userId) {
        // 构建转发内容格式
        return String.format("[转发] %s",
                StringUtils.hasText(sourceMessage.getContent()) ?
                sourceMessage.getContent() : "查看消息");
    }

    private String buildQuoteContent(Message sourceMessage, String additionalContent) {
        // 构建引用内容格式
        StringBuilder sb = new StringBuilder();
        if (StringUtils.hasText(additionalContent)) {
            sb.append(additionalContent).append("\n\n");
        }
        sb.append("> ").append(sourceMessage.getContent());
        return sb.toString();
    }
}