package com.project.gamevaultcli.storage;

import com.project.gamevaultcli.entities.Cart;
import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.interfaces.StorageInterface;
import com.project.gamevaultcli.helpers.DBUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartStorage implements StorageInterface<Cart, Integer> {

    private final GameStorage gameStorage;

    public CartStorage(GameStorage gameStorage) {
        this.gameStorage = gameStorage;
    }

    @Override
    public Cart findById(Integer userId) {
        String sql = "SELECT userId FROM Carts WHERE userId = ?";
        try {
            List<Cart> carts = DBUtil.executeQuery(sql, rs -> mapResultSetToCart(rs), userId);
            return carts.isEmpty() ? null : carts.get(0);
        } catch (SQLException | IOException e) {
            System.err.println("Error finding cart by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Cart> findAll() {
        String sql = "SELECT userId FROM Carts";
        try {
            return DBUtil.executeQuery(sql, rs -> mapResultSetToCart(rs));
        } catch (SQLException | IOException e) {
            System.err.println("Error finding all carts: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(Cart cart) {
        String sql = "INSERT INTO Carts (userId) VALUES (?)";
        try {
            DBUtil.executeUpdate(sql, cart.getUserId());
        } catch (SQLException | IOException e) {
            System.err.println("Error saving cart: " + e.getMessage());
        }
    }

    @Override
    public void update(Cart cart) {
        // Cart table only contains userId, so there's nothing to update directly.
        // The games in the cart are managed in the CartItems table.
        System.out.println("Updating cart is not supported, since game table is not present.");
    }

    @Override
    public void delete(Integer userId) {
        String sql = "DELETE FROM Carts WHERE userId = ?";
        try {
            DBUtil.executeUpdate(sql, userId);
        } catch (SQLException | IOException e) {
            System.err.println("Error deleting cart: " + e.getMessage());
        }
    }

    // Adds game to cart
    public void addGameToCart(int userId, int gameId) {
        String sql = "INSERT INTO CartItems (userId, gameId) VALUES (?, ?)";
        try {
            DBUtil.executeUpdate(sql, userId, gameId);
        } catch (SQLException | IOException e) {
            System.err.println("Error adding game to cart: " + e.getMessage());
        }
    }

    // Removes game from cart
    public void removeGameFromCart(int userId, int gameId) {
        String sql = "DELETE FROM CartItems WHERE userId = ? AND gameId = ?";
        try {
            DBUtil.executeUpdate(sql, userId, gameId);
        } catch (SQLException | IOException e) {
            System.err.println("Error removing game from cart: " + e.getMessage());
        }
    }

    // Gets games in cart
    public List<Game> getGamesInCart(int userId) {
        String sql = "SELECT g.* FROM CartItems ci JOIN Games g ON ci.gameId = g.gameId WHERE ci.userId = ?";
        try {
            return DBUtil.executeQuery(sql, rs -> gameStorage.mapResultSetToGame(rs), userId);
        } catch (SQLException | IOException e) {
            System.err.println("Error getting games in cart: " + e.getMessage());
            return new ArrayList<>(); // Return empty list in case of error
        }
    }

    public void clearCart(int userId) {
        String sql = "DELETE FROM CartItems WHERE userId = ?";
        try {
            DBUtil.executeUpdate(sql, userId);
        } catch (SQLException | IOException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
        }
    }

    private Cart mapResultSetToCart(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setUserId(rs.getInt("userId"));
        return cart;
    }
}