package com.project.gamevaultgui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.project.gamevaultgui.GameVaultFrame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends JPanel {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorMessageLabel; // Label to display login errors
    private JLabel signupLink; // Label for the signup link
    private JLabel credentialsLabel; // Label to display default credentials

    private final GameVaultFrame parentFrame; // Reference to the parent frame

    public LoginPanel(GameVaultFrame parentFrame) {
        this.parentFrame = parentFrame;

        setLayout(new GridBagLayout()); // Use GridBagLayout for alignment
        // Remove the border here, let the frame's centering handle it
        setBackground(new Color(230, 235, 240)); // Light background color for the whole area

        initComponents();
        addComponents();
        setupEventHandlers();
    }

    private void initComponents() {
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        errorMessageLabel = new JLabel("");
        errorMessageLabel.setForeground(Color.RED);

        signupLink = new JLabel("<html><a href=\"\">Don't have an account? Sign Up</a></html>");
        signupLink.setForeground(new Color(0, 123, 255)); // Link color
        signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor

        // Update default login credentials label to show the correct credentials
        credentialsLabel = new JLabel(
                "<html><b>Default Login:</b> Email: user@user.com | Password: 1234</html>");
        credentialsLabel.setForeground(new Color(70, 130, 180)); // Steel blue color
        credentialsLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        credentialsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // --- Styling ---
        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);
        emailLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        emailField.setFont(fieldFont);
        passwordField.setFont(fieldFont);

        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        loginButton.setOpaque(true); // Ensure background is painted

        // Button Hover Effect
        Color loginBtnBg = new Color(0, 123, 255);
        Color loginBtnHover = loginBtnBg.darker();
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(loginBtnHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(loginBtnBg);
            }
        });
    }

    private void addComponents() {
        // Panel to hold the login form content with a border
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 1), // Light grey border
                BorderFactory.createEmptyBorder(30, 30, 30, 30) // Inner padding
        ));
        formPanel.setBackground(Color.WHITE); // White background for the form panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title Label
        JLabel titleLabel = new JLabel("User Login");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(titleLabel, gbc);

        // Add credentials helper label below the title
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        formPanel.add(credentialsLabel, gbc);

        // Email Row
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0; // Label doesn't take extra space
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0; // Field takes extra space
        formPanel.add(emailField, gbc);

        // Password Row
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        // Error Message Label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 0;
        formPanel.add(errorMessageLabel, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; // Button doesn't fill horizontally
        gbc.insets = new Insets(10, 0, 10, 0); // Padding above the button
        gbc.weightx = 0;
        formPanel.add(loginButton, gbc);

        // Signup Link
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 0, 0); // Padding above the link
        gbc.weightx = 0;
        formPanel.add(signupLink, gbc);

        // Add the form panel to the main LoginPanel (which uses GridBagLayout for
        // centering)
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.anchor = GridBagConstraints.CENTER; // Center the form panel in the overall panel
        mainGbc.fill = GridBagConstraints.NONE; // Don't make the form panel fill the whole area
        add(formPanel, mainGbc);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(e -> performLogin());

        // Allow pressing Enter key to trigger login
        emailField.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());

        // Add MouseListener to the signupLink label
        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showPanel("Signup"); // Call parent frame to show Signup panel
            }
        });
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()); // Get password as String

        // Clear previous error message
        errorMessageLabel.setText("");

        if (email.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Please enter email and password.");
            return;
        }

        // Call the parent frame's login method
        parentFrame.attemptLogin(email, password);
    }

    /**
     * Displays an error message on the login panel.
     * 
     * @param message The error message to display.
     */
    public void displayErrorMessage(String message) {
        errorMessageLabel.setText(message);
    }

    /**
     * Pre-fills the login form with default credentials or clears it when the panel
     * is shown.
     */
    public void resetLoginForm() {
        // Pre-fill with the default user credentials
        emailField.setText("user@user.com");
        passwordField.setText("1234");
        errorMessageLabel.setText("");
    }
}