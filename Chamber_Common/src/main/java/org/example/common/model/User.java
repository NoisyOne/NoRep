// Chamber_Common/src/main/java/org/example/common/model/User.java
package org.example.common.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    // 序列化ID，确保跨端兼容性
    private static final long serialVersionUID = 1L;

    // 所有字段需与数据库列和UserRepository的操作对应
    private String userId;
    private String phone;
    private String nickname;
    private String avatar;
    private String signature;
    private Date lastLogin;
    private boolean isOnline;
    private int status; // 用于标记用户状态（如1=正常，0=禁用）

    // 无参构造函数（反序列化必需）
    public User() {}

    // 客户端登录/注册时常用的构造函数（最少参数）
    public User(String userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    // 完整构造函数（服务端数据库映射用）
    public User(String userId, String phone, String nickname, String avatar,
                String signature, Date lastLogin, boolean isOnline, int status) {
        this.userId = userId;
        this.phone = phone;
        this.nickname = nickname;
        this.avatar = avatar;
        this.signature = signature;
        this.lastLogin = lastLogin;
        this.isOnline = isOnline;
        this.status = status;
    }

    // 所有字段的getter和setter（必须完整，否则序列化/反序列化失败）
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}