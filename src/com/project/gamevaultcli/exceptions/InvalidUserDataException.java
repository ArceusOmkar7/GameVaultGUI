/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.gamevaultcli.exceptions;

public class InvalidUserDataException extends Exception {
    private String field;

    public InvalidUserDataException(String field, String message) {
        super("Invalid data for field: " + field + ". " + message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}