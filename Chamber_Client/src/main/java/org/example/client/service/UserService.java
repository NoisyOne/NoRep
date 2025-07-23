package org.example.client.service;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.example.common.constant.ProtocolConstant;
import org.example.common.model.Message;
import org.example.common.model.User;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

/**
 * 用户服务
 * 处理与用户相关的业务逻辑
 */
public class UserService {
    private final NetworkService networkService;
    private User currentUser;

    public UserService(NetworkService networkService) {
        this.networkService = networkService;
        // 设置网络服务的UserService
        if (networkService != null && networkService.getUserService() != this) {
            networkService.setUserService(this);
        }
    }

    /**
     * 登录
     */
    public boolean login(String phone, String password) {
        // 创建登录消息
        Message loginMessage = new Message(
                ProtocolConstant.CLIENT_USER_ID,
                ProtocolConstant.SERVER_USER_ID,
                ProtocolConstant.LOGIN_REQUEST,
                String.format("%s,%s", phone, password)
        );

        // 发送登录请求
        Message response = networkService.sendMessageAndWaitResponse(loginMessage);
        LOGGER.info("登录响应详情: " + (response != null ?
                "类型=" + response.getType() + ", 内容=" + response.getContent() : "无响应"));
        if (response != null && response.getType() == ProtocolConstant.LOGIN_SUCCESS) {
                currentUser = new User();
                return true;
        }
        return false;
    }

    /**
     * 注册
     */
    public boolean register(String phone, String password, String nickname) {
        // 创建注册消息
        Message registerMessage = new Message(
                ProtocolConstant.CLIENT_USER_ID,
                ProtocolConstant.SERVER_USER_ID,
                ProtocolConstant.REGISTER_REQUEST,
                String.format("%s,%s,%s", phone, password, nickname)
        );
        LOGGER.info("准备发送注册请求: " + registerMessage);
        // 发送注册请求
        Message response = networkService.sendMessageAndWaitResponse(registerMessage);
        if (response != null) {
            LOGGER.info("收到注册响应: " + response.getType() + " - " + response.getContent()); // 添加调试日志
        }
        return response != null && response.getType() == ProtocolConstant.REGISTER_SUCCESS;
    }

    /**
     * 处理登录成功消息
     */
    public void handleLoginSuccess(Message message) {
        // 在JavaFX线程中处理UI更新
        Platform.runLater(() -> {
            // 解析用户信息
            String[] userInfo = message.getContent().split(",");
            if (userInfo.length >= 2) {
                currentUser = new User();

                // 跳转到主界面
                try {
                    org.example.client.App.openMain();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 处理登录失败消息
     */
    public void handleLoginFailure(Message message) {
        // 在JavaFX线程中处理UI更新
        Platform.runLater(() -> {
            // 显示登录失败消息
            System.out.println("登录失败: " + message.getContent());
            // 这里可以显示一个错误对话框
        });
    }

    /**
     * 处理注册成功消息
     */
    public void handleRegisterSuccess(Message message) {
        // 在JavaFX线程中处理UI更新
        Platform.runLater(() -> {
            // 显示注册成功消息
            System.out.println("注册成功: " + message.getContent());

            // 跳转到登录界面
            try {
                org.example.client.App.openLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 处理注册失败消息
     */
    public void handleRegisterFailure(Message message) {
        // 在JavaFX线程中处理UI更新
        Platform.runLater(() -> {
            // 显示注册失败消息
            System.out.println("注册失败: " + message.getContent());
            // 这里可以显示一个错误对话框
        });
    }

    /**
     * 获取当前用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
