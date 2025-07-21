package org.example.server.repository;

import org.example.server.model.Message;
import org.example.server.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 消息仓库
 * 负责与数据库交互，进行消息相关操作
 */
public class MessageRepository {
    private static final Logger LOGGER = Logger.getLogger(MessageRepository.class.getName());

    /**
     * 保存消息
     */
    public boolean saveMessage(Message message) {
        String sql = "INSERT INTO messages (message_id, sender_id, receiver_id, group_id, content_type, content, sent_time, delivered, read_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, message.getMessageId());
            pstmt.setString(2, message.getSenderId());
            pstmt.setString(3, message.getReceiverId());
            pstmt.setString(4, message.getGroupId());
            pstmt.setInt(5, message.getType()); // 使用type字段表示内容类型
            pstmt.setString(6, message.getContent());
            pstmt.setTimestamp(7, new Timestamp(message.getTimestamp().getTime()));
            pstmt.setBoolean(8, message.isDelivered());
            pstmt.setBoolean(9, message.isRead());

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error saving message: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取聊天记录
     */
    public List<Message> getChatHistory(String userId, String friendId, long startTime, long endTime) {
        String sql = "SELECT * FROM messages WHERE " +
                "((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)) " +
                "AND sent_time BETWEEN ? AND ?";

        List<Message> messages = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, friendId);
            pstmt.setString(3, friendId);
            pstmt.setString(4, userId);
            pstmt.setTimestamp(5, new Timestamp(startTime));
            pstmt.setTimestamp(6, new Timestamp(endTime));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(mapRowToMessage(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error fetching chat history: " + e.getMessage());
        }

        return messages;
    }

    /**
     * 获取离线消息
     */
    public List<Message> getOfflineMessages(String userId) {
        String sql = "SELECT * FROM messages WHERE receiver_id = ? AND delivered = FALSE";
        List<Message> messages = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(mapRowToMessage(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error fetching offline messages: " + e.getMessage());
        }

        return messages;
    }

    /**
     * 更新消息送达状态
     */
    public boolean updateDeliveryStatus(String messageId, boolean delivered) {
        String sql = "UPDATE messages SET delivered = ? WHERE message_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, delivered);
            pstmt.setString(2, messageId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error updating delivery status: " + e.getMessage());
            return false;
        }
    }

    /**
     * 更新消息已读状态
     */
    public boolean updateReadStatus(String messageId, boolean read) {
        String sql = "UPDATE messages SET read_status = ? WHERE message_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, read);
            pstmt.setString(2, messageId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error updating read status: " + e.getMessage());
            return false;
        }
    }

    /**
     * 将数据库行映射到消息对象
     */
    private Message mapRowToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setMessageId(rs.getString("message_id"));
        message.setSenderId(rs.getString("sender_id"));
        message.setReceiverId(rs.getString("receiver_id"));
        message.setGroupId(rs.getString("group_id"));
        message.setType(rs.getInt("content_type")); // 使用content_type作为消息类型
        message.setContent(rs.getString("content"));
        message.setTimestamp(rs.getTimestamp("sent_time"));
        message.setDelivered(rs.getBoolean("delivered"));
        message.setRead(rs.getBoolean("read_status"));

        return message;
    }
}
