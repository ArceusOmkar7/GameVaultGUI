/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.gamevaultcli.exceptions;

public class OrderNotFoundException extends Exception {
    private int orderId;

    public OrderNotFoundException(int orderId) {
        super("Order with ID " + orderId + " not found.");
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }
}