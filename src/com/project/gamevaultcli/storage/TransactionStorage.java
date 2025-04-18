package com.project.gamevaultcli.storage;

import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.interfaces.StorageInterface;
import com.project.gamevaultcli.helpers.DBUtil;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionStorage implements StorageInterface<Transaction, Integer> {

    @Override
    public Transaction findById(Integer transactionId) {
        String sql = "SELECT * FROM Transactions WHERE transactionId = ?";
        try {
            List<Transaction> transactions = DBUtil.executeQuery(sql, rs -> mapResultSetToTransaction(rs), transactionId);
            return transactions.isEmpty() ? null : transactions.get(0);
        } catch (SQLException | IOException e) {
            System.err.println("Error finding transaction by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM Transactions";
        try {
            return DBUtil.executeQuery(sql, rs -> mapResultSetToTransaction(rs));
        } catch (SQLException | IOException e) {
            System.err.println("Error finding all transactions: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(Transaction transaction) {
        String sql = "INSERT INTO Transactions (orderId, userId, transactionType, amount, transactionDate) VALUES (?, ?, ?, ?, ?)";
        try (ResultSet generatedKeys = DBUtil.executeInsert(sql, transaction.getOrderId(), transaction.getUserId(), transaction.getTransactionType(), transaction.getAmount(), Timestamp.valueOf(transaction.getTransactionDate()))) {

            if (generatedKeys.next()) {
                transaction.setTransactionId(generatedKeys.getInt(1));
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE Transactions SET orderId = ?, userId = ?, transactionType = ?, amount = ?, transactionDate = ? WHERE transactionId = ?";
        try {
            DBUtil.executeUpdate(sql, transaction.getOrderId(), transaction.getUserId(), transaction.getTransactionType(), transaction.getAmount(), Timestamp.valueOf(transaction.getTransactionDate()), transaction.getTransactionId());
        } catch (SQLException | IOException e) {
            System.err.println("Error updating transaction: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer transactionId) {
        String sql = "DELETE FROM Transactions WHERE transactionId = ?";
        try {
            DBUtil.executeUpdate(sql, transactionId);
        } catch (SQLException | IOException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
        }
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rs.getInt("transactionId"));
        transaction.setOrderId(rs.getInt("orderId"));
        transaction.setUserId(rs.getInt("userId"));
        transaction.setTransactionType(rs.getString("transactionType"));
        transaction.setAmount(rs.getFloat("amount"));
        transaction.setTransactionDate(rs.getTimestamp("transactionDate").toLocalDateTime());
        return transaction;
    }
}