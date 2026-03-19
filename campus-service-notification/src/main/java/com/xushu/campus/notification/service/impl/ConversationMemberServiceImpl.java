package com.xushu.campus.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xushu.campus.common.exception.BusinessException;
import com.xushu.campus.notification.dto.ConversationMemberDTO;
import com.xushu.campus.notification.dto.MemberStatisticsDTO;
import com.xushu.campus.notification.dto.RoleDistributionDTO;
import com.xushu.campus.notification.entity.Conversation;
import com.xushu.campus.notification.entity.ConversationMember;
import com.xushu.campus.notification.mapper.ConversationMapper;
import com.xushu.campus.notification.mapper.ConversationMemberMapper;
import com.xushu.campus.notification.service.ConversationMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 会话成员服务实现类
 */
@Slf4j
@Service
public class ConversationMemberServiceImpl implements ConversationMemberService {

    @Autowired
    private ConversationMemberMapper conversationMemberMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Override
    public ConversationMemberDTO getConversationMember(Long conversationId, Long userId, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException("用户不是会话成员");
        }

        return convertToDTO(member);
    }

    @Override
    public List<ConversationMemberDTO> getAllConversationMembers(Long conversationId, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }

        return members.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationMemberDTO> getActiveConversationMembers(Long conversationId, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }

        return members.stream()
                .filter(member -> member.getDeleted() == 0 && member.getLeaveTime() == null)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConversationMemberDTO> getConversationMembersByUserId(Long userId, Integer limit) {
        List<ConversationMember> members = conversationMemberMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyList();
        }

        List<ConversationMemberDTO> dtos = members.stream()
                .filter(member -> member.getDeleted() == 0 && member.getLeaveTime() == null)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (limit != null && limit > 0 && dtos.size() > limit) {
            dtos = dtos.subList(0, limit);
        }

        return dtos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationMemberDTO updateMemberNickname(Long conversationId, Long userId, String nickname, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        // 检查操作权限：用户可以修改自己的昵称，管理员可以修改其他人的
        boolean isSelfUpdate = userId.equals(operatorId);
        if (!isSelfUpdate && !hasManagementPermission(conversationId, operatorId)) {
            throw new BusinessException("没有权限修改其他成员的昵称");
        }

        ConversationMember member = getActiveMember(conversationId, userId);
        member.setNicknameInConversation(nickname);

        conversationMemberMapper.updateById(member);

        log.info("用户 {} 更新成员 {} 在会话 {} 中的昵称为: {}", operatorId, userId, conversationId, nickname);
        return convertToDTO(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationMemberDTO updateMemberRole(Long conversationId, Long userId, String newRole, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        // 只有管理员可以修改角色
        if (!hasManagementPermission(conversationId, operatorId)) {
            throw new BusinessException("只有管理员可以修改成员角色");
        }

        ConversationMember member = getActiveMember(conversationId, userId);

        // 不能修改拥有者的角色，除非是拥有者自己
        if ("OWNER".equals(member.getUserRole()) && !userId.equals(operatorId)) {
            throw new BusinessException("不能修改拥有者的角色");
        }

        // 验证新角色有效性
        if (!isValidRole(newRole)) {
            throw new BusinessException("无效的角色: " + newRole);
        }

        member.setUserRole(newRole);
        conversationMemberMapper.updateById(member);

        log.info("用户 {} 更新成员 {} 在会话 {} 中的角色为: {}", operatorId, userId, conversationId, newRole);
        return convertToDTO(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationMemberDTO toggleMemberMute(Long conversationId, Long userId, boolean mute, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        // 检查操作权限：用户可以静音自己，管理员可以静音其他人
        boolean isSelfUpdate = userId.equals(operatorId);
        if (!isSelfUpdate && !hasManagementPermission(conversationId, operatorId)) {
            throw new BusinessException("没有权限静音其他成员");
        }

        ConversationMember member = getActiveMember(conversationId, userId);
        member.setIsMuted(mute ? 1 : 0);

        conversationMemberMapper.updateById(member);

        log.info("用户 {} {} 成员 {} 在会话 {} 中", operatorId, mute ? "静音" : "取消静音", userId, conversationId);
        return convertToDTO(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberUnreadCount(Long conversationId, Long userId, Integer delta) throws BusinessException {
        ConversationMember member = getActiveMember(conversationId, userId);

        int newUnreadCount = (member.getUnreadCount() != null ? member.getUnreadCount() : 0) + delta;
        if (newUnreadCount < 0) {
            newUnreadCount = 0;
        }

        member.setUnreadCount(newUnreadCount);
        conversationMemberMapper.updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetMemberUnreadCount(Long conversationId, Long userId, Long lastReadMessageId) throws BusinessException {
        ConversationMember member = getActiveMember(conversationId, userId);

        member.setUnreadCount(0);
        member.setLastReadMessageId(lastReadMessageId);
        conversationMemberMapper.updateById(member);
    }

    @Override
    public Integer getMemberUnreadCount(Long conversationId, Long userId) throws BusinessException {
        ConversationMember member = getActiveMember(conversationId, userId);
        return member.getUnreadCount() != null ? member.getUnreadCount() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMemberLastReadMessage(Long conversationId, Long userId, Long lastReadMessageId) throws BusinessException {
        ConversationMember member = getActiveMember(conversationId, userId);
        member.setLastReadMessageId(lastReadMessageId);
        conversationMemberMapper.updateById(member);
    }

    @Override
    public boolean isConversationMember(Long conversationId, Long userId) {
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        return member != null && member.getDeleted() == 0 && member.getLeaveTime() == null;
    }

    @Override
    public boolean hasMemberRole(Long conversationId, Long userId, String role) {
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1 || member.getLeaveTime() != null) {
            return false;
        }
        return role.equals(member.getUserRole());
    }

    @Override
    public boolean hasManagementPermission(Long conversationId, Long userId) {
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1 || member.getLeaveTime() != null) {
            return false;
        }
        return "OWNER".equals(member.getUserRole()) || "ADMIN".equals(member.getUserRole());
    }

    @Override
    public Integer getConversationMemberCount(Long conversationId) throws BusinessException {
        validateConversationExists(conversationId);

        Integer count = conversationMemberMapper.countByConversationId(conversationId);
        return count != null ? count : 0;
    }

    @Override
    public Integer getActiveMemberCount(Long conversationId) throws BusinessException {
        validateConversationExists(conversationId);

        List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        if (CollectionUtils.isEmpty(members)) {
            return 0;
        }

        long activeCount = members.stream()
                .filter(member -> member.getDeleted() == 0 && member.getLeaveTime() == null)
                .count();

        return (int) activeCount;
    }

    @Override
    public List<ConversationMemberDTO> getOnlineMembers(Long conversationId, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        // 简化实现：返回所有活跃成员
        // 实际应该集成在线状态服务
        return getActiveConversationMembers(conversationId, operatorId);
    }

    @Override
    public List<ConversationMemberDTO> getOfflineMembers(Long conversationId, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        // 简化实现：返回空列表
        // 实际应该集成在线状态服务
        return Collections.emptyList();
    }

    @Override
    public List<ConversationMemberDTO> getRecentlyActiveMembers(Long conversationId, Long operatorId, Integer limit) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        List<ConversationMemberDTO> allMembers = getActiveConversationMembers(conversationId, operatorId);

        // 简化实现：按加入时间排序
        allMembers.sort((a, b) -> {
            if (a.getJoinTime() == null && b.getJoinTime() == null) return 0;
            if (a.getJoinTime() == null) return 1;
            if (b.getJoinTime() == null) return -1;
            return b.getJoinTime().compareTo(a.getJoinTime());
        });

        if (limit != null && limit > 0 && allMembers.size() > limit) {
            return allMembers.subList(0, limit);
        }

        return allMembers;
    }

    @Override
    public List<ConversationMemberDTO> searchMembers(Long conversationId, String keyword, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        List<ConversationMemberDTO> allMembers = getActiveConversationMembers(conversationId, operatorId);
        if (CollectionUtils.isEmpty(allMembers) || !StringUtils.hasText(keyword)) {
            return allMembers;
        }

        return allMembers.stream()
                .filter(member -> {
                    // 简化搜索：只搜索昵称和用户名称
                    return (member.getNicknameInConversation() != null &&
                            member.getNicknameInConversation().contains(keyword)) ||
                           (member.getUserName() != null &&
                            member.getUserName().contains(keyword));
                })
                .collect(Collectors.toList());
    }

    @Override
    public MemberStatisticsDTO getMemberStatistics(Long conversationId, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        MemberStatisticsDTO statistics = new MemberStatisticsDTO();
        statistics.setConversationId(conversationId);

        List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        if (CollectionUtils.isEmpty(members)) {
            statistics.calculate();
            return statistics;
        }

        // 统计各种数量
        int totalCount = 0;
        int activeCount = 0;
        int mutedCount = 0;
        int ownerCount = 0;
        int adminCount = 0;
        int memberCount = 0;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7).toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.minusDays(30).toLocalDate().atStartOfDay();

        int todayJoined = 0;
        int weekJoined = 0;
        int monthJoined = 0;
        int todayLeft = 0;
        int weekLeft = 0;
        int monthLeft = 0;

        for (ConversationMember member : members) {
            if (member.getDeleted() == 1) {
                // 统计离开的成员
                if (member.getLeaveTime() != null) {
                    if (member.getLeaveTime().isAfter(todayStart)) {
                        todayLeft++;
                    }
                    if (member.getLeaveTime().isAfter(weekStart)) {
                        weekLeft++;
                    }
                    if (member.getLeaveTime().isAfter(monthStart)) {
                        monthLeft++;
                    }
                }
                continue;
            }

            totalCount++;

            // 活跃成员（简化：没有离开的就是活跃）
            if (member.getLeaveTime() == null) {
                activeCount++;
            }

            // 静音成员
            if (member.getIsMuted() != null && member.getIsMuted() == 1) {
                mutedCount++;
            }

            // 角色统计
            if ("OWNER".equals(member.getUserRole())) {
                ownerCount++;
            } else if ("ADMIN".equals(member.getUserRole())) {
                adminCount++;
            } else {
                memberCount++;
            }

            // 加入时间统计
            if (member.getJoinTime() != null) {
                if (member.getJoinTime().isAfter(todayStart)) {
                    todayJoined++;
                }
                if (member.getJoinTime().isAfter(weekStart)) {
                    weekJoined++;
                }
                if (member.getJoinTime().isAfter(monthStart)) {
                    monthJoined++;
                }
            }
        }

        statistics.setTotalMemberCount(totalCount);
        statistics.setActiveMemberCount(activeCount);
        statistics.setMutedMemberCount(mutedCount);
        statistics.setOwnerCount(ownerCount);
        statistics.setAdminCount(adminCount);
        statistics.setMemberCount(memberCount);
        statistics.setTodayJoinedCount(todayJoined);
        statistics.setWeekJoinedCount(weekJoined);
        statistics.setMonthJoinedCount(monthJoined);
        statistics.setTodayLeftCount(todayLeft);
        statistics.setWeekLeftCount(weekLeft);
        statistics.setMonthLeftCount(monthLeft);

        statistics.calculate();
        return statistics;
    }

    @Override
    public List<ConversationMemberDTO> exportMembers(Long conversationId, Long operatorId) throws BusinessException {
        // 导出所有成员信息
        return getAllConversationMembers(conversationId, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ConversationMemberDTO> batchUpdateMembers(Long conversationId, List<Long> userIds, ConversationMemberDTO updateRequest, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        if (CollectionUtils.isEmpty(userIds)) {
            throw new BusinessException("用户ID列表不能为空");
        }

        // 检查操作权限
        if (!hasManagementPermission(conversationId, operatorId)) {
            throw new BusinessException("没有权限批量更新成员");
        }

        List<ConversationMemberDTO> results = new ArrayList<>();
        for (Long userId : userIds) {
            try {
                ConversationMember member = getActiveMember(conversationId, userId);

                // 更新字段
                if (StringUtils.hasText(updateRequest.getNicknameInConversation())) {
                    member.setNicknameInConversation(updateRequest.getNicknameInConversation());
                }
                if (updateRequest.getIsMuted() != null) {
                    member.setIsMuted(updateRequest.getIsMuted());
                }
                if (updateRequest.getUserRole() != null && !"OWNER".equals(member.getUserRole())) {
                    // 不能批量修改拥有者的角色
                    member.setUserRole(updateRequest.getUserRole());
                }

                conversationMemberMapper.updateById(member);
                results.add(convertToDTO(member));
            } catch (BusinessException e) {
                log.warn("批量更新成员 {} 失败: {}", userId, e.getMessage());
            }
        }

        return results;
    }

    @Override
    public void validateMemberAccess(Long conversationId, Long userId) throws BusinessException {
        if (!isConversationMember(conversationId, userId)) {
            throw new BusinessException("用户不是会话成员");
        }
    }

    @Override
    public String getMemberJoinTimeDesc(Long conversationId, Long userId) throws BusinessException {
        ConversationMember member = getActiveMember(conversationId, userId);
        if (member.getJoinTime() == null) {
            return "未知";
        }

        LocalDateTime now = LocalDateTime.now();
        long days = ChronoUnit.DAYS.between(member.getJoinTime(), now);

        if (days == 0) {
            return "今天加入";
        } else if (days == 1) {
            return "昨天加入";
        } else if (days < 7) {
            return days + "天前加入";
        } else if (days < 30) {
            return (days / 7) + "周前加入";
        } else {
            return (days / 30) + "月前加入";
        }
    }

    @Override
    public Double getMemberActivityLevel(Long conversationId, Long userId) throws BusinessException {
        // 简化实现：返回随机值
        // 实际应该根据消息发送频率、阅读情况等计算
        return Math.random() * 100;
    }

    @Override
    public RoleDistributionDTO getRoleDistribution(Long conversationId, Long operatorId) throws BusinessException {
        validateConversationAndAccess(conversationId, operatorId);

        RoleDistributionDTO distribution = new RoleDistributionDTO();
        distribution.setConversationId(conversationId);

        List<ConversationMember> members = conversationMemberMapper.selectByConversationId(conversationId);
        if (CollectionUtils.isEmpty(members)) {
            distribution.calculate();
            return distribution;
        }

        Map<String, RoleDistributionDTO.RoleStats> roleStatsMap = new HashMap<>();

        // 统计各角色
        for (ConversationMember member : members) {
            if (member.getDeleted() == 1 || member.getLeaveTime() != null) {
                continue;
            }

            String role = member.getUserRole();
            if (role == null) {
                role = "MEMBER";
            }

            RoleDistributionDTO.RoleStats stats = roleStatsMap.computeIfAbsent(role, k -> {
                RoleDistributionDTO.RoleStats newStats = new RoleDistributionDTO.RoleStats();
                newStats.setRoleName(k);
                newStats.setRoleDesc(getRoleDescription(k));
                newStats.setMemberCount(0);
                return newStats;
            });

            stats.setMemberCount(stats.getMemberCount() + 1);
        }

        // 计算总成员数
        int totalCount = roleStatsMap.values().stream()
                .mapToInt(RoleDistributionDTO.RoleStats::getMemberCount)
                .sum();

        distribution.setTotalMemberCount(totalCount);
        distribution.setRoleStats(roleStatsMap);
        distribution.calculate();

        return distribution;
    }

    // ============= 私有辅助方法 =============

    private void validateConversationAndAccess(Long conversationId, Long userId) throws BusinessException {
        validateConversationExists(conversationId);
        validateMemberAccess(conversationId, userId);
    }

    private void validateConversationExists(Long conversationId) throws BusinessException {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || conversation.getDeleted() == 1) {
            throw new BusinessException("会话不存在或已被删除");
        }
    }

    private ConversationMember getActiveMember(Long conversationId, Long userId) throws BusinessException {
        ConversationMember member = conversationMemberMapper.selectByConversationAndUser(conversationId, userId);
        if (member == null || member.getDeleted() == 1 || member.getLeaveTime() != null) {
            throw new BusinessException("用户不是活跃的会话成员");
        }
        return member;
    }

    private boolean isValidRole(String role) {
        return "OWNER".equals(role) || "ADMIN".equals(role) || "MEMBER".equals(role);
    }

    private String getRoleDescription(String role) {
        switch (role) {
            case "OWNER":
                return "拥有者";
            case "ADMIN":
                return "管理员";
            case "MEMBER":
                return "成员";
            default:
                return role;
        }
    }

    private ConversationMemberDTO convertToDTO(ConversationMember member) {
        if (member == null) {
            return null;
        }
        ConversationMemberDTO dto = new ConversationMemberDTO();
        BeanUtils.copyProperties(member, dto);
        dto.calculateDesc();
        return dto;
    }
}