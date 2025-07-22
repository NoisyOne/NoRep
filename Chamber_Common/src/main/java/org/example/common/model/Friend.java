// src/main/java/org/example/common/model/Friend.java
package org.example.common.model;

import java.io.Serializable;

/**
 * 好友模型（客户端和服务端共用）
 */
public class Friend implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;      // 好友的用户ID
    private String nickname;    // 好友昵称（客户端显示用）
    private String avatar;      // 好友头像URL（客户端显示用）
    private String groupName;   // 好友所在分组（如"家人"、"同事"）
    private int onlineStatus;   // 在线状态（对应ProtocolConstant中的用户状态）

    // 构造方法
    public Friend(String userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public Friend() {}

    // Getter和Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public int getOnlineStatus() { return onlineStatus; }
    public void setOnlineStatus(int onlineStatus) { this.onlineStatus = onlineStatus; }
}