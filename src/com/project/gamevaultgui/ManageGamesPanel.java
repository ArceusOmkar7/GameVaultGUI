package com.project.gamevaultgui;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.management.GameManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector; // Needed for DefaultTableModel with Vector


public class ManageGamesPanel extends JPanel {

    private final GameManagement gameManagement; // Need GameManagement to interact with games

    private JTable gamesTable;
    private DefaultTableModel gamesTableModel;
    private JScrollPane gamesScrollPane;

    private JTextField idField; // Hidden field for game ID when editing
    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField developerField;
    private JTextField platformField;
    private JTextField priceField;
    private JTextField releaseDateField; // For release date (YYYY-MM-DD)

    private JButton saveButton; // Button to add or update game
    private JButton deleteButton; // Button to delete selected game
    private JButton clearFormButton; // Button to clear form for new entry

    private boolean isEditing = false; // Flag to indicate if we are editing an existing game

    // Date format for display and input
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    public ManageGamesPanel(GameManagement gameManagement) { // Accept GameManagement
        this.gameManagement = gameManagement;

        setLayout(new BorderLayout(10, 10)); // Use BorderLayout with gaps
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
        setBackground(new Color(230, 235, 240)); // Light background

        initComponents();
        addComponents();
        setupEventHandlers();

        loadGames(); // Load games when the panel is initialized
        setFormEnabled(false); // Initially disable the form until "Add" or a row is selected
        clearForm(); // Clear the form initially
    }

    private void initComponents() {
        // --- Games Table ---
        gamesTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Developer", "Platform", "Price", "Release Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        gamesTable = new JTable(gamesTableModel);
        gamesScrollPane = new JScrollPane(gamesTable);
         gamesScrollPane.setBorder(BorderFactory.createTitledBorder(
                 BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Available Games",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)
         ));
         customizeTable(gamesTable); // Apply custom table style


        // --- Game Form Components ---
        idField = new JTextField();
        idField.setVisible(false); // Keep ID field hidden

        titleField = new JTextField(20);
        descriptionField = new JTextField(30);
        developerField = new JTextField(20);
        platformField = new JTextField(15);
        priceField = new JTextField(10);
        releaseDateField = new JTextField(10); // YYYY-MM-DD format expected

        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete Selected");
        clearFormButton = new JButton("Add New Game"); // Button to clear form and enter add mode

        // Style buttons
         styleButton(saveButton, new Color(0, 123, 255), Color.WHITE);
         styleButton(deleteButton, new Color(220, 53, 69), Color.WHITE);
         styleButton(clearFormButton, new Color(40, 167, 69), Color.WHITE); // Green for "Add New"

    }

