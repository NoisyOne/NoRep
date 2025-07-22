package org.example.server.service;

import org.example.common.model.Message;
import org.example.server.repository.MessageRepository;

import java.util.List;

/**
 * 消息服务
 * 处理与消息相关的业务逻辑
 */
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * 发送消息
     */
    public boolean sendMessage(Message message) {
        // 设置消息ID（如果未设置）
        if (message.getMessageId() == null || message.getMessageId().isEmpty()) {
            message.setMessageId(generateMessageId());
        }

        // 保存消息到数据库
        return messageRepository.saveMessage(message);
    }

    /**
     * 获取聊天记录
     */
    public List<Message> getChatHistory(String userId, String friendId, long startTime, long endTime) {
        return messageRepository.getChatHistory(userId, friendId, startTime, endTime);
    }

    /**
     * 获取离线消息
     */
    public List<Message> getOfflineMessages(String userId) {
        return messageRepository.getOfflineMessages(userId);
    }

    /**
     * 标记消息为已送达
     */
    public boolean markAsDelivered(String messageId) {
        return messageRepository.updateDeliveryStatus(messageId, true);
    }

    /**
     * 标记消息为已读
     */
    public boolean markAsRead(String messageId) {
        return messageRepository.updateReadStatus(messageId, true);
    }

    /**
     * 生成唯一的消息ID
     */
    private String generateMessageId() {
        // 实际应用中应使用更安全的ID生成策略
        return "M" + System.currentTimeMillis();
    }
}
