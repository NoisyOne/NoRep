package org.example.server.service;

import org.example.server.model.Friend;
import org.example.server.repository.FriendRepository;

import java.util.List;

/**
 * 好友服务
 * 处理与好友相关的业务逻辑
 */
public class FriendService {
    private final FriendRepository friendRepository;

    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    /**
     * 添加好友
     */
    public boolean addFriend(String userId, String friendId, String groupName) {
        return friendRepository.addFriend(userId, friendId, groupName);
    }

    /**
     * 移除好友
     */
    public boolean removeFriend(String userId, String friendId) {
        return friendRepository.removeFriend(userId, friendId);
    }

    /**
     * 获取好友列表
     */
    public List<Friend> getFriends(String userId) {
        return friendRepository.getFriends(userId);
    }

    /**
     * 更新好友分组
     */
    public boolean updateFriendGroup(String userId, String friendId, String newGroupName) {
        return friendRepository.updateFriendGroup(userId, friendId, newGroupName);
    }

    /**
     * 搜索好友
     */
    public List<Friend> searchFriends(String query) {
        return friendRepository.searchFriends(query);
    }
}
