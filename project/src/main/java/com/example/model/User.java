package com.example.model;

import java.io.Serializable;
import java.sql.Date;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String loginId;
    private String passwordHash;
    private Date createdAt;
    private String userName;

    public User() {
    }

    public User(Integer userId, String loginId, String passwordHash, Date createdAt, String userName) {
        this.userId = userId;
        this.loginId = loginId;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
