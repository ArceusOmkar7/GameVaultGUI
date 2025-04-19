
package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.exceptions.InvalidUserDataException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date; // Import Date

public class SignupPanel extends JPanel {

    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField; // For password confirmation
    private JButton signupButton;
    private JLabel errorMessageLabel;
    private JLabel loginLink; // Link back to login

    private final GameVaultFrame parentFrame;

    public SignupPanel(GameVaultFrame parentFrame) {
        this.parentFrame = parentFrame;

        setLayout(new GridBagLayout());
        setBackground(new Color(230, 235, 240)); // Match login background

        initComponents();
        addComponents();
        setupEventHandlers();
    }

    private void initComponents() {
        JLabel emailLabel = new JLabel("Email:");
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");

        emailField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        signupButton = new JButton("Sign Up");
        errorMessageLabel = new JLabel("");
        errorMessageLabel.setForeground(Color.RED);

        loginLink = new JLabel("<html><a href=\"\">Already have an account? Login</a></html>");
        loginLink.setForeground(new Color(0, 123, 255));
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // --- Styling ---
        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 14);
        emailLabel.setFont(labelFont);
        usernameLabel.setFont(labelFont);
        passwordLabel.setFont(labelFont);
        confirmPasswordLabel.setFont(labelFont);

        emailField.setFont(fieldFont);
        usernameField.setFont(fieldFont);
        passwordField.setFont(fieldFont);
        confirmPasswordField.setFont(fieldFont);


        signupButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        signupButton.setBackground(new Color(40, 167, 69)); // Bootstrap-like green
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        signupButton.setOpaque(true);

         // Button Hover Effect
         Color signupBtnBg = new Color(40, 167, 69);
         Color signupBtnHover = signupBtnBg.darker();
         signupButton.addMouseListener(new MouseAdapter() {
             @Override public void mouseEntered(MouseEvent e) { signupButton.setBackground(signupBtnHover); }
             @Override public void mouseExited(MouseEvent e) { signupButton.setBackground(signupBtnBg); }
         });
    }

    private void addComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
             BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
             BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title Label
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(titleLabel, gbc);

        // Email Row
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(5, 5, 5, 5); gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);

        // Username Row
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        // Password Row
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        // Confirm Password Row
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formPanel.add(confirmPasswordField, gbc);

        // Error Message Label
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(10, 0, 10, 0); gbc.weightx = 0;
        formPanel.add(errorMessageLabel, gbc);

        // Signup Button
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE; gbc.insets = new Insets(10, 0, 10, 0); gbc.weightx = 0;
        formPanel.add(signupButton, gbc);

        // Login Link
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(10, 0, 0, 0); gbc.weightx = 0;
        formPanel.add(loginLink, gbc);


        // Add the form panel to the main SignupPanel for centering
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.anchor = GridBagConstraints.CENTER;
        mainGbc.fill = GridBagConstraints.NONE;
        add(formPanel, mainGbc);
    }

    private void setupEventHandlers() {
        signupButton.addActionListener(e -> performSignup());

        // Allow pressing Enter on any text field to trigger signup (optional, can limit to last field)
        emailField.addActionListener(e -> performSignup());
        usernameField.addActionListener(e -> performSignup());
        passwordField.addActionListener(e -> performSignup());
        confirmPasswordField.addActionListener(e -> performSignup());


        // Add MouseListener to the loginLink label
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.showPanel("Login"); // Call parent frame to show Login panel
            }
        });
    }

    private void performSignup() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        errorMessageLabel.setText(""); // Clear previous errors

        // --- Basic Validation ---
        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessageLabel.setText("Please fill in all fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            errorMessageLabel.setText("Passwords do not match.");
            return;
        }
         // Optional: Add more validation (email format, username length, etc.)

        try {
            // Create a new user object (initial wallet balance could be 0.0f or user-defined)
            User newUser = new User(email, password, username, 0.0f); // Start with $0 wallet

            // Call UserManagement to add the user (handles DB insertion)
            parentFrame.getUserManagement().addUser(newUser);

            // Signup successful
            JOptionPane.showMessageDialog(this, "Account created successfully!\nYou can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Redirect to the login page
            parentFrame.showPanel("Login");

        } catch (InvalidUserDataException e) {
            // Handle validation errors from the management layer (e.g., username already exists)
            errorMessageLabel.setText("Signup failed: " + e.getMessage());
             // Optional: Log the full exception server-side or to a file
        } catch (Exception e) {
            // Handle other potential errors (e.g., database connection issues)
            JOptionPane.showMessageDialog(this, "An error occurred during signup: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print stack trace for debugging
            errorMessageLabel.setText("An unexpected error occurred."); // Generic error for the user
        }
    }

    /**
     * Clears the input fields and error message when the panel is shown.
     */
    public void resetSignupForm() {
        emailField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        errorMessageLabel.setText("");
    }
}