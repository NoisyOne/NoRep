package org.example.server.handler;

import org.example.server.model.Message;
import org.example.server.cache.UserCache;
import org.example.server.constant.ProtocolConstant;

/**
 * 请求处理器
 * 处理各种客户端请求
 */
public class RequestHandler {
    private final UserCache userCache;

    public RequestHandler(UserCache userCache) {
        this.userCache = userCache;
    }

    /**
     * 处理在线状态请求
     */
    public void handleOnlineStatusRequest(Message request, ClientConnection connection) {
        String userIdToCheck = request.getContent(); // 请求中包含要查询的用户ID

        boolean isOnline = userCache.isUserOnline(userIdToCheck);

        Message statusMessage = new Message(
                ProtocolConstant.SERVER_USER_ID,
                connection.getUserId(),
                ProtocolConstant.USER_ONLINE_STATUS,
                Boolean.toString(isOnline)
        );
        statusMessage.setReceiverId(request.getSenderId());

        connection.sendMessage(statusMessage);
    }

    /**
     * 发送在线用户列表
     */
    public void sendOnlineUsersList(ClientConnection connection) {
        StringBuilder userListBuilder = new StringBuilder();
        for (String onlineUserId : userCache.getAllOnlineUsers().keySet()) {
            if (!onlineUserId.equals(connection.getUserId())) { // 不包含自己
                userListBuilder.append(onlineUserId).append(",");
            }
        }

        if (userListBuilder.length() > 0) {
            userListBuilder.deleteCharAt(userListBuilder.length() - 1); // 移除最后一个逗号

            Message userListMessage = new Message(
                    ProtocolConstant.SERVER_USER_ID,
                    connection.getUserId(),
                    ProtocolConstant.ONLINE_USERS_LIST,
                    userListBuilder.toString()
            );
            connection.sendMessage(userListMessage);
        }
    }
}
