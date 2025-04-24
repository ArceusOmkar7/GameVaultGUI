package com.project.gamevaultcli.exceptions;

public class GameAlreadyOwnedException extends Exception {
    public GameAlreadyOwnedException(String message) {
        super(message);
    }
}