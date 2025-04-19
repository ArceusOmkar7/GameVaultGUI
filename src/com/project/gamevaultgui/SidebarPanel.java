
package com.project.gamevaultgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class SidebarPanel extends JPanel {

    private final GameVaultFrame parentFrame;
    private JButton dashboardButton;
    private JButton cartButton;
    private JButton yourOrdersButton; // Renamed from billingButton
    private JButton userButton;
    private JButton manageGamesButton;
    private JButton manageUsersButton;

    private JPanel logoPanel;
    private JLabel logoLabel;
    private JLabel titleLabel;

    private JButton currentlySelectedButton; // To track the selected button
    private Color defaultBgColor = new Color(60, 63, 65);
    private Color hoverBgColor = new Color(75, 78, 80);
    private Color selectedBgColor = new Color(255, 193, 7); // Orange accent color
    private Color defaultFgColor = Color.WHITE;
    private Color selectedFgColor = Color.BLACK; // Black text for selected


    public SidebarPanel(GameVaultFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, getHeight()));
        setBackground(defaultBgColor);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));

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
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
         logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        logoLabel = new JLabel("VG");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 30));
        logoLabel.setForeground(selectedBgColor);

        titleLabel = new JLabel("Game Vault");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(defaultFgColor);

        logoPanel.add(logoLabel);
        logoPanel.add(titleLabel);
         logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sidebar Buttons
        dashboardButton = createSidebarButton("Dashboard", null);
        cartButton = createSidebarButton("Cart", null);
        yourOrdersButton = createSidebarButton("Your Orders", null); // Renamed button
        userButton = createSidebarButton("User Profile", null);

        // Admin-specific buttons
        manageGamesButton = createSidebarButton("Manage Games", null);
        manageUsersButton = createSidebarButton("Manage Users", null);
    }

     private JButton createSidebarButton(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(defaultBgColor);
        button.setForeground(defaultFgColor);
        button.setFont(new Font("SansSerif", Font.PLAIN, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        button.setOpaque(true); // Ensure background is painted


        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                if (button != currentlySelectedButton) {
                    button.setBackground(hoverBgColor);
                }
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                 if (button != currentlySelectedButton) {
                     button.setBackground(defaultBgColor);
                 }
            }
             @Override
             public void mousePressed(MouseEvent evt) {
                  button.setBackground(hoverBgColor.darker());
             }
             @Override
             public void mouseReleased(MouseEvent evt) {
                 if (button != currentlySelectedButton) {
                     button.setBackground(button.getModel().isRollover() ? hoverBgColor : defaultBgColor);
                 } else {
                     button.setBackground(selectedBgColor);
                 }
             }
        });


        return button;
    }


    private void addComponents() {
        add(logoPanel);

        add(Box.createRigidArea(new Dimension(0, 20)));

        // User Buttons
        add(dashboardButton);
        add(Box.createRigidArea(new Dimension(0, 3)));
        add(cartButton);
        add(Box.createRigidArea(new Dimension(0, 3)));
        add(yourOrdersButton); // Add the renamed button
        add(Box.createRigidArea(new Dimension(0, 3)));
        add(userButton);

        // Admin Buttons
        add(Box.createRigidArea(new Dimension(0, 15)));
         JLabel adminLabel = new JLabel("Admin Tools:");
         adminLabel.setForeground(defaultFgColor);
         adminLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
         adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         add(adminLabel);
         add(Box.createRigidArea(new Dimension(0, 5)));
        add(manageGamesButton);
        add(Box.createRigidArea(new Dimension(0, 3)));
        add(manageUsersButton);

        add(Box.createVerticalGlue());
    }

    private void setupEventHandlers() {
        ActionListener buttonActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                // Map button text to the EXACT CardLayout key
                String panelName;
                switch(source.getText()) {
                    case "Dashboard":       panelName = "Dashboard"; break;
                    case "Cart":            panelName = "Cart"; break;
                    case "Your Orders":     panelName = "Billing"; break; // Map "Your Orders" button to "Billing" panel key
                    case "User Profile":    panelName = "User Profile"; break;
                    case "Manage Games":    panelName = "Manage Games"; break;
                    case "Manage Users":    panelName = "Manage Users"; break;
                    default:                panelName = ""; break; // Should not happen
                }
                 if (!panelName.isEmpty()) {
                     parentFrame.showPanel(panelName);
                     // Highlighting is handled by updateUIState in GameVaultFrame
                 }
            }
        };

        dashboardButton.addActionListener(buttonActionListener);
        cartButton.addActionListener(buttonActionListener);
        yourOrdersButton.addActionListener(buttonActionListener); // Add listener to the renamed button
        userButton.addActionListener(buttonActionListener);
        manageGamesButton.addActionListener(buttonActionListener);
        manageUsersButton.addActionListener(buttonActionListener);
    }

    public void highlightButton(JButton button) {
        resetButtonColors();
        button.setBackground(selectedBgColor);
        button.setForeground(selectedFgColor);
        currentlySelectedButton = button;
    }

     public void resetButtonColors() {
         JButton[] buttons = {dashboardButton, cartButton, yourOrdersButton, userButton, manageGamesButton, manageUsersButton}; // Include the new button
         for (JButton button : buttons) {
             button.setBackground(defaultBgColor);
             button.setForeground(defaultFgColor);
         }
     }

    // Add specific highlight methods for GameVaultFrame to call (update method name)
    public void highlightDashboardButton() { highlightButton(dashboardButton); }
    public void highlightCartButton() { highlightButton(cartButton); }
    public void highlightYourOrdersButton() { highlightButton(yourOrdersButton); } // Renamed highlight method
    public void highlightUserButton() { highlightButton(userButton); }
    public void highlightManageGamesButton() { highlightButton(manageGamesButton); }
    public void highlightManageUsersButton() { highlightButton(manageUsersButton); }


    // Method to update sidebar visibility for a user (update button reference)
    public void updateSidebarForUser() {
        dashboardButton.setVisible(true);
        cartButton.setVisible(true);
        yourOrdersButton.setVisible(true); // Show the new button
        userButton.setVisible(true);
        manageGamesButton.setVisible(false);
        manageUsersButton.setVisible(false);
        resetButtonColors();
        revalidate();
        repaint();
    }

     // Method to update sidebar visibility for an admin (update button reference)
    public void updateSidebarForAdmin() {
        dashboardButton.setVisible(true);
        cartButton.setVisible(false);
        yourOrdersButton.setVisible(false); // Hide in admin view (or show all orders if admin view is different)
        userButton.setVisible(false);
        manageGamesButton.setVisible(true);
        manageUsersButton.setVisible(true);
        resetButtonColors();
        revalidate();
        repaint();
    }

     public void hideAllButtons() {
         JButton[] buttons = {dashboardButton, cartButton, yourOrdersButton, userButton, manageGamesButton, manageUsersButton}; // Include the new button
         for (JButton button : buttons) {
             button.setVisible(false);
         }
         revalidate();
         repaint();
     }
}