     private void customizeTable(JTable table) {
         table.setFont(new Font("SansSerif", Font.PLAIN, 13));
         table.setRowHeight(20);
         table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
         table.setFillsViewportHeight(true);
         table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         table.setGridColor(new Color(200, 200, 200));
         table.setBackground(Color.WHITE);
         table.getTableHeader().setBackground(new Color(220, 220, 220));
     }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("SansSerif", Font.BOLD, 12)); // Smaller font for form buttons
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12)); // Smaller padding
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
        // Panel for the form inputs
        JPanel formInputPanel = new JPanel(new GridBagLayout()); // GridBagLayout for form
        formInputPanel.setOpaque(false);
         formInputPanel.setBorder(BorderFactory.createTitledBorder(
             BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
             "Game Details",
             TitledBorder.LEADING, TitledBorder.TOP,
             new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)
         ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8); // Padding around form elements
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Fields take extra horizontal space

        int row = 0;
        // ID (hidden)
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; formInputPanel.add(idField, gbc); // Add hidden ID field

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        formInputPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formInputPanel.add(titleField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        formInputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formInputPanel.add(descriptionField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        formInputPanel.add(new JLabel("Developer:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formInputPanel.add(developerField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        formInputPanel.add(new JLabel("Platform:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formInputPanel.add(platformField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        formInputPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formInputPanel.add(priceField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST; gbc.weightx = 0;
        formInputPanel.add(new JLabel("Release Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formInputPanel.add(releaseDateField, gbc);

        // Buttons Panel below the form inputs
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formButtonPanel.setOpaque(false);
        formButtonPanel.add(saveButton);
        formButtonPanel.add(clearFormButton);

        JPanel formPanel = new JPanel(new BorderLayout()); // Panel combining inputs and form buttons
        formPanel.setOpaque(false);
        formPanel.add(formInputPanel, BorderLayout.CENTER);
        formPanel.add(formButtonPanel, BorderLayout.SOUTH);


        // Panel for table buttons (Delete)
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align to the right
        tableButtonPanel.setOpaque(false);
        tableButtonPanel.add(deleteButton);


        // Combine table and its buttons
        JPanel tableAreaPanel = new JPanel(new BorderLayout(0, 5)); // BorderLayout with vertical gap
        tableAreaPanel.setOpaque(false);
        tableAreaPanel.add(gamesScrollPane, BorderLayout.CENTER);
        tableAreaPanel.add(tableButtonPanel, BorderLayout.SOUTH);


        // Add table area to CENTER, and form area to SOUTH of the main panel
        add(tableAreaPanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Table row selection listener
        gamesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && gamesTable.getSelectedRow() != -1) {
                int selectedRow = gamesTable.getSelectedRow();
                populateForm(selectedRow);
                setFormEnabled(true); // Enable form when a row is selected
                isEditing = true; // We are now in edit mode
                saveButton.setText("Save Changes"); // Change button text
            }
        });

        // Save Button Action
        saveButton.addActionListener(e -> saveGame());

        // Delete Button Action
        deleteButton.addActionListener(e -> deleteGame());

        // Clear Form Button Action
        clearFormButton.addActionListener(e -> {
            clearForm();
            setFormEnabled(true); // Enable form for new entry
            isEditing = false; // We are in add mode
            saveButton.setText("Add Game"); // Change button text
            gamesTable.clearSelection(); // Clear table selection
        });
    }

    /**
     * Populates the form fields with data from the selected table row.
     * @param selectedRow The index of the selected row.
     */
    private void populateForm(int selectedRow) {
        idField.setText(gamesTableModel.getValueAt(selectedRow, 0).toString());
        titleField.setText(gamesTableModel.getValueAt(selectedRow, 1).toString());
        developerField.setText(gamesTableModel.getValueAt(selectedRow, 2).toString());
        platformField.setText(gamesTableModel.getValueAt(selectedRow, 3).toString());
        priceField.setText(gamesTableModel.getValueAt(selectedRow, 4).toString().replace("$", "")); // Remove dollar sign
        // Format the date from the table (which might be a Date object or String)
        Object releaseDateObj = gamesTableModel.getValueAt(selectedRow, 5);
        if (releaseDateObj instanceof Date) {
            releaseDateField.setText(DATE_FORMAT.format((Date) releaseDateObj));
        } else if (releaseDateObj != null) {
             // Try to parse if it's a string, otherwise just display as is
             try {
                 Date date = new SimpleDateFormat("yyyy-MM-dd").parse(releaseDateObj.toString());
                  releaseDateField.setText(DATE_FORMAT.format(date));
             } catch (ParseException e) {
                 releaseDateField.setText(releaseDateObj.toString()); // Fallback
             }
        } else {
            releaseDateField.setText("");
        }

        // Assuming description is not shown in the main table, but you might want to load it.
        // You'd need to fetch the full Game object by ID here or add description to the table model (if possible).
        // For now, description field will remain empty unless you modify the table model or fetch strategy.
        // To load description, you would need to:
        // 1. Store Game objects in the table model directly OR
        // 2. Fetch the full Game object using gameManagement.getGame(gameId) after selection.
        // Let's fetch the full game object for simplicity in this example.
        try {
             int gameId = Integer.parseInt(idField.getText());
             Game fullGame = gameManagement.getGame(gameId);
             if (fullGame != null) {
                 descriptionField.setText(fullGame.getDescription());
             } else {
                 descriptionField.setText("Description not found.");
             }
         } catch (NumberFormatException | com.project.gamevaultcli.exceptions.GameNotFoundException ex) {
             descriptionField.setText("Error loading description.");
         }
    }


    /**
     * Clears all form fields and resets the mode.
     */
    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        descriptionField.setText("");
        developerField.setText("");
        platformField.setText("");
        priceField.setText("");
        releaseDateField.setText("");
        isEditing = false;
        saveButton.setText("Add Game");
        gamesTable.clearSelection(); // Clear table selection
        setFormEnabled(true); // Re-enable form for new entry
    }

    /**
     * Enables or disables the form fields and save/clear buttons.
     * Delete button is controlled separately based on table selection.
     */
    private void setFormEnabled(boolean enabled) {
        titleField.setEnabled(enabled);
        descriptionField.setEnabled(enabled);
        developerField.setEnabled(enabled);
        platformField.setEnabled(enabled);
        priceField.setEnabled(enabled);
        releaseDateField.setEnabled(enabled);
        saveButton.setEnabled(enabled);
        // clearFormButton is always enabled to allow starting a new entry
        // deleteButton is enabled/disabled based on table selection model, not this method
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

        if (title.isEmpty() || developer.isEmpty() || platform.isEmpty() || priceText.isEmpty() || releaseDateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (Title, Developer, Platform, Price, Release Date).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        float price;
        try {
            price = Float.parseFloat(priceText);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format. Please enter a positive number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date releaseDate;
        try {
            // Parse the date using the defined format
            releaseDate = DATE_FORMAT.parse(releaseDateText);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid release date format. Please use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Game game;
            if (isEditing) {
                // --- Update Existing Game ---
                int gameId = Integer.parseInt(idField.getText()); // Get the ID from the hidden field
                game = new Game(gameId, title, description, developer, platform, price, releaseDate);
                gameManagement.updateGame(game);
                JOptionPane.showMessageDialog(this, "Game updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // --- Add New Game ---
                game = new Game(title, description, developer, platform, price, releaseDate);
                gameManagement.addGame(game); // The addGame method should set the new ID
                JOptionPane.showMessageDialog(this, "Game added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            // Refresh the table
            loadGames();
            // Clear and disable the form after saving
            clearForm();
            setFormEnabled(false); // Disable form after saving/adding until new action
            isEditing = false; // Reset mode
            saveButton.setText("Add Game"); // Reset button text


        } catch (NumberFormatException e) {
             // Should be caught by price validation, but catch here just in case of ID parsing error
             JOptionPane.showMessageDialog(this, "Error parsing Game ID.", "Internal Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Catch any other exceptions from the management/storage layer
            JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Handles deleting the selected game.
     */
    private void deleteGame() {
        int selectedRow = gamesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a game to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int gameId = (int) gamesTableModel.getValueAt(selectedRow, 0);
        String gameTitle = (String) gamesTableModel.getValueAt(selectedRow, 1);

        int confirmResult = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete game '" + gameTitle + "' (ID: " + gameId + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                gameManagement.deleteGame(gameId);
                JOptionPane.showMessageDialog(this, "Game deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadGames(); // Refresh the table
                clearForm(); // Clear the form in case the deleted item was being edited
                 setFormEnabled(false); // Disable form after deletion
                 isEditing = false; // Reset mode
                 saveButton.setText("Add Game"); // Reset button text
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting game: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }


    /**
     * Loads all games from the database and populates the table.
     */
    public void loadGames() {
        gamesTableModel.setRowCount(0); // Clear existing data
        try {
            List<Game> games = gameManagement.getAllGames();
            if (games != null) {
                for (Game game : games) {
                    // Format date for display
                    String releaseDateStr = (game.getReleaseDate() != null) ? DATE_FORMAT.format(game.getReleaseDate()) : "N/A";
                    gamesTableModel.addRow(new Object[]{
                            game.getGameId(),
                            game.getTitle(),
                            game.getDeveloper(),
                            game.getPlatform(),
                            String.format("%.2f", game.getPrice()), // Format price
                            releaseDateStr
                    });
                }
            }
             // Enable/disable delete button based on table selection (handled by listener)
             deleteButton.setEnabled(gamesTable.getSelectedRow() != -1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading games: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}