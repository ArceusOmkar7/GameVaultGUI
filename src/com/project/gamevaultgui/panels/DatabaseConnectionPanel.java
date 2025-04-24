package com.project.gamevaultgui.panels;

import com.project.gamevaultcli.helpers.DBUtil;
import com.project.gamevaultgui.GameVaultFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConnectionPanel extends JPanel {
    private JTextField dbNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private JLabel statusLabel;
    private GameVaultFrame parentFrame;

    public DatabaseConnectionPanel(GameVaultFrame parentFrame) {
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(230, 235, 240));

        initComponents();
    }

    private void initComponents() {
        // Create a panel with GridBagLayout for the form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(230, 235, 240));

        // Title label
        JLabel titleLabel = new JLabel("Database Connection Setup");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Database name field
        JLabel dbNameLabel = new JLabel("Database Name:");
        dbNameField = new JTextField(20);
        dbNameField.setText("gamevaultdb"); // Default value

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        usernameField.setText("root"); // Default value

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        // Connect button
        connectButton = new JButton("Connect to Database");
        styleButton(connectButton);
        connectButton.addActionListener(e -> connectToDatabase());

        // Status label for feedback
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        // Add components to the form panel using GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add title with appropriate constraints
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        // Database name row
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(dbNameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(dbNameField, gbc);

        // Username row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(usernameField, gbc);

        // Password row
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(passwordField, gbc);

        // Connect button row
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(connectButton, gbc);

        // Status label row
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        // Center the form panel
        JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centeringPanel.setBackground(new Color(230, 235, 240));
        centeringPanel.add(formPanel);

        // Add the centered form panel to this panel
        add(centeringPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void connectToDatabase() {
        String dbName = dbNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input
        if (dbName.isEmpty() || username.isEmpty()) {
            statusLabel.setText("Database name and username are required!");
            return;
        }

        // Set the credentials in DBUtil
        DBUtil.setDatabaseCredentials(dbName, username, password);

        connectButton.setEnabled(false);
        statusLabel.setText("Connecting to database...");

        // Try to connect to the database in a background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    DBUtil.getConnection();
                    return true;
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        statusLabel.setText("Connection successful!");
                        statusLabel.setForeground(new Color(0, 128, 0)); // Green

                        // Initialize default data
                        parentFrame.getGameVaultManagement().initializeData();

                        // Proceed to the role selection screen after a brief delay
                        Timer timer = new Timer(1000, e -> parentFrame.showPanel("RoleSelection"));
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        statusLabel.setText("Connection failed. Please check your credentials.");
                        statusLabel.setForeground(Color.RED);
                        connectButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    connectButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }
}