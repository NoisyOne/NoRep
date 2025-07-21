package org.example.server.repository;

import org.example.server.model.Group;
import org.example.server.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 群组仓库
 * 负责与数据库交互，进行群组相关操作
 */
public class GroupRepository {
    private static final Logger LOGGER = Logger.getLogger(GroupRepository.class.getName());

    /**
     * 创建群组
     */
    public String createGroup(String ownerId, String name, String description) {
        String sql = "INSERT INTO chat_groups (group_id, name, owner_user_id, description) VALUES (?, ?, ?, ?)";
        String groupId = generateGroupId(); // 生成唯一群组ID

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupId);
            pstmt.setString(2, name);
            pstmt.setString(3, ownerId);
            pstmt.setString(4, description);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // 添加创建者为群主
                addMember(groupId, ownerId, ownerId, 2); // 2表示群主角色
                return groupId;
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error creating group: " + e.getMessage());
        }

        return null;
    }

    /**
     * 解散群组
     */
    public boolean disbandGroup(String groupId, String ownerId) {
        String sql = "DELETE FROM chat_groups WHERE group_id = ? AND owner_user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupId);
            pstmt.setString(2, ownerId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error disbanding group: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取用户群组列表
     */
    public List<Group> getUserGroups(String userId) {
        String sql = "SELECT g.group_id, g.name, g.owner_user_id, g.description, g.created_at " +
                "FROM chat_groups g " +
                "JOIN group_members m ON g.group_id = m.chat_group_id " +
                "WHERE m.member_user_id = ? AND m.status = 1";

        List<Group> groups = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                groups.add(mapRowToGroup(rs));
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error fetching user groups: " + e.getMessage());
        }

        return groups;
    }

    /**
     * 添加成员到群组
     */
    /**
     * 添加成员到群组
     * @param groupId 群组ID
     * @param memberId 成员ID
     * @param inviterId 邀请人ID
     * @param role 指定角色（0=普通成员, 1=管理员, 2=群主）
     */

    /**
     * 添加成员到群组（默认角色）
     */
    public boolean addMember(String groupId, String memberId, String inviterId) {
        return addMember(groupId, memberId, inviterId, determineRole(groupId, memberId));
    }


    public boolean addMember(String groupId, String memberId, String inviterId, int role) {
        // 首先检查邀请者是否有权限添加成员
        if (!canAddMember(groupId, inviterId)) {
            return false;
        }

        String sql = "INSERT INTO group_members (chat_group_id, member_user_id, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupId);
            pstmt.setString(2, memberId);
            pstmt.setInt(3, role); // 使用传入的角色

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error adding group member: " + e.getMessage());
            return false;
        }
    }


    /**
     * 从群组移除成员
     */
    public boolean removeMember(String groupId, String memberId, String removerId) {
        // 首先检查移除者是否有权限移除成员
        if (!canRemoveMember(groupId, memberId, removerId)) {
            return false;
        }

        String sql = "DELETE FROM group_members WHERE chat_group_id = ? AND member_user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupId);
            pstmt.setString(2, memberId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error removing group member: " + e.getMessage());
            return false;
        }
    }

    /**
     * 获取群组成员
     */
    public List<String> getGroupMembers(String groupId) {
        String sql = "SELECT member_user_id FROM group_members WHERE chat_group_id = ? AND status = 1";
        List<String> members = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                members.add(rs.getString("member_user_id"));
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error fetching group members: " + e.getMessage());
        }

        return members;
    }

    /**
     * 更新群组信息
     */
    public boolean updateGroupInfo(String groupId, String name, String description, String updaterId) {
        // 首先检查更新者是否有权限更新群组信息
        if (!canUpdateGroupInfo(groupId, updaterId)) {
            return false;
        }

        String sql = "UPDATE chat_groups SET name = ?, description = ? WHERE group_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, groupId);

            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.severe("Database error updating group info: " + e.getMessage());
            return false;
        }
    }

    /**
     * 将数据库行映射到群组对象
     */
    private Group mapRowToGroup(ResultSet rs) throws SQLException {
        Group group = new Group();
        group.setGroupId(rs.getString("group_id"));
        group.setName(rs.getString("name"));
        group.setOwnerId(rs.getString("owner_user_id"));
        group.setDescription(rs.getString("description"));
        group.setCreatedAt(rs.getTimestamp("created_at"));

        return group;
    }

    /**
     * 生成唯一的群组ID
     */
    private String generateGroupId() {
        // 实际应用中应使用更安全的ID生成策略
        return "G" + System.currentTimeMillis();
    }

    /**
     * 检查用户是否可以添加成员
     */
    private boolean canAddMember(String groupId, String inviterId) {
        // 群主或管理员可以添加成员
        int role = getMemberRole(groupId, inviterId);
        return role == 2 || role == 1; // 2=群主, 1=管理员
    }


    /**
     * 检查用户是否可以移除成员
     */
    private boolean canRemoveMember(String groupId, String memberId, String removerId) {
        // 群主或管理员可以移除成员
        // 或者自己退出群聊
        if (removerId.equals(memberId)) {
            return true; // 允许自己退出
        }

        int removerRole = getMemberRole(groupId, removerId);
        int memberRole = getMemberRole(groupId, memberId);

        // 群主可以移除任何人
        if (removerRole == 2) {
            return true;
        }

        // 管理员只能移除普通成员
        return removerRole == 1 && memberRole < 1;
    }

    /**
     * 检查用户是否可以更新群组信息
     */
    private boolean canUpdateGroupInfo(String groupId, String updaterId) {
        // 只有群主可以更新群组信息
        int role = getMemberRole(groupId, updaterId);
        return role == 2; // 2=群主
    }

    /**
     * 获取成员角色
     */
    private int getMemberRole(String groupId, String userId) {
        String sql = "SELECT role FROM group_members WHERE chat_group_id = ? AND member_user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, groupId);
            pstmt.setString(2, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("role");
            }
        } catch (SQLException e) {
            LOGGER.severe("Database error fetching member role: " + e.getMessage());
        }

        return -1; // 默认返回-1表示错误
    }

    /**
     * 确定新成员的角色
     */
    private int determineRole(String groupId, String memberId) {
        // 新加入的成员默认是普通成员
        return 0; // 0=普通成员
    }
}
