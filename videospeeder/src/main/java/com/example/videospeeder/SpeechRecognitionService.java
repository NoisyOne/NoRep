package com.example.videospeeder;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.SpeakerModel;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SpeechRecognitionService {

    private Model model;
    private SpeakerModel speakerModel;
    private boolean isRunning = false;
    private boolean isModelLoaded = true;

    public SpeechRecognitionService() {
    // 初始化模型路径（需下载 vosk 中文模型）
    String modelPath = "D:/Intern/videospeeder/src/main/resources/vosk-model-small-cn-0.22";
    try {
        model = new Model(modelPath);
        speakerModel = null;
    } catch (IOException e) {
        System.err.println("Failed to load Vosk models: " + e.getMessage());
        e.printStackTrace();
        // Optionally disable speech recognition or notify the user
        model = null;
        speakerModel = null;
        isModelLoaded = false;
    }
}

    public void start() {
        if (isRunning) return;
        isRunning = true;

        new Thread(this::recognizeAudio).start();
    }

    public void stop() {
        isRunning = false;
    }

    private void recognizeAudio() {
    try {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];

        Recognizer recognizer = new Recognizer(model, 16000, speakerModel);
        int audioBlockCount = 0;

        while (isRunning) {
            int bytesRead = line.read(buffer, 0, buffer.length);
            if (bytesRead >= 0) {
                out.write(buffer, 0, bytesRead);

                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    String result = recognizer.getResult();
                    System.out.println("识别结果: " + result);
                    processCommand(result);
                }

                // 调试输出：显示音频块读取情况
                System.out.print(".");
                audioBlockCount++;
                if (audioBlockCount % 50 == 0) {
                    System.out.println("\n[DEBUG] 已读取音频块: " + audioBlockCount);
                }
            }
        }

        line.stop();
        line.close();
    } catch (LineUnavailableException e) {
        e.printStackTrace();
    }
}

    private void processCommand(String result) {
    System.out.println("[DEBUG] 收到语音指令: " + result);

    if (result.contains("快进")) {
        System.out.println("[ACTION] 正在执行：快进");
        VideoPlayerController.fastForward();
    } else if (result.contains("回退")) {
        System.out.println("[ACTION] 正在执行：回退");
        VideoPlayerController.rewind();
    } else if (result.contains("倍速播放")) {
        System.out.println("[ACTION] 正在执行：倍速播放");
        VideoPlayerController.increaseSpeed();
    } else if (result.contains("暂停")) {
        System.out.println("[ACTION] 正在执行：暂停");
        VideoPlayerController.pause();
    } else {
        System.out.println("[INFO] 未匹配到有效指令");
    }
}
}