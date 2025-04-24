package com.project.gamevaultgui.panels.admin;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultgui.GameVaultFrame;
import com.project.gamevaultgui.dialogs.GameFormDialog;
import com.project.gamevaultcli.exceptions.GameNotFoundException;

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
import java.util.Vector;

public class ManageGamesPanel extends JPanel {

    private final GameManagement gameManagement;
    private final GameVaultFrame parentFrame; // Need parent frame to launch the dialog

    private JTable gamesTable;
    private DefaultTableModel gamesTableModel;
    private JScrollPane gamesScrollPane;

    private JButton addNewGameButton; // Button to trigger the add dialog
    private JButton deleteButton;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public ManageGamesPanel(GameManagement gameManagement, GameVaultFrame parentFrame) { // Accept parent frame
        this.gameManagement = gameManagement;
        this.parentFrame = parentFrame; // Initialize parent frame

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(230, 235, 240));

        initComponents();
        addComponents();
        setupEventHandlers();

        // loadGames(); // Load games when the panel is shown, not initialized
        // Handled by GameVaultFrame calling loadGames() when the panel is shown.

        // Disable delete button initially
        deleteButton.setEnabled(false);
    }

    private void initComponents() {
        // --- Games Table ---
        gamesTableModel = new DefaultTableModel(
                new Object[] { "ID", "Title", "Developer", "Platform", "Price", "Release Date" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gamesTable = new JTable(gamesTableModel);
        gamesScrollPane = new JScrollPane(gamesTable);
        gamesScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Available Games",
                TitledBorder.LEADING, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(50, 50, 50)));
        customizeTable(gamesTable);

        // --- Buttons ---
        addNewGameButton = new JButton("New Game Entry"); // Button to open dialog for adding
        deleteButton = new JButton("Delete Selected");

        // Style buttons
        styleButton(addNewGameButton, new Color(40, 167, 69), Color.WHITE); // Green for New
        styleButton(deleteButton, new Color(220, 53, 69), Color.WHITE); // Red for Delete
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
        table.setAutoCreateRowSorter(true); // Enable sorting
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
        });
    }

    private void addComponents() {
        // Panel for table buttons (New Game Entry, Delete)
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Right alignment, 10px gap
        tableButtonPanel.setOpaque(false);
        tableButtonPanel.add(addNewGameButton);
        tableButtonPanel.add(deleteButton);

        // Combine table and its buttons
        JPanel tableAreaPanel = new JPanel(new BorderLayout(0, 5)); // BorderLayout with vertical gap
        tableAreaPanel.setOpaque(false);
        tableAreaPanel.add(gamesScrollPane, BorderLayout.CENTER);
        tableAreaPanel.add(tableButtonPanel, BorderLayout.SOUTH);

        // Add the table area to the main panel
        add(tableAreaPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        // Table row selection listener - now only controls delete button and opens
        // dialog
        gamesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (gamesTable.getSelectedRow() != -1) {
                    deleteButton.setEnabled(true); // Enable delete button when a row is selected
                } else {
                    deleteButton.setEnabled(false); // Disable delete button when selection is cleared
                }
            }
        });

        // Double-click on table row to edit
        gamesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && gamesTable.getSelectedRow() != -1) {
                    int selectedRow = gamesTable.getSelectedRow();
                    int gameId = (int) gamesTableModel.getValueAt(selectedRow, 0);
                    editGame(gameId); // Call method to open edit dialog
                }
            }
        });

        // Add New Game Button Action
        addNewGameButton.addActionListener(e -> addNewGame());

        // Delete Button Action
        deleteButton.addActionListener(e -> deleteSelectedGame());
    }

    /**
     * Opens the dialog to add a new game.
     */
    private void addNewGame() {
        GameFormDialog dialog = new GameFormDialog(parentFrame, gameManagement);
        dialog.setVisible(true); // Show the modal dialog

        // After the dialog is closed, check if it was saved
        if (dialog.wasSaved()) {
            loadGames(); // Refresh the table to show the new game
            parentFrame.refreshGameData(); // Refresh dashboard data when a game is added
        }
    }

    /**
     * Fetches the game and opens the dialog to edit it.
     * 
     * @param gameId The ID of the game to edit.
     */
    private void editGame(int gameId) {
        try {
            Game gameToEdit = gameManagement.getGame(gameId); // Fetch the full game object
            if (gameToEdit != null) {
                GameFormDialog dialog = new GameFormDialog(parentFrame, gameManagement, gameToEdit);
                dialog.setVisible(true); // Show the modal dialog

                // After the dialog is closed, check if it was saved
                if (dialog.wasSaved()) {
                    loadGames(); // Refresh the table to show the updated game
                    parentFrame.refreshGameData(); // Refresh dashboard data when a game is updated
                }
            } else {
                JOptionPane.showMessageDialog(this, "Game not found for editing.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (GameNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Game not found for editing: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error fetching game for editing: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        // Clear table selection after trying to edit (dialog closes anyway)
        gamesTable.clearSelection();
    }

    /**
     * Handles deleting the selected game.
     */
    private void deleteSelectedGame() { // Renamed for clarity
        int selectedRow = gamesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a game to delete.", "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get ID directly from the table model
        int gameId = (int) gamesTableModel.getValueAt(selectedRow, 0);
        String gameTitle = (String) gamesTableModel.getValueAt(selectedRow, 1);

        int confirmResult = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete game '" + gameTitle + "' (ID: " + gameId
                        + ")?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmResult == JOptionPane.YES_OPTION) {
            try {
                // Potential foreign key issue here if the game is in a cart or order.
                // Your storage/management layer needs to handle this (e.g., delete dependent
                // records or database ON DELETE CASCADE).
                gameManagement.deleteGame(gameId);
                JOptionPane.showMessageDialog(this, "Game deleted successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadGames(); // Refresh the table
                parentFrame.refreshGameData(); // Refresh dashboard data when a game is deleted
                gamesTable.clearSelection(); // Clear selection after deletion
                deleteButton.setEnabled(false); // Disable delete button
            } catch (Exception e) {
                // Provide specific message if deletion fails due to constraint violation (e.g.,
                // game in cart)
                if (e.getMessage() != null && e.getMessage().contains("foreign key constraint fails")) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete game because it is referenced in user carts or orders.", "Deletion Failed",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting game: " + e.getMessage(), "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads all games from the database and populates the table.
     * This method should be called when the panel is made visible and after
     * saves/deletes.
     */
    public void loadGames() {
        gamesTableModel.setRowCount(0); // Clear existing data
        try {
            List<Game> games = gameManagement.getAllGames();
            if (games != null) {
                for (Game game : games) {
                    String releaseDateStr = (game.getReleaseDate() != null) ? DATE_FORMAT.format(game.getReleaseDate())
                            : "N/A";
                    gamesTableModel.addRow(new Object[] {
                            game.getGameId(),
                            game.getTitle(),
                            game.getDeveloper(),
                            game.getPlatform(),
                            String.format("%.2f", game.getPrice()),
                            releaseDateStr
                    });
                }
            }
            // Ensure delete button state is correct after loading
            deleteButton.setEnabled(gamesTable.getSelectedRow() != -1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading games: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}