package org.example.server.service;

import org.example.server.model.Group;
import org.example.server.repository.GroupRepository;

import java.util.List;

/**
 * 群组服务
 * 处理与群组相关的业务逻辑
 */
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * 创建群组
     */
    public String createGroup(String ownerId, String name, String description) {
        return groupRepository.createGroup(ownerId, name, description);
    }

    /**
     * 解散群组
     */
    public boolean disbandGroup(String groupId, String ownerId) {
        return groupRepository.disbandGroup(groupId, ownerId);
    }

    /**
     * 获取用户群组列表
     */
    public List<Group> getUserGroups(String userId) {
        return groupRepository.getUserGroups(userId);
    }

    /**
     * 添加成员到群组
     */
    public boolean addMember(String groupId, String memberId, String inviterId) {
        return groupRepository.addMember(groupId, memberId, inviterId);
    }

    /**
     * 从群组移除成员
     */
    public boolean removeMember(String groupId, String memberId, String removerId) {
        return groupRepository.removeMember(groupId, memberId, removerId);
    }

    /**
     * 获取群组成员
     */
    public List<String> getGroupMembers(String groupId) {
        return groupRepository.getGroupMembers(groupId);
    }

    /**
     * 更新群组信息
     */
    public boolean updateGroupInfo(String groupId, String name, String description, String updaterId) {
        return groupRepository.updateGroupInfo(groupId, name, description, updaterId);
    }
}
