package org.example.server.handler;

import org.example.server.constant.ProtocolConstant;
import org.example.server.model.Message;
import org.example.server.cache.UserCache;
import org.example.server.service.UserService;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    private final Socket socket;
    private final UserCache userCache;
    private final UserService userService;
    private final AuthHandler authHandler;
    private final MessageHandler messageHandler;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ClientConnection connection;
    private String clientAddress; // 缓存客户端地址

    public ClientHandler(Socket socket, UserCache userCache, UserService userService) {
        this.socket = socket;
        this.userCache = userCache;
        this.userService = userService;
        this.authHandler = new AuthHandler(userService, userCache);
        this.messageHandler = new MessageHandler(userCache);
        this.clientAddress = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try {
            initializeStreams();
            createConnection();
            sendWelcomeMessage();

            if (authenticate()) {
                processMessages();
            }
        } catch (SocketException e) {
            LOGGER.info("Client disconnected unexpectedly: " + clientAddress);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "IO error handling client " + clientAddress, e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Invalid message format from client " + clientAddress, e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error handling client " + clientAddress, e);
        } finally {
            closeConnection();
        }
    }

    /**
     * 初始化输入输出流
     */
    private void initializeStreams() throws IOException {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush(); // 确保头信息被发送
        input = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * 创建客户端连接对象
     */
    private void createConnection() {
        connection = new ClientConnection(socket, input, output, null);
    }

    /**
     * 认证流程
     */
    private boolean authenticate() throws IOException, ClassNotFoundException {
        Message message;
        int authAttempts = 0;
        final int MAX_AUTH_ATTEMPTS = 3;

        while (authAttempts < MAX_AUTH_ATTEMPTS && (message = readMessage()) != null) {
            authAttempts++;

            if (message.getType() == ProtocolConstant.LOGIN_REQUEST) {
                if (authHandler.handleLogin(message, connection)) {
                    return true;
                }
            } else if (message.getType() == ProtocolConstant.REGISTER_REQUEST) {
                if (authHandler.handleRegister(message, connection)) {
                    return true;
                }
            } else {
                sendUnexpectedMessage();
            }
        }

        if (authAttempts >= MAX_AUTH_ATTEMPTS) {
            sendAuthFailureMessage();
        }
        return false;
    }

    /**
     * 发送欢迎消息
     */
    private void sendWelcomeMessage() {
        try {
            Message welcomeMessage = new Message(
                    ProtocolConstant.SERVER_USER_ID,
                    null,
                    ProtocolConstant.WELCOME_MESSAGE,
                    ProtocolConstant.WELCOME_MESSAGE_CONTENT
            );
            output.writeObject(welcomeMessage);
            output.flush();
        } catch (IOException e) {
            LOGGER.warning("Failed to send welcome message to " + clientAddress);
        }
    }

    /**
     * 发送认证失败消息
     */
    private void sendAuthFailureMessage() {
        try {
            Message authFailureMessage = new Message(
                    ProtocolConstant.SERVER_USER_ID,
                    null,
                    ProtocolConstant.AUTH_FAILURE,
                    "Too many authentication attempts"
            );
            output.writeObject(authFailureMessage);
            output.flush();
        } catch (IOException e) {
            LOGGER.warning("Failed to send auth failure message to " + clientAddress);
        }
    }

    /**
     * 发送意外消息响应
     */
    private void sendUnexpectedMessage() {
        if (connection != null) {
            try {
                Message unexpectedMessage = new Message(
                        ProtocolConstant.SERVER_USER_ID,
                        connection.getUserId(),
                        ProtocolConstant.UNEXPECTED_MESSAGE,
                        "Expected login or registration request"
                );
                connection.sendMessage(unexpectedMessage);
            } catch (Exception e) {
                LOGGER.warning("Failed to send unexpected message response to " + clientAddress);
            }
        }
    }

    /**
     * 读取客户端发送的消息
     */
    private Message readMessage() throws IOException, ClassNotFoundException {
        try {
            Object object = input.readObject();
            if (object instanceof Message) {
                return (Message) object;
            }
            LOGGER.warning("Received non-Message object from " + clientAddress);
            return null;
        } catch (SocketException e) {
            // 客户端断开连接，这是正常情况
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error reading message from " + clientAddress, e);
            throw e;
        }
    }

    /**
     * 处理客户端发送的消息
     */
    private void processMessages() throws IOException, ClassNotFoundException {
        Message message;
        while ((message = readMessage()) != null) {
            try {
                messageHandler.handleMessage(message, connection);

                if (message.getType() == ProtocolConstant.LOGOUT_REQUEST) {
                    LOGGER.info("User logged out: " + connection.getUserId());
                    break;
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error processing message from " + clientAddress, e);
                // 继续处理其他消息，不因单个消息错误而断开连接
            }
        }
    }

    /**
     * 关闭连接
     */
    private void closeConnection() {
        if (connection != null) {
            String userId = connection.getUserId();
            if (userId != null) {
                userCache.removeOnlineUser(userId);
                LOGGER.info("User disconnected: " + userId);
            }
        }

        closeQuietly(input);
        closeQuietly(output);
        closeQuietly(socket);

        LOGGER.info("Client connection closed: " + clientAddress);
    }

    /**
     * 安静地关闭资源
     */
    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // 静默处理关闭异常
            }
        }
    }
}
