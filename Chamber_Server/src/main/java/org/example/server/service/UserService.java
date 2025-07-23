package org.example.server.service;

import org.example.server.repository.UserRepository;
import org.example.common.model.User;

import java.util.List;

/**
 * 用户服务
 * 处理与用户相关的业务逻辑
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 用户认证
     */
    public String authenticate(String phone, String password) {
        // 在实际应用中，这里应该验证密码哈希而不是明文密码
        return userRepository.getUserIdByPhoneAndPassword(phone, password);
    }

    /**
     * 用户注册
     */
    public String register(String phone, String password, String nickname) {
        // 在实际应用中，这里应该存储密码哈希而不是明文密码
        return userRepository.createUser(phone, password, nickname);
    }

    /**
     * 获取用户信息
     */
    public User getUserById(String userId) {
        return userRepository.getUserById(userId);
    }

    /**
     * 更新用户状态
     */
    public boolean updateOnlineStatus(String userId, boolean isOnline, boolean isLogin) {
        return userRepository.updateOnlineStatus(userId, isOnline, isLogin);
    }

    public List<User> getOnlineUsers() {
        return userRepository.getOnlineUsers();
    }

    /**
     * 更新用户资料
     */
    public boolean updateProfile(String userId, String nickname, String avatar, String signature) {
        return userRepository.updateUserProfile(userId, nickname, avatar, signature);
    }
}
