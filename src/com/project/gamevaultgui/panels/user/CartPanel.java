package com.project.gamevaultgui.panels.user;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.exceptions.CartEmptyException;
import com.project.gamevaultcli.management.CartManagement;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultgui.GameVaultFrame;
import com.project.gamevaultcli.entities.User; // Import User

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CartPanel extends JPanel {

    private final CartManagement cartManagement;
    private final GameManagement gameManagement;
    private final GameVaultFrame parentFrame; // Reference to parent frame

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel totalLabel;
    private JButton removeButton;
    private JButton checkoutButton;

    public CartPanel(CartManagement cartManagement, GameManagement gameManagement, GameVaultFrame parentFrame) {
        this.cartManagement = cartManagement;
        this.gameManagement = gameManagement;
        this.parentFrame = parentFrame; // Initialize parent frame

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Shopping Cart")); // Consider more styling later
        setBackground(new Color(250, 250, 250));

        initComponents();
        addComponents();
        setupEventHandlers();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new Object[] { "Game ID", "Title", "Price" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        cartTable = new JTable(tableModel);
        scrollPane = new JScrollPane(cartTable);
        customizeTable(cartTable); // Apply styling

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        removeButton = new JButton("Remove Selected Game");
        checkoutButton = new JButton("Checkout");

        // Style buttons
        styleButton(removeButton, new Color(255, 193, 7), Color.BLACK); // Warning color
        styleButton(checkoutButton, new Color(40, 167, 69), Color.WHITE); // Success color
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

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        Color hoverColor = bgColor.darker();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                button.setBackground(hoverColor.darker());
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                button.setBackground(button.getModel().isRollover() ? hoverColor : bgColor);
            }
        });
    }

    private void addComponents() {
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Panel for "Items in your cart:" label
        northPanel.setOpaque(false);
        northPanel.add(new JLabel("Items in your cart:"));
        add(northPanel, BorderLayout.NORTH); // Add label panel to NORTH

        add(scrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false); // Make background transparent
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Add padding above total/buttons

        southPanel.add(totalLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Add gap between buttons
        buttonPanel.setOpaque(false);
        buttonPanel.add(removeButton);
        buttonPanel.add(checkoutButton);

        southPanel.add(buttonPanel, BorderLayout.EAST);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        removeButton.addActionListener(e -> removeSelectedGame());
        checkoutButton.addActionListener(e -> checkoutCart());
    }

    public void loadCart(int userId) {
        tableModel.setRowCount(0); // Clear previous data
        double total = 0.0;
        boolean hasItems = false;

        try {
            List<Game> gamesInCart = cartManagement.getGamesInCart(userId);
            if (gamesInCart != null && !gamesInCart.isEmpty()) { // Check if list is not null and not empty
                hasItems = true;
                for (Game game : gamesInCart) {
                    tableModel.addRow(
                            new Object[] { game.getGameId(), game.getTitle(), String.format("%.2f", game.getPrice()) });
                    total += game.getPrice();
                }
            }

        } catch (CartEmptyException e) {
            // Cart is empty, this is expected sometimes. No need for a dialog here, total
            // will be $0.00.
            // JOptionPane.showMessageDialog(this, "Your cart is empty.", "Cart Status",
            // JOptionPane.INFORMATION_MESSAGE); // Optional dialog
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading cart: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            totalLabel.setText(String.format("Total: $%.2f", total));
            // Enable checkout if there are items in cart, regardless of total price
            checkoutButton.setEnabled(hasItems);
        }
    }

    private void removeSelectedGame() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            try {
                int gameId = (int) tableModel.getValueAt(selectedRow, 0);
                User currentUser = parentFrame.getCurrentUser();
                if (currentUser != null) {
                    cartManagement.removeGameFromCart(currentUser.getUserId(), gameId);
                    loadCart(currentUser.getUserId()); // Reload cart after removal
                    JOptionPane.showMessageDialog(this, "Game removed from cart.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "User not logged in.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error removing game from cart: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a game to remove.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkoutCart() {
        try {
            User currentUser = parentFrame.getCurrentUser();
            if (currentUser != null) {
                // The placeOrder method now handles checking balance and updating it in the DB.
                parentFrame.getOrderManagement().placeOrder(currentUser.getUserId());

                JOptionPane.showMessageDialog(this, "Order placed successfully!", "Checkout Complete",
                        JOptionPane.INFORMATION_MESSAGE);

                // After a successful order, refresh the cart display (which will be empty)
                loadCart(currentUser.getUserId());

                // Refresh the user's balance, owned games and UI elements
                parentFrame.refreshCurrentUserAndUI();

                // Refresh dashboard to update available games list (removing purchased games)
                parentFrame.refreshGameData();

                // Optional: Automatically navigate to the billing page after checkout
                // parentFrame.showPanel("Billing");

            } else {
                JOptionPane.showMessageDialog(this, "Please log in to checkout.", "Login Required",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (CartEmptyException e) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Cannot checkout.", "Checkout Failed",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException e) { // Catch Insufficient balance or other OrderManagement specific errors
            JOptionPane.showMessageDialog(this, "Checkout failed: " + e.getMessage(), "Checkout Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred during checkout: " + e.getMessage(),
                    "Checkout Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}