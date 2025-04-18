package com.project.gamevaultcli.storage;

import com.project.gamevaultcli.entities.Order;
import com.project.gamevaultcli.interfaces.StorageInterface;
import com.project.gamevaultcli.helpers.DBUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class OrderStorage implements StorageInterface<Order, Integer>{

    @Override
    public Order findById(Integer orderId) {
        String sql = "SELECT * FROM Orders WHERE orderId = ?";
        try {
            List<Order> orders = DBUtil.executeQuery(sql, rs -> mapResultSetToOrder(rs), orderId);
            return orders.isEmpty() ? null : orders.get(0);
        } catch (SQLException | IOException e) {
            System.err.println("Error finding order by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM Orders";
        try {
            return DBUtil.executeQuery(sql, rs -> mapResultSetToOrder(rs));
        } catch (SQLException | IOException e) {
            System.err.println("Error finding all orders: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO Orders (userId, totalAmount, orderDate) VALUES (?, ?, ?)";
        try (ResultSet generatedKeys = DBUtil.executeInsert(sql, order.getUserId(), order.getTotalAmount(), new Timestamp(order.getOrderDate().getTime()))) {

            if (generatedKeys.next()) {
                order.setOrderId(generatedKeys.getInt(1));
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE Orders SET userId = ?, totalAmount = ?, orderDate = ? WHERE orderId = ?";
        try {
            DBUtil.executeUpdate(sql, order.getUserId(), order.getTotalAmount(), new Timestamp(order.getOrderDate().getTime()), order.getOrderId());
        } catch (SQLException | IOException e) {
            System.err.println("Error updating order: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer orderId) {
        String sql = "DELETE FROM Orders WHERE orderId = ?";
        try {
            DBUtil.executeUpdate(sql, orderId);
        } catch (SQLException | IOException e) {
            System.err.println("Error deleting order: " + e.getMessage());
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order =  new Order();
        order.setOrderId(rs.getInt("orderId"));
        order.setUserId(rs.getInt("userId"));
        order.setTotalAmount(rs.getDouble("totalAmount"));
        order.setOrderDate(rs.getTimestamp("orderDate"));
        //need games table to get games
        return order;
    }
}