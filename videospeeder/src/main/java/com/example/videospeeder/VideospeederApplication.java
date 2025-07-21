package com.example.videospeeder;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VideospeederApplication {

    public static void main(String[] args) {
        // 启动 JavaFX UI
        VideoControlUI.main(args);
    }
}