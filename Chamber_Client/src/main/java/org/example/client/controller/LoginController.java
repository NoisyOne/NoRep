package org.example.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.client.App;
import org.example.common.model.User;
import org.example.client.service.UserService;
import org.example.client.service.NetworkService;

import java.io.IOException;

public class LoginController {

    public BorderPane rootContainer;
    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    private double xOffset = 0;
    private double yOffset = 0;

    // 网络服务和用户服务
    private NetworkService networkService;
    private UserService userService;

    public LoginController() {
        try {
            // 初始化网络服务和用户服务
            networkService = new NetworkService(null);
            networkService = new NetworkService(userService); // 这里需要传递UserService，暂时用null
            userService = new UserService(networkService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // 假设你的主容器是 BorderPane 或 VBox 根节点，并有 fx:id="rootContainer"
        rootContainer.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        rootContainer.setOnMouseDragged(event -> {
            Stage stage = App.getPrimaryStage();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    // 最小化窗口
    public void minimizeWindow(ActionEvent event) {
        App.getPrimaryStage().setIconified(true);
    }

    // 关闭窗口
    public void closeWindow(ActionEvent event) {
        App.getPrimaryStage().close();
    }

    public void handleLogin(ActionEvent event) {
        String phone = phoneField.getText();
        String password = passwordField.getText();

        if (phone == null || phone.isEmpty() || password == null || password.isEmpty()) {
            // 显示错误消息
            System.out.println("手机号或密码不能为空");
            return;
        }

        // 执行登录
        if (userService.login(phone, password)) {
            // 登录成功，跳转到主界面
            try {
                App.openMain();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 显示登录失败消息
            System.out.println("登录失败，请检查手机号和密码");
        }
    }

    public void handleRegister(ActionEvent event) {
        try {
            App.openRegister();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
