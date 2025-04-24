package com.project.gamevaultcli.management;

import com.project.gamevaultcli.entities.Cart;
import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.exceptions.CartEmptyException;
import com.project.gamevaultcli.exceptions.GameAlreadyOwnedException;
import com.project.gamevaultcli.storage.CartStorage;
import com.project.gamevaultcli.storage.GameStorage;

import java.util.List;

public class CartManagement {

    private final CartStorage cartStorage;
    private final GameStorage gameStorage;

    public CartManagement(CartStorage cartStorage) {
        this.cartStorage = cartStorage;
        this.gameStorage = null; // Will be initialized in the constructor that takes GameStorage
    }

    public CartManagement(CartStorage cartStorage, GameStorage gameStorage) {
        this.cartStorage = cartStorage;
        this.gameStorage = gameStorage;
    }

    public Cart getCart(int userId) {
        Cart cart = cartStorage.findById(userId);
        if (cart == null) {
            cart = new Cart(userId); // Create a new cart if it doesn't exist
            cartStorage.save(cart);
        }
        return cart;
    }

    public void addGameToCart(int userId, int gameId) throws GameAlreadyOwnedException {
        // Check if the user already owns the game
        if (gameStorage != null) {
            List<Game> ownedGames = gameStorage.findOwnedGamesByUser(userId);
            boolean alreadyOwns = ownedGames.stream().anyMatch(game -> game.getGameId() == gameId);
            if (alreadyOwns) {
                throw new GameAlreadyOwnedException("You already own this game");
            }
        }

        // If they don't own it, proceed with adding to cart
        Cart cart = getCart(userId);
        cartStorage.addGameToCart(userId, gameId);
    }

    public void removeGameFromCart(int userId, int gameId) {
        Cart cart = getCart(userId);
        cartStorage.removeGameFromCart(userId, gameId);
    }

    public List<Game> getGamesInCart(int userId) throws CartEmptyException {
        List<Game> games = cartStorage.getGamesInCart(userId);
        if (games.isEmpty()) {
            throw new CartEmptyException("Cart is empty for user: " + userId);
        }
        return games;
    }
}