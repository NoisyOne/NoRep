package org.example.client.model;

public class User {
    private String userId;
    private String nickname;

    public User(String userId, String nickname) {
        this.userId = userId;
        this.nickname = nickname;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }
}
