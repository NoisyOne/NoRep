// src/main/java/org/example/common/model/User.java
package org.example.common.model;

import org.example.common.constant.ProtocolConstant;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户模型（客户端和服务端共用）
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;       // 用户唯一ID
    private String phone;        // 手机号（登录账号）
    private String password;     // 密码（客户端传输时需加密）
    private String nickname;     // 昵称
    private String avatar;       // 头像URL
    private int status;          // 状态（对应ProtocolConstant中的用户状态）
    private Date createdAt;      // 创建时间

    // 构造方法
    public User(String phone, String password, String nickname) {
        this.phone = phone;
        this.password = password;
        this.nickname = nickname;
        this.status = ProtocolConstant.USER_STATUS_PENDING; // 默认待激活
    }

    public User() {}

    // Getter和Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}