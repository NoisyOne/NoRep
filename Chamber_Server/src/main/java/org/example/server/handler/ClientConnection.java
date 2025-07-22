package org.example.server.handler;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;
import org.example.common.model.Message;

/**
 * 客户端连接管理类
 * 负责维护客户端的Socket连接和输入/输出流
 */
public class ClientConnection {
    private static final Logger LOGGER = Logger.getLogger(ClientConnection.class.getName());

    private final Socket socket;
    private final ObjectInputStream input;
    private final ObjectOutputStream output;
    private final String userId;
    private boolean authenticated;

    public ClientConnection(Socket socket, ObjectInputStream input, ObjectOutputStream output, String userId) {
        this.socket = socket;
        this.input = input;
        this.output = output;
        this.userId = userId;
        this.authenticated = (userId != null);
    }

    /**
     * 获取Socket连接
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * 获取输入流
     */
    public ObjectInputStream getInput() {
        return input;
    }

    /**
     * 获取输出流
     */
    public ObjectOutputStream getOutput() {
        return output;
    }

    /**
     * 获取关联的用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 检查是否已认证
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * 标记为已认证
     */
    public void markAsAuthenticated(String userId) {
        this.authenticated = true;
    }

    /**
     * 发送消息给客户端
     */
    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            LOGGER.severe("Failed to send message to client: " + e.getMessage());
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.severe("Error closing connection: " + e.getMessage());
        }
    }
}
