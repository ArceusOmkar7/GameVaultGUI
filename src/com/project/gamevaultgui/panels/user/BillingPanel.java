package com.project.gamevaultgui.panels.user;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.entities.Order;
import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;
import com.project.gamevaultgui.GameVaultFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder; // Import TitledBorder
import java.awt.*;
import java.util.List;
import java.util.Vector; // Necessary for DefaultTableModel constructor with column names as Vector
import javax.swing.table.DefaultTableModel;

public class BillingPanel extends JPanel {

    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;
    private final GameManagement gameManagement; // Added GameManagement reference
    private final GameVaultFrame parentFrame; // Reference to the parent frame

    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JScrollPane ordersScrollPane;

    private JTable transactionsTable;
    private DefaultTableModel transactionsTableModel;
    private JScrollPane transactionsScrollPane;

    public BillingPanel(OrderManagement orderManagement, TransactionManagement transactionManagement,
            GameVaultFrame parentFrame) {
        this.orderManagement = orderManagement;
        this.transactionManagement = transactionManagement;
        this.gameManagement = parentFrame.getGameManagement(); // Get GameManagement from parent frame
        this.parentFrame = parentFrame; // Initialize parent frame reference

        setLayout(new GridLayout(2, 1, 10, 10)); // 2 rows, 1 column, with spacing
        // Change the main panel's title border text
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65), 2),
                "Your Orders History", // Changed title text
                TitledBorder.LEADING, // Align title to the left
                TitledBorder.TOP, // Place title at the top
                new Font("SansSerif", Font.BOLD, 18), // Title font
                new Color(60, 63, 65) // Title color
        ));
        setBackground(new Color(240, 240, 240)); // Set a light gray background

        initComponents();
        addComponents();
    }

    private void initComponents() {
        // --- Orders Table components ---
        // Columns for the orders table - added Game Name column
        ordersTableModel = new DefaultTableModel(new Object[] { "Order ID", "Game Name", "Total Amount", "Order Date" },
                0);
        ordersTable = new JTable(ordersTableModel);
        ordersScrollPane = new JScrollPane(ordersTable);
        // Add a titled border specifically for the orders scroll pane
        ordersScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Light gray border
                "Past Orders", // Sub-title for this table
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), // Sub-title font
                new Color(50, 50, 50) // Sub-title color
        ));
        customizeTable(ordersTable); // Customize table appearance

        // --- Transactions Table components ---
        // Columns for the transactions table
        transactionsTableModel = new DefaultTableModel(
                new Object[] { "Transaction ID", "Order ID", "Type", "Amount", "Date" }, 0);
        transactionsTable = new JTable(transactionsTableModel);
        transactionsScrollPane = new JScrollPane(transactionsTable);
        // Add a titled border specifically for the transactions scroll pane
        transactionsScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1), // Light gray border
                "Transaction History", // Sub-title for this table
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(50, 50, 50)));
        customizeTable(transactionsTable); // Customize table appearance
    }

    /**
     * Applies common styling to a JTable.
     * 
     * @param table The table to style.
     */
    private void customizeTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 13)); // Data row font
        table.setRowHeight(20); // Adjust row height
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13)); // Header font
        table.setFillsViewportHeight(true); // Make the table fill the scroll pane vertically
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row selection
        // Optional: Set grid color, background, etc.
        table.setGridColor(new Color(200, 200, 200)); // Lighter grid lines
        table.setBackground(Color.WHITE); // White background for table data
        table.getTableHeader().setBackground(new Color(220, 220, 220)); // Light gray header background
    }

    private void addComponents() {
        // Add the scroll panes containing the tables to the panel
        add(ordersScrollPane);
        add(transactionsScrollPane);
    }

    /**
     * Loads and displays the orders and transactions for the given user ID.
     * 
     * @param userId The ID of the user whose history to load.
     */
    public void loadBills(int userId) {
        ordersTableModel.setRowCount(0); // Clear previous data from the orders table
        transactionsTableModel.setRowCount(0); // Clear previous data from the transactions table

        try {
            // Load past orders for the current user
            List<Order> allOrders = orderManagement.getAllOrders();
            if (allOrders != null) { // Check if the list is not null
                for (Order order : allOrders) {
                    if (order.getUserId() == userId) {
                        // Get game name for this order from the transaction
                        List<Transaction> transactions = transactionManagement.getAllTransactions();
                        String gameName = "Unknown Game";

                        // Find transaction for this order to get the game ID
                        for (Transaction transaction : transactions) {
                            if (transaction.getOrderId() != null &&
                                    transaction.getOrderId() == order.getOrderId() &&
                                    transaction.getTransactionType().equals("Purchase")) {

                                // Try to get game using orderId from transaction (which might be gameId)
                                try {
                                    Game game = gameManagement.getGame(transaction.getOrderId());
                                    if (game != null) {
                                        gameName = game.getTitle();
                                        break;
                                    }
                                } catch (Exception e) {
                                    // Game not found, continue with default name
                                }
                            }
                        }

                        // Add row with game name
                        ordersTableModel.addRow(new Object[] {
                                order.getOrderId(),
                                gameName,
                                String.format("%.2f", order.getTotalAmount()), // Format total amount to 2 decimal
                                                                               // places
                                order.getOrderDate() // Display the order date
                        });
                    }
                }
            }

            // Load transactions for the current user
            List<Transaction> allTransactions = transactionManagement.getAllTransactions();
            if (allTransactions != null) { // Check if the list is not null
                for (Transaction transaction : allTransactions) {
                    if (transaction.getUserId() == userId) {
                        transactionsTableModel.addRow(new Object[] {
                                transaction.getTransactionId(),
                                transaction.getOrderId(),
                                // transaction.getUserId(), // User ID column is typically hidden in user's own
                                // view
                                transaction.getTransactionType(),
                                String.format("%.2f", transaction.getAmount()), // Format amount
                                transaction.getTransactionDate() // Display the transaction date (LocalDateTime or Date)
                        });
                    }
                }
            }

            // Ensure column identifiers are correctly set in case they were changed or
            // cleared
            // This line is optional if column names are always the same, but can help
            // ensure consistency
            ordersTableModel.setColumnIdentifiers(
                    new Vector<>(java.util.Arrays.asList("Order ID", "Game Name", "Total Amount", "Order Date")));
            transactionsTableModel.setColumnIdentifiers(
                    new Vector<>(java.util.Arrays.asList("Transaction ID", "Order ID", "Type", "Amount", "Date")));

            // After loading data, ensure the tables are updated in the UI
            ordersTable.revalidate();
            ordersTable.repaint();
            transactionsTable.revalidate();
            transactionsTable.repaint();

        } catch (Exception e) {
            // Display an error message to the user if data loading fails
            JOptionPane.showMessageDialog(this, "Error loading order and transaction history: " + e.getMessage(),
                    "Loading Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print stack trace for debugging purposes
        }
    }
}