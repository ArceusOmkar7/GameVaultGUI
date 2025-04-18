package com.project.gamevaultcli.entities;

import java.util.Date;

public class User {
    private int userId;
    private String email;
    private String password;
    private String username;
    private float walletBalance;
    private Date createdAt;

    public User(String email, String password, String username, float walletBalance) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.walletBalance = walletBalance;
        this.createdAt = new Date();
    }

    public User(int userId, String email, String password, String username, float walletBalance, Date createdAt) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.username = username;
        this.walletBalance = walletBalance;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(float walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}