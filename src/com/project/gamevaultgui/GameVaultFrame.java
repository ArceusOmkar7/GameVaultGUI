package com.project.gamevaultgui;

import com.project.gamevaultcli.management.GameVaultManagement;
import com.project.gamevaultcli.management.CartManagement;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;
import com.project.gamevaultcli.management.UserManagement;
import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.exceptions.InvalidUserDataException;
import com.project.gamevaultcli.exceptions.UserNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.LocalDateTime;

// Import DBUtil for closing connection
import com.project.gamevaultcli.helpers.DBUtil;
import java.sql.SQLException;
import java.io.IOException;
import java.util.List;

public class GameVaultFrame extends JFrame {

    private JPanel centerPanel;
    private CardLayout cardLayout;
    private SidebarPanel sidebarPanel;
    private NavbarPanel navbarPanel;

    // Panels for the center content
    private DashboardPanel dashboardPanel;
    private CartPanel cartPanel;
    private BillingPanel billingPanel;
    private UserPanel userPanel;
    private JPanel roleSelectionPanel;
    private LoginPanel loginPanel;
    private SignupPanel signupPanel;

    private ManageGamesPanel manageGamesPanel; // Now requires GameManagement and Frame
    private ManageUsersPanel manageUsersPanel;

    // Management classes
    private final GameVaultManagement gameVaultManagement;
    private final UserManagement userManagement;
    private final GameManagement gameManagement;
    private final CartManagement cartManagement;
    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;

    private User currentUser;
    private boolean isAdmin = false; // Flag to track if the current perspective is admin

