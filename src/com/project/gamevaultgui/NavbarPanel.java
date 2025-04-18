package com.project.gamevaultgui;

import javax.swing.*;
import java.awt.*;

public class NavbarPanel extends JPanel {

    private final GameVaultFrame parentFrame;

    private JLabel brandLabel;
    private JLabel pageTitleLabel;
    private JLabel greetingLabel;
    private JLabel profileIconLabel; // Placeholder for profile icon

    public NavbarPanel(String initialGreeting, GameVaultFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(Color.WHITE);

        initComponents(initialGreeting);
        addComponents();
        // No event handlers for profile icon in this version
    }

    private void initComponents(String initialGreeting) {
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        brandPanel.setOpaque(false);

        JLabel brandLogoLabel = new JLabel("VG");
        brandLogoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        brandLogoLabel.setForeground(new Color(255, 193, 7));

        brandLabel = new JLabel("Game Vault");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        brandLabel.setForeground(Color.BLACK);

        brandPanel.add(brandLogoLabel);
        brandPanel.add(brandLabel);

        pageTitleLabel = new JLabel("Welcome");
        pageTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        pageTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        userInfoPanel.setOpaque(false);

        greetingLabel = new JLabel(initialGreeting);
        greetingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        greetingLabel.setForeground(Color.BLACK);

        profileIconLabel = new JLabel("👤"); // Placeholder
        profileIconLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        profileIconLabel.setForeground(Color.BLACK);
        profileIconLabel.setVisible(false); // Hide profile icon initially
    }

    private void addComponents() {
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        brandPanel.setOpaque(false);

        JLabel brandLogoLabel = new JLabel("VG");
        brandLogoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        brandLogoLabel.setForeground(new Color(255, 193, 7));

        JLabel brandNameLabel = new JLabel("Game Vault");
        brandNameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        brandNameLabel.setForeground(Color.BLACK);

        brandPanel.add(brandLogoLabel);
        brandPanel.add(brandNameLabel);

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        userInfoPanel.setOpaque(false);

        userInfoPanel.add(greetingLabel);
        userInfoPanel.add(profileIconLabel);


        add(brandPanel, BorderLayout.WEST);
        add(pageTitleLabel, BorderLayout.CENTER);
        add(userInfoPanel, BorderLayout.EAST);
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

    // Optional: Method to update profile picture or admin icon
    // public void setIcon(ImageIcon icon) {
    //     profileIconLabel.setIcon(icon);
    // }
}