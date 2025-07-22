package org.example.server.cache;

import org.example.common.model.User;
import org.example.server.handler.ClientConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserCache {
    // 单例实例
    private static final UserCache INSTANCE = new UserCache();

    // 在线用户缓存：userId -> User
    private final Map<String, User> onlineUsers = new ConcurrentHashMap<>();
    // 用户Socket连接缓存：userId -> Socket连接
    private final Map<String, ClientConnection> connections = new ConcurrentHashMap<>();

    private UserCache() {
        // 私有构造函数
    }

    public static UserCache getInstance() {
        return INSTANCE;
    }

    /**
     * 添加在线用户
     */
    public void addOnlineUser(User user, ClientConnection connection) {
        if (user != null && connection != null) {
            onlineUsers.put(user.getUserId(), user);
            connections.put(user.getUserId(), connection);
            // 更新数据库中的在线状态
            updateUserOnlineStatusInDB(user.getUserId(), true);
        }
    }

    /**
     * 移除在线用户
     */
    public void removeOnlineUser(String userId) {
        if (userId != null) {
            onlineUsers.remove(userId);
            connections.remove(userId);
            // 更新数据库中的在线状态
            updateUserOnlineStatusInDB(userId, false);
        }
    }

    /**
     * 获取在线用户
     */
    public User getOnlineUser(String userId) {
        return onlineUsers.get(userId);
    }

    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(String userId) {
        return onlineUsers.containsKey(userId);
    }

    /**
     * 获取所有在线用户
     */
    public Map<String, User> getAllOnlineUsers() {
        return new HashMap<>(onlineUsers);
    }

    /**
     * 获取用户连接
     */
    public ClientConnection getUserConnection(String userId) {
        return connections.get(userId);
    }

    /**
     * 更新数据库中的在线状态
     */
    private void updateUserOnlineStatusInDB(String userId, boolean isOnline) {
        // 这里应该调用UserRepository来更新数据库中的在线状态
        // 示例代码，实际应通过服务层更新
        System.out.println("User " + userId + " is now " + (isOnline ? "online" : "offline"));
    }

    /**
     * 加载已存在的在线用户状态
     */
    public void loadOnlineUsers() {
        // 从数据库加载当前在线用户
        // 实际实现应查询数据库中is_online = true的用户
        System.out.println("Loading online users from database...");
    }

    public void clearAllUsers() {
    }
}
