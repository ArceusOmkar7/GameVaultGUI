package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.Order;
import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class BillingPanel extends JPanel {

    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;
    private final GameVaultFrame parentFrame;

    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JScrollPane ordersScrollPane; // Make scroll panes instance variables

    private JTable transactionsTable;
    private DefaultTableModel transactionsTableModel;
    private JScrollPane transactionsScrollPane; // Make scroll panes instance variables

    public BillingPanel(OrderManagement orderManagement, TransactionManagement transactionManagement, GameVaultFrame parentFrame) {
        this.orderManagement = orderManagement;
        this.transactionManagement = transactionManagement;
        this.parentFrame = parentFrame;

        setLayout(new GridLayout(2, 1, 10, 10)); // Two rows, 1 column, with vertical spacing
        setBorder(BorderFactory.createTitledBorder("Billing History"));

        initComponents();
        addComponents();
    }

    private void initComponents() {
        // Orders Table
        ordersTableModel = new DefaultTableModel(new Object[]{"Order ID", "Total Amount", "Order Date"}, 0);
        ordersTable = new JTable(ordersTableModel);
        ordersScrollPane = new JScrollPane(ordersTable); // Create scroll pane with the table
        ordersScrollPane.setBorder(BorderFactory.createTitledBorder("Past Orders"));

        // Transactions Table
        transactionsTableModel = new DefaultTableModel(new Object[]{"Transaction ID", "Order ID", "Type", "Amount", "Date"}, 0);
        transactionsTable = new JTable(transactionsTableModel);
        transactionsScrollPane = new JScrollPane(transactionsTable); // Create scroll pane with the table
        transactionsScrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));
    }

    private void addComponents() {
        add(ordersScrollPane); // Add the scroll pane to the GridLayout
        add(transactionsScrollPane); // Add the scroll pane to the GridLayout
    }

    public void loadBills(int userId) {
        ordersTableModel.setRowCount(0); // Clear previous data
        transactionsTableModel.setRowCount(0); // Clear previous data

        try {
            // Load past orders for the current user
            List<Order> allOrders = orderManagement.getAllOrders();
            for (Order order : allOrders) {
                if (order.getUserId() == userId) {
                    ordersTableModel.addRow(new Object[]{
                            order.getOrderId(),
                            String.format("%.2f", order.getTotalAmount()),
                            order.getOrderDate()
                    });
                }
            }

            // Load transactions for the current user
            List<Transaction> allTransactions = transactionManagement.getAllTransactions();
            for (Transaction transaction : allTransactions) {
                if (transaction.getUserId() == userId) {
                    transactionsTableModel.addRow(new Object[]{
                            transaction.getTransactionId(),
                            transaction.getOrderId(),
                            transaction.getTransactionType(),
                            String.format("%.2f", transaction.getAmount()),
                            transaction.getTransactionDate()
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading billing data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}