package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.entities.Order;
import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;
import com.project.gamevaultcli.management.UserManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class DashboardPanel extends JPanel {

    private final UserManagement userManagement;
    private final GameManagement gameManagement;
    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;

    private JLabel userCountLabel;
    private JLabel gameCountLabel;
    private JLabel totalRevenueLabel;
    private JTable recentOrdersTable;
    private JTable recentTransactionsTable;
    private DefaultTableModel ordersTableModel;
    private DefaultTableModel transactionsTableModel;

    public DashboardPanel(UserManagement userManagement, GameManagement gameManagement, OrderManagement orderManagement, TransactionManagement transactionManagement) {
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
        this.orderManagement = orderManagement;
        this.transactionManagement = transactionManagement;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Dashboard"));

        initComponents();
        addComponents();
    }

    private void initComponents() {
        // Summary section
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3));
        userCountLabel = new JLabel("Total Users: 0");
        gameCountLabel = new JLabel("Total Games: 0");
        totalRevenueLabel = new JLabel("Total Revenue: $0.00");

        summaryPanel.add(userCountLabel);
        summaryPanel.add(gameCountLabel);
        summaryPanel.add(totalRevenueLabel);

        // Recent Orders Table
        ordersTableModel = new DefaultTableModel(new Object[]{"Order ID", "User ID", "Total Amount", "Order Date"}, 0);
        recentOrdersTable = new JTable(ordersTableModel);
        JScrollPane ordersScrollPane = new JScrollPane(recentOrdersTable);
        ordersScrollPane.setBorder(BorderFactory.createTitledBorder("Recent Orders"));

        // Recent Transactions Table
        transactionsTableModel = new DefaultTableModel(new Object[]{"Transaction ID", "Order ID", "User ID", "Type", "Amount", "Date"}, 0);
        recentTransactionsTable = new JTable(transactionsTableModel);
        JScrollPane transactionsScrollPane = new JScrollPane(recentTransactionsTable);
        transactionsScrollPane.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
    }

    private void addComponents() {
        add(createSummaryPanel(), BorderLayout.NORTH); // Create and add summary panel
        add(createTablesPanel(), BorderLayout.CENTER); // Create and add tables panel
    }

     private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 10)); // Add padding
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        summaryPanel.add(userCountLabel);
        summaryPanel.add(gameCountLabel);
        summaryPanel.add(totalRevenueLabel);
        return summaryPanel;
    }

    private JPanel createTablesPanel() {
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // 2 rows, 1 column, with vertical padding
        tablesPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Add padding

        // Add recent orders table with scroll pane and title
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBorder(BorderFactory.createTitledBorder("Recent Orders"));
        ordersPanel.add(new JScrollPane(recentOrdersTable), BorderLayout.CENTER);
        tablesPanel.add(ordersPanel);

        // Add recent transactions table with scroll pane and title
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        transactionsPanel.add(new JScrollPane(recentTransactionsTable), BorderLayout.CENTER);
        tablesPanel.add(transactionsPanel);

        return tablesPanel;
    }


    public void loadDashboardData(int currentUserId) {
        try {
            // Load user count
            List<User> users = userManagement.getAllUsers();
            userCountLabel.setText("Total Users: " + users.size());

            // Load game count
            List<Game> games = gameManagement.getAllGames();
            gameCountLabel.setText("Total Games: " + games.size());

            // Load total revenue
            List<Transaction> transactions = transactionManagement.getAllTransactions();
            double totalRevenue = transactions.stream()
                    .filter(t -> "Purchase".equals(t.getTransactionType()))
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            totalRevenueLabel.setText(String.format("Total Revenue: $%.2f", totalRevenue));

            // Clear previous order and transaction data
            ordersTableModel.setRowCount(0);
            transactionsTableModel.setRowCount(0);

            if (currentUserId != -1) { // Load user-specific data if not in admin view
                // Load recent orders for the current user
                List<Order> userOrders = orderManagement.getAllOrders();
                 for (Order order : userOrders) {
                    if (order.getUserId() == currentUserId) {
                         ordersTableModel.addRow(new Object[]{
                                order.getOrderId(),
                                order.getUserId(),
                                String.format("%.2f", order.getTotalAmount()),
                                order.getOrderDate()
                         });
                    }
                }

                // Load recent transactions for the current user
                List<Transaction> userTransactions = transactionManagement.getAllTransactions();
                for (Transaction transaction : userTransactions) {
                     if (transaction.getUserId() == currentUserId) {
                        transactionsTableModel.addRow(new Object[]{
                                transaction.getTransactionId(),
                                transaction.getOrderId(),
                                transaction.getUserId(),
                                transaction.getTransactionType(),
                                String.format("%.2f", transaction.getAmount()),
                                transaction.getTransactionDate()
                        });
                     }
                }
            } else {
                // Admin view: Potentially show all orders or transactions, or a summary view
                // For now, we'll leave the tables empty or populate with all data if needed.
                // Example: Display all orders for admin
                List<Order> allOrders = orderManagement.getAllOrders();
                 for (Order order : allOrders) {
                     ordersTableModel.addRow(new Object[]{
                            order.getOrderId(),
                            order.getUserId(),
                            String.format("%.2f", order.getTotalAmount()),
                            order.getOrderDate()
                     });
                 }

                 // Example: Display all transactions for admin
                 List<Transaction> allTransactions = transactionManagement.getAllTransactions();
                 for (Transaction transaction : allTransactions) {
                     transactionsTableModel.addRow(new Object[]{
                            transaction.getTransactionId(),
                            transaction.getOrderId(),
                            transaction.getUserId(),
                            transaction.getTransactionType(),
                            String.format("%.2f", transaction.getAmount()),
                            transaction.getTransactionDate()
                     });
                 }
            }


        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}