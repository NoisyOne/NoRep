package com.example.videospeeder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VideoControlUI extends Application {

    private SpeechRecognitionService speechService;

    @Override
    public void start(Stage primaryStage) {
        speechService = new SpeechRecognitionService();

        Button startButton = new Button("启动");
        Button stopButton = new Button("关闭");

        startButton.setOnAction(e -> speechService.start());
        stopButton.setOnAction(e -> speechService.stop());

        VBox root = new VBox(10, startButton, stopButton);
        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("视频语音控制器");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}