    public GameVaultFrame(
            GameVaultManagement gameVaultManagement,
            UserManagement userManagement,
            GameManagement gameManagement,
            CartManagement cartManagement,
            OrderManagement orderManagement,
            TransactionManagement transactionManagement) {
        this.gameVaultManagement = gameVaultManagement;
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
        this.cartManagement = cartManagement;
        this.orderManagement = orderManagement;
        this.transactionManagement = transactionManagement;

        setTitle("Game Vault");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center the frame

        initComponents();
        addComponentsToFrame();
        setupEventHandlers();

        // Initially show the role selection panel
        showPanel("RoleSelection");

        // Handle closing the application to close DB connection
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DBUtil.closeConnection(); // Use the imported DBUtil
            }
        });
    }

    private void initComponents() {
        sidebarPanel = new SidebarPanel(this);
        navbarPanel = new NavbarPanel("Welcome", this);

        centerPanel = new JPanel();
        cardLayout = new CardLayout();
        centerPanel.setLayout(cardLayout);

        dashboardPanel = new DashboardPanel(userManagement, gameManagement, orderManagement, transactionManagement,
                cartManagement, this);
        cartPanel = new CartPanel(cartManagement, gameManagement, this);
        billingPanel = new BillingPanel(orderManagement, transactionManagement, this);
        userPanel = new UserPanel(this, userManagement, transactionManagement);
        roleSelectionPanel = createRoleSelectionPanel();
        loginPanel = new LoginPanel(this);
        signupPanel = new SignupPanel(this);

        // Initialize Admin panels, PASSING MANAGEMENT INSTANCES and the Frame
        manageGamesPanel = new ManageGamesPanel(gameManagement, this); // Pass GameManagement and Frame
        manageUsersPanel = new ManageUsersPanel(userManagement);
    }

    private void addComponentsToFrame() {
        add(sidebarPanel, BorderLayout.WEST);
        add(navbarPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // Add all panels to the CardLayout using their unique keys
        centerPanel.add(roleSelectionPanel, "RoleSelection");
        centerPanel.add(loginPanel, "Login");
        centerPanel.add(signupPanel, "Signup");
        centerPanel.add(dashboardPanel, "Dashboard");
        centerPanel.add(cartPanel, "Cart");
        centerPanel.add(billingPanel, "Billing"); // Use the key "Billing" for the BillingPanel
        centerPanel.add(userPanel, "User Profile");
        centerPanel.add(manageGamesPanel, "Manage Games");
        centerPanel.add(manageUsersPanel, "Manage Users");
    }

    private void setupEventHandlers() {
        // Event handlers for sidebar buttons are handled in SidebarPanel
        // Role selection handled in the roleSelectionPanel
        // Navbar profile click handled in NavbarPanel
    }

    /**
     * Displays the panel with the given key in the CardLayout.
     * 
     * @param panelName The key of the panel to show.
     */
    public void showPanel(String panelName) {
        cardLayout.show(centerPanel, panelName);

        // Update content of panels when shown, using the CardLayout key
        if ("Dashboard".equals(panelName)) {
            dashboardPanel.loadDashboardData(currentUser != null ? currentUser.getUserId() : -1);
        } else if ("Cart".equals(panelName) && currentUser != null) {
            cartPanel.loadCart(currentUser.getUserId());
        } else if ("Billing".equals(panelName) && currentUser != null) { // Use the key "Billing" here
            billingPanel.loadBills(currentUser.getUserId());
        } else if ("User Profile".equals(panelName)) { // Match panel name
            userPanel.loadUserInfo(currentUser);
        } else if ("Login".equals(panelName)) {
            loginPanel.resetLoginForm();
        } else if ("Signup".equals(panelName)) {
            signupPanel.resetSignupForm();
        } else if ("Manage Games".equals(panelName)) { // Load data when Manage Games is shown
            manageGamesPanel.loadGames(); // Call load method
        } else if ("Manage Users".equals(panelName)) { // Load data when Manage Users is shown
            manageUsersPanel.loadUsers(); // Call load method
        }

        // Update Navbar and Sidebar visibility/state after showing the panel
        updateUIState(panelName);
    }

    // Method to manage Navbar and Sidebar visibility and state
    private void updateUIState(String intendedPanelName) {
        String pageTitle;

        switch (intendedPanelName) {
            case "Dashboard":
                pageTitle = "Dashboard";
                break;
            case "Cart":
                pageTitle = "Shopping Cart";
                break;
            case "Billing":
                pageTitle = "Your Orders History";
                break;
            case "User Profile":
                pageTitle = "User Profile";
                break;
            case "Manage Games":
                pageTitle = "Manage Games";
                break;
            case "Manage Users":
                pageTitle = "Manage Users";
                break;
            case "RoleSelection":
                pageTitle = "Game Vault - Role Selection";
                break;
            case "Login":
                pageTitle = "Game Vault - Login";
                break;
            case "Signup":
                pageTitle = "Game Vault - Signup";
                break;
            default:
                pageTitle = "Game Vault";
                break;
        }

        // Update Navbar
        if (currentUser != null) {
            navbarPanel.setGreeting("Hello, " + currentUser.getUsername());
            navbarPanel.setPageTitle(pageTitle);
            navbarPanel.showProfileIcon(true);
        } else if (isAdmin) {
            navbarPanel.setGreeting("Hello, Admin");
            navbarPanel.setPageTitle(pageTitle);
            navbarPanel.showProfileIcon(true); // Show profile icon for admin too for logout access
        } else {
            navbarPanel.setGreeting("");
            navbarPanel.setPageTitle(pageTitle);
            navbarPanel.showProfileIcon(false);
        }

        // Update Sidebar visibility and button state
        if (currentUser != null) {
            sidebarPanel.setVisible(true);
            sidebarPanel.updateSidebarForUser();
            switch (intendedPanelName) {
                case "Dashboard":
                    sidebarPanel.highlightDashboardButton();
                    break;
                case "Cart":
                    sidebarPanel.highlightCartButton();
                    break;
                case "Billing":
                    sidebarPanel.highlightYourOrdersButton();
                    break;
                case "User Profile":
                    sidebarPanel.highlightUserButton();
                    break;
            }

        } else if (isAdmin) {
            sidebarPanel.setVisible(true);
            sidebarPanel.updateSidebarForAdmin();
            switch (intendedPanelName) {
                case "Dashboard":
                    sidebarPanel.highlightDashboardButton();
                    break;
                case "Manage Games":
                    sidebarPanel.highlightManageGamesButton();
                    break;
                case "Manage Users":
                    sidebarPanel.highlightManageUsersButton();
                    break;
            }
        } else {
            sidebarPanel.setVisible(false);
            sidebarPanel.hideAllButtons();
            sidebarPanel.resetButtonColors();
        }

        revalidate();
        repaint();
    }

    // Method to handle user selection from RoleSelection -> now shows Login
    public void selectUserPerspective() {
        showPanel("Login");
    }

    // Method to handle admin selection from RoleSelection -> goes to admin
    // dashboard
    public void selectAdminPerspective() {
        currentUser = null;
        isAdmin = true;
        showPanel("Dashboard");
    }

    /**
     * Attempts to log in a user with the given email and password.
     * Called from LoginPanel.
     * 
     * @param email    The user's email.
     * @param password The user's password.
     */
    public void attemptLogin(String email, String password) {
        try {
            User user = userManagement.login(email, password);
            this.currentUser = user;
            this.isAdmin = false;

            // Optional: JOptionPane.showMessageDialog(this, "Login successful! Welcome, " +
            // user.getUsername(), "Success", JOptionPane.INFORMATION_MESSAGE);

            showPanel("Dashboard");

        } catch (UserNotFoundException e) {
            loginPanel.displayErrorMessage("Invalid email or password.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred during login: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            loginPanel.displayErrorMessage("An unexpected error occurred.");
        }
    }

    /**
     * Handles adding balance to the current user's wallet.
     * Called from UserPanel.
     * 
     * @param amount The amount to add (must be positive).
     */
    public void addBalanceToCurrentUser(float amount) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No user logged in to add balance.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Amount must be positive.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            userManagement.updateWalletBalance(currentUser.getUserId(), amount);

            // Re-fetch the user from the database to get the absolutely latest balance
            this.currentUser = userManagement.getUser(currentUser.getUserId());

            // Record the transaction for the top-up
            Transaction topupTransaction = new Transaction(
                    null, // transactionId (will be generated by DB)
                    null, // orderId (null for top-up transactions)
                    currentUser.getUserId(),
                    "Top-up", // Transaction type
                    amount,
                    LocalDateTime.now() // Current date/time
            );
            transactionManagement.addTransaction(topupTransaction);

            JOptionPane.showMessageDialog(this, String.format("$%.2f added to your wallet!", amount), "Balance Updated",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh the UI (User Profile and potentially Dashboard)
            refreshCurrentUserAndUI(); // Call the dedicated refresh method

        } catch (UserNotFoundException e) {
            JOptionPane.showMessageDialog(this, "User not found during balance update: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred while adding balance: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the current user object from the DB and updates relevant panels.
     * Called after actions that change user state (like adding balance or
     * purchasing).
     */
    public void refreshCurrentUserAndUI() {
        if (currentUser != null) {
            try {
                // Re-fetch the user object to get the latest state (especially wallet balance)
                this.currentUser = userManagement.getUser(currentUser.getUserId());

                // Find the currently visible panel and reload its data if applicable
                String currentPanel = getCurrentPanelName();
                if ("User Profile".equals(currentPanel)) {
                    userPanel.loadUserInfo(this.currentUser);
                } else if ("Dashboard".equals(currentPanel)) {
                    dashboardPanel.loadDashboardData(this.currentUser.getUserId());
                } else if ("Billing".equals(currentPanel)) {
                    billingPanel.loadBills(this.currentUser.getUserId());
                } else {
                    // For any other visible panel, still refresh owned games to keep them up to
                    // date
                    dashboardPanel.refreshOwnedGames(this.currentUser.getUserId());
                }

                // Update Navbar greeting (in case username changed, though not expected here)
                updateUIState(currentPanel); // Refresh UI state for the current panel

            } catch (UserNotFoundException e) {
                // This means the user was somehow deleted while logged in - handle
                // appropriately
                JOptionPane.showMessageDialog(this, "Your user account was not found. Logging out.", "Account Error",
                        JOptionPane.ERROR_MESSAGE);
                logout(); // Force logout
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error refreshing user data: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        // If no user is logged in (e.g., Admin view), this method does nothing to
        // currentUser,
        // but might still need to trigger dashboard refresh if admin actions affected
        // global stats.
    }

    /**
     * Refreshes the game data on the dashboard.
     * This should be called after any action that modifies games (add/edit/delete).
     */
    public void refreshGameData() {
        String currentPanel = getCurrentPanelName();
        if ("Dashboard".equals(currentPanel)) {
            // If currently on dashboard, reload it immediately
            dashboardPanel.loadDashboardData(currentUser != null ? currentUser.getUserId() : -1);
        }
        // We don't need an else clause here because when the user navigates back to the
        // dashboard,
        // showPanel method will call loadDashboardData
    }

    /**
     * Handles updating the current user's username.
     * Called from UserPanel.
     * 
     * @param newUsername The new username provided by the user.
     */
    public void updateCurrentUserUsername(String newUsername) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No user logged in to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (newUsername == null || newUsername.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create a new User object with updated username, keeping other fields the same
            User updatedUser = new User(
                    currentUser.getUserId(),
                    currentUser.getEmail(),
                    currentUser.getPassword(),
                    newUsername.trim(),
                    currentUser.getWalletBalance(),
                    currentUser.getCreatedAt());

            userManagement.updateUser(updatedUser);
            this.currentUser = updatedUser; // Update the frame's current user object

            JOptionPane.showMessageDialog(this, "Username updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh the User Profile panel
            userPanel.loadUserInfo(this.currentUser);
            // Update navbar greeting
            updateUIState(getCurrentPanelName());

        } catch (InvalidUserDataException e) {
            JOptionPane.showMessageDialog(this, "Update Failed: " + e.getMessage(), "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (UserNotFoundException e) {
            JOptionPane.showMessageDialog(this, "User not found during update: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred while updating username: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Handles the logout process.
     * Called from UserPanel or NavbarPanel.
     * This now returns to the Role Selection screen.
     */
    public void logout() {
        if (currentUser == null && !isAdmin) {
            return;
        }
        gameVaultManagement.logout();
        currentUser = null;
        isAdmin = false;
        JOptionPane.showMessageDialog(this, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
        showPanel("RoleSelection"); // Navigate back to Role Selection
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    // Helper method to get the currently displayed panel name
    private String getCurrentPanelName() {
        LayoutManager layout = centerPanel.getLayout();
        if (layout instanceof CardLayout) {
            for (Component comp : centerPanel.getComponents()) {
                if (comp.isVisible()) {
                    // Check instances directly for panels with unique types
                    if (comp instanceof DashboardPanel)
                        return "Dashboard";
                    if (comp instanceof CartPanel)
                        return "Cart";
                    if (comp instanceof BillingPanel)
                        return "Billing";
                    if (comp instanceof UserPanel)
                        return "User Profile";
                    if (comp instanceof ManageGamesPanel)
                        return "Manage Games";
                    if (comp instanceof ManageUsersPanel)
                        return "Manage Users";
                    // For panels that are just JPanels, compare instances
                    if (comp.equals(roleSelectionPanel))
                        return "RoleSelection";
                    if (comp.equals(loginPanel))
                        return "Login";
                    if (comp.equals(signupPanel))
                        return "Signup";
                }
            }
        }
        return "";
    }

    // Add methods to access management classes
    public UserManagement getUserManagement() {
        return userManagement;
    }

    public GameManagement getGameManagement() {
        return gameManagement;
    }

    public CartManagement getCartManagement() {
        return cartManagement;
    }

    public OrderManagement getOrderManagement() {
        return orderManagement;
    }

    public TransactionManagement getTransactionManagement() {
        return transactionManagement;
    }

    private JPanel createRoleSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(230, 235, 240));
        JLabel selectRoleLabel = new JLabel("Select Your Role:");
        selectRoleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        selectRoleLabel.setForeground(new Color(50, 50, 50));

        JButton userButton = new JButton("User");
        JButton adminButton = new JButton("Admin");

        styleRoleButton(userButton);
        styleRoleButton(adminButton);

        userButton.addActionListener(e -> selectUserPerspective());
        adminButton.addActionListener(e -> selectAdminPerspective());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 10, 30, 10);
        panel.add(selectRoleLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.add(userButton);
        buttonPanel.add(adminButton);

        gbc.gridy = 1;
        gbc.insets = new Insets(20, 10, 20, 10);
        panel.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private void styleRoleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 2, true), // Blue border
                BorderFactory.createEmptyBorder(15, 30, 15, 30) // Padding
        ));
        button.setBackground(new Color(0, 123, 255)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color bgColor = new Color(0, 123, 255);
        Color hoverColor = bgColor.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    public static void main(String[] args) {
        // --- FORCE DATABASE CONNECTION AT THE VERY START ---
        try {
            System.out.println("Attempting to establish database connection...");
            DBUtil.getConnection();
            System.out.println("Database connection established successfully.");

        } catch (SQLException | IOException e) {
            System.err.println("Failed to establish initial database connection.");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to the database.\nPlease check connection details and ensure MySQL is running.\nError: "
                            + e.getMessage(),
                    "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Initialize management classes
        com.project.gamevaultcli.storage.UserStorage userStorage = new com.project.gamevaultcli.storage.UserStorage();
        com.project.gamevaultcli.storage.GameStorage gameStorage = new com.project.gamevaultcli.storage.GameStorage();
        com.project.gamevaultcli.storage.CartStorage cartStorage = new com.project.gamevaultcli.storage.CartStorage(
                gameStorage);
        com.project.gamevaultcli.storage.OrderStorage orderStorage = new com.project.gamevaultcli.storage.OrderStorage();
        com.project.gamevaultcli.storage.TransactionStorage transactionStorage = new com.project.gamevaultcli.storage.TransactionStorage();

        UserManagement userManagement = new UserManagement(userStorage);
        GameManagement gameManagement = new GameManagement(gameStorage);
        CartManagement cartManagement = new CartManagement(cartStorage);
        TransactionManagement transactionManagement = new TransactionManagement(transactionStorage);
        OrderManagement orderManagement = new OrderManagement(orderStorage, cartStorage, userStorage,
                transactionManagement);

        GameVaultManagement vaultManager = new GameVaultManagement(userManagement, gameManagement, orderManagement,
                transactionManagement);

        // Check if data initialization is needed (only initialize if users table is
        // empty)
        try {
            boolean shouldInitializeData = false;

            try {
                // Check if there are any users in the database
                List<User> existingUsers = userManagement.getAllUsers();
                shouldInitializeData = (existingUsers == null || existingUsers.isEmpty());
                System.out.println("Database check: " + (shouldInitializeData ? "Empty database, will initialize data."
                        : "Data exists, skipping initialization."));
            } catch (Exception e) {
                // If there's an error checking, we'll assume we need to initialize
                System.out.println("Error checking database state: " + e.getMessage());
                shouldInitializeData = true;
            }

            if (shouldInitializeData) {
                System.out.println("Initializing predefined data...");

                // Add default user with updated credentials
                try {
                    User defaultUser = new User("user@user.com", "1234", "DefaultUser", 5000.0f);
                    userManagement.addUser(defaultUser);
                    System.out.println("Added default user: " + defaultUser.getUsername());
                } catch (Exception e) {
                    System.out.println("Error adding default user: " + e.getMessage());
                }

                // Add default games for testing
                try {
                    Game game1 = new Game("Minecraft", "A sandbox building game", "Mojang", "PC/Mobile/Console", 29.99f,
                            new java.util.Date());
                    gameManagement.addGame(game1);

                    Game game2 = new Game("FIFA 2025", "Latest soccer simulation", "EA Sports", "PC/Console", 59.99f,
                            new java.util.Date());
                    gameManagement.addGame(game2);

                    Game game3 = new Game("Call of Duty: Modern Warfare", "FPS action game", "Activision", "PC/Console",
                            49.99f, new java.util.Date());
                    gameManagement.addGame(game3);

                    Game game4 = new Game("The Legend of Zelda", "Action-adventure game", "Nintendo", "Switch", 59.99f,
                            new java.util.Date());
                    gameManagement.addGame(game4);

                    Game game5 = new Game("Among Us", "Social deduction game", "InnerSloth", "PC/Mobile", 4.99f,
                            new java.util.Date());
                    gameManagement.addGame(game5);

                    System.out.println("Added default games to the database");
                } catch (Exception e) {
                    System.out.println("Error adding default games: " + e.getMessage());
                }

                System.out.println("Predefined data initialized.");
            } else {
                System.out.println("Skipping data initialization as data already exists in the database.");
            }
        } catch (Exception e) {
            System.err.println("Error during data initialization check: " + e.getMessage());
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new GameVaultFrame(vaultManager, userManagement, gameManagement, cartManagement, orderManagement,
                    transactionManagement).setVisible(true);
        });
    }
}