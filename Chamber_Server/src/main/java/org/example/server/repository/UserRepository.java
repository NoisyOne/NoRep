package org.example.server.repository;

import org.example.common.model.User;
import org.example.server.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * 用户仓库
 * 负责与数据库交互，进行用户相关操作
 */
public class UserRepository {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    /**
     * 通过手机号和密码获取用户ID
     */
    public String getUserIdByPhoneAndPassword(String phone, String password) {
        String sql = "SELECT user_id FROM app_users WHERE phone = ? AND password_hash = ? AND status = 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, password); // 实际应使用密码哈希
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("user_id");
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error during authentication: " + e.getMessage());
        }

        return null;
    }

    /**
     * 创建新用户
     */
    public String createUser(String phone, String password, String nickname) {
        String sql = "INSERT INTO app_users (user_id, phone, password_hash, nickname) VALUES (?, ?, ?, ?)";
        String userId = generateUserId(); // 生成唯一用户ID

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, phone);
            pstmt.setString(3, password); // 实际应使用密码哈希
            pstmt.setString(4, nickname);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return userId;
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error during registration: " + e.getMessage());
        }

        return null;
    }

    /**
     * 获取用户信息
     */
    public User getUserById(String userId) {
        String sql = "SELECT user_id, phone, nickname, avatar, signature, last_login, is_online " +
                "FROM app_users WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("user_id"));
                user.setPhone(rs.getString("phone")); // 注意：User类需要添加这个字段
                user.setNickname(rs.getString("nickname"));
                user.setAvatar(rs.getString("avatar"));
                user.setSignature(rs.getString("signature"));
                user.setLastLogin(rs.getTimestamp("last_login"));
                user.setOnline(rs.getBoolean("is_online"));
                return user;
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error fetching user data: " + e.getMessage());
        }

        return null;
    }

    /**
     * 更新用户资料
     */
    public boolean updateUserProfile(String userId, String nickname, String avatar, String signature) {
        String sql = "UPDATE app_users SET nickname = ?, avatar = ?, signature = ? WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.setString(2, avatar);
            pstmt.setString(3, signature);
            pstmt.setString(4, userId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error updating user profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * 生成唯一的用户ID
     */
    private String generateUserId() {
        // 实际应用中应使用更安全的ID生成策略
        return "U" + System.currentTimeMillis();
    }

}
