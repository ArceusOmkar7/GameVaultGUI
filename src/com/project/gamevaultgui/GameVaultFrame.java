package com.project.gamevaultgui;

import com.project.gamevaultcli.management.GameVaultManagement;
import com.project.gamevaultcli.management.CartManagement;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;
import com.project.gamevaultcli.management.UserManagement;
import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.entities.Transaction;
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


public class GameVaultFrame extends JFrame {

    private JPanel centerPanel;
    private CardLayout cardLayout;
    private SidebarPanel sidebarPanel;
    private NavbarPanel navbarPanel;

    // Panels for the center content
    private DashboardPanel dashboardPanel;
    private CartPanel cartPanel;
    private BillingPanel billingPanel; // Stays "Billing" internally as the CardLayout key
    private UserPanel userPanel;
    private JPanel roleSelectionPanel;
    private LoginPanel loginPanel;
    private SignupPanel signupPanel;

    private ManageGamesPanel manageGamesPanel;
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
            TransactionManagement transactionManagement
    ) {
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

        // Pass management classes and this frame to panels that need them
        dashboardPanel = new DashboardPanel(userManagement, gameManagement, orderManagement, transactionManagement, cartManagement, this);
        cartPanel = new CartPanel(cartManagement, gameManagement, this);
        billingPanel = new BillingPanel(orderManagement, transactionManagement, this);
        // Pass user and transaction management to UserPanel
        userPanel = new UserPanel(this, userManagement, transactionManagement);
        roleSelectionPanel = createRoleSelectionPanel();
        loginPanel = new LoginPanel(this);
        signupPanel = new SignupPanel(this);

        // Initialize Admin panels, PASSING MANAGEMENT INSTANCES
        manageGamesPanel = new ManageGamesPanel(gameManagement);
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
             manageGamesPanel.loadGames();
        } else if ("Manage Users".equals(panelName)) { // Load data when Manage Users is shown
             manageUsersPanel.loadUsers();
        }

        // Update Navbar and Sidebar visibility/state after showing the panel
        // PASS THE INTENDED PANEL NAME (the CardLayout key)
        updateUIState(panelName);
    }

     // Method to manage Navbar and Sidebar visibility and state
     // ACCEPTS THE CARDLAYOUT KEY AS intendedPanelName
     private void updateUIState(String intendedPanelName) {
        String pageTitle;

        // Determine the display title based on the CardLayout key
        switch(intendedPanelName) {
            case "Dashboard":       pageTitle = "Dashboard"; break;
            case "Cart":            pageTitle = "Shopping Cart"; break;
            case "Billing":         pageTitle = "Your Orders History"; break; // Map "Billing" key to new title
            case "User Profile":    pageTitle = "User Profile"; break;
            case "Manage Games":    pageTitle = "Manage Games"; break;
            case "Manage Users":    pageTitle = "Manage Users"; break;
            case "RoleSelection":   pageTitle = "Game Vault - Role Selection"; break;
            case "Login":           pageTitle = "Game Vault - Login"; break;
            case "Signup":          pageTitle = "Game Vault - Signup"; break;
            default:                pageTitle = "Game Vault"; break; // Default title
        }


        // Update Navbar
        if (currentUser != null) { // Logged in as a regular user
            navbarPanel.setGreeting("Hello, " + currentUser.getUsername());
            navbarPanel.setPageTitle(pageTitle);
            navbarPanel.showProfileIcon(true); // Show profile icon
        } else if (isAdmin) { // Logged in as admin (via direct selection for now)
             navbarPanel.setGreeting("Hello, Admin");
             navbarPanel.setPageTitle(pageTitle);
             navbarPanel.showProfileIcon(true); // Show profile icon (can customize this later if needed)
        }
        else { // Not logged in (Role Selection, Login, or Signup)
            navbarPanel.setGreeting(""); // No greeting
            navbarPanel.setPageTitle(pageTitle); // Show specific title
            navbarPanel.showProfileIcon(false); // Hide profile icon
        }

        // Update Sidebar visibility and button state
        if (currentUser != null) {
            sidebarPanel.setVisible(true);
            sidebarPanel.updateSidebarForUser();
             // Highlight the button corresponding to the intended panel (using CardLayout key)
             switch(intendedPanelName) {
                 case "Dashboard":    sidebarPanel.highlightDashboardButton(); break;
                 case "Cart":         sidebarPanel.highlightCartButton(); break;
                 case "Billing":      sidebarPanel.highlightYourOrdersButton(); break; // Call highlight method for "Your Orders" button
                 case "User Profile": sidebarPanel.highlightUserButton(); break;
                 // No default needed, other panels are admin-only or non-sidebar
             }

        } else if (isAdmin) {
             sidebarPanel.setVisible(true);
             sidebarPanel.updateSidebarForAdmin();
             // Highlight the button corresponding to the intended panel (using CardLayout key)
             switch(intendedPanelName) {
                 case "Dashboard":    sidebarPanel.highlightDashboardButton(); break;
                 case "Manage Games": sidebarPanel.highlightManageGamesButton(); break;
                 case "Manage Users": sidebarPanel.highlightManageUsersButton(); break;
                 // No default needed, other panels are user-only or non-sidebar
             }
        }
        else { // Not logged in (Role Selection, Login, or Signup)
            sidebarPanel.setVisible(false); // Hide sidebar
             sidebarPanel.hideAllButtons(); // Ensure buttons are hidden
             sidebarPanel.resetButtonColors(); // Ensure no buttons are highlighted
        }

        revalidate();
        repaint();
     }


     // Method to handle user selection from RoleSelection -> now shows Login
    public void selectUserPerspective() {
         // Transition to the Login panel
         showPanel("Login");
    }

    // Method to handle admin selection from RoleSelection -> goes to admin dashboard
    public void selectAdminPerspective() {
        // For this simple version, Admin bypasses login
        currentUser = null; // Admin is not a 'User' object in this model
        isAdmin = true;
        showPanel("Dashboard"); // Show admin dashboard
    }

    /**
     * Attempts to log in a user with the given email and password.
     * Called from LoginPanel.
     * @param email The user's email.
     * @param password The user's password.
     */
    public void attemptLogin(String email, String password) {
        try {
            User user = userManagement.login(email, password); // This throws UserNotFoundException on failure
            this.currentUser = user; // Set the logged-in user
            this.isAdmin = false; // Logged in via this method is always a regular user

            // Successful login
            // Optional: JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + user.getUsername(), "Success", JOptionPane.INFORMATION_MESSAGE);

            // Navigate to the user's dashboard
            showPanel("Dashboard");

        } catch (UserNotFoundException e) {
            // Login failed (invalid credentials)
            loginPanel.displayErrorMessage("Invalid email or password.");
        } catch (Exception e) {
            // Handle other potential errors during login (e.g., DB issues)
            JOptionPane.showMessageDialog(this, "An error occurred during login: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            loginPanel.displayErrorMessage("An unexpected error occurred.");
        }
    }

     /**
      * Handles adding balance to the current user's wallet.
      * Called from UserPanel.
      * @param amount The amount to add (must be positive).
      */
    public void addBalanceToCurrentUser(float amount) {
         if (currentUser == null) {
             JOptionPane.showMessageDialog(this, "No user logged in to add balance.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
         }
         if (amount <= 0) {
              JOptionPane.showMessageDialog(this, "Amount must be positive.", "Validation Error", JOptionPane.WARNING_MESSAGE);
              return;
         }

         try {
             // Update the user's wallet balance via management layer
             userManagement.updateWalletBalance(currentUser.getUserId(), amount);

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

             // Re-fetch the user from the database to get the absolutely latest balance
             // This is important after any transaction (purchase or top-up)
             this.currentUser = userManagement.getUser(currentUser.getUserId());

             JOptionPane.showMessageDialog(this, String.format("$%.2f added to your wallet!", amount), "Balance Updated", JOptionPane.INFORMATION_MESSAGE);

             // Refresh the UI (User Profile and potentially Dashboard)
             // Find the current panel and reload its data
             String currentPanel = getCurrentPanelName(); // Get the panel name *before* attempting to reload
             if ("User Profile".equals(currentPanel)) {
                  userPanel.loadUserInfo(this.currentUser); // Load updated user info
             } else if ("Dashboard".equals(currentPanel)) {
                  dashboardPanel.loadDashboardData(this.currentUser.getUserId()); // Reload dashboard data
             }
             // Note: Billing/Your Orders history will automatically refresh the next time that panel is shown.

             // Update Navbar greeting and UI state
             updateUIState(currentPanel); // Update UI State to reflect potentially changed user info

         } catch (UserNotFoundException e) {
             // This should ideally not happen if currentUser is valid, but good to catch
             JOptionPane.showMessageDialog(this, "User not found during balance update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
         }
          catch (Exception e) {
             JOptionPane.showMessageDialog(this, "An error occurred while adding balance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
         }
     }


    /**
     * Handles updating the current user's username.
     * Called from UserPanel.
     * @param newUsername The new username provided by the user.
     */
    public void updateCurrentUserUsername(String newUsername) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "No user logged in to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Basic validation in GUI before calling management
            if (newUsername == null || newUsername.trim().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                 return; // Stop the update process
            }

            // Create a new User object with updated username, keeping other fields the same
            User updatedUser = new User(
                currentUser.getUserId(),
                currentUser.getEmail(),
                currentUser.getPassword(), // Keep existing password
                newUsername.trim(), // Use trimmed username
                currentUser.getWalletBalance(), // Keep existing balance (username update doesn't change balance)
                currentUser.getCreatedAt()
            );

            userManagement.updateUser(updatedUser); // Call the management layer to update DB
            this.currentUser = updatedUser; // Update the frame's current user object

            JOptionPane.showMessageDialog(this, "Username updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the User Profile panel to show the new username
            userPanel.loadUserInfo(this.currentUser);
            // Update navbar greeting and UI state
            updateUIState(getCurrentPanelName()); // Update UI state for the current panel

        } catch (InvalidUserDataException e) {
             JOptionPane.showMessageDialog(this, "Update Failed: " + e.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (UserNotFoundException e) {
             JOptionPane.showMessageDialog(this, "User not found during update: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred while updating username: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            // Already logged out or not in a session
            return;
        }
        gameVaultManagement.logout(); // Clears currentUser in the manager
        currentUser = null;
        isAdmin = false;
        JOptionPane.showMessageDialog(this, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
        // !!! Navigate back to the Role Selection screen !!!
        showPanel("RoleSelection");
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    // Helper method to get the currently displayed panel name
       // Helper method to get the currently displayed panel name (CORRECTED)
    private String getCurrentPanelName() {
         LayoutManager layout = centerPanel.getLayout();
         if (layout instanceof CardLayout) {
             // Iterate through components to find the visible one
             for (Component comp : centerPanel.getComponents()) {
                 if (comp.isVisible()) {
                      // Compare component instance to known panels
                      if (comp.equals(roleSelectionPanel)) return "RoleSelection"; // CORRECTED
                      if (comp.equals(loginPanel)) return "Login"; // CORRECTED
                      if (comp.equals(signupPanel)) return "Signup"; // CORRECTED
                      // Use instanceof for custom JPanel subclasses
                      if (comp instanceof DashboardPanel) return "Dashboard";
                      if (comp instanceof CartPanel) return "Cart";
                      if (comp instanceof BillingPanel) return "Billing"; // Use key
                      if (comp instanceof UserPanel) return "User Profile";
                      if (comp instanceof ManageGamesPanel) return "Manage Games";
                      if (comp instanceof ManageUsersPanel) return "Manage Users";
                 }
             }
         }
        return ""; // Should not happen if a panel is visible
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


        userButton.addActionListener(e -> selectUserPerspective()); // User button goes to Login
        adminButton.addActionListener(e -> selectAdminPerspective()); // Admin button goes to Dashboard

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

         // Add vertical glue to push content towards the center (inside the 'panel')
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
             JOptionPane.showMessageDialog(null, "Failed to connect to the database.\nPlease check connection details and ensure MySQL is running.\nError: " + e.getMessage(), "Database Connection Error", JOptionPane.ERROR_MESSAGE);
             System.exit(1);
        }


        // Initialize management classes
        com.project.gamevaultcli.storage.UserStorage userStorage = new com.project.gamevaultcli.storage.UserStorage();
        com.project.gamevaultcli.storage.GameStorage gameStorage = new com.project.gamevaultcli.storage.GameStorage();
        com.project.gamevaultcli.storage.CartStorage cartStorage = new com.project.gamevaultcli.storage.CartStorage(gameStorage);
        com.project.gamevaultcli.storage.OrderStorage orderStorage = new com.project.gamevaultcli.storage.OrderStorage();
        com.project.gamevaultcli.storage.TransactionStorage transactionStorage = new com.project.gamevaultcli.storage.TransactionStorage();

        UserManagement userManagement = new UserManagement(userStorage);
        GameManagement gameManagement = new GameManagement(gameStorage);
        CartManagement cartManagement = new CartManagement(cartStorage);
        TransactionManagement transactionManagement = new TransactionManagement(transactionStorage);
        OrderManagement orderManagement = new OrderManagement(orderStorage, cartStorage, userStorage, transactionManagement);

        GameVaultManagement vaultManager = new GameVaultManagement(userManagement, gameManagement, orderManagement, transactionManagement);

        // Initialize predefined data *after* connection and storage are ready.
        // Consider adding a check here if you want this to run only once ever.
        // try {
        //     System.out.println("Initializing predefined data...");
        //     vaultManager.initializeData();
        //     System.out.println("Predefined data initialized.");
        // } catch (Exception e) {
        //     System.err.println("Error initializing predefined data: " + e.getMessage());
        //     e.printStackTrace();
        // }


        SwingUtilities.invokeLater(() -> {
            new GameVaultFrame(vaultManager, userManagement, gameManagement, cartManagement, orderManagement, transactionManagement).setVisible(true);
        });
    }
}