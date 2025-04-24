package com.project.gamevaultgui.panels.admin;

import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.management.UserManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class ManageUsersPanel extends JPanel {

    private final UserManagement userManagement;

    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JScrollPane usersScrollPane;
    private JButton deleteButton;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public ManageUsersPanel(UserManagement userManagement) {
        this.userManagement = userManagement;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(230, 235, 240));

        initComponents();
        addComponents();
        setupEventHandlers();

        // loadUsers(); // Load users when the panel is shown, not initialized
        // Handled by GameVaultFrame calling loadUsers() when the panel is shown.
    }

    private void initComponents() {
        usersTableModel = new DefaultTableModel(
                new Object[] { "ID", "Username", "Email", "Wallet Balance", "Member Since" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(usersTableModel);
        usersScrollPane = new JScrollPane(usersTable);
        usersScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "All Users",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)));
        customizeTable(usersTable);

        // Initialize delete button
        deleteButton = new JButton("Delete Selected User");
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteButton.setBackground(new Color(220, 53, 69)); // Red color for delete button
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false); // Initially disabled until a row is selected
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
        table.setAutoCreateRowSorter(true); // Enable sorting
    }

    private void addComponents() {
        add(usersScrollPane, BorderLayout.CENTER);

        // Add the delete button to a panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(230, 235, 240));
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Table row selection listener - controls delete button
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(usersTable.getSelectedRow() != -1);
            }
        });

        // Delete Button Action
        deleteButton.addActionListener(e -> deleteSelectedUser());
    }

    /**
     * Handles deleting the selected user.
     */
    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get ID directly from the table model
        int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
        String username = (String) usersTableModel.getValueAt(selectedRow, 1);

        int confirmResult = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user '" + username + "' (ID: " + userId
                        + ")?\nThis action cannot be undone and will delete all associated data including orders and transactions.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                userManagement.deleteUser(userId);
                JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers(); // Refresh the table
                usersTable.clearSelection(); // Clear selection after deletion
                deleteButton.setEnabled(false); // Disable delete button
            } catch (Exception e) {
                // Provide specific message if deletion fails due to constraint violation
                if (e.getMessage() != null && e.getMessage().contains("foreign key constraint fails")) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete user because they have active orders or transactions. " +
                                    "Please delete those records first.",
                            "Deletion Failed",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads all users from the database and populates the table.
     * This method should be called when the panel is made visible.
     */
    public void loadUsers() {
        usersTableModel.setRowCount(0); // Clear existing data
        try {
            List<User> users = userManagement.getAllUsers();
            if (users != null) {
                for (User user : users) {
                    String createdAtStr = (user.getCreatedAt() != null) ? DATE_FORMAT.format(user.getCreatedAt())
                            : "N/A";
                    usersTableModel.addRow(new Object[] {
                            user.getUserId(),
                            user.getUsername(),
                            user.getEmail(),
                            String.format("%.2f", user.getWalletBalance()),
                            createdAtStr
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}