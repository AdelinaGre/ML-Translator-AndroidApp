package com.example.translator.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String nickname;
    private String email;
    private Timestamp createdTimestamp;
    private String userId;

    public UserModel() {
    }

    public UserModel(String nickname, String email, Timestamp createdTimestamp, String userId) {
        this.nickname = nickname;
        this.email = email;
        this.createdTimestamp = createdTimestamp;
        this.userId=userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
