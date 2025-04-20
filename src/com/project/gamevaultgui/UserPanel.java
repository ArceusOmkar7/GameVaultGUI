package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.management.UserManagement; // Import UserManagement
import com.project.gamevaultcli.management.TransactionManagement; // Import TransactionManagement

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime; // Import LocalDateTime
import com.project.gamevaultcli.entities.Transaction; // Import Transaction


public class UserPanel extends JPanel {

    private JLabel usernameKeyLabel;
    private JLabel emailKeyLabel;
    private JLabel walletBalanceKeyLabel;
    private JLabel createdAtKeyLabel;

    private JLabel usernameValueLabel;
    private JLabel emailValueLabel;
    private JLabel walletBalanceValueLabel;
    private JLabel createdAtValueLabel;

    private JButton editButton;
    private JButton logoutButton;
    private JButton addBalanceButton; // Add the "Add Balance" button

    private final GameVaultFrame parentFrame; // Keep reference to the parent frame
    // We need management classes to update balance and add transactions
    private final UserManagement userManagement;
    private final TransactionManagement transactionManagement;


    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Accept management classes in constructor
    public UserPanel(GameVaultFrame parentFrame, UserManagement userManagement, TransactionManagement transactionManagement) {
        this.parentFrame = parentFrame;
        this.userManagement = userManagement;
        this.transactionManagement = transactionManagement;


        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(60, 63, 65), 2, true),
                        "User Information",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("SansSerif", Font.BOLD, 20),
                        new Color(60, 63, 65)
                )
        ));
        setBackground(new Color(250, 250, 250));

        initComponents();
        addComponents();
        setupEventHandlers();

        setLoggedInState(false); // Hide components initially
    }

    private void initComponents() {
        usernameKeyLabel = new JLabel("Username:");
        emailKeyLabel = new JLabel("Email:");
        walletBalanceKeyLabel = new JLabel("Wallet Balance:");
        createdAtKeyLabel = new JLabel("Member Since:");

        usernameValueLabel = new JLabel("");
        emailValueLabel = new JLabel("");
        walletBalanceValueLabel = new JLabel("");
        createdAtValueLabel = new JLabel("");

        Font keyFont = new Font("SansSerif", Font.BOLD, 14);
        Font valueFont = new Font("SansSerif", Font.PLAIN, 14);
        Color keyColor = new Color(80, 80, 80);
        Color valueColor = new Color(0, 0, 0);

        usernameKeyLabel.setFont(keyFont); usernameKeyLabel.setForeground(keyColor);
        emailKeyLabel.setFont(keyFont); emailKeyLabel.setForeground(keyColor);
        walletBalanceKeyLabel.setFont(keyFont); walletBalanceKeyLabel.setForeground(keyColor);
        createdAtKeyLabel.setFont(keyFont); createdAtKeyLabel.setForeground(keyColor);

        usernameValueLabel.setFont(valueFont); usernameValueLabel.setForeground(valueColor);
        emailValueLabel.setFont(valueFont); emailValueLabel.setForeground(valueColor);
        walletBalanceValueLabel.setFont(valueFont); walletBalanceValueLabel.setForeground(valueColor);
        createdAtValueLabel.setFont(valueFont); createdAtValueLabel.setForeground(valueColor);


        editButton = new JButton("Edit Username");
        logoutButton = new JButton("Logout");
        addBalanceButton = new JButton("Add Balance"); // Initialize the new button

        styleButton(editButton, new Color(0, 123, 255), Color.WHITE);
        styleButton(logoutButton, new Color(220, 53, 69), Color.WHITE);
        styleButton(addBalanceButton, new Color(40, 167, 69), Color.WHITE); // Green for Add Balance
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
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
             @Override public void mousePressed(MouseEvent evt) { button.setBackground(hoverColor.darker()); }
             @Override public void mouseReleased(MouseEvent evt) { button.setBackground(button.getModel().isRollover() ? hoverColor : bgColor); }
        });
    }


    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 15, 6, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Profile picture placeholder
        JPanel profilePicPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (parentFrame.getCurrentUser() != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int diameter = Math.min(getWidth(), getHeight());
                    int x = (getWidth() - diameter) / 2;
                    int y = (getHeight() - diameter) / 2;
                    g2d.setColor(new Color(255, 235, 200));
                    g2d.fillOval(x, y, diameter, diameter);

                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 36));
                    String initial = "U";
                    if (parentFrame.getCurrentUser().getUsername() != null && !parentFrame.getCurrentUser().getUsername().isEmpty()) {
                         initial = parentFrame.getCurrentUser().getUsername().substring(0, 1).toUpperCase();
                    }
                     FontMetrics fm = g2d.getFontMetrics();
                     int stringWidth = fm.stringWidth(initial);
                     int stringHeight = fm.getAscent() - fm.getLeading() - fm.getDescent();
                     int textX = x + (diameter - stringWidth) / 2;
                     int textY = y + (diameter + stringHeight) / 2;
                     g2d.drawString(initial, textX, textY);

                    g2d.dispose();
                }
            }
             @Override
            public Dimension getPreferredSize() { return new Dimension(70, 70); }
             @Override
            public Dimension getMaximumSize() { return getPreferredSize(); }
        };
        profilePicPanel.setOpaque(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 15, 10, 15);
        add(profilePicPanel, gbc);

        // --- User Details (aligned key-value pairs) ---
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST; // Align values left
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 15, 4, 15); // Smaller padding

        // Username Row
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        add(usernameKeyLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        add(usernameValueLabel, gbc);

        // Email Row
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        add(emailKeyLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        add(emailValueLabel, gbc);

        // Wallet Balance Row
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        add(walletBalanceKeyLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        add(walletBalanceValueLabel, gbc);

        // Member Since Row
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        add(createdAtKeyLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        add(createdAtValueLabel, gbc);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(editButton);
        buttonPanel.add(addBalanceButton); // Add the new button
        buttonPanel.add(logoutButton);


        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 15, 15, 15);
        add(buttonPanel, gbc);

        // Add vertical glue
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createVerticalGlue(), gbc);
    }

    private void setupEventHandlers() {
        editButton.addActionListener(e -> showEditUsernameDialog());
        logoutButton.addActionListener(e -> parentFrame.logout());
        addBalanceButton.addActionListener(e -> showAddBalanceDialog()); // Add listener for the new button
    }

    private void setLoggedInState(boolean isLoggedIn) {
         usernameKeyLabel.setVisible(isLoggedIn);
         emailKeyLabel.setVisible(isLoggedIn);
         walletBalanceKeyLabel.setVisible(isLoggedIn);
         createdAtKeyLabel.setVisible(isLoggedIn);
         usernameValueLabel.setVisible(isLoggedIn);
         emailValueLabel.setVisible(isLoggedIn);
         walletBalanceValueLabel.setVisible(isLoggedIn);
         createdAtValueLabel.setVisible(isLoggedIn);
         editButton.setVisible(isLoggedIn);
         logoutButton.setVisible(isLoggedIn);
         addBalanceButton.setVisible(isLoggedIn); // Control visibility of add balance button
         repaint();
    }


    /**
     * Loads and displays the user information on the panel.
     *
     * @param user The User object whose information should be displayed.
     *             If null, the panel will display empty fields and hide controls.
     */
    public void loadUserInfo(User user) {
        if (user != null) {
            usernameValueLabel.setText(user.getUsername());
            emailValueLabel.setText(user.getEmail());
            walletBalanceValueLabel.setText(String.format("$%.2f", user.getWalletBalance()));
             Date createdAtDate = user.getCreatedAt();
             if (createdAtDate != null) {
                createdAtValueLabel.setText(DATE_FORMAT.format(createdAtDate));
             } else {
                createdAtValueLabel.setText("N/A");
             }
             setLoggedInState(true);
        } else {
            usernameValueLabel.setText("");
            emailValueLabel.setText("");
            walletBalanceValueLabel.setText("");
            createdAtValueLabel.setText("");
            setLoggedInState(false);
        }
         revalidate();
         repaint();
    }

    private void showEditUsernameDialog() {
        if (parentFrame.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentUsername = parentFrame.getCurrentUser().getUsername();
        JTextField usernameField = new JTextField(currentUsername);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Enter new username:"), BorderLayout.NORTH);
        panel.add(usernameField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Username",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newUsername = usernameField.getText().trim();
            if (newUsername.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            } else if (!newUsername.equals(currentUsername)) {
                // Call parent frame to handle the update logic
                parentFrame.updateCurrentUserUsername(newUsername);
            }
        }
    }

    // New method to handle adding balance
    private void showAddBalanceDialog() {
        if (parentFrame.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt user for the amount
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to add to wallet:", "Add Balance", JOptionPane.PLAIN_MESSAGE);

        if (amountStr != null) { // Check if user didn't cancel
            try {
                float amount = Float.parseFloat(amountStr.trim());

                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive amount.", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
                } else {
                    // Call parent frame to handle the balance update logic
                    parentFrame.addBalanceToCurrentUser(amount);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}