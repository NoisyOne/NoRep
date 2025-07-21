package org.example.server.handler;

import org.example.server.model.Message;
import org.example.server.cache.UserCache;
import org.example.server.constant.ProtocolConstant;

import java.util.logging.Logger;

/**
 * 消息处理器
 * 处理不同类型的消息并执行相应的业务逻辑
 */
public class MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MessageHandler.class.getName());

    private final UserCache userCache;

    public MessageHandler(UserCache userCache) {
        this.userCache = userCache;
    }

    /**
     * 处理接收到的消息
     */
    public void handleMessage(Message message, ClientConnection connection) {
        if (message == null || !connection.isAuthenticated()) {
            return; // 忽略无效消息或未认证的连接
        }

        switch (message.getType()) {
            case ProtocolConstant.PRIVATE_MESSAGE:
                handlePrivateMessage(message, connection);
                break;
            case ProtocolConstant.GROUP_MESSAGE:
                handleGroupMessage(message, connection);
                break;
            case ProtocolConstant.FRIEND_REQUEST:
                handleFriendRequest(message, connection);
                break;
            case ProtocolConstant.ONLINE_STATUS_REQUEST:
                handleOnlineStatusRequest(message, connection);
                break;
            case ProtocolConstant.LOGOUT_REQUEST:
                handleLogoutRequest(connection);
                break;
            default:
                sendUnknownMessageType(message, connection);
        }
    }

    /**
     * 处理私聊消息
     */
    private void handlePrivateMessage(Message message, ClientConnection connection) {
        String senderId = connection.getUserId();
        String receiverId = message.getReceiverId();

        // 确保消息发送者正确
        message.setSenderId(senderId);

        if (userCache.isUserOnline(receiverId)) {
            // 接收者在线，直接转发消息
            ClientConnection receiverConnection = userCache.getUserConnection(receiverId);
            receiverConnection.sendMessage(message);

            // 发送送达回执
            sendDeliveryReceipt(message.getMessageId(), receiverId, connection);
        } else {
            // 接收者不在线，保存为离线消息
            saveOfflineMessage(message);

            // 发送离线消息通知
            sendOfflineMessageNotification(receiverId, connection);
        }
    }

    /**
     * 处理群组消息
     */
    private void handleGroupMessage(Message message, ClientConnection connection) {
        String senderId = connection.getUserId();
        String groupId = message.getGroupId();

        // 确保消息发送者正确
        message.setSenderId(senderId);

        // 获取群组成员列表（实际应调用GroupService获取）
        String[] members = getGroupMembers(groupId);

        for (String memberId : members) {
            if (!memberId.equals(senderId)) { // 不发给自己
                if (userCache.isUserOnline(memberId)) {
                    ClientConnection memberConnection = userCache.getUserConnection(memberId);
                    memberConnection.sendMessage(message);
                } else {
                    // 保存离线消息
                    saveOfflineMessage(message);
                }
            }
        }
    }

    /**
     * 处理好友请求
     */
    private void handleFriendRequest(Message message, ClientConnection connection) {
        String senderId = connection.getUserId();
        String targetUserId = message.getReceiverId();

        // 确保消息发送者正确
        message.setSenderId(senderId);

        if (userCache.isUserOnline(targetUserId)) {
            // 目标用户在线，转发请求
            ClientConnection targetConnection = userCache.getUserConnection(targetUserId);
            targetConnection.sendMessage(message);
        } else {
            // 目标用户不在线，可以保存请求或返回错误
            sendOfflineMessageNotification(targetUserId, connection);
        }
    }

    /**
     * 处理在线状态请求
     */
    private void handleOnlineStatusRequest(Message message, ClientConnection connection) {
        String userIdToCheck = message.getContent(); // 请求中包含要查询的用户ID

        boolean isOnline = userCache.isUserOnline(userIdToCheck);

        Message statusMessage = new Message(
                ProtocolConstant.SERVER_USER_ID,
                connection.getUserId(),
                ProtocolConstant.USER_ONLINE_STATUS,
                Boolean.toString(isOnline)
        );
        statusMessage.setReceiverId(message.getSenderId());

        connection.sendMessage(statusMessage);
    }

    /**
     * 处理登出请求
     */
    private void handleLogoutRequest(ClientConnection connection) {
        connection.markAsAuthenticated(null); // 标记为未认证
    }

    /**
     * 发送未知消息类型响应
     */
    private void sendUnknownMessageType(Message message, ClientConnection connection) {
        Message response = new Message(
                ProtocolConstant.SERVER_USER_ID,
                connection.getUserId(),
                ProtocolConstant.UNKNOWN_MESSAGE_TYPE,
                "Unknown message type: " + message.getType()
        );
        connection.sendMessage(response);
    }

    /**
     * 发送送达回执
     */
    private void sendDeliveryReceipt(String messageId, String receiverId, ClientConnection connection) {
        Message receipt = new Message(
                ProtocolConstant.SERVER_USER_ID,
                receiverId,
                ProtocolConstant.DELIVERY_RECEIPT,
                messageId
        );
        connection.sendMessage(receipt);
    }

    /**
     * 发送离线消息通知
     */
    private void sendOfflineMessageNotification(String userId, ClientConnection connection) {
        Message notification = new Message(
                ProtocolConstant.SERVER_USER_ID,
                connection.getUserId(),
                ProtocolConstant.OFFLINE_MESSAGE_NOTIFICATION,
                "User " + userId + " is offline"
        );
        connection.sendMessage(notification);
    }

    /**
     * 保存离线消息
     */
    private void saveOfflineMessage(Message message) {
        // 实际实现应调用MessageService保存到数据库
        System.out.println("Saving offline message: " + message.getMessageId());
    }

    /**
     * 获取群组成员（示例方法）
     */
    private String[] getGroupMembers(String groupId) {
        // 实际实现应调用GroupService获取群组成员
        return new String[]{"user1", "user2", "user3"};
    }
}
