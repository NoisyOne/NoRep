// src/main/java/org/example/common/model/Message.java
package org.example.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息模型（客户端和服务端共用，支持序列化）
 */
public class Message implements Serializable {
    // 固定序列化ID，确保两端版本兼容
    private static final long serialVersionUID = 1L;

    private String messageId;
    private String senderId;
    private String receiverId;
    private String groupId;
    private int type; // 对应ProtocolConstant中的消息类型
    private String content;
    private Date timestamp;
    private boolean delivered; // 服务端需要的送达状态
    private boolean read;      // 服务端需要的已读状态

    // 客户端常用构造方法（发送消息时使用）
    public Message(String senderId, String receiverId, int type, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.content = content;
        this.timestamp = new Date();
        this.delivered = false;
        this.read = false;
    }

    // 空构造方法（反序列化时需要）
    public Message() {}

    // Getter和Setter
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}