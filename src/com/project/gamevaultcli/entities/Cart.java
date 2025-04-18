package com.project.gamevaultcli.entities;

public class Cart {
    private int userId;

    public Cart(int userId) {
        this.userId = userId;
    }

    public Cart() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}