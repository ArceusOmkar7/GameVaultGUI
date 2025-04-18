package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.exceptions.CartEmptyException;
import com.project.gamevaultcli.management.CartManagement;
import com.project.gamevaultcli.management.GameManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class CartPanel extends JPanel {

    private final CartManagement cartManagement;
    private final GameManagement gameManagement;
    private final GameVaultFrame parentFrame;

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane; // Make scroll pane instance variable
    private JLabel totalLabel;
    private JButton removeButton;
    private JButton checkoutButton;

    public CartPanel(CartManagement cartManagement, GameManagement gameManagement, GameVaultFrame parentFrame) {
        this.cartManagement = cartManagement;
        this.gameManagement = gameManagement;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Shopping Cart"));

        initComponents();
        addComponents();
        setupEventHandlers();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(new Object[]{"Game ID", "Title", "Price"}, 0);
        cartTable = new JTable(tableModel);
        scrollPane = new JScrollPane(cartTable); // Instantiate scroll pane here

        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        removeButton = new JButton("Remove Selected Game");
        checkoutButton = new JButton("Checkout");
    }

    private void addComponents() {
        add(new JLabel("Items in your cart:"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER); // Use the instance variable scrollPane

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(totalLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
        try {
            List<Game> gamesInCart = cartManagement.getGamesInCart(userId);
            for (Game game : gamesInCart) {
                tableModel.addRow(new Object[]{game.getGameId(), game.getTitle(), String.format("%.2f", game.getPrice())});
                total += game.getPrice();
            }
        } catch (CartEmptyException e) {
            // Cart is empty, nothing to load
             JOptionPane.showMessageDialog(this, "Your cart is empty.", "Cart Status", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading cart: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
             totalLabel.setText(String.format("Total: $%.2f", total));
        }
    }

    private void removeSelectedGame() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            try {
                int gameId = (int) tableModel.getValueAt(selectedRow, 0);
                cartManagement.removeGameFromCart(parentFrame.getCurrentUser().getUserId(), gameId);
                loadCart(parentFrame.getCurrentUser().getUserId()); // Reload cart after removal
                JOptionPane.showMessageDialog(this, "Game removed from cart.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error removing game from cart: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a game to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkoutCart() {
        try {
            if (parentFrame.getCurrentUser() != null) {
                parentFrame.getOrderManagement().placeOrder(parentFrame.getCurrentUser().getUserId());
                JOptionPane.showMessageDialog(this, "Order placed successfully!", "Checkout Complete", JOptionPane.INFORMATION_MESSAGE);
                loadCart(parentFrame.getCurrentUser().getUserId()); // Clear cart display
                parentFrame.showPanel("Billing"); // Optionally show billing after checkout
            } else {
                 JOptionPane.showMessageDialog(this, "Please log in to checkout.", "Login Required", JOptionPane.WARNING_MESSAGE);
            }
        } catch (CartEmptyException e) {
             JOptionPane.showMessageDialog(this, "Your cart is empty. Cannot checkout.", "Checkout Failed", JOptionPane.WARNING_MESSAGE);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Checkout failed: " + e.getMessage(), "Checkout Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}