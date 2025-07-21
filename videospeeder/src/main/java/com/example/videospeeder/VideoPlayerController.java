package com.example.videospeeder;

public class VideoPlayerController {

    public static void fastForward() {
        System.out.println("执行：快进");
        // 调用播放器 API 或系统命令实现快进
        KeySimulator.sendRightArrowKey();
    }

    public static void rewind() {
        System.out.println("执行：回退");
        // 模拟按下“左箭头”键
        KeySimulator.sendLeftArrowKey();
    }

    public static void increaseSpeed() {
        System.out.println("执行：倍速播放");
        // 模拟按下“Ctrl + >”组合键
        KeySimulator.sendSpeedUpKeys();
    }

    public static void pause() {
        System.out.println("执行：暂停");
        KeySimulator.sendSpaceKey(); // 暂停/播放切换
    }
}