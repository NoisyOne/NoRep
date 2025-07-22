package org.example.server.handler;

import org.example.common.constant.ProtocolConstant;
import org.example.common.model.Message;
import org.example.server.cache.UserCache;
import org.example.server.service.UserService;
import org.example.common.model.User;


import java.io.IOException;
import java.util.logging.Logger;

/**
 * 认证处理器
 * 处理用户登录、注册等认证相关操作
 */
public class AuthHandler {
    private static final Logger LOGGER = Logger.getLogger(AuthHandler.class.getName());

    private final UserService userService;
    private final UserCache userCache;

    public AuthHandler(UserService userService, UserCache userCache) {
        this.userService = userService;
        this.userCache = userCache;
    }

    /**
     * 处理登录请求
     */
    public boolean handleLogin(Message loginMessage, ClientConnection connection) throws IOException {
        // 解析登录信息（JSON格式示例：{"phone":"1234567890","password":"xxxx"}
        String[] credentials = parseCredentials(loginMessage.getContent());

        if (credentials == null || credentials.length < 2) {
            sendLoginFailure("Invalid credentials format", connection);
            return false;
        }

        String phone = credentials[0];
        String password = credentials[1];

        // 验证用户
        String userId = userService.authenticate(phone, password);

        if (userId != null) {
            // 登录成功
            connection.markAsAuthenticated(userId);

            User user = userService.getUserById(userId);

            if (user == null) {
                sendLoginFailure("Failed to load user details", connection);
                connection.close();
                return false;
            }

            // 更新用户在线状态
            userCache.addOnlineUser(user, connection);

            // 发送登录成功消息
            Message successMessage = new Message(
                    ProtocolConstant.SERVER_USER_ID,
                    userId,
                    ProtocolConstant.LOGIN_SUCCESS,
                    "Login successful"
            );
            connection.sendMessage(successMessage);

            // 发送当前在线用户列表
            sendOnlineUsersList(userId, connection);

            return true;
        } else {
            // 登录失败
            sendLoginFailure("Invalid phone or password", connection);
            return false;
        }
    }

    /**
     * 处理注册请求
     */
    public boolean handleRegister(Message registerMessage, ClientConnection connection) throws IOException {
        // 解析注册信息（JSON格式示例：{"phone":"1234567890","password":"xxxx","nickname":"xxx"}
        String[] registrationData = parseCredentials(registerMessage.getContent());

        if (registrationData == null || registrationData.length < 3) {
            sendRegisterFailure("Invalid registration data format", connection);
            return false;
        }

        String phone = registrationData[0];
        String password = registrationData[1];
        String nickname = registrationData[2];

        // 注册新用户
        String userId = userService.register(phone, password, nickname);

        if (userId != null) {
            // 注册成功
            connection.markAsAuthenticated(userId);

            // 获取完整用户信息
            User user = userService.getUserById(userId);

            if (user == null) {
                sendRegisterFailure("Failed to load user details", connection);
                connection.close();
                return false;
            }

            // 更新用户在线状态
            userCache.addOnlineUser(user, connection);

            // 发送注册成功消息
            Message successMessage = new Message(
                    ProtocolConstant.SERVER_USER_ID,
                    userId,
                    ProtocolConstant.REGISTER_SUCCESS,
                    "Registration successful"
            );
            connection.sendMessage(successMessage);

            // 发送当前在线用户列表
            sendOnlineUsersList(userId, connection);

            return true;
        } else {
            // 注册失败
            sendRegisterFailure("Phone number already exists", connection);
            return false;
        }
    }

    /**
     * 解析凭证数据
     */
    private String[] parseCredentials(String credentialsJson) {
        // 简单的解析方法（实际应使用JSON解析库）
        // 假设格式是简单的键值对字符串
        // 示例："phone=123,password=456,nickname=test"
        if (credentialsJson == null || credentialsJson.isEmpty()) {
            return null;
        }

        return credentialsJson.split(",");
    }

    /**
     * 发送登录失败消息
     */
    private void sendLoginFailure(String reason, ClientConnection connection) throws IOException {
        Message failureMessage = new Message(
                ProtocolConstant.SERVER_USER_ID,
                connection.getUserId(),
                ProtocolConstant.LOGIN_FAILURE,
                reason
        );
        connection.sendMessage(failureMessage);
    }

    /**
     * 发送注册失败消息
     */
    private void sendRegisterFailure(String reason, ClientConnection connection) throws IOException {
        Message failureMessage = new Message(
                ProtocolConstant.SERVER_USER_ID,
                connection.getUserId(),
                ProtocolConstant.REGISTER_FAILURE,
                reason
        );
        connection.sendMessage(failureMessage);
    }

    /**
     * 发送在线用户列表
     */
    private void sendOnlineUsersList(String userId, ClientConnection connection) {
        // 实际实现应从UserCache获取在线用户列表
        StringBuilder userListBuilder = new StringBuilder();
        for (String onlineUserId : userCache.getAllOnlineUsers().keySet()) {
            if (!onlineUserId.equals(userId)) { // 不包含自己
                userListBuilder.append(onlineUserId).append(",");
            }
        }

        if (userListBuilder.length() > 0) {
            userListBuilder.deleteCharAt(userListBuilder.length() - 1); // 移除最后一个逗号

            Message userListMessage = new Message(
                    ProtocolConstant.SERVER_USER_ID,
                    userId,
                    ProtocolConstant.ONLINE_USERS_LIST,
                    userListBuilder.toString()
            );
            connection.sendMessage(userListMessage);
        }
    }
}
