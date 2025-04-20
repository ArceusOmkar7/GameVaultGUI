package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.entities.Order;
import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;
import com.project.gamevaultcli.management.UserManagement;
import com.project.gamevaultcli.management.CartManagement;
import com.project.gamevaultcli.exceptions.GameNotFoundException;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;


public class DashboardPanel extends JPanel {

    private final UserManagement userManagement;
    private final GameManagement gameManagement;
    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;
    private final CartManagement cartManagement;
    private final GameVaultFrame parentFrame; // Parent frame reference

    // Summary labels
    private JLabel userCountLabel;
    private JLabel gameCountLabel;
    private JLabel totalRevenueLabel;

    // Tables
    private JTable gamesTable;
    private DefaultTableModel gamesTableModel;
    private JScrollPane gamesScrollPane;

    private JTable recentOrdersTable;
    private JTable recentTransactionsTable;
    private DefaultTableModel ordersTableModel;
    private DefaultTableModel transactionsTableModel;
    private JScrollPane ordersScrollPane;
    private JScrollPane transactionsScrollPane;


    // Buttons
    private JButton addToCartButton;

    public DashboardPanel(UserManagement userManagement, GameManagement gameManagement, OrderManagement orderManagement, TransactionManagement transactionManagement, CartManagement cartManagement, GameVaultFrame parentFrame) {
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
        this.orderManagement = orderManagement;
        this.transactionManagement = transactionManagement;
        this.cartManagement = cartManagement;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(230, 235, 240));

