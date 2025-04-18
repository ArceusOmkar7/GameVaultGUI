package com.project.gamevaultcli.entities;

import java.time.LocalDateTime;

public class Transaction {
    private Integer transactionId;
    private Integer orderId; // Changed this
    private Integer userId;
    private String transactionType;
    private Float amount;
    private LocalDateTime transactionDate;

    public Transaction() {}

    public Transaction (Integer transactionId, Integer orderId, Integer userId, String transactionType, Float amount, LocalDateTime transactionDate) {
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public Float getAmount() { return amount; }
    public void setAmount(Float amount) { this.amount = amount; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
}