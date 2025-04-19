
package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date; // Import Date

public class UserPanel extends JPanel {

    // Use JLabels for keys as they are static text
    private JLabel usernameKeyLabel;
    private JLabel emailKeyLabel;
    private JLabel walletBalanceKeyLabel;
    private JLabel createdAtKeyLabel;

    // Use JLabels for dynamic values
    private JLabel usernameValueLabel;
    private JLabel emailValueLabel;
    private JLabel walletBalanceValueLabel;
    private JLabel createdAtValueLabel;

    private JButton editButton;
    private JButton logoutButton; // Add Logout button

    private final GameVaultFrame parentFrame; // Keep reference to the parent frame

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public UserPanel(GameVaultFrame parentFrame) { // Accept parent frame in constructor
        this.parentFrame = parentFrame;

        // Use GridBagLayout for flexible alignment and spacing
        setLayout(new GridBagLayout());
        // Add a more visually appealing titled border
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15), // Outer padding
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(60, 63, 65), 2, true), // Border color and thickness
                        "User Information", // Title text
                        TitledBorder.CENTER, // Title position
                        TitledBorder.TOP,    // Title justification
                        new Font("SansSerif", Font.BOLD, 20), // Title font
                        new Color(60, 63, 65) // Title color
                )
        ));
        setBackground(new Color(250, 250, 250)); // Slightly lighter background

        initComponents();
        addComponents();
        setupEventHandlers();

        // Initially, hide components that require a logged-in user until loadUserInfo is called
        setLoggedInState(false);
    }

    private void initComponents() {
        // Create static labels (keys)
        usernameKeyLabel = new JLabel("Username:");
        emailKeyLabel = new JLabel("Email:");
        walletBalanceKeyLabel = new JLabel("Wallet Balance:");
        createdAtKeyLabel = new JLabel("Member Since:");

        // Create dynamic labels (values) - initialized as empty
        usernameValueLabel = new JLabel("");
        emailValueLabel = new JLabel("");
        walletBalanceValueLabel = new JLabel("");
        createdAtValueLabel = new JLabel("");

        // --- Styling for Labels ---
        Font keyFont = new Font("SansSerif", Font.BOLD, 14);
        Font valueFont = new Font("SansSerif", Font.PLAIN, 14);
        Color keyColor = new Color(80, 80, 80); // Medium gray for keys
        Color valueColor = new Color(0, 0, 0); // Black text for values

        usernameKeyLabel.setFont(keyFont);
        emailKeyLabel.setFont(keyFont);
        walletBalanceKeyLabel.setFont(keyFont);
        createdAtKeyLabel.setFont(keyFont);

        usernameKeyLabel.setForeground(keyColor);
        emailKeyLabel.setForeground(keyColor);
        walletBalanceKeyLabel.setForeground(keyColor);
        createdAtKeyLabel.setForeground(keyColor);

        usernameValueLabel.setFont(valueFont);
        emailValueLabel.setFont(valueFont);
        walletBalanceValueLabel.setFont(valueFont);
        createdAtValueLabel.setFont(valueFont);

        usernameValueLabel.setForeground(valueColor);
        emailValueLabel.setForeground(valueColor);
        walletBalanceValueLabel.setForeground(valueColor);
        createdAtValueLabel.setForeground(valueColor);


        // Buttons
        editButton = new JButton("Edit Username"); // More specific text
        logoutButton = new JButton("Logout");

        // Style buttons
        styleButton(editButton, new Color(0, 123, 255), Color.WHITE); // Bootstrap-like blue
        styleButton(logoutButton, new Color(220, 53, 69), Color.WHITE); // Bootstrap-like red
    }

     private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false); // Remove focus border
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Adjusted padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Hand cursor on hover
        button.setOpaque(true); // Ensure background is painted

        // Add hover effect (optional)
        Color hoverColor = bgColor.darker(); // Darken color on hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                 button.setBackground(bgColor);
            }
        });
    }


    private void addComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 15, 6, 15); // Padding around components (top, left, bottom, right)
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow components to grow horizontally

        // Profile picture placeholder (optional, matching the reference image concept)
        // Using a JPanel with a circle shape and center image/label
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
                    g2d.setColor(new Color(255, 235, 200)); // Light orange background color
                    g2d.fillOval(x, y, diameter, diameter);

                    // Add a placeholder initial or simple icon
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 36)); // Adjusted font size
                    String initial = "U"; // Default initial
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
            public Dimension getPreferredSize() {
                return new Dimension(70, 70); // Set preferred size for the circle
            }
             @Override
            public Dimension getMaximumSize() {
                return getPreferredSize(); // Don't allow it to grow
            }
        };
        profilePicPanel.setOpaque(false); // Make background transparent for painting oval

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across both columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the profile pic
        gbc.insets = new Insets(15, 15, 10, 15); // Top padding
        add(profilePicPanel, gbc);

        // --- User Details (aligned key-value pairs) ---
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.anchor = GridBagConstraints.WEST; // Default alignment for values
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.weightx = 1.0; // Allow value column to take space
        gbc.insets = new Insets(4, 15, 4, 15); // Smaller padding between rows

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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); // FlowLayout for buttons
        buttonPanel.setOpaque(false); // Make background transparent
        buttonPanel.add(editButton);
        buttonPanel.add(logoutButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across both columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the button panel
        gbc.fill = GridBagConstraints.NONE; // Do not fill horizontally
        gbc.insets = new Insets(20, 15, 15, 15); // Top padding before buttons
        add(buttonPanel, gbc);

        // Add vertical glue to push content towards the top
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weighty = 1.0; // Allow this row to take extra vertical space
        gbc.gridwidth = 2; // Span across both columns
        gbc.fill = GridBagConstraints.BOTH; // Fill vertically and horizontally (the glue itself fills)
        add(Box.createVerticalGlue(), gbc);
    }

    private void setupEventHandlers() {
        editButton.addActionListener(e -> showEditUsernameDialog());
        logoutButton.addActionListener(e -> parentFrame.logout()); // Call logout on parent frame
    }

    /**
     * Sets the visibility of user-specific components based on login state.
     */
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
         // The profilePicPanel will handle its own drawing based on currentUser state
         repaint(); // Repaint to show/hide components
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
             setLoggedInState(true); // Show components
        } else {
            // Clear information and hide components
            usernameValueLabel.setText("");
            emailValueLabel.setText("");
            walletBalanceValueLabel.setText("");
            createdAtValueLabel.setText("");
            setLoggedInState(false); // Hide components
        }
         // Repaint profile pic panel as it uses paintComponent
         // Accessing it might be tricky without storing a reference,
         // but a general repaint() on this panel should cover it.
         revalidate(); // Revalidate to update layout
         repaint(); // Repaint to show changes
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
            } else {
                 // JOptionPane.showMessageDialog(this, "Username is the same. No changes saved.", "Info", JOptionPane.INFORMATION_MESSAGE); // Optional info
            }
        }
    }
}