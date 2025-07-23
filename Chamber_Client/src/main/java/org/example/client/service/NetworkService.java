package org.example.client.service;

import org.example.common.constant.ProtocolConstant;
import org.example.common.model.Message;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

/**
 * 网络服务
 * 处理与服务器的通信
 */
public class NetworkService {
    private static final Logger LOGGER = Logger.getLogger(NetworkService.class.getName());

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private final BlockingQueue<Message> responseQueue = new LinkedBlockingQueue<>();
    private final MessageReceiver messageReceiver;
    private UserService userService;

    public NetworkService(UserService userService) throws IOException {
        this.userService = userService;
        this.socket = new Socket(SERVER_HOST, SERVER_PORT);
        LOGGER.info("成功连接到服务器: " + SERVER_HOST + ":" + SERVER_PORT);
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
        this.messageReceiver = new MessageReceiver(input, responseQueue, userService);

        // 启动消息接收线程
        new Thread(messageReceiver).start();
    }

    /**
     * 发送消息并等待响应
     */
    public Message sendMessageAndWaitResponse(Message message) {
        try {
            // 发送消息
            output.writeObject(message);
            output.flush();
            LOGGER.info("已发送消息: " + message);

            // 等待响应（设置超时时间）
            Message response = responseQueue.poll(30, java.util.concurrent.TimeUnit.SECONDS);
            if (response != null) {
                LOGGER.info("收到响应: " + response); // 添加调试日志
            } else {
                LOGGER.warning("等待响应超时");
            }
            return response;
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Error sending message: " + e.getMessage());
            return null;
        }
    }

    /**
     * 发送消息
     */
    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            LOGGER.severe("Error sending message: " + e.getMessage());
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            messageReceiver.stop();
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.severe("Error closing network service: " + e.getMessage());
        }
    }

    /**
     * 获取UserService
     */
    public UserService getUserService() {
        return userService;
    }

    /**
     * 设置UserService
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 消息接收器
     */
    private static class MessageReceiver implements Runnable {
        private final ObjectInputStream input;
        private final BlockingQueue<Message> responseQueue;
        private UserService userService;
        private volatile boolean running = true;

        public MessageReceiver(ObjectInputStream input, BlockingQueue<Message> responseQueue, UserService userService) {
            this.input = input;
            this.responseQueue = responseQueue;
            this.userService = userService;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    Object object = input.readObject();
                    if (object instanceof Message) {
                        Message message = (Message) object;
                        handleMessage(message);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    LOGGER.severe("Error receiving message: " + e.getMessage());
                }
            }
        }

        /**
         * 处理收到的消息
         */
        private void handleMessage(Message message) {
            LOGGER.info("收到响应类型: " + message.getType() + ", 预期成功类型: " + ProtocolConstant.LOGIN_SUCCESS);
            // 处理通用消息
            switch (message.getType()) {
                case ProtocolConstant.LOGIN_SUCCESS:
                    handleLoginSuccess(message);
                    responseQueue.offer(message);
                    break;
                case ProtocolConstant.LOGIN_FAILURE:
                    handleLoginFailure(message);
                    break;
                case ProtocolConstant.REGISTER_SUCCESS:
                    handleRegisterSuccess(message);
                    break;
                case ProtocolConstant.REGISTER_FAILURE:
                    handleRegisterFailure(message);
                    break;
                // 其他消息类型处理...
                default:
                    LOGGER.info("收到非响应消息，类型：" + message.getType());
            }
        }

        /**
         * 处理登录成功消息
         */
        private void handleLoginSuccess(Message message) {
            // 登录成功后发送给UserService处理
            if (userService != null) {
                userService.handleLoginSuccess(message);
            }
        }

        /**
         * 处理登录失败消息
         */
        private void handleLoginFailure(Message message) {
            // 登录失败后发送给UserService处理
            if (userService != null) {
                userService.handleLoginFailure(message);
            }
        }

        /**
         * 处理注册成功消息
         */
        private void handleRegisterSuccess(Message message) {
            // 注册成功后发送给UserService处理
            if (userService != null) {
                userService.handleRegisterSuccess(message);
            }
        }

        /**
         * 处理注册失败消息
         */
        private void handleRegisterFailure(Message message) {
            // 注册失败后发送给UserService处理
            if (userService != null) {
                userService.handleRegisterFailure(message);
            }
        }

        /**
         * 停止消息接收
         */
        public void stop() {
            running = false;
        }
    }

    /**
     * 添加消息处理回调
     */

}
