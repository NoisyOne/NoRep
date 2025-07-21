package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainViewController {

    public Button sendButton;
    public TextArea textArea;
    public ScrollPane chatFrame;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Pane titleBar; // 新增：用于拖动的面板

    @FXML
    private Button minimizeButton; // 对应最小化按钮（—）
    @FXML
    private Button closeButton;     // 对应关闭按钮（╳）

    private Stage stage;

    // FXML 加载完成后绑定事件
    @FXML
    public void initialize() {
        if (closeButton == null || minimizeButton == null || titleBar == null) {
            System.err.println("Error: Required elements not initialized, check FXML ids.");
            return;
        }

        // 绑定最小化和关闭按钮事件
        minimizeButton.setOnAction(event -> {
            Stage stage = getStageFromEvent(event);
            if (stage != null) stage.setIconified(true);
        });

        closeButton.setOnAction(event -> {
            Stage stage = getStageFromEvent(event);
            if (stage != null) stage.close();
        });

        // 绑定拖动事件到整个 titleBar 区域
        titleBar.setOnMousePressed(this::handleMousePressed);
        titleBar.setOnMouseDragged(this::handleMouseDragged);
    }

    // 从事件中安全获取 Stage
    private Stage getStageFromEvent(javafx.event.Event event) {
        return (Stage) ((Button) event.getSource()).getScene().getWindow();
    }


    // 鼠标按下记录偏移量
    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    // 鼠标拖动实现窗口移动
    private void handleMouseDragged(MouseEvent event) {
        Pane sourcePane = (Pane) event.getSource();
        Stage stage = (Stage) sourcePane.getScene().getWindow();

        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }

}
