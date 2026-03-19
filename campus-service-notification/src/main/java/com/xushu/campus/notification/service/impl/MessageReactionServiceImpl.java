package com.xushu.campus.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.dto.*;
import com.xushu.campus.notification.entity.Conversation;
import com.xushu.campus.notification.entity.ConversationMember;
import com.xushu.campus.notification.entity.Message;
import com.xushu.campus.notification.entity.MessageReaction;
import com.xushu.campus.notification.mapper.ConversationMapper;
import com.xushu.campus.notification.mapper.ConversationMemberMapper;
import com.xushu.campus.notification.mapper.MessageMapper;
import com.xushu.campus.notification.mapper.MessageReactionMapper;
import com.xushu.campus.notification.service.MessageReactionService;
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
 * 消息反应服务实现类
 */
@Slf4j
@Service
public class MessageReactionServiceImpl implements MessageReactionService {

    @Autowired
    private MessageReactionMapper messageReactionMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMemberMapper conversationMemberMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageReactionDTO addReaction(AddReactionRequest request, Long userId) throws BusinessException {
        // 验证请求参数
        request.validate();

        // 验证消息存在和用户权限
        validateMessageAccess(request.getMessageId(), userId);

        // 检查是否已存在相同反应
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, request.getMessageId())
               .eq(MessageReaction::getUserId, userId)
               .eq(MessageReaction::getReactionType, request.getReactionType());

        MessageReaction existingReaction = messageReactionMapper.selectOne(wrapper);
        if (existingReaction != null) {
            // 已存在相同反应，更新表情（如果需要）
            if (StringUtils.hasText(request.getReactionEmoji()) &&
                !request.getReactionEmoji().equals(existingReaction.getReactionEmoji())) {
                existingReaction.setReactionEmoji(request.getReactionEmoji());
                messageReactionMapper.updateById(existingReaction);
            }
            return convertToDTO(existingReaction);
        }

        // 创建新反应
        MessageReaction reaction = new MessageReaction();
        reaction.setMessageId(request.getMessageId());
        reaction.setUserId(userId);
        reaction.setReactionType(request.getReactionType());
        reaction.setReactionEmoji(StringUtils.hasText(request.getReactionEmoji()) ?
                                 request.getReactionEmoji() : getDefaultEmoji(request.getReactionType()));

        int result = messageReactionMapper.insert(reaction);
        if (result <= 0) {
            throw new BusinessException("添加反应失败");
        }

        log.info("用户 {} 对消息 {} 添加了 {} 反应", userId, request.getMessageId(), request.getReactionType());

        return convertToDTO(reaction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeReaction(Long messageId, Long userId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, userId);

        // 删除用户对该消息的所有反应
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, messageId)
               .eq(MessageReaction::getUserId, userId);

        int result = messageReactionMapper.delete(wrapper);
        if (result > 0) {
            log.info("用户 {} 移除了对消息 {} 的所有反应", userId, messageId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSpecificReaction(Long messageId, Long userId, String reactionType) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, userId);

        // 验证反应类型
        if (!isValidReactionType(reactionType)) {
            throw new BusinessException("无效的反应类型");
        }

        // 删除特定反应
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, messageId)
               .eq(MessageReaction::getUserId, userId)
               .eq(MessageReaction::getReactionType, reactionType);

        int result = messageReactionMapper.delete(wrapper);
        if (result > 0) {
            log.info("用户 {} 移除了对消息 {} 的 {} 反应", userId, messageId, reactionType);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageReactionDTO toggleReaction(Long messageId, String reactionType, Long userId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, userId);

        // 验证反应类型
        if (!isValidReactionType(reactionType)) {
            throw new BusinessException("无效的反应类型");
        }

        // 检查是否已存在相同反应
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, messageId)
               .eq(MessageReaction::getUserId, userId)
               .eq(MessageReaction::getReactionType, reactionType);

        MessageReaction existingReaction = messageReactionMapper.selectOne(wrapper);

        if (existingReaction != null) {
            // 已存在，移除它
            messageReactionMapper.deleteById(existingReaction.getId());
            log.info("用户 {} 移除了对消息 {} 的 {} 反应", userId, messageId, reactionType);
            return null;
        } else {
            // 不存在，添加新反应
            MessageReaction reaction = new MessageReaction();
            reaction.setMessageId(messageId);
            reaction.setUserId(userId);
            reaction.setReactionType(reactionType);
            reaction.setReactionEmoji(getDefaultEmoji(reactionType));

            int result = messageReactionMapper.insert(reaction);
            if (result <= 0) {
                throw new BusinessException("切换反应失败");
            }

            log.info("用户 {} 对消息 {} 添加了 {} 反应", userId, messageId, reactionType);
            return convertToDTO(reaction);
        }
    }

    @Override
    public List<MessageReactionDTO> getMessageReactions(Long messageId, Long operatorId) throws BusinessException {
        // 验证消息存在和操作者权限
        validateMessageAccess(messageId, operatorId);

        // 查询消息的所有反应
        List<MessageReaction> reactions = messageReactionMapper.selectByMessageId(messageId);

        return reactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<MessageReactionDTO> getMessageReactionsPage(Long messageId, Long operatorId, Integer page, Integer size) throws BusinessException {
        // 验证消息存在和操作者权限
        validateMessageAccess(messageId, operatorId);

        // 设置分页参数
        Page<MessageReaction> pageParam = new Page<>(page, size);

        // 查询条件
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, messageId)
               .orderByDesc(MessageReaction::getCreateTime);

        IPage<MessageReaction> reactionPage = messageReactionMapper.selectPage(pageParam, wrapper);

        List<MessageReactionDTO> dtos = reactionPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<MessageReactionDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(reactionPage, resultPage, "records");
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public MessageReactionDTO getUserReactionToMessage(Long messageId, Long userId, Long operatorId) throws BusinessException {
        // 验证消息存在和操作者权限
        validateMessageAccess(messageId, operatorId);

        // 验证操作者是否有权限查看其他用户的反应
        if (!operatorId.equals(userId)) {
            // 检查操作者是否在同一个会话中
            validateSameConversationAccess(messageId, operatorId, userId);
        }

        // 查询用户对消息的反应
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, messageId)
               .eq(MessageReaction::getUserId, userId)
               .orderByDesc(MessageReaction::getCreateTime)
               .last("LIMIT 1");

        MessageReaction reaction = messageReactionMapper.selectOne(wrapper);

        return reaction != null ? convertToDTO(reaction) : null;
    }

    @Override
    public List<MessageReactionDTO> getUserReactions(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 50;
        }

        // 查询用户的反应记录
        List<MessageReaction> reactions = messageReactionMapper.selectByUserId(userId);

        // 限制数量
        if (reactions.size() > limit) {
            reactions = reactions.subList(0, limit);
        }

        return reactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IPage<MessageReactionDTO> getUserReactionsPage(Long userId, Integer page, Integer size) {
        // 设置分页参数
        Page<MessageReaction> pageParam = new Page<>(page, size);

        // 查询条件
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getUserId, userId)
               .orderByDesc(MessageReaction::getCreateTime);

        IPage<MessageReaction> reactionPage = messageReactionMapper.selectPage(pageParam, wrapper);

        List<MessageReactionDTO> dtos = reactionPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<MessageReactionDTO> resultPage = new Page<>();
        BeanUtils.copyProperties(reactionPage, resultPage, "records");
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public ReactionStatisticsDTO getMessageReactionStatistics(Long messageId, Long operatorId) throws BusinessException {
        // 验证消息存在和操作者权限
        validateMessageAccess(messageId, operatorId);

        // 查询反应统计
        List<MessageReactionMapper.ReactionStat> stats = messageReactionMapper.selectReactionStats(messageId);

        // 构建统计DTO
        ReactionStatisticsDTO statistics = new ReactionStatisticsDTO();
        statistics.setMessageId(messageId);

        // 初始化各类型计数
        long likeCount = 0;
        long heartCount = 0;
        long laughCount = 0;
        long sadCount = 0;
        long angryCount = 0;
        long otherCount = 0;

        // 统计各类型数量
        for (MessageReactionMapper.ReactionStat stat : stats) {
            switch (stat.getReactionType()) {
                case "LIKE":
                    likeCount = stat.getCount();
                    break;
                case "HEART":
                    heartCount = stat.getCount();
                    break;
                case "LAUGH":
                    laughCount = stat.getCount();
                    break;
                case "SAD":
                    sadCount = stat.getCount();
                    break;
                case "ANGRY":
                    angryCount = stat.getCount();
                    break;
                default:
                    otherCount += stat.getCount();
                    break;
            }
        }

        // 设置计数
        statistics.setLikeCount(likeCount);
        statistics.setHeartCount(heartCount);
        statistics.setLaughCount(laughCount);
        statistics.setSadCount(sadCount);
        statistics.setAngryCount(angryCount);
        statistics.setOtherCount(otherCount);

        // 计算统计信息
        statistics.calculate();

        return statistics;
    }

    @Override
    public ConversationReactionStatisticsDTO getConversationReactionStatistics(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        // 查询会话信息
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        // 查询会话中的所有消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getConversationId, conversationId)
                     .eq(Message::getDeleted, 0);
        List<Message> messages = messageMapper.selectList(messageWrapper);

        // 查询所有消息的反应
        List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(messageIds)) {
            // 没有消息，返回空统计
            ConversationReactionStatisticsDTO statistics = new ConversationReactionStatisticsDTO();
            statistics.setConversationId(conversationId);
            statistics.setConversationTitle(conversation.getTitle());
            statistics.calculate();
            return statistics;
        }

        // 批量查询反应
        List<MessageReaction> allReactions = messageReactionMapper.selectByMessageIds(messageIds);

        // 构建统计DTO
        ConversationReactionStatisticsDTO statistics = new ConversationReactionStatisticsDTO();
        statistics.setConversationId(conversationId);
        statistics.setConversationTitle(conversation.getTitle());
        statistics.setTotalMessageCount((long) messages.size());

        // 统计总反应数量
        statistics.setTotalReactionCount((long) allReactions.size());

        // 统计各类型数量
        long likeCount = allReactions.stream()
                .filter(r -> "LIKE".equals(r.getReactionType()))
                .count();
        long heartCount = allReactions.stream()
                .filter(r -> "HEART".equals(r.getReactionType()))
                .count();
        long laughCount = allReactions.stream()
                .filter(r -> "LAUGH".equals(r.getReactionType()))
                .count();
        long sadCount = allReactions.stream()
                .filter(r -> "SAD".equals(r.getReactionType()))
                .count();
        long angryCount = allReactions.stream()
                .filter(r -> "ANGRY".equals(r.getReactionType()))
                .count();
        long otherCount = allReactions.size() - likeCount - heartCount - laughCount - sadCount - angryCount;

        statistics.setTotalLikeCount(likeCount);
        statistics.setTotalHeartCount(heartCount);
        statistics.setTotalLaughCount(laughCount);
        statistics.setTotalSadCount(sadCount);
        statistics.setTotalAngryCount(angryCount);
        statistics.setTotalOtherCount(otherCount);

        // 找到最常用的反应类型
        Map<String, Long> typeCounts = new HashMap<>();
        typeCounts.put("LIKE", likeCount);
        typeCounts.put("HEART", heartCount);
        typeCounts.put("LAUGH", laughCount);
        typeCounts.put("SAD", sadCount);
        typeCounts.put("ANGRY", angryCount);
        typeCounts.put("OTHER", otherCount);

        Map.Entry<String, Long> maxEntry = typeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (maxEntry != null) {
            statistics.setMostCommonReactionType(maxEntry.getKey());
            statistics.setMostCommonReactionCount(maxEntry.getValue());
        }

        // 计算统计信息
        statistics.calculate();

        return statistics;
    }

    @Override
    public UserReactionStatisticsDTO getUserReactionStatistics(Long userId) {
        // 查询用户给出的所有反应
        LambdaQueryWrapper<MessageReaction> givenWrapper = new LambdaQueryWrapper<>();
        givenWrapper.eq(MessageReaction::getUserId, userId);
        List<MessageReaction> givenReactions = messageReactionMapper.selectList(givenWrapper);

        // 查询用户发送的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getSenderId, userId)
                     .eq(Message::getDeleted, 0);
        List<Message> userMessages = messageMapper.selectList(messageWrapper);

        // 查询用户收到的反应
        List<Long> userMessageIds = userMessages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());

        List<MessageReaction> receivedReactions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userMessageIds)) {
            receivedReactions = messageReactionMapper.selectByMessageIds(userMessageIds);
        }

        // 构建统计DTO
        UserReactionStatisticsDTO statistics = new UserReactionStatisticsDTO();
        statistics.setUserId(userId);
        statistics.setUserName("用户" + userId); // 实际应从用户服务获取
        statistics.setTotalReactionsGiven((long) givenReactions.size());
        statistics.setTotalReactionsReceived((long) receivedReactions.size());

        // 统计给出的反应类型
        long likesGiven = givenReactions.stream()
                .filter(r -> "LIKE".equals(r.getReactionType()))
                .count();
        long heartsGiven = givenReactions.stream()
                .filter(r -> "HEART".equals(r.getReactionType()))
                .count();
        long laughsGiven = givenReactions.stream()
                .filter(r -> "LAUGH".equals(r.getReactionType()))
                .count();
        long sadsGiven = givenReactions.stream()
                .filter(r -> "SAD".equals(r.getReactionType()))
                .count();
        long angersGiven = givenReactions.stream()
                .filter(r -> "ANGRY".equals(r.getReactionType()))
                .count();
        long othersGiven = givenReactions.size() - likesGiven - heartsGiven - laughsGiven - sadsGiven - angersGiven;

        statistics.setLikesGiven(likesGiven);
        statistics.setHeartsGiven(heartsGiven);
        statistics.setLaughsGiven(laughsGiven);
        statistics.setSadsGiven(sadsGiven);
        statistics.setAngersGiven(angersGiven);
        statistics.setOthersGiven(othersGiven);

        // 找到最常给出的反应类型
        Map<String, Long> givenCounts = new HashMap<>();
        givenCounts.put("LIKE", likesGiven);
        givenCounts.put("HEART", heartsGiven);
        givenCounts.put("LAUGH", laughsGiven);
        givenCounts.put("SAD", sadsGiven);
        givenCounts.put("ANGRY", angersGiven);

        Map.Entry<String, Long> maxGivenEntry = givenCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (maxGivenEntry != null) {
            statistics.setMostGivenReactionType(maxGivenEntry.getKey());
        }

        // 计算统计信息
        statistics.calculate();

        return statistics;
    }

    @Override
    public List<MessageReactionDTO> getTopReactedMessages(Long conversationId, Long operatorId, Integer limit) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 查询会话中的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getConversationId, conversationId)
                     .eq(Message::getDeleted, 0)
                     .orderByDesc(Message::getCreateTime);
        List<Message> messages = messageMapper.selectList(messageWrapper);

        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }

        // 批量查询消息的反应
        List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        List<MessageReaction> allReactions = messageReactionMapper.selectByMessageIds(messageIds);

        // 统计每个消息的反应数量
        Map<Long, Long> messageReactionCounts = allReactions.stream()
                .collect(Collectors.groupingBy(MessageReaction::getMessageId, Collectors.counting()));

        // 按反应数量排序
        List<Map.Entry<Long, Long>> sortedEntries = messageReactionCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .collect(Collectors.toList());

        // 构建返回结果
        List<MessageReactionDTO> result = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : sortedEntries) {
            Long messageId = entry.getKey();
            // 找到对应的消息
            Message message = messages.stream()
                    .filter(m -> m.getId().equals(messageId))
                    .findFirst()
                    .orElse(null);

            if (message != null) {
                // 查询该消息的最新反应
                LambdaQueryWrapper<MessageReaction> reactionWrapper = new LambdaQueryWrapper<>();
                reactionWrapper.eq(MessageReaction::getMessageId, messageId)
                              .orderByDesc(MessageReaction::getCreateTime)
                              .last("LIMIT 1");
                MessageReaction latestReaction = messageReactionMapper.selectOne(reactionWrapper);

                if (latestReaction != null) {
                    result.add(convertToDTO(latestReaction));
                }
            }
        }

        return result;
    }

    @Override
    public String getMostCommonReactionType(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        // 查询会话中的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getConversationId, conversationId)
                     .eq(Message::getDeleted, 0);
        List<Message> messages = messageMapper.selectList(messageWrapper);

        if (CollectionUtils.isEmpty(messages)) {
            return "NONE";
        }

        // 批量查询反应
        List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        List<MessageReaction> allReactions = messageReactionMapper.selectByMessageIds(messageIds);

        if (CollectionUtils.isEmpty(allReactions)) {
            return "NONE";
        }

        // 统计各类型数量
        Map<String, Long> typeCounts = allReactions.stream()
                .collect(Collectors.groupingBy(MessageReaction::getReactionType, Collectors.counting()));

        // 找到最常用的类型
        return typeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NONE");
    }

    @Override
    public ReactionTypeDistributionDTO getReactionTypeDistribution(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        // 查询会话信息
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        // 查询会话中的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getConversationId, conversationId)
                     .eq(Message::getDeleted, 0);
        List<Message> messages = messageMapper.selectList(messageWrapper);

        // 批量查询反应
        List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        List<MessageReaction> allReactions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(messageIds)) {
            allReactions = messageReactionMapper.selectByMessageIds(messageIds);
        }

        // 构建分布DTO
        ReactionTypeDistributionDTO distribution = new ReactionTypeDistributionDTO();
        distribution.setConversationId(conversationId);
        distribution.setConversationTitle(conversation.getTitle());

        // 统计各类型数量
        long likeCount = allReactions.stream()
                .filter(r -> "LIKE".equals(r.getReactionType()))
                .count();
        long heartCount = allReactions.stream()
                .filter(r -> "HEART".equals(r.getReactionType()))
                .count();
        long laughCount = allReactions.stream()
                .filter(r -> "LAUGH".equals(r.getReactionType()))
                .count();
        long sadCount = allReactions.stream()
                .filter(r -> "SAD".equals(r.getReactionType()))
                .count();
        long angryCount = allReactions.stream()
                .filter(r -> "ANGRY".equals(r.getReactionType()))
                .count();
        long otherCount = allReactions.size() - likeCount - heartCount - laughCount - sadCount - angryCount;

        distribution.setLikeCount(likeCount);
        distribution.setHeartCount(heartCount);
        distribution.setLaughCount(laughCount);
        distribution.setSadCount(sadCount);
        distribution.setAngryCount(angryCount);
        distribution.setOtherCount(otherCount);

        // 计算统计信息
        distribution.calculate();

        return distribution;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MessageReactionDTO> batchAddReactions(List<AddReactionRequest> requests, Long userId) throws BusinessException {
        if (CollectionUtils.isEmpty(requests)) {
            return Collections.emptyList();
        }

        List<MessageReactionDTO> results = new ArrayList<>();

        for (AddReactionRequest request : requests) {
            try {
                MessageReactionDTO dto = addReaction(request, userId);
                if (dto != null) {
                    results.add(dto);
                }
            } catch (BusinessException e) {
                log.error("批量添加反应失败，消息ID: {}，错误: {}", request.getMessageId(), e.getMessage());
                // 继续处理其他请求
            }
        }

        log.info("用户 {} 批量添加了 {} 个反应", userId, results.size());
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveReactions(List<Long> messageIds, Long userId) throws BusinessException {
        if (CollectionUtils.isEmpty(messageIds)) {
            return;
        }

        int totalRemoved = 0;

        for (Long messageId : messageIds) {
            try {
                removeReaction(messageId, userId);
                totalRemoved++;
            } catch (BusinessException e) {
                log.error("批量移除反应失败，消息ID: {}，错误: {}", messageId, e.getMessage());
                // 继续处理其他请求
            }
        }

        log.info("用户 {} 批量移除了 {} 个消息的反应", userId, totalRemoved);
    }

    @Override
    public boolean hasUserReactedToMessage(Long messageId, Long userId) {
        // 查询用户对该消息是否有反应
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, messageId)
               .eq(MessageReaction::getUserId, userId);

        Long count = messageReactionMapper.selectCount(wrapper);
        return count != null && count > 0;
    }

    @Override
    public boolean hasUserReactedWithType(Long messageId, Long userId, String reactionType) {
        if (!isValidReactionType(reactionType)) {
            return false;
        }

        // 查询用户对该消息是否有特定反应
        Integer count = messageReactionMapper.checkUserReaction(messageId, userId, reactionType);
        return count != null && count > 0;
    }

    @Override
    public Long countMessageReactions(Long messageId) throws BusinessException {
        // 验证消息存在
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 统计消息的反应数量
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, messageId);

        Long count = messageReactionMapper.selectCount(wrapper);
        return count != null ? count : 0L;
    }

    @Override
    public Long countMessageReactionsByType(Long messageId, String reactionType) throws BusinessException {
        // 验证消息存在
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        if (!isValidReactionType(reactionType)) {
            throw new BusinessException("无效的反应类型");
        }

        // 统计消息的特定反应数量
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getMessageId, messageId)
               .eq(MessageReaction::getReactionType, reactionType);

        Long count = messageReactionMapper.selectCount(wrapper);
        return count != null ? count : 0L;
    }

    @Override
    public Long countUserReactions(Long userId) {
        // 统计用户的反应数量
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getUserId, userId);

        Long count = messageReactionMapper.selectCount(wrapper);
        return count != null ? count : 0L;
    }

    @Override
    public Long countUserReactionsByType(Long userId, String reactionType) {
        if (!isValidReactionType(reactionType)) {
            return 0L;
        }

        // 统计用户的特定反应数量
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getUserId, userId)
               .eq(MessageReaction::getReactionType, reactionType);

        Long count = messageReactionMapper.selectCount(wrapper);
        return count != null ? count : 0L;
    }

    @Override
    public Long countReactionsReceivedByUser(Long userId) {
        // 查询用户发送的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getSenderId, userId)
                     .eq(Message::getDeleted, 0);
        List<Message> userMessages = messageMapper.selectList(messageWrapper);

        if (CollectionUtils.isEmpty(userMessages)) {
            return 0L;
        }

        // 批量查询反应
        List<Long> messageIds = userMessages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        List<MessageReaction> reactions = messageReactionMapper.selectByMessageIds(messageIds);

        return (long) reactions.size();
    }

    @Override
    public Long countReactionsReceivedByUserAndType(Long userId, String reactionType) {
        if (!isValidReactionType(reactionType)) {
            return 0L;
        }

        // 查询用户发送的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getSenderId, userId)
                     .eq(Message::getDeleted, 0);
        List<Message> userMessages = messageMapper.selectList(messageWrapper);

        if (CollectionUtils.isEmpty(userMessages)) {
            return 0L;
        }

        // 批量查询特定反应
        List<Long> messageIds = userMessages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(messageIds)) {
            return 0L;
        }

        // 由于Mapper没有批量按类型查询的方法，这里简化处理
        // 实际项目中应该优化这个查询
        long count = 0;
        for (Long messageId : messageIds) {
            LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MessageReaction::getMessageId, messageId)
                   .eq(MessageReaction::getReactionType, reactionType);
            Long messageCount = messageReactionMapper.selectCount(wrapper);
            count += messageCount != null ? messageCount : 0;
        }

        return count;
    }

    @Override
    public void cleanupExpiredReactions() {
        // 清理过期的反应记录
        // 这里可以实现清理逻辑，比如删除一定时间前的反应记录
        // 简化实现，记录日志
        log.info("清理过期反应记录任务执行");

        // 示例：删除30天前的反应记录
        // LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        // LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        // wrapper.lt(MessageReaction::getCreateTime, thirtyDaysAgo);
        // int deletedCount = messageReactionMapper.delete(wrapper);
        // log.info("清理了 {} 条过期反应记录", deletedCount);
    }

    @Override
    public List<MessageReactionDTO> exportReactions(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        // 查询会话中的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getConversationId, conversationId)
                     .eq(Message::getDeleted, 0)
                     .orderByDesc(Message::getCreateTime);
        List<Message> messages = messageMapper.selectList(messageWrapper);

        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }

        // 批量查询反应
        List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        List<MessageReaction> allReactions = messageReactionMapper.selectByMessageIds(messageIds);

        // 按时间排序
        allReactions.sort((r1, r2) -> r2.getCreateTime().compareTo(r1.getCreateTime()));

        return allReactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteReactions(List<Long> reactionIds, Long operatorId) throws BusinessException {
        if (CollectionUtils.isEmpty(reactionIds)) {
            return;
        }

        int totalDeleted = 0;

        for (Long reactionId : reactionIds) {
            try {
                // 验证权限
                validateReactionAccess(reactionId, operatorId);

                // 删除反应
                int result = messageReactionMapper.deleteById(reactionId);
                if (result > 0) {
                    totalDeleted++;
                }
            } catch (BusinessException e) {
                log.error("批量删除反应失败，反应ID: {}，错误: {}", reactionId, e.getMessage());
                // 继续处理其他请求
            }
        }

        log.info("操作者 {} 批量删除了 {} 个反应记录", operatorId, totalDeleted);
    }

    @Override
    public void validateReactionAccess(Long reactionId, Long userId) throws BusinessException {
        // 查询反应记录
        MessageReaction reaction = messageReactionMapper.selectById(reactionId);
        if (reaction == null) {
            throw new BusinessException("反应记录不存在");
        }

        // 检查是否是反应的所有者或消息的发送者
        if (!reaction.getUserId().equals(userId)) {
            // 如果不是反应的所有者，检查是否是消息的发送者
            Message message = messageMapper.selectById(reaction.getMessageId());
            if (message == null || !message.getSenderId().equals(userId)) {
                throw new BusinessException("无权访问此反应记录");
            }
        }
    }

    @Override
    public MessageReactionDTO getReactionById(Long reactionId, Long userId) throws BusinessException {
        // 验证权限
        validateReactionAccess(reactionId, userId);

        // 查询反应记录
        MessageReaction reaction = messageReactionMapper.selectById(reactionId);
        if (reaction == null) {
            throw new BusinessException("反应记录不存在");
        }

        return convertToDTO(reaction);
    }

    @Override
    public List<MessageReactionDTO> getRecentReactions(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20;
        }

        // 查询最近的反应记录
        LambdaQueryWrapper<MessageReaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReaction::getUserId, userId)
               .orderByDesc(MessageReaction::getCreateTime)
               .last("LIMIT " + limit);

        List<MessageReaction> reactions = messageReactionMapper.selectList(wrapper);

        return reactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReactionTrendDTO getReactionTrend(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        // 简化实现，返回空趋势数据
        ReactionTrendDTO trend = new ReactionTrendDTO();
        trend.setConversationId(conversationId);

        // 实际项目中应该实现趋势分析逻辑
        // 这里返回简化版本
        trend.setOverallTrendDirection("STABLE");

        return trend;
    }

    @Override
    public List<Long> getMostReactiveUsers(Long conversationId, Long operatorId, Integer limit) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 查询会话中的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getConversationId, conversationId)
                     .eq(Message::getDeleted, 0);
        List<Message> messages = messageMapper.selectList(messageWrapper);

        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }

        // 批量查询反应
        List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        List<MessageReaction> allReactions = messageReactionMapper.selectByMessageIds(messageIds);

        // 统计用户反应数量
        Map<Long, Long> userReactionCounts = allReactions.stream()
                .collect(Collectors.groupingBy(MessageReaction::getUserId, Collectors.counting()));

        // 按数量排序
        return userReactionCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getMostPopularMessages(Long conversationId, Long operatorId, Integer limit) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // 查询会话中的消息
        LambdaQueryWrapper<Message> messageWrapper = new LambdaQueryWrapper<>();
        messageWrapper.eq(Message::getConversationId, conversationId)
                     .eq(Message::getDeleted, 0);
        List<Message> messages = messageMapper.selectList(messageWrapper);

        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }

        // 批量查询反应
        List<Long> messageIds = messages.stream()
                .map(Message::getId)
                .collect(Collectors.toList());
        List<MessageReaction> allReactions = messageReactionMapper.selectByMessageIds(messageIds);

        // 统计消息反应数量
        Map<Long, Long> messageReactionCounts = allReactions.stream()
                .collect(Collectors.groupingBy(MessageReaction::getMessageId, Collectors.counting()));

        // 按数量排序
        return messageReactionCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageReactionDTO updateReactionEmoji(Long reactionId, String newEmoji, Long userId) throws BusinessException {
        // 验证权限
        validateReactionAccess(reactionId, userId);

        // 查询反应记录
        MessageReaction reaction = messageReactionMapper.selectById(reactionId);
        if (reaction == null) {
            throw new BusinessException("反应记录不存在");
        }

        // 更新表情
        reaction.setReactionEmoji(newEmoji);
        int result = messageReactionMapper.updateById(reaction);
        if (result <= 0) {
            throw new BusinessException("更新反应表情失败");
        }

        log.info("用户 {} 更新了反应 {} 的表情为 {}", userId, reactionId, newEmoji);

        return convertToDTO(reaction);
    }

    @Override
    public ReactionAnalysisReportDTO getReactionAnalysisReport(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        // 简化实现，返回基本报告
        ReactionAnalysisReportDTO report = new ReactionAnalysisReportDTO();
        report.setReportId("REPORT_" + conversationId + "_" + System.currentTimeMillis());
        report.setConversationId(conversationId);
        report.setGeneratedAt(LocalDateTime.now());
        report.setAnalysisPeriod("最近30天");

        // 设置报告概述
        ReactionAnalysisReportDTO.ReportOverview overview = new ReactionAnalysisReportDTO.ReportOverview();
        overview.setTitle("会话反应分析报告");
        overview.setExecutiveSummary("本报告分析了会话中的反应互动情况。");
        overview.setAnalysisPurpose("评估用户互动质量和参与度");
        overview.setDataScope("最近30天的反应数据");
        overview.setAnalysisMethodology("统计分析、趋势分析、用户行为分析");
        overview.setMainConclusion("会话互动良好，反应多样性适中");
        report.setOverview(overview);

        // 设置总体统计
        ReactionAnalysisReportDTO.OverallStatistics overallStats = new ReactionAnalysisReportDTO.OverallStatistics();
        overallStats.setTotalReactions(150L);
        overallStats.setTotalMessages(50L);
        overallStats.setReactedMessagesRatio(0.6);
        overallStats.setAverageReactionsPerMessage(3.0);
        overallStats.setTotalParticipatingUsers(20);
        overallStats.setActiveUsersRatio(0.8);
        report.setOverallStatistics(overallStats);

        // 计算报告评分
        report.calculateReportScore();

        return report;
    }

    @Override
    public ReactionNetworkDTO getReactionNetwork(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAccess(conversationId, operatorId);

        // 简化实现，返回基本网络数据
        ReactionNetworkDTO network = new ReactionNetworkDTO();
        network.setConversationId(conversationId);

        // 设置基本网络指标
        network.setNetworkDensity(0.3);
        network.setAveragePathLength(2.5);
        network.setClusteringCoefficient(0.4);
        network.setNetworkStructureType("中等密度网络");
        network.setNetworkHealthScore(75.0);

        return network;
    }

    @Override
    public boolean checkReactionExists(Long reactionId) {
        MessageReaction reaction = messageReactionMapper.selectById(reactionId);
        return reaction != null;
    }

    @Override
    public Double getReactionImpactScore(Long messageId) throws BusinessException {
        // 验证消息存在
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 查询消息的反应
        List<MessageReaction> reactions = messageReactionMapper.selectByMessageId(messageId);

        if (CollectionUtils.isEmpty(reactions)) {
            return 0.0;
        }

        // 计算影响分数
        double score = 0.0;

        // 反应数量基础分
        score += Math.min(reactions.size() * 0.5, 50);

        // 反应类型多样性加分
        long distinctTypes = reactions.stream()
                .map(MessageReaction::getReactionType)
                .distinct()
                .count();
        score += Math.min(distinctTypes * 10, 30);

        // 点赞和爱心额外加分
        long positiveReactions = reactions.stream()
                .filter(r -> "LIKE".equals(r.getReactionType()) || "HEART".equals(r.getReactionType()))
                .count();
        score += Math.min(positiveReactions * 0.3, 20);

        return Math.min(score, 100.0);
    }

    // ============ 私有方法 ============

    /**
     * 验证消息访问权限
     */
    private void validateMessageAccess(Long messageId, Long userId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 检查用户是否是会话成员
        LambdaQueryWrapper<ConversationMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConversationMember::getConversationId, message.getConversationId())
               .eq(ConversationMember::getUserId, userId);

        ConversationMember member = conversationMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException("无权访问此消息");
        }
    }

    /**
     * 验证会话访问权限
     */
    private void validateConversationAccess(Long conversationId, Long userId) throws BusinessException {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException("会话不存在");
        }

        // 检查用户是否是会话成员
        LambdaQueryWrapper<ConversationMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConversationMember::getConversationId, conversationId)
               .eq(ConversationMember::getUserId, userId);

        ConversationMember member = conversationMemberMapper.selectOne(wrapper);
        if (member == null) {
            throw new BusinessException("无权访问此会话");
        }
    }

    /**
     * 验证两个用户是否在同一个会话中（用于查看他人反应）
     */
    private void validateSameConversationAccess(Long messageId, Long operatorId, Long targetUserId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }

        Long conversationId = message.getConversationId();

        // 检查操作者是否是会话成员
        LambdaQueryWrapper<ConversationMember> operatorWrapper = new LambdaQueryWrapper<>();
        operatorWrapper.eq(ConversationMember::getConversationId, conversationId)
                      .eq(ConversationMember::getUserId, operatorId);
        ConversationMember operatorMember = conversationMemberMapper.selectOne(operatorWrapper);

        // 检查目标用户是否是会话成员
        LambdaQueryWrapper<ConversationMember> targetWrapper = new LambdaQueryWrapper<>();
        targetWrapper.eq(ConversationMember::getConversationId, conversationId)
                    .eq(ConversationMember::getUserId, targetUserId);
        ConversationMember targetMember = conversationMemberMapper.selectOne(targetWrapper);

        if (operatorMember == null || targetMember == null) {
            throw new BusinessException("无权查看该用户的反应");
        }
    }

    /**
     * 验证反应类型是否有效
     */
    private boolean isValidReactionType(String reactionType) {
        return reactionType != null && (
            "LIKE".equals(reactionType) ||
            "HEART".equals(reactionType) ||
            "LAUGH".equals(reactionType) ||
            "SAD".equals(reactionType) ||
            "ANGRY".equals(reactionType)
        );
    }

    /**
     * 获取默认表情
     */
    private String getDefaultEmoji(String reactionType) {
        switch (reactionType) {
            case "LIKE":
                return "👍";
            case "HEART":
                return "❤️";
            case "LAUGH":
                return "😄";
            case "SAD":
                return "😢";
            case "ANGRY":
                return "😠";
            default:
                return "👍";
        }
    }

    /**
     * 转换为DTO
     */
    private MessageReactionDTO convertToDTO(MessageReaction reaction) {
        if (reaction == null) {
            return null;
        }

        MessageReactionDTO dto = new MessageReactionDTO();
        BeanUtils.copyProperties(reaction, dto);

        // 查询消息信息
        Message message = messageMapper.selectById(reaction.getMessageId());
        if (message != null) {
            dto.setMessageContent(message.getContent());
            dto.setMessageContentSummary(message.getContentSummary());
            dto.setMessageSenderId(message.getSenderId());
            dto.setConversationId(message.getConversationId());

            // 查询会话信息
            Conversation conversation = conversationMapper.selectById(message.getConversationId());
            if (conversation != null) {
                dto.setConversationTitle(conversation.getTitle());
            }
        }

        // 用户信息（实际应从用户服务获取）
        dto.setUserName("用户" + reaction.getUserId());

        return dto;
    }
}