        initComponents();
        addComponents();
        setupEventHandlers();
    }

    private void initComponents() {
        // --- Summary section components ---
        userCountLabel = new JLabel("Total Users: 0");
        gameCountLabel = new JLabel("Total Games: 0");
        totalRevenueLabel = new JLabel("Total Revenue: $0.00");

        Font summaryFont = new Font("SansSerif", Font.BOLD, 16);
        userCountLabel.setFont(summaryFont);
        gameCountLabel.setFont(summaryFont);
        totalRevenueLabel.setFont(summaryFont);
         Color summaryColor = new Color(50, 50, 50);
         userCountLabel.setForeground(summaryColor);
         gameCountLabel.setForeground(summaryColor);
         totalRevenueLabel.setForeground(summaryColor);


        // --- Games Table ---
        gamesTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Developer", "Platform", "Price", "Release Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        gamesTable = new JTable(gamesTableModel);
        gamesScrollPane = new JScrollPane(gamesTable);
         gamesScrollPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Available Games",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)
         ));
         customizeTable(gamesTable);


        // --- Recent Orders Table ---
        ordersTableModel = new DefaultTableModel(new Object[]{"Order ID", "User ID", "Total Amount", "Order Date"}, 0);
        recentOrdersTable = new JTable(ordersTableModel);
        ordersScrollPane = new JScrollPane(recentOrdersTable);
         ordersScrollPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Recent Orders",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)
         ));
         customizeTable(recentOrdersTable);

        // --- Recent Transactions Table ---
        transactionsTableModel = new DefaultTableModel(new Object[]{"Transaction ID", "Order ID", "User ID", "Type", "Amount", "Date"}, 0);
        recentTransactionsTable = new JTable(transactionsTableModel);
        transactionsScrollPane = new JScrollPane(recentTransactionsTable);
         transactionsScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Recent Transactions",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)
         ));
         customizeTable(recentTransactionsTable);

         // --- Button component ---
         addToCartButton = new JButton("Add Selected Game to Cart");
         addToCartButton.setFont(new Font("SansSerif", Font.BOLD, 14));
         addToCartButton.setBackground(new Color(40, 167, 69));
         addToCartButton.setForeground(Color.WHITE);
         addToCartButton.setFocusPainted(false);
         addToCartButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
         addToCartButton.setOpaque(true);
         addToCartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

          Color btnBg = new Color(40, 167, 69);
         Color btnHover = btnBg.darker();
         addToCartButton.addMouseListener(new MouseAdapter() {
             @Override public void mouseEntered(MouseEvent e) { addToCartButton.setBackground(btnHover); }
             @Override public void mouseExited(MouseEvent e) { addToCartButton.setBackground(btnBg); }
             @Override public void mousePressed(MouseEvent evt) { addToCartButton.setBackground(btnHover.darker()); }
             @Override public void mouseReleased(MouseEvent evt) { addToCartButton.setBackground(addToCartButton.getModel().isRollover() ? btnHover : btnBg); }
         });
    }


     private void customizeTable(JTable table) {
         table.setFont(new Font("SansSerif", Font.PLAIN, 13));
         table.setRowHeight(20);
         table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
         table.setFillsViewportHeight(true);
         table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         table.setGridColor(new Color(200, 200, 200));
         table.setBackground(Color.WHITE);
         table.getTableHeader().setBackground(new Color(220, 220, 220));
     }


    private void addComponents() {
        // Panel for summary labels
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        summaryPanel.setOpaque(false);
        summaryPanel.add(userCountLabel);
        summaryPanel.add(gameCountLabel);
        summaryPanel.add(totalRevenueLabel);

        // Panel for the game list table and the "Add to Cart" button
        JPanel gameListPanel = new JPanel(new BorderLayout(0, 8));
        gameListPanel.setOpaque(false);
        gameListPanel.add(gamesScrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(addToCartButton);
        gameListPanel.add(buttonPanel, BorderLayout.SOUTH);


        // Panel for orders and transactions tables
        // Use BoxLayout or another layout to ensure they share space reasonably,
        // maybe within a JSplitPane if you want resizable dividers.
        // For simple layout, put them side-by-side in a GridLayout panel.
        JPanel ordersTransactionsContainer = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 row, 2 columns, 10px horizontal gap
        ordersTransactionsContainer.setOpaque(false);
        ordersTransactionsContainer.add(ordersScrollPane);
        ordersTransactionsContainer.add(transactionsScrollPane);


        // Main panel layout (BoxLayout vertically)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
         contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));


        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12))); // Adjusted Space
        contentPanel.add(gameListPanel); // Contains the game table and its button
        contentPanel.add(Box.createRigidArea(new Dimension(0, 18))); // Adjusted Space
        contentPanel.add(ordersTransactionsContainer); // Add the container for orders/transactions


        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User currentUser = parentFrame.getCurrentUser();
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(DashboardPanel.this, "Please log in to add games to your cart.", "Login Required", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int selectedRow = gamesTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(DashboardPanel.this, "Please select a game from the list.", "No Game Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int gameId = (int) gamesTableModel.getValueAt(selectedRow, 0);
                    String gameTitle = (String) gamesTableModel.getValueAt(selectedRow, 1);

                    cartManagement.addGameToCart(currentUser.getUserId(), gameId);

                    JOptionPane.showMessageDialog(DashboardPanel.this, "'" + gameTitle + "' added to your cart!", "Added to Cart", JOptionPane.INFORMATION_MESSAGE);

                     // Optional: Automatically switch to the Cart panel after adding
                     // parentFrame.showPanel("Cart");


                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DashboardPanel.this, "Error adding game to cart: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
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
            gamesTableModel.setRowCount(0);
             for (Game game : games) {
                 // Format date for display in games table if you add it there later
                 // String releaseDateStr = (game.getReleaseDate() != null) ? new SimpleDateFormat("yyyy-MM-dd").format(game.getReleaseDate()) : "N/A";
                 gamesTableModel.addRow(new Object[]{
                         game.getGameId(),
                         game.getTitle(),
                         game.getDeveloper(),
                         game.getPlatform(),
                         String.format("%.2f", game.getPrice())
                         // , releaseDateStr // Add release date if you modify the table model columns
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
            if (currentUserId != -1) { // User view: Show only user's orders and transactions
                List<Order> allOrders = orderManagement.getAllOrders();
                 if (allOrders != null) {
                     for (Order order : allOrders) {
                        if (order.getUserId() == currentUserId) {
                             ordersTableModel.addRow(new Object[]{
                                     order.getOrderId(),
                                     // order.getUserId(), // Hide User ID in user's own history table
                                     String.format("%.2f", order.getTotalAmount()),
                                     order.getOrderDate()
                              });
                         }
                     }
                 }


                List<Transaction> allTransactions = transactionManagement.getAllTransactions();
                if (allTransactions != null) {
                    for (Transaction transaction : allTransactions) {
                         if (transaction.getUserId() == currentUserId) {
                            transactionsTableModel.addRow(new Object[]{
                                    transaction.getTransactionId(),
                                    transaction.getOrderId(),
                                    // transaction.getUserId(), // Hide User ID in user's own history table
                                    transaction.getTransactionType(),
                                    String.format("%.2f", transaction.getAmount()),
                                    transaction.getTransactionDate()
                             });
                         }
                     }
                }

                 // Adjust column identifiers for the user view
                 ordersTableModel.setColumnIdentifiers(new Vector<>(java.util.Arrays.asList("Order ID", "Total Amount", "Order Date")));
                 transactionsTableModel.setColumnIdentifiers(new Vector<>(java.util.Arrays.asList("Transaction ID", "Order ID", "Type", "Amount", "Date")));


                 // Make the "Add to Cart" button visible for logged-in users
                 addToCartButton.setVisible(true);

            } else { // Admin view: Show all orders and transactions
                List<Order> allOrders = orderManagement.getAllOrders();
                 if (allOrders != null) {
                     for (Order order : allOrders) {
                          ordersTableModel.addRow(new Object[]{
                                 order.getOrderId(),
                                 order.getUserId(),
                                 String.format("%.2f", order.getTotalAmount()),
                                 order.getOrderDate()
                          });
                      }
                 }


                 List<Transaction> allTransactions = transactionManagement.getAllTransactions();
                 if (allTransactions != null) {
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
                 // Ensure column names are correct for admin view (includes User ID)
                 ordersTableModel.setColumnIdentifiers(new Vector<>(java.util.Arrays.asList("Order ID", "User ID", "Total Amount", "Order Date")));
                 transactionsTableModel.setColumnIdentifiers(new Vector<>(java.util.Arrays.asList("Transaction ID", "Order ID", "User ID", "Type", "Amount", "Date")));

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
            e.printStackTrace();
        }
    }
}