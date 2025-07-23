package org.example.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import org.example.client.App;
import org.example.client.service.UserService;
import org.example.client.service.NetworkService;
import org.example.common.constant.ProtocolConstant;
import org.example.common.model.Message;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

public class RegisterController {
    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField nicknameField;

    // 网络服务和用户服务
    private NetworkService networkService;
    private UserService userService;


    public RegisterController() {
        try {
            // 初始化网络服务和用户服务
            networkService = new NetworkService(null); // 这里需要传递UserService，暂时用null
            userService = new UserService(networkService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitRegister(ActionEvent event) {
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String nickname = nicknameField.getText();

        if (phone == null || phone.isEmpty() ||
                password == null || password.isEmpty() ||
                nickname == null || nickname.isEmpty()) {
            // 显示错误消息
            System.out.println("所有字段都是必填的");
            return;
        }
        LOGGER.info("开始注册: phone=" + phone + ", nickname=" + nickname);
        // 执行注册
        userService.register(phone, password, nickname);
    }


    public void backToLogin(ActionEvent event) {
        try {
            App.openLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
