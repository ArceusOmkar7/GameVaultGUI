package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.User;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class UserPanel extends JPanel {

    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JLabel walletBalanceLabel;
    private JLabel createdAtLabel;

    public UserPanel() {
        setLayout(new GridLayout(0, 1, 10, 10)); // Single column, with vertical spacing
        setBorder(BorderFactory.createTitledBorder("User Information"));
        setPreferredSize(new Dimension(300, 200)); // Set a preferred size
        setMaximumSize(getPreferredSize()); // Prevent panel from growing unnecessarily

        initComponents();
        addComponents();
    }

    private void initComponents() {
        usernameLabel = new JLabel("Username: ");
        emailLabel = new JLabel("Email: ");
        walletBalanceLabel = new JLabel("Wallet Balance: ");
        createdAtLabel = new JLabel("Member Since: ");

        // Optional: Set font or styling
        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);
        usernameLabel.setFont(labelFont);
        emailLabel.setFont(labelFont);
        walletBalanceLabel.setFont(labelFont);
        createdAtLabel.setFont(labelFont);
    }

    private void addComponents() {
        add(usernameLabel);
        add(emailLabel);
        add(walletBalanceLabel);
        add(createdAtLabel);
         add(Box.createVerticalGlue()); // Push components to the top
    }

    public void loadUserInfo(User user) {
        if (user != null) {
            usernameLabel.setText("Username: " + user.getUsername());
            emailLabel.setText("Email: " + user.getEmail());
            walletBalanceLabel.setText(String.format("Wallet Balance: $%.2f", user.getWalletBalance()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            createdAtLabel.setText("Member Since: " + dateFormat.format(user.getCreatedAt()));
        } else {
            // Clear information if no user is logged in
            usernameLabel.setText("Username: ");
            emailLabel.setText("Email: ");
            walletBalanceLabel.setText("Wallet Balance: ");
            createdAtLabel.setText("Member Since: ");
        }
         revalidate(); // Revalidate to update layout
         repaint(); // Repaint to show changes
    }
}