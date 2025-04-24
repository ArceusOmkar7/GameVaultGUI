package com.project.gamevaultgui.panels;

import javax.swing.*;

import com.project.gamevaultgui.GameVaultFrame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NavbarPanel extends JPanel {

    private final GameVaultFrame parentFrame;

    private JLabel brandLabel;
    private JLabel pageTitleLabel;
    private JLabel greetingLabel;
    private JLabel profileIconLabel; // Placeholder for profile icon

    public NavbarPanel(String initialGreeting, GameVaultFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(getWidth(), 50));

        initComponents(initialGreeting);
        addComponents();
        setupEventHandlers(); // Add event handlers
    }

    private void initComponents(String initialGreeting) {
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        brandPanel.setOpaque(false);

        JLabel brandLogoLabel = new JLabel("VG");
        brandLogoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        brandLogoLabel.setForeground(new Color(255, 193, 7));

        brandLabel = new JLabel("Game Vault");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        brandLabel.setForeground(new Color(50, 50, 50));

        pageTitleLabel = new JLabel("Welcome");
        pageTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        pageTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pageTitleLabel.setForeground(new Color(50, 50, 50));

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        userInfoPanel.setOpaque(false);

        greetingLabel = new JLabel(initialGreeting);
        greetingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        greetingLabel.setForeground(new Color(80, 80, 80));

        profileIconLabel = new JLabel("👤");
        profileIconLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        profileIconLabel.setForeground(new Color(60, 63, 65));
        profileIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileIconLabel.setVisible(false);
    }

    private void addComponents() {
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        brandPanel.setOpaque(false);
        JLabel brandLogoLabel = new JLabel("VG");
        brandLogoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        brandLogoLabel.setForeground(new Color(255, 193, 7));
        JLabel brandNameLabel = new JLabel("Game Vault");
        brandNameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        brandNameLabel.setForeground(new Color(50, 50, 50));
        brandPanel.add(brandLogoLabel);
        brandPanel.add(brandNameLabel);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        userInfoPanel.setOpaque(false);
        userInfoPanel.add(greetingLabel);
        userInfoPanel.add(profileIconLabel);

        add(brandPanel, BorderLayout.WEST);
        add(pageTitleLabel, BorderLayout.CENTER);
        add(userInfoPanel, BorderLayout.EAST);
    }

    private void setupEventHandlers() {
        profileIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show menu if *either* a user is logged in *or* it's the admin view
                if (parentFrame.getCurrentUser() != null || parentFrame.isAdmin()) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem logoutItem = new JMenuItem("Logout"); // Label can just be "Logout"
                    logoutItem.addActionListener(ae -> parentFrame.logout()); // Call the frame's logout
                    menu.add(logoutItem);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public void setGreeting(String greeting) {
        greetingLabel.setText(greeting);
    }

    public void setPageTitle(String title) {
        pageTitleLabel.setText(title);
    }

    public void showProfileIcon(boolean show) {
        profileIconLabel.setVisible(show);
    }
}