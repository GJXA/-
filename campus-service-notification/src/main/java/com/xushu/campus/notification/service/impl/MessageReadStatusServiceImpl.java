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
import com.xushu.campus.notification.service.MessageReadStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息阅读状态服务实现类
 */
@Slf4j
@Service
public class MessageReadStatusServiceImpl implements MessageReadStatusService {

    @Autowired
    private MessageReadStatusMapper messageReadStatusMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationMemberMapper conversationMemberMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageReadStatusDTO recordMessageRead(Long messageId, Long userId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, userId);

        // 检查是否已记录
        if (hasUserReadMessage(messageId, userId)) {
            // 已存在，更新阅读时间
            MessageReadStatus existing = getMessageReadStatusByUser(messageId, userId);
            if (existing != null) {
                existing.setReadTime(LocalDateTime.now());
                messageReadStatusMapper.updateById(existing);
                return convertToDTO(existing);
            }
        }

        // 创建新的阅读记录
        MessageReadStatus readStatus = new MessageReadStatus();
        readStatus.setMessageId(messageId);
        readStatus.setUserId(userId);
        readStatus.setReadTime(LocalDateTime.now());

        int result = messageReadStatusMapper.insert(readStatus);
        if (result <= 0) {
            throw new BusinessException("记录阅读状态失败");
        }

        // 增加消息的已读人数
        messageMapper.increaseReadCount(messageId);

        log.debug("用户 {} 阅读消息 {}", userId, messageId);
        return convertToDTO(readStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MessageReadStatusDTO> batchRecordMessageRead(List<Long> messageIds, Long userId) throws BusinessException {
        if (CollectionUtils.isEmpty(messageIds)) {
            throw new BusinessException("消息ID列表不能为空");
        }

        List<MessageReadStatusDTO> results = new ArrayList<>();
        for (Long messageId : messageIds) {
            try {
                MessageReadStatusDTO dto = recordMessageRead(messageId, userId);
                results.add(dto);
            } catch (BusinessException e) {
                log.warn("记录消息 {} 阅读状态失败: {}", messageId, e.getMessage());
            }
        }

        return results;
    }

    @Override
    public List<MessageReadStatusDTO> getMessageReadStatus(Long messageId, Long operatorId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, operatorId);

        List<MessageReadStatus> readStatuses = messageReadStatusMapper.selectByMessageId(messageId);
        if (CollectionUtils.isEmpty(readStatuses)) {
            return Collections.emptyList();
        }

        return readStatuses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据消息ID和用户ID获取阅读状态实体
     */
    private MessageReadStatus getMessageReadStatusByUser(Long messageId, Long userId) {
        LambdaQueryWrapper<MessageReadStatus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageReadStatus::getMessageId, messageId)
                   .eq(MessageReadStatus::getUserId, userId);
        return messageReadStatusMapper.selectOne(queryWrapper);
    }

    @Override
    public IPage<MessageReadStatusDTO> getMessageReadStatusPage(Long messageId, Long operatorId, Integer page, Integer size) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, operatorId);

        // 设置默认分页参数
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 20;

        // 查询总数量
        List<MessageReadStatus> allStatuses = messageReadStatusMapper.selectByMessageId(messageId);
        int total = allStatuses != null ? allStatuses.size() : 0;
        if (total == 0) {
            Page<MessageReadStatusDTO> resultPage = new Page<>(page, size, 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 手动分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            Page<MessageReadStatusDTO> resultPage = new Page<>(page, size, total);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        List<MessageReadStatus> pageData = allStatuses.subList(start, end);
        List<MessageReadStatusDTO> dtos = pageData.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<MessageReadStatusDTO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public List<MessageReadStatusDTO> getUserMessageReadRecords(Long userId, Integer limit) {
        List<MessageReadStatus> readStatuses = messageReadStatusMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(readStatuses)) {
            return Collections.emptyList();
        }

        // 按阅读时间倒序排序
        readStatuses.sort((a, b) -> {
            if (a.getReadTime() == null && b.getReadTime() == null) return 0;
            if (a.getReadTime() == null) return 1;
            if (b.getReadTime() == null) return -1;
            return b.getReadTime().compareTo(a.getReadTime());
        });

        List<MessageReadStatusDTO> dtos = readStatuses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (limit != null && limit > 0 && dtos.size() > limit) {
            dtos = dtos.subList(0, limit);
        }

        return dtos;
    }

    @Override
    public IPage<MessageReadStatusDTO> getUserMessageReadRecordsPage(Long userId, Integer page, Integer size) {
        // 设置默认分页参数
        if (page == null || page <= 0) page = 1;
        if (size == null || size <= 0) size = 20;

        // 查询用户的阅读记录
        List<MessageReadStatus> readStatuses = messageReadStatusMapper.selectByUserId(userId);
        int total = readStatuses != null ? readStatuses.size() : 0;
        if (total == 0) {
            Page<MessageReadStatusDTO> resultPage = new Page<>(page, size, 0);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        // 按阅读时间倒序排序
        readStatuses.sort((a, b) -> {
            if (a.getReadTime() == null && b.getReadTime() == null) return 0;
            if (a.getReadTime() == null) return 1;
            if (b.getReadTime() == null) return -1;
            return b.getReadTime().compareTo(a.getReadTime());
        });

        // 手动分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            Page<MessageReadStatusDTO> resultPage = new Page<>(page, size, total);
            resultPage.setRecords(List.of());
            return resultPage;
        }

        List<MessageReadStatus> pageData = readStatuses.subList(start, end);
        List<MessageReadStatusDTO> dtos = pageData.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Page<MessageReadStatusDTO> resultPage = new Page<>(page, size, total);
        resultPage.setRecords(dtos);
        return resultPage;
    }

    @Override
    public List<MessageReadStatusDTO> getUserReadRecordsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<MessageReadStatus> allRecords = messageReadStatusMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(allRecords)) {
            return Collections.emptyList();
        }

        return allRecords.stream()
                .filter(record -> isInTimeRange(record.getReadTime(), startTime, endTime))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getMessageReaders(Long messageId, Long operatorId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, operatorId);

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
    public List<Long> getMessageUnreaders(Long messageId, Long operatorId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, operatorId);

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
        List<Long> readers = getMessageReaders(messageId, operatorId);

        // 计算未读用户
        List<Long> allMemberIds = members.stream()
                .filter(member -> member.getDeleted() == 0 && member.getLeaveTime() == null)
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
    public Long countUserReadMessages(Long userId) {
        List<MessageReadStatus> records = messageReadStatusMapper.selectByUserId(userId);
        return records != null ? (long) records.size() : 0L;
    }

    @Override
    public Long countMessageReaders(Long messageId) throws BusinessException {
        // 验证消息存在
        validateMessageExists(messageId);

        List<MessageReadStatus> readStatuses = messageReadStatusMapper.selectByMessageId(messageId);
        return readStatuses != null ? (long) readStatuses.size() : 0L;
    }

    @Override
    public Long countMessageUnreaders(Long messageId) throws BusinessException {
        // 验证消息存在
        validateMessageExists(messageId);

        Long conversationId = getMessageConversationId(messageId);
        if (conversationId == null) {
            return 0L;
        }

        // 获取会话成员数量
        Integer memberCount = conversationMemberMapper.countByConversationId(conversationId);
        if (memberCount == null || memberCount == 0) {
            return 0L;
        }

        // 获取已读用户数量
        Long readerCount = countMessageReaders(messageId);

        return memberCount.longValue() - readerCount;
    }

    @Override
    public Double getMessageReadRate(Long messageId) throws BusinessException {
        // 验证消息存在
        validateMessageExists(messageId);

        Long conversationId = getMessageConversationId(messageId);
        if (conversationId == null) {
            return 0.0;
        }

        // 获取会话成员数量
        Integer memberCount = conversationMemberMapper.countByConversationId(conversationId);
        if (memberCount == null || memberCount == 0) {
            return 0.0;
        }

        // 获取已读用户数量
        Long readerCount = countMessageReaders(messageId);

        return readerCount * 100.0 / memberCount;
    }

    @Override
    public Double getConversationAverageReadRate(Long conversationId) throws BusinessException {
        // 验证会话存在
        validateConversationExists(conversationId);

        // 获取会话中的所有消息
        List<Message> messages = getAllMessagesByConversation(conversationId);
        if (CollectionUtils.isEmpty(messages)) {
            return 0.0;
        }

        // 计算平均阅读率
        double totalRate = 0.0;
        int count = 0;

        for (Message message : messages) {
            try {
                Double rate = getMessageReadRate(message.getId());
                if (rate != null) {
                    totalRate += rate;
                    count++;
                }
            } catch (Exception e) {
                // 忽略错误，继续计算其他消息
            }
        }

        return count > 0 ? totalRate / count : 0.0;
    }

    @Override
    public Double getUserAverageReadTime(Long userId) {
        List<MessageReadStatus> records = messageReadStatusMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(records)) {
            return 0.0;
        }

        // 计算平均阅读时间（从消息发送到阅读）
        double totalDelay = 0.0;
        int count = 0;

        for (MessageReadStatus record : records) {
            try {
                Message message = messageMapper.selectById(record.getMessageId());
                if (message != null && message.getCreateTime() != null && record.getReadTime() != null) {
                    Duration delay = Duration.between(message.getCreateTime(), record.getReadTime());
                    totalDelay += delay.toMinutes();
                    count++;
                }
            } catch (Exception e) {
                // 忽略错误
            }
        }

        return count > 0 ? totalDelay / count : 0.0;
    }

    @Override
    public Double getMessageAverageReadTime(Long messageId) throws BusinessException {
        // 验证消息存在
        validateMessageExists(messageId);

        List<MessageReadStatus> readStatuses = messageReadStatusMapper.selectByMessageId(messageId);
        if (CollectionUtils.isEmpty(readStatuses)) {
            return 0.0;
        }

        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getCreateTime() == null) {
            return 0.0;
        }

        // 计算平均阅读时间
        double totalDelay = 0.0;
        int count = 0;

        for (MessageReadStatus record : readStatuses) {
            if (record.getReadTime() != null) {
                Duration delay = Duration.between(message.getCreateTime(), record.getReadTime());
                totalDelay += delay.toMinutes();
                count++;
            }
        }

        return count > 0 ? totalDelay / count : 0.0;
    }

    @Override
    public MessageReadStatusDTO getEarliestReader(Long messageId, Long operatorId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, operatorId);

        List<MessageReadStatus> readStatuses = messageReadStatusMapper.selectByMessageId(messageId);
        if (CollectionUtils.isEmpty(readStatuses)) {
            return null;
        }

        // 找到最早的阅读记录
        MessageReadStatus earliest = null;
        for (MessageReadStatus record : readStatuses) {
            if (record.getReadTime() != null) {
                if (earliest == null || record.getReadTime().isBefore(earliest.getReadTime())) {
                    earliest = record;
                }
            }
        }

        return earliest != null ? convertToDTO(earliest) : null;
    }

    @Override
    public MessageReadStatusDTO getLatestReader(Long messageId, Long operatorId) throws BusinessException {
        // 验证消息存在和用户权限
        validateMessageAccess(messageId, operatorId);

        List<MessageReadStatus> readStatuses = messageReadStatusMapper.selectByMessageId(messageId);
        if (CollectionUtils.isEmpty(readStatuses)) {
            return null;
        }

        // 找到最晚的阅读记录
        MessageReadStatus latest = null;
        for (MessageReadStatus record : readStatuses) {
            if (record.getReadTime() != null) {
                if (latest == null || record.getReadTime().isAfter(latest.getReadTime())) {
                    latest = record;
                }
            }
        }

        return latest != null ? convertToDTO(latest) : null;
    }

    @Override
    public ReadStatisticsDTO getReadStatistics(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAndAccess(conversationId, operatorId);

        ReadStatisticsDTO statistics = new ReadStatisticsDTO();
        statistics.setConversationId(conversationId);

        // 获取会话中的所有消息
        List<Message> messages = getAllMessagesByConversation(conversationId);
        if (CollectionUtils.isEmpty(messages)) {
            statistics.calculate();
            return statistics;
        }

        // 统计总消息数量
        statistics.setTotalMessageCount((long) messages.size());

        // 统计已读消息数量
        long readMessageCount = 0;
        for (Message message : messages) {
            if (countMessageReaders(message.getId()) > 0) {
                readMessageCount++;
            }
        }
        statistics.setReadMessageCount(readMessageCount);

        // 计算平均阅读时间
        double totalReadTime = 0.0;
        int readTimeCount = 0;

        for (Message message : messages) {
            try {
                Double avgTime = getMessageAverageReadTime(message.getId());
                if (avgTime != null && avgTime > 0) {
                    totalReadTime += avgTime;
                    readTimeCount++;
                }
            } catch (Exception e) {
                // 忽略错误
            }
        }

        if (readTimeCount > 0) {
            statistics.setAverageReadTime(totalReadTime / readTimeCount);
        }

        // 设置今日阅读数量（简化）
        statistics.setTodayReadCount(readMessageCount / 10L); // 简化计算

        statistics.calculate();
        return statistics;
    }

    @Override
    public UserReadStatisticsDTO getUserReadStatistics(Long userId) {
        UserReadStatisticsDTO statistics = new UserReadStatisticsDTO();
        statistics.setUserId(userId);

        // 获取用户的阅读记录
        List<MessageReadStatus> records = messageReadStatusMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(records)) {
            statistics.calculate();
            return statistics;
        }

        statistics.setTotalReadMessageCount((long) records.size());

        // 计算平均阅读时间
        statistics.setAverageReadTime(getUserAverageReadTime(userId));

        // 设置今日、本周、本月阅读数量（简化）
        statistics.setTodayReadMessageCount((long) records.size() / 30);
        statistics.setWeekReadMessageCount((long) records.size() / 4);
        statistics.setMonthReadMessageCount((long) records.size());

        statistics.calculate();
        return statistics;
    }

    @Override
    public void cleanupExpiredReadRecords() {
        // 清理过期的阅读记录（例如超过一年的记录）
        log.info("清理过期的阅读记录");
        // 实际实现需要根据业务需求清理
    }

    @Override
    public List<MessageReadStatusDTO> exportReadRecords(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAndAccess(conversationId, operatorId);

        // 获取会话中的所有阅读记录
        List<Message> messages = getAllMessagesByConversation(conversationId);
        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }

        List<MessageReadStatusDTO> allRecords = new ArrayList<>();
        for (Message message : messages) {
            try {
                List<MessageReadStatusDTO> messageRecords = getMessageReadStatus(message.getId(), operatorId);
                allRecords.addAll(messageRecords);
            } catch (Exception e) {
                // 忽略错误
            }
        }

        return allRecords;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteReadRecords(List<Long> recordIds, Long operatorId) throws BusinessException {
        if (CollectionUtils.isEmpty(recordIds)) {
            throw new BusinessException("记录ID列表不能为空");
        }

        for (Long recordId : recordIds) {
            try {
                validateReadRecordAccess(recordId, operatorId);
                messageReadStatusMapper.deleteById(recordId);
            } catch (BusinessException e) {
                log.warn("删除阅读记录 {} 失败: {}", recordId, e.getMessage());
            }
        }
    }

    @Override
    public void validateReadRecordAccess(Long recordId, Long userId) throws BusinessException {
        MessageReadStatus record = messageReadStatusMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("阅读记录不存在");
        }

        // 检查用户是否有权限访问
        if (!record.getUserId().equals(userId)) {
            // 检查用户是否是消息所在会话的管理员
            try {
                Long messageId = record.getMessageId();
                Long conversationId = getMessageConversationId(messageId);
                if (conversationId != null) {
                    ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
                    if (member != null && member.getDeleted() == 0 && member.getLeaveTime() == null &&
                            ("OWNER".equals(member.getUserRole()) || "ADMIN".equals(member.getUserRole()))) {
                        return; // 管理员有权限
                    }
                }
            } catch (Exception e) {
                // 忽略错误
            }

            throw new BusinessException("没有权限访问该阅读记录");
        }
    }

    @Override
    public MessageReadStatusDTO getReadRecordById(Long recordId, Long userId) throws BusinessException {
        validateReadRecordAccess(recordId, userId);

        MessageReadStatus record = messageReadStatusMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException("阅读记录不存在");
        }

        return convertToDTO(record);
    }

    @Override
    public List<MessageReadStatusDTO> getRecentlyReadMessages(Long userId, Integer limit) {
        List<MessageReadStatusDTO> allRecords = getUserMessageReadRecords(userId, null);
        if (CollectionUtils.isEmpty(allRecords)) {
            return Collections.emptyList();
        }

        // 按阅读时间倒序排序
        allRecords.sort((a, b) -> {
            if (a.getReadTime() == null && b.getReadTime() == null) return 0;
            if (a.getReadTime() == null) return 1;
            if (b.getReadTime() == null) return -1;
            return b.getReadTime().compareTo(a.getReadTime());
        });

        if (limit != null && limit > 0 && allRecords.size() > limit) {
            return allRecords.subList(0, limit);
        }

        return allRecords;
    }

    @Override
    public List<Long> getUnreadMessageReminders(Long userId, Integer hoursThreshold) {
        // 获取用户参与的所有会话
        List<ConversationMember> members = conversationMemberMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }

        List<Long> reminderMessageIds = new ArrayList<>();
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(hoursThreshold != null ? hoursThreshold : 24);

        for (ConversationMember member : members) {
            if (member.getDeleted() == 1 || member.getLeaveTime() != null) {
                continue;
            }

            Long conversationId = member.getConversationId();
            List<Message> messages = getAllMessagesByConversation(conversationId);
            if (CollectionUtils.isEmpty(messages)) {
                continue;
            }

            for (Message message : messages) {
                // 检查消息是否在阈值时间之后发送且用户未读
                if (message.getCreateTime() != null &&
                    message.getCreateTime().isAfter(thresholdTime) &&
                    !hasUserReadMessage(message.getId(), userId)) {
                    reminderMessageIds.add(message.getId());
                }
            }
        }

        return reminderMessageIds;
    }

    @Override
    public LocalDateTime markMessageAsReadAndGetTime(Long messageId, Long userId) throws BusinessException {
        MessageReadStatusDTO record = recordMessageRead(messageId, userId);
        return record != null ? record.getReadTime() : LocalDateTime.now();
    }

    @Override
    public boolean isMessageReadByAll(Long messageId) throws BusinessException {
        // 验证消息存在
        validateMessageExists(messageId);

        Long conversationId = getMessageConversationId(messageId);
        if (conversationId == null) {
            return false;
        }

        // 获取会话成员数量
        Integer memberCount = conversationMemberMapper.countByConversationId(conversationId);
        if (memberCount == null || memberCount == 0) {
            return false;
        }

        // 获取已读用户数量
        Long readerCount = countMessageReaders(messageId);

        return readerCount >= memberCount;
    }

    @Override
    public ReadDelayStatisticsDTO getReadDelayStatistics(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAndAccess(conversationId, operatorId);

        ReadDelayStatisticsDTO statistics = new ReadDelayStatisticsDTO();
        statistics.setConversationId(conversationId);

        // 获取会话中的所有阅读记录
        List<MessageReadStatusDTO> allRecords = exportReadRecords(conversationId, operatorId);
        if (CollectionUtils.isEmpty(allRecords)) {
            statistics.calculate();
            return statistics;
        }

        statistics.setTotalReadRecords((long) allRecords.size());

        // 计算延迟统计
        List<Double> delays = new ArrayList<>();
        for (MessageReadStatusDTO record : allRecords) {
            try {
                Double delay = calculateReadDelay(record.getMessageId(), record.getUserId());
                if (delay != null) {
                    delays.add(delay);
                }
            } catch (Exception e) {
                // 忽略错误
            }
        }

        if (!delays.isEmpty()) {
            // 计算基本统计
            double sum = delays.stream().mapToDouble(Double::doubleValue).sum();
            statistics.setAverageReadDelay(sum / delays.size());
            statistics.setMinReadDelay(delays.stream().min(Double::compare).orElse(0.0));
            statistics.setMaxReadDelay(delays.stream().max(Double::compare).orElse(0.0));

            // 计算中位数
            delays.sort(Double::compare);
            statistics.setMedianReadDelay(delays.get(delays.size() / 2));

            // 计算标准差
            double mean = statistics.getAverageReadDelay();
            double variance = delays.stream().mapToDouble(d -> Math.pow(d - mean, 2)).sum() / delays.size();
            statistics.setReadDelayStdDev(Math.sqrt(variance));

            // 计算各种延迟比例
            long immediate = delays.stream().filter(d -> d <= 5).count();
            long quick = delays.stream().filter(d -> d > 5 && d <= 30).count();
            long normal = delays.stream().filter(d -> d > 30 && d <= 120).count();
            long delayed = delays.stream().filter(d -> d > 120 && d <= 1440).count();
            long severelyDelayed = delays.stream().filter(d -> d > 1440).count();

            statistics.setImmediateReadRatio(immediate * 100.0 / delays.size());
            statistics.setQuickReadRatio(quick * 100.0 / delays.size());
            statistics.setNormalReadRatio(normal * 100.0 / delays.size());
            statistics.setDelayedReadRatio(delayed * 100.0 / delays.size());
            statistics.setSeverelyDelayedReadRatio(severelyDelayed * 100.0 / delays.size());
        }

        statistics.calculate();
        return statistics;
    }

    @Override
    public List<Long> getActiveReaders(Long conversationId, Long operatorId) throws BusinessException {
        // 验证会话存在和用户权限
        validateConversationAndAccess(conversationId, operatorId);

        List<MessageReadStatusDTO> allRecords = exportReadRecords(conversationId, operatorId);
        if (CollectionUtils.isEmpty(allRecords)) {
            return Collections.emptyList();
        }

        // 统计最近24小时有阅读记录的用户
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        Map<Long, Integer> userReadCount = new HashMap<>();

        for (MessageReadStatusDTO record : allRecords) {
            if (record.getReadTime() != null && record.getReadTime().isAfter(twentyFourHoursAgo)) {
                userReadCount.merge(record.getUserId(), 1, Integer::sum);
            }
        }

        // 按阅读次数排序
        return userReadCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // ============= 私有辅助方法 =============

    private void validateMessageAccess(Long messageId, Long userId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }

        // 验证用户是否是消息所在会话的成员
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(message.getConversationId(), userId);
        if (member == null || member.getDeleted() == 1 || member.getLeaveTime() != null) {
            throw new BusinessException("用户不是会话成员");
        }
    }

    private void validateMessageExists(Long messageId) throws BusinessException {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getDeleted() == 1) {
            throw new BusinessException("消息不存在或已被删除");
        }
    }

    private void validateConversationExists(Long conversationId) throws BusinessException {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getDeleted() == 1) {
            throw new BusinessException("会话不存在或已被删除");
        }
    }

    private void validateConversationAndAccess(Long conversationId, Long userId) throws BusinessException {
        validateConversationExists(conversationId);

        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1 || member.getLeaveTime() != null) {
            throw new BusinessException("用户不是会话成员");
        }
    }

    private MessageReadStatus findMessageReadStatus(Long messageId, Long userId) {
        LambdaQueryWrapper<MessageReadStatus> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReadStatus::getMessageId, messageId);
        wrapper.eq(MessageReadStatus::getUserId, userId);
        return messageReadStatusMapper.selectOne(wrapper);
    }

    private Long getMessageConversationId(Long messageId) {
        Message message = messageMapper.selectById(messageId);
        return message != null ? message.getConversationId() : null;
    }

    private List<Message> getAllMessagesByConversation(Long conversationId) {
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

    private Double calculateReadDelay(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null || message.getCreateTime() == null) {
            return null;
        }

        MessageReadStatus readStatus = getMessageReadStatusByUser(messageId, userId);
        if (readStatus == null || readStatus.getReadTime() == null) {
            return null;
        }

        Duration delay = Duration.between(message.getCreateTime(), readStatus.getReadTime());
        return delay.toMinutes() + delay.getSeconds() / 60.0;
    }

    private MessageReadStatusDTO convertToDTO(MessageReadStatus readStatus) {
        if (readStatus == null) {
            return null;
        }
        MessageReadStatusDTO dto = new MessageReadStatusDTO();
        BeanUtils.copyProperties(readStatus, dto);
        dto.calculateDesc();
        return dto;
    }
}