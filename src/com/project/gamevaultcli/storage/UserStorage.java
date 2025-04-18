package com.project.gamevaultcli.storage;

import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.interfaces.StorageInterface;
import com.project.gamevaultcli.helpers.DBUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class UserStorage implements StorageInterface<User, Integer> {

    @Override
    public User findById(Integer userId) {
        String sql = "SELECT * FROM Users WHERE userId = ?";
        try {
            List<User> users = DBUtil.executeQuery(sql, rs -> mapResultSetToUser(rs), userId);
            return users.isEmpty() ? null : users.get(0);
        } catch (SQLException | IOException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            return null;
        }
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try {
            List<User> users = DBUtil.executeQuery(sql, rs -> mapResultSetToUser(rs), email);
            return users.isEmpty() ? null : users.get(0);
        } catch (SQLException | IOException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM Users";
        try {
            return DBUtil.executeQuery(sql, rs -> mapResultSetToUser(rs));
        } catch (SQLException | IOException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO Users (email, password, username, walletBalance, createdAt) VALUES (?, ?, ?, ?, ?)";
        try (ResultSet generatedKeys = DBUtil.executeInsert(sql, user.getEmail(), user.getPassword(), user.getUsername(), user.getWalletBalance(), new Timestamp(user.getCreatedAt().getTime()))) {

            if (generatedKeys.next()) {
                user.setUserId(generatedKeys.getInt(1));
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE Users SET email = ?, password = ?, username = ?, walletBalance = ? WHERE userId = ?";
        try {
            DBUtil.executeUpdate(sql, user.getEmail(), user.getPassword(), user.getUsername(), user.getWalletBalance(), user.getUserId());
        } catch (SQLException | IOException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer userId) {
        String sql = "DELETE FROM Users WHERE userId = ?";
        try {
            DBUtil.executeUpdate(sql, userId);
        } catch (SQLException | IOException e) {
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("userId"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("username"),
                rs.getFloat("walletBalance"),
                rs.getTimestamp("createdAt")
        );
    }
}