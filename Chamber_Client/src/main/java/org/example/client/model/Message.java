package org.example.client.model;

import java.util.Date;

/**
 * 消息模型
 * 表示系统中的消息实体
 */
public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String groupId;
    private int type;
    private String content;
    private Date timestamp;

    public Message(String senderId, String receiverId, int type, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.content = content;
        this.timestamp = new Date();
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
