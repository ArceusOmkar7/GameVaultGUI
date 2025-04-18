/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.gamevaultcli.management;


import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.exceptions.GameNotFoundException;
import com.project.gamevaultcli.storage.GameStorage;

import java.util.List;

public class GameManagement {

    private final GameStorage gameStorage;

    public GameManagement(GameStorage gameStorage) {
        this.gameStorage = gameStorage;
    }

    public Game getGame(int gameId) throws GameNotFoundException {
        Game game = gameStorage.findById(gameId);
        if (game == null) {
            throw new GameNotFoundException("Game not found with ID: " + gameId);
        }
        return game;
    }

    public List<Game> getAllGames() {
        return gameStorage.findAll();
    }

    public void addGame(Game game) {
        gameStorage.save(game);
    }

    public void updateGame(Game game) {
        gameStorage.update(game);
    }

    public void deleteGame(int gameId) {
        gameStorage.delete(gameId);
    }
}
