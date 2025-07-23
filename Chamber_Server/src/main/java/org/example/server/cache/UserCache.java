package org.example.server.cache;

import org.example.common.model.User;
import org.example.server.handler.ClientConnection;
import org.example.server.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

public class UserCache {
    private static final Logger LOGGER = Logger.getLogger(UserCache.class.getName()); // 初始化日志
    private final UserService userService;
    private final Map<String, User> onlineUsers = new ConcurrentHashMap<>();
    private final Map<String, ClientConnection> connections = new ConcurrentHashMap<>();

    // 构造函数注入 UserService
    public UserCache(UserService userService) {
        this.userService = userService;
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
        // 区分登录（true）和登出（false）操作
        boolean result = userService.updateOnlineStatus(userId, isOnline, isOnline);

        if (result) {
            if (isOnline) {
                LOGGER.info("用户 " + userId + " 登录成功，在线状态已更新");
            } else {
                LOGGER.info("用户 " + userId + " 已下线，在线状态已更新");
            }
        } else {
            LOGGER.warning("用户 " + userId + " 在线状态更新失败");
        }
    }

    /**
     * 加载已存在的在线用户状态
     */
    public void loadOnlineUsers() {
        System.out.println("Loading online users from database...");
        // 调用 UserService 获取所有在线用户（需在 UserService 中添加对应方法）
        List<User> onlineUsers = userService.getOnlineUsers();
        for (User user : onlineUsers) {
            // 注意：此时无客户端连接，仅加载用户信息到 onlineUsers 缓存（连接为 null）
            this.onlineUsers.put(user.getUserId(), user);
            // 连接信息可暂不加载，因为用户实际连接会在登录时更新
        }
    }

    public void clearAllUsers() {
    }
}
