package com.project.gamevaultgui;

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
import java.util.Vector; // Necessary for DefaultTableModel with Vector

public class ManageUsersPanel extends JPanel {

    private final UserManagement userManagement; // Need UserManagement to interact with users

    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JScrollPane usersScrollPane;

    // Maybe a delete button? The request was just to view details.
    // private JButton deleteUserButton; // Optional: Add delete button


    // Date format for display
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public ManageUsersPanel(UserManagement userManagement) { // Accept UserManagement
        this.userManagement = userManagement;

        setLayout(new BorderLayout(10, 10)); // Use BorderLayout
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        setBackground(new Color(230, 235, 240)); // Light background

        initComponents();
        addComponents();
        // setupEventHandlers(); // No specific handlers yet

        loadUsers(); // Load users when the panel is initialized
    }

    private void initComponents() {
        // --- Users Table ---
        // Columns for the users table, including more details
        usersTableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Email", "Wallet Balance", "Member Since"}, 0) {
             @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        usersTable = new JTable(usersTableModel);
        usersScrollPane = new JScrollPane(usersTable);
         usersScrollPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "All Users", // Title
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)
         ));
         customizeTable(usersTable); // Apply custom table style

         // Optional: Delete Button
         // deleteUserButton = new JButton("Delete Selected User");
         // styleButton(deleteUserButton, new Color(220, 53, 69), Color.WHITE);

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

     // Optional: Style button if adding delete
     /*
     private void styleButton(JButton button, Color bgColor, Color fgColor) {
         button.setBackground(bgColor);
         button.setForeground(fgColor);
         button.setFont(new Font("SansSerif", Font.BOLD, 12));
         button.setFocusPainted(false);
         button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
         button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         button.setOpaque(true);
         Color hoverColor = bgColor.darker();
         button.addMouseListener(new MouseAdapter() {
             @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
             @Override public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
              @Override public void mousePressed(MouseEvent evt) { button.setBackground(hoverColor.darker()); }
              @Override public void mouseReleased(MouseEvent evt) { button.setBackground(button.getModel().isRollover() ? hoverColor : bgColor); }
         });
     }
     */


    private void addComponents() {
        // Add the table scroll pane
        add(usersScrollPane, BorderLayout.CENTER);

        // Optional: Add button panel if adding delete
        /*
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(deleteUserButton);
        add(buttonPanel, BorderLayout.SOUTH);
        */
    }

    /*
    // Optional: Setup delete event handler
    private void setupEventHandlers() {
         deleteUserButton.addActionListener(e -> deleteSelectedUser());
          // Enable/disable delete button based on table selection
         usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                 deleteUserButton.setEnabled(usersTable.getSelectedRow() != -1);
            }
         });
         deleteUserButton.setEnabled(false); // Initially disabled
    }
     */

     /*
     // Optional: Implement delete logic
     private void deleteSelectedUser() {
         int selectedRow = usersTable.getSelectedRow();
         if (selectedRow == -1) {
             JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
             return;
         }

         int userId = (int) usersTableModel.getValueAt(selectedRow, 0);
         String username = (String) usersTableModel.getValueAt(selectedRow, 1);

         int confirmResult = JOptionPane.showConfirmDialog(this,
                 "Are you sure you want to delete user '" + username + "' (ID: " + userId + ")?\nThis action cannot be undone.",
                 "Confirm Delete User",
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.QUESTION_MESSAGE);

         if (confirmResult == JOptionPane.YES_OPTION) {
             try {
                 // Note: Deleting a user might require deleting associated carts, orders, transactions first due to foreign keys.
                 // Your storage and management layers need to handle these dependencies or the database needs CASCADE ON DELETE.
                 userManagement.deleteUser(userId);
                 JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                 loadUsers(); // Refresh the table
             } catch (Exception e) {
                 JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                 e.printStackTrace();
             }
         }
     }
      */


    /**
     * Loads all users from the database and populates the table.
     */
    public void loadUsers() {
        usersTableModel.setRowCount(0); // Clear existing data
        try {
            List<User> users = userManagement.getAllUsers();
            if (users != null) {
                for (User user : users) {
                    // Format date for display
                    String createdAtStr = (user.getCreatedAt() != null) ? DATE_FORMAT.format(user.getCreatedAt()) : "N/A";
                    usersTableModel.addRow(new Object[]{
                            user.getUserId(),
                            user.getUsername(),
                            user.getEmail(),
                            String.format("%.2f", user.getWalletBalance()), // Format wallet balance
                            createdAtStr
                    });
                }
            }
             // Optional: Update delete button state
             // if (deleteUserButton != null) {
             //    deleteUserButton.setEnabled(usersTable.getSelectedRow() != -1);
             // }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}