package com.project.gamevaultgui.dialogs;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.management.GameManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class GameFormDialog extends JDialog {

    private JTextField idField; // Hidden ID field
    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField developerField;
    private JTextField platformField;
    private JTextField priceField;
    private JTextField releaseDateField;

    private JButton saveButton;
    private JButton cancelButton;

    private final GameManagement gameManagement;
    private boolean saved = false; // Flag to indicate if changes were saved
    private boolean isEditing = false; // Flag to indicate if we are editing

    // Use SimpleDateFormat with the correct format pattern (DD should be dd for
    // day)
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Constructor for adding a new game
    public GameFormDialog(Frame owner, GameManagement gameManagement) {
        super(owner, "Add New Game", true); // Modal dialog
        this.gameManagement = gameManagement;
        this.isEditing = false; // Not editing
        initComponents();
        addComponents();
        setupEventHandlers();
        pack(); // Size the dialog based on its components
        setLocationRelativeTo(owner); // Center relative to the parent frame
    }

    // Constructor for editing an existing game
    public GameFormDialog(Frame owner, GameManagement gameManagement, Game game) {
        super(owner, "Edit Game: " + game.getTitle(), true); // Modal dialog
        this.gameManagement = gameManagement;
        this.isEditing = true; // Editing
        initComponents();
        populateForm(game); // Populate with existing game data
        addComponents();
        setupEventHandlers();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        // Form Fields
        idField = new JTextField(); // Hidden ID field
        idField.setVisible(false);

        titleField = new JTextField(25);
        descriptionField = new JTextField(30); // Larger for description
        developerField = new JTextField(25);
        platformField = new JTextField(20);
        priceField = new JTextField(10);
        releaseDateField = new JTextField(12); // YYYY-MM-DD format

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        // Style buttons
        styleButton(saveButton, new Color(40, 167, 69), Color.WHITE); // Green
        styleButton(cancelButton, new Color(108, 117, 125), Color.WHITE); // Gray
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        Color hoverColor = bgColor.darker();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled())
                    button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled())
                    button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                if (button.isEnabled())
                    button.setBackground(hoverColor.darker());
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (button.isEnabled())
                    button.setBackground(button.getModel().isRollover() ? hoverColor : bgColor);
            }
        }); // <-- Semicolon added here
    }

    private void addComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for the form layout
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15)); // Padding inside the dialog
        formPanel.setBackground(Color.WHITE); // White background for the form area

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8); // Padding around elements
        gbc.anchor = GridBagConstraints.WEST; // Align labels right, fields left
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fields fill horizontally
        gbc.weightx = 1.0; // Fields get extra space

        int row = 0;
        // ID (hidden) - added to layout but invisible
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(idField, gbc); // Add hidden ID field
        row++; // Move to next row (even though ID is hidden, it occupies a space)

        // Title Row
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);
        row++;

        // Description Row
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(descriptionField, gbc);
        row++;

        // Developer and Platform on the same row
        JPanel devPlatformPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // FlowLayout for fields
                                                                                      // side-by-side
        devPlatformPanel.setOpaque(false);
        devPlatformPanel.add(developerField);
        devPlatformPanel.add(platformField);
        // Labels for Dev and Platform
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel devPlatformLabelPanel = new JPanel(new GridLayout(1, 2, 5, 0)); // Panel for labels
        devPlatformLabelPanel.setOpaque(false);
        devPlatformLabelPanel.add(new JLabel("Developer:"));
        devPlatformLabelPanel.add(new JLabel("Platform:"));
        formPanel.add(devPlatformLabelPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(devPlatformPanel, gbc);
        row++;

        // Price and Release Date on the same row
        JPanel priceDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        priceDatePanel.setOpaque(false);
        priceDatePanel.add(priceField);
        priceDatePanel.add(releaseDateField);
        // Labels for Price and Release Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JPanel priceDateLabelPanel = new JPanel(new GridLayout(1, 2, 5, 0)); // Panel for labels
        priceDateLabelPanel.setOpaque(false);
        priceDateLabelPanel.add(new JLabel("Price:"));
        priceDateLabelPanel.add(new JLabel("Release Date:"));
        formPanel.add(priceDateLabelPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        formPanel.add(priceDatePanel, gbc);
        row++;

        // Add vertical glue to push elements to the top
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(Box.createVerticalGlue(), gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add form panel to the center and button panel to the south
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        saveButton.addActionListener(e -> saveGame());
        cancelButton.addActionListener(e -> cancel());
    }

    /**
     * Populates the form fields with data from a Game object when editing.
     * 
     * @param game The Game object to populate from.
     */
    private void populateForm(Game game) {
        idField.setText(String.valueOf(game.getGameId())); // Set the hidden ID
        titleField.setText(game.getTitle());
        descriptionField.setText(game.getDescription());
        developerField.setText(game.getDeveloper());
        platformField.setText(game.getPlatform());
        priceField.setText(String.valueOf(game.getPrice()));
        releaseDateField.setText((game.getReleaseDate() != null) ? DATE_FORMAT.format(game.getReleaseDate()) : "");
        saveButton.setText("Save Changes"); // Change button text in edit mode
    }

    /**
     * Handles saving (adding or updating) a game.
     */
    private void saveGame() {
        // --- Validation ---
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String developer = developerField.getText().trim();
        String platform = platformField.getText().trim();
        String priceText = priceField.getText().trim();
        String releaseDateText = releaseDateField.getText().trim();

        if (title.isEmpty() || developer.isEmpty() || platform.isEmpty() || priceText.isEmpty()
                || releaseDateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceText);
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price must be a positive number.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format. Please enter a number.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date releaseDate;
        try {
            releaseDate = DATE_FORMAT.parse(releaseDateText);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid release date format. Please use YYYY-MM-DD.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Game game;
            if (isEditing) {
                // --- Update Existing Game ---
                int gameId = Integer.parseInt(idField.getText()); // Get the ID from the hidden field
                game = new Game(gameId, title, description, developer, platform, price, releaseDate);
                gameManagement.updateGame(game);
                // JOptionPane.showMessageDialog(this, "Game updated successfully!", "Success",
                // JOptionPane.INFORMATION_MESSAGE); // Success message shown on panel after
                // dialog closes
            } else {
                // --- Add New Game ---
                game = new Game(title, description, developer, platform, price, releaseDate);
                gameManagement.addGame(game); // The addGame method should set the new ID on the game object
                // JOptionPane.showMessageDialog(this, "Game added successfully!", "Success",
                // JOptionPane.INFORMATION_MESSAGE); // Success message shown on panel after
                // dialog closes
            }

            saved = true; // Set flag to true on successful save/add
            dispose(); // Close the dialog

        } catch (NumberFormatException e) {
            // Should only happen if idField is somehow non-integer in edit mode
            JOptionPane.showMessageDialog(this, "Internal Error: Invalid Game ID format.", "Internal Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Catch any other exceptions from the management/storage layer
            JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Handles cancelling the operation.
     */
    private void cancel() {
        saved = false; // Set flag to false on cancel
        dispose(); // Close the dialog
    }

    /**
     * Returns whether the dialog was saved successfully.
     * Used by the calling panel (ManageGamesPanel) to know if the table needs
     * refreshing.
     * 
     * @return true if saved, false otherwise.
     */
    public boolean wasSaved() {
        return saved;
    }
}