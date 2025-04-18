package com.project.gamevaultgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SidebarPanel extends JPanel {

    private final GameVaultFrame parentFrame;
    private JButton dashboardButton;
    private JButton cartButton;
    private JButton billingButton;
    private JButton userButton; // Button for UserPanel
    // Add Admin-specific buttons (placeholders for now)
    private JButton manageGamesButton;
    private JButton manageUsersButton;

    private JPanel logoPanel;
    private JLabel logoLabel;
    private JLabel titleLabel;

    private JButton currentlySelectedButton; // To track the selected button

    public SidebarPanel(GameVaultFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, getHeight()));
        setBackground(new Color(60, 63, 65));

        initComponents();
        addComponents();
        setupEventHandlers();

        setVisible(false); // Hide sidebar initially
    }

    private void initComponents() {
        // Logo and Title Panel
        logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        logoPanel.setBackground(new Color(75, 78, 80));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        logoLabel = new JLabel("YG");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 30));
        logoLabel.setForeground(new Color(255, 193, 7));

        titleLabel = new JLabel("Game Vault");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        logoPanel.add(logoLabel);
        logoPanel.add(titleLabel);

        // Sidebar Buttons (initially created but their visibility will be controlled)
        dashboardButton = createSidebarButton("Dashboard", null);
        cartButton = createSidebarButton("Cart", null);
        billingButton = createSidebarButton("Billing", null);
        userButton = createSidebarButton("User", null);

        // Admin-specific buttons
        manageGamesButton = createSidebarButton("Manage Games", null);
        manageUsersButton = createSidebarButton("Manage Users", null);
    }

     private JButton createSidebarButton(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(new Color(60, 63, 65));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 78, 80));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                 if (button != currentlySelectedButton) {
                     button.setBackground(new Color(60, 63, 65));
                 }
            }
        });

        return button;
    }


    private void addComponents() {
        add(logoPanel);

        add(Box.createRigidArea(new Dimension(0, 20)));
        add(dashboardButton);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(cartButton);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(billingButton);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(userButton);

        // Add admin buttons (initially hidden)
        add(Box.createRigidArea(new Dimension(0, 10))); // More space for admin section
        add(new JLabel("Admin Tools:"){{ setForeground(Color.WHITE); setAlignmentX(Component.CENTER_ALIGNMENT); }});
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(manageGamesButton);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(manageUsersButton);


        add(Box.createVerticalGlue());
    }

    private void setupEventHandlers() {
        ActionListener buttonActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                String panelName = source.getText().replace("Manage ", ""); // Adjust panel name for admin buttons
                parentFrame.showPanel(panelName);
                highlightButton(source);
            }
        };

        dashboardButton.addActionListener(buttonActionListener);
        cartButton.addActionListener(buttonActionListener);
        billingButton.addActionListener(buttonActionListener);
        userButton.addActionListener(buttonActionListener);
        manageGamesButton.addActionListener(buttonActionListener); // Add listener for admin buttons
        manageUsersButton.addActionListener(buttonActionListener);
    }

    public void highlightButton(JButton button) {
        // Reset all buttons
        dashboardButton.setBackground(new Color(60, 63, 65));
        cartButton.setBackground(new Color(60, 63, 65));
        billingButton.setBackground(new Color(60, 63, 65));
        userButton.setBackground(new Color(60, 63, 65));
        manageGamesButton.setBackground(new Color(60, 63, 65));
        manageUsersButton.setBackground(new Color(60, 63, 65));


        // Highlight the selected button
        button.setBackground(new Color(255, 193, 7));
        currentlySelectedButton = button;
    }

    // Method to update sidebar visibility for a user
    public void updateSidebarForUser() {
        dashboardButton.setVisible(true);
        cartButton.setVisible(true);
        billingButton.setVisible(true);
        userButton.setVisible(true);
        manageGamesButton.setVisible(false); // Hide admin buttons
        manageUsersButton.setVisible(false);
         revalidate();
         repaint();
    }

     // Method to update sidebar visibility for an admin
    public void updateSidebarForAdmin() {
        dashboardButton.setVisible(true);
        cartButton.setVisible(false); // Hide user-specific buttons
        billingButton.setVisible(false);
        userButton.setVisible(false);
        manageGamesButton.setVisible(true); // Show admin buttons
        manageUsersButton.setVisible(true);
         revalidate();
         repaint();
    }
}