package org.example.server.repository;

import org.example.server.model.Friend;
import org.example.server.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 好友仓库
 * 负责与数据库交互，进行好友相关操作
 */
public class FriendRepository {
    private static final Logger LOGGER = Logger.getLogger(FriendRepository.class.getName());

    /**
     * 添加好友
     */
    public boolean addFriend(String userId, String friendId, String groupName) {
        String sql = "INSERT INTO friends (owner_user_id, friend_user_id, group_name) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, friendId);
            pstmt.setString(3, groupName != null ? groupName : "默认分组");

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error adding friend: " + e.getMessage());
            return false;
        }
    }

    /**
     * 移除好友
     */
    public boolean removeFriend(String userId, String friendId) {
        String sql = "DELETE FROM friends WHERE owner_user_id = ? AND friend_user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, friendId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error removing friend: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取好友列表
     */
    public List<Friend> getFriends(String userId) {
        String sql = "SELECT f.friend_user_id, u.nickname, u.avatar, f.group_name " +
                "FROM friends f " +
                "JOIN app_users u ON f.friend_user_id = u.user_id " +
                "WHERE f.owner_user_id = ? AND f.status = 1";

        List<Friend> friends = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                friends.add(mapRowToFriend(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error fetching friends list: " + e.getMessage());
        }

        return friends;
    }

    /**
     * 更新好友分组
     */
    public boolean updateFriendGroup(String userId, String friendId, String newGroupName) {
        String sql = "UPDATE friends SET group_name = ? WHERE owner_user_id = ? AND friend_user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newGroupName);
            pstmt.setString(2, userId);
            pstmt.setString(3, friendId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error updating friend group: " + e.getMessage());
            return false;
        }
    }

    /**
     * 搜索好友
     */
    public List<Friend> searchFriends(String query) {
        String sql = "SELECT user_id, nickname, avatar FROM app_users " +
                "WHERE phone LIKE ? OR nickname LIKE ?";

        List<Friend> friends = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Friend friend = new Friend();
                friend.setUserId(rs.getString("user_id"));
                friend.setNickname(rs.getString("nickname"));
                friend.setAvatar(rs.getString("avatar"));
                friends.add(friend);
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error searching friends: " + e.getMessage());
        }

        return friends;
    }

    /**
     * 将数据库行映射到好友对象
     */
    private Friend mapRowToFriend(ResultSet rs) throws SQLException {
        Friend friend = new Friend();
        friend.setUserId(rs.getString("friend_user_id"));
        friend.setNickname(rs.getString("nickname"));
        friend.setAvatar(rs.getString("avatar"));
        friend.setGroupName(rs.getString("group_name"));

        return friend;
    }
}
