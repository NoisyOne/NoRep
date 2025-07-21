package com.example.videospeeder;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class KeySimulator {

    public static void sendSpaceKey() {
        keyPressAndRelease(KeyEvent.VK_SPACE);
    }

    public static void sendLeftArrowKey() {
        keyPressAndRelease(KeyEvent.VK_LEFT);
    }

    public static void sendRightArrowKey() {
        keyPressAndRelease(KeyEvent.VK_RIGHT);
    }

    public static void sendSpeedUpKeys() {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ADD); // Ctrl + +
            robot.keyRelease(KeyEvent.VK_ADD);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void keyPressAndRelease(int keyCode) {
        try {
            Robot robot = new Robot();
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}