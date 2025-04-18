package com.project.gamevaultcli.management;

import com.project.gamevaultcli.entities.Cart;
import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.exceptions.CartEmptyException;
import com.project.gamevaultcli.storage.CartStorage;

import java.util.List;

public class CartManagement {

    private final CartStorage cartStorage;

    public CartManagement(CartStorage cartStorage) {
        this.cartStorage = cartStorage;
    }

    public Cart getCart(int userId) {
        Cart cart = cartStorage.findById(userId);
        if (cart == null) {
            cart = new Cart(userId); // Create a new cart if it doesn't exist
            cartStorage.save(cart);
        }
        return cart;
    }

    public void addGameToCart(int userId, int gameId) {
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