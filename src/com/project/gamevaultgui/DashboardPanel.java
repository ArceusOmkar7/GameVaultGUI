
package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.entities.Order;
import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;
import com.project.gamevaultcli.management.UserManagement;
import com.project.gamevaultcli.management.CartManagement; // Import CartManagement
import com.project.gamevaultcli.exceptions.GameNotFoundException; // Import GameNotFoundException

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Import EmptyBorder
import javax.swing.border.TitledBorder; // Import TitledBorder
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent; // Import ActionEvent
import java.awt.event.ActionListener; // Import ActionListener
import java.awt.event.MouseAdapter; // Import MouseAdapter
import java.awt.event.MouseEvent; // Import MouseEvent
import java.util.List;
import java.util.Vector;

public class DashboardPanel extends JPanel {

    private final UserManagement userManagement;
    private final GameManagement gameManagement;
    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;
    private final CartManagement cartManagement; // Add CartManagement reference
    private final GameVaultFrame parentFrame; // Add parent frame reference

    // Summary labels
    private JLabel userCountLabel;
    private JLabel gameCountLabel;
    private JLabel totalRevenueLabel;

    // Tables
    private JTable gamesTable; // Table for listing all games
    private DefaultTableModel gamesTableModel; // Model for games table
    private JScrollPane gamesScrollPane; // Scroll pane for games table

    private JTable recentOrdersTable; // Table for recent orders
    private JTable recentTransactionsTable; // Table for recent transactions
    private DefaultTableModel ordersTableModel; // Model for orders table
    private DefaultTableModel transactionsTableModel; // Model for transactions table

    // Buttons
    private JButton addToCartButton; // Button to add selected game to cart

    public DashboardPanel(UserManagement userManagement, GameManagement gameManagement, OrderManagement orderManagement, TransactionManagement transactionManagement, CartManagement cartManagement, GameVaultFrame parentFrame) { // Update constructor
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
        this.orderManagement = orderManagement;
        this.transactionManagement = transactionManagement;
        this.cartManagement = cartManagement; // Initialize CartManagement
        this.parentFrame = parentFrame; // Initialize parent frame

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(230, 235, 240));

        initComponents();
        addComponents();
        setupEventHandlers(); // Add event handlers setup
    }

    private void initComponents() {
        // --- Summary section components ---
        userCountLabel = new JLabel("Total Users: 0");
        gameCountLabel = new JLabel("Total Games: 0");
        totalRevenueLabel = new JLabel("Total Revenue: $0.00");

        // Style summary labels
        Font summaryFont = new Font("SansSerif", Font.BOLD, 16);
        userCountLabel.setFont(summaryFont);
        gameCountLabel.setFont(summaryFont);
        totalRevenueLabel.setFont(summaryFont);
         Color summaryColor = new Color(50, 50, 50); // Dark gray text
         userCountLabel.setForeground(summaryColor);
         gameCountLabel.setForeground(summaryColor);
         totalRevenueLabel.setForeground(summaryColor);


        // --- Games Table components ---
        // Columns for the games table
        gamesTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Developer", "Platform", "Price"}, 0);
        gamesTable = new JTable(gamesTableModel);
        gamesScrollPane = new JScrollPane(gamesTable);
         gamesScrollPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Available Games",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(50, 50, 50)
         ));


        // --- Recent Orders Table components ---
        ordersTableModel = new DefaultTableModel(new Object[]{"Order ID", "User ID", "Total Amount", "Order Date"}, 0);
        recentOrdersTable = new JTable(ordersTableModel);
        JScrollPane ordersScrollPane = new JScrollPane(recentOrdersTable);
         ordersScrollPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Recent Orders",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(50, 50, 50)
         ));

        // --- Recent Transactions Table components ---
        transactionsTableModel = new DefaultTableModel(new Object[]{"Transaction ID", "Order ID", "User ID", "Type", "Amount", "Date"}, 0);
        recentTransactionsTable = new JTable(transactionsTableModel);
        JScrollPane transactionsScrollPane = new JScrollPane(recentTransactionsTable);
         transactionsScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Recent Transactions",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(50, 50, 50)
         ));

         // Optional: Customize table appearance (e.g., font, row height)
         customizeTable(gamesTable);
         customizeTable(recentOrdersTable);
         customizeTable(recentTransactionsTable);

         // --- Button component ---
         addToCartButton = new JButton("Add Selected Game to Cart");
         // Style the button
         addToCartButton.setFont(new Font("SansSerif", Font.BOLD, 14));
         addToCartButton.setBackground(new Color(40, 167, 69)); // Green color
         addToCartButton.setForeground(Color.WHITE);
         addToCartButton.setFocusPainted(false);
         addToCartButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
         addToCartButton.setOpaque(true);
         addToCartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

         // Button Hover Effect (DEFINED AND APPLIED WITHIN initComponents)
         Color btnBg = new Color(40, 167, 69); // Define locally within initComponents
         Color btnHover = btnBg.darker();     // Define locally within initComponents
         addToCartButton.addMouseListener(new MouseAdapter() {
             @Override public void mouseEntered(MouseEvent e) { addToCartButton.setBackground(btnHover); }
             @Override public void mouseExited(MouseEvent e) { addToCartButton.setBackground(btnBg); }
              // Optional: Add pressed effect
             @Override
             public void mousePressed(MouseEvent evt) {
                  addToCartButton.setBackground(btnHover.darker()); // Darker on press
             }
             @Override
             public void mouseReleased(MouseEvent evt) {
                 addToCartButton.setBackground(addToCartButton.getModel().isRollover() ? btnHover : btnBg);
             }
         });
    } // <--- THIS IS THE CORRECT CLOSING BRACE FOR initComponents()


     private void customizeTable(JTable table) {
         table.setFont(new Font("SansSerif", Font.PLAIN, 13));
         table.setRowHeight(20); // Adjust row height
         table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13)); // Header font
         table.setFillsViewportHeight(true); // Make the table fill the scroll pane
         table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only one row to be selected
     }


    private void addComponents() {
        // Panel for summary labels
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        summaryPanel.setOpaque(false);
        summaryPanel.add(userCountLabel);
        summaryPanel.add(gameCountLabel);
        summaryPanel.add(totalRevenueLabel);

        // Panel for the game list table and the "Add to Cart" button
        JPanel gameListPanel = new JPanel(new BorderLayout(0, 10)); // BorderLayout with vertical gap
        gameListPanel.setOpaque(false);
        gameListPanel.add(gamesScrollPane, BorderLayout.CENTER); // Table takes center
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel to center the button
        buttonPanel.setOpaque(false);
        buttonPanel.add(addToCartButton);
        gameListPanel.add(buttonPanel, BorderLayout.SOUTH); // Button below the table


        // Panel for orders and transactions tables
        JPanel ordersTransactionsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        ordersTransactionsPanel.setOpaque(false);
        ordersTransactionsPanel.add(recentOrdersTable);
        ordersTransactionsPanel.add(recentTransactionsTable);


        // Main panel layout (BoxLayout vertically)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space
        contentPanel.add(gameListPanel); // Add the panel containing game table and button
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Space
        contentPanel.add(ordersTransactionsPanel); // Add orders/transactions panel

        add(contentPanel, BorderLayout.CENTER); // Add the main content panel to the dashboard
    }

    // Event handlers setup
    private void setupEventHandlers() {
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if a user is logged in
                User currentUser = parentFrame.getCurrentUser();
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(DashboardPanel.this, "Please log in to add games to your cart.", "Login Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Get the selected row
                int selectedRow = gamesTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(DashboardPanel.this, "Please select a game from the list.", "No Game Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    // Get the Game ID from the selected row (assuming ID is in the first column)
                    int gameId = (int) gamesTableModel.getValueAt(selectedRow, 0);
                    String gameTitle = (String) gamesTableModel.getValueAt(selectedRow, 1); // Get title for message

                    // Add the game to the user's cart
                    cartManagement.addGameToCart(currentUser.getUserId(), gameId);

                    JOptionPane.showMessageDialog(DashboardPanel.this, "'" + gameTitle + "' added to your cart!", "Added to Cart", JOptionPane.INFORMATION_MESSAGE);

                     // Optional: Automatically switch to the Cart panel after adding
                     // parentFrame.showPanel("Cart");


                } catch (Exception ex) {
                     // Catch generic exception for unexpected errors (e.g., DB issues during add)
                    JOptionPane.showMessageDialog(DashboardPanel.this, "Error adding game to cart: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Print stack trace for debugging
                }
            }
        });
    }


    public void loadDashboardData(int currentUserId) {
        try {
            // Load user count
            List<User> users = userManagement.getAllUsers();
            userCountLabel.setText("Total Users: " + users.size());

            // Load all games and display in the games table
            List<Game> games = gameManagement.getAllGames();
            gameCountLabel.setText("Total Games: " + games.size());
            gamesTableModel.setRowCount(0); // Clear previous data
             for (Game game : games) {
                 gamesTableModel.addRow(new Object[]{
                         game.getGameId(),
                         game.getTitle(),
                         game.getDeveloper(),
                         game.getPlatform(),
                         String.format("%.2f", game.getPrice()) // Format price
                 });
             }


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

            // Decide which orders/transactions to show based on isAdmin status (derived from currentUserId check)
            // If currentUserId is -1, it's treated as admin view showing all
            if (currentUserId != -1) { // User view: Show only user's orders and transactions
                List<Order> allOrders = orderManagement.getAllOrders();
                 for (Order order : allOrders) {
                    if (order.getUserId() == currentUserId) {
                         ordersTableModel.addRow(new Object[]{
                                 order.getOrderId(),
                                 // Don't show User ID in user's own history table for simplicity
                                 // order.getUserId(),
                                 String.format("%.2f", order.getTotalAmount()),
                                 order.getOrderDate()
                          });
                    }
                }

                List<Transaction> allTransactions = transactionManagement.getAllTransactions();
                for (Transaction transaction : allTransactions) {
                     if (transaction.getUserId() == currentUserId) {
                         // Displaying User ID for context, even in user view
                        transactionsTableModel.addRow(new Object[]{
                                transaction.getTransactionId(),
                                transaction.getOrderId(),
                                // Don't show User ID in user's own history table
                                // transaction.getUserId(),
                                transaction.getTransactionType(),
                                String.format("%.2f", transaction.getAmount()),
                                transaction.getTransactionDate()
                         });
                     }
                }
                 // Adjust column names for user view if User ID is removed
                 ordersTableModel.setColumnIdentifiers(new Object[]{"Order ID", "Total Amount", "Order Date"});
                 transactionsTableModel.setColumnIdentifiers(new Object[]{"Transaction ID", "Order ID", "Type", "Amount", "Date"});

                 // Make the "Add to Cart" button visible for logged-in users
                 addToCartButton.setVisible(true);

            } else { // Admin view: Show all orders and transactions
                List<Order> allOrders = orderManagement.getAllOrders();
                 for (Order order : allOrders) {
                      ordersTableModel.addRow(new Object[]{
                             order.getOrderId(),
                             order.getUserId(),
                             String.format("%.2f", order.getTotalAmount()),
                             order.getOrderDate()
                      });
                  }

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
                 // Ensure column names are correct for admin view (includes User ID)
                 ordersTableModel.setColumnIdentifiers(new Object[]{"Order ID", "User ID", "Total Amount", "Order Date"});
                 transactionsTableModel.setColumnIdentifiers(new Object[]{"Transaction ID", "Order ID", "User ID", "Type", "Amount", "Date"});

                 // Hide the "Add to Cart" button in admin view
                 addToCartButton.setVisible(false);
            }

            // Ensure tables are updated in the UI
             gamesTable.revalidate();
             gamesTable.repaint();
             recentOrdersTable.revalidate();
             recentOrdersTable.repaint();
             recentTransactionsTable.revalidate();
             recentTransactionsTable.repaint();


        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading dashboard data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print stack trace for debugging
        }
    }
}