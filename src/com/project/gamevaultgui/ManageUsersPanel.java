// No changes needed in ManageUsersPanel.java for this request.
// The code from the previous step should be fine:
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
import java.util.Vector;


public class ManageUsersPanel extends JPanel {

    private final UserManagement userManagement;

    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JScrollPane usersScrollPane;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public ManageUsersPanel(UserManagement userManagement) {
        this.userManagement = userManagement;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(230, 235, 240));

        initComponents();
        addComponents();
        // setupEventHandlers(); // No specific handlers yet

        // loadUsers(); // Load users when the panel is shown, not initialized
        // Handled by GameVaultFrame calling loadUsers() when the panel is shown.
    }

    private void initComponents() {
        usersTableModel = new DefaultTableModel(new Object[]{"ID", "Username", "Email", "Wallet Balance", "Member Since"}, 0) {
             @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usersTable = new JTable(usersTableModel);
        usersScrollPane = new JScrollPane(usersTable);
         usersScrollPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "All Users",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)
         ));
         customizeTable(usersTable);
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
                    String createdAtStr = (user.getCreatedAt() != null) ? DATE_FORMAT.format(user.getCreatedAt()) : "N/A";
                    usersTableModel.addRow(new Object[]{
                            user.getUserId(),
                            user.getUsername(),
                            user.getEmail(),
                            String.format("%.2f", user.getWalletBalance()),
                            createdAtStr
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}