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
    private UserPanel userPanel; // Now requires management classes
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
            // Dashboard always loads data when shown, user ID determines filtering
            dashboardPanel.loadDashboardData(currentUser != null ? currentUser.getUserId() : -1);
        } else if ("Cart".equals(panelName) && currentUser != null) {
            // Cart only loads for logged-in users
            cartPanel.loadCart(currentUser.getUserId());
        } else if ("Billing".equals(panelName) && currentUser != null) {
            // Billing only loads for logged-in users
            billingPanel.loadBills(currentUser.getUserId());
        } else if ("User Profile".equals(panelName)) {
            // User Profile loads data when shown, requires a logged-in user
            userPanel.loadUserInfo(currentUser); // UserPanel handles null currentUser internally now
        } else if ("Login".equals(panelName)) {
            loginPanel.resetLoginForm();
        } else if ("Signup".equals(panelName)) {
            signupPanel.resetSignupForm();
        } else if ("Manage Games".equals(panelName)) {
             manageGamesPanel.loadGames();
        } else if ("Manage Users".equals(panelName)) {
             manageUsersPanel.loadUsers();
        }

        // Update Navbar and Sidebar visibility/state *after* showing the panel
        updateUIState(panelName);
    }

     // Method to manage Navbar and Sidebar visibility and state
     private void updateUIState(String intendedPanelName) {
        String pageTitle;

        switch(intendedPanelName) {
            case "Dashboard":       pageTitle = "Dashboard"; break;
            case "Cart":            pageTitle = "Shopping Cart"; break;
            case "Billing":         pageTitle = "Your Orders History"; break;
            case "User Profile":    pageTitle = "User Profile"; break;
            case "Manage Games":    pageTitle = "Manage Games"; break;
            case "Manage Users":    pageTitle = "Manage Users"; break;
            case "RoleSelection":   pageTitle = "Game Vault - Role Selection"; break;
            case "Login":           pageTitle = "Game Vault - Login"; break;
            case "Signup":          pageTitle = "Game Vault - Signup"; break;
            default:                pageTitle = "Game Vault"; break;
        }

        // Update Navbar
        if (currentUser != null) { // Logged in as a regular user
            navbarPanel.setGreeting("Hello, " + currentUser.getUsername());
            navbarPanel.setPageTitle(pageTitle);
            navbarPanel.showProfileIcon(true);
        } else if (isAdmin) { // Logged in as admin
             navbarPanel.setGreeting("Hello, Admin");
             navbarPanel.setPageTitle(pageTitle);
             navbarPanel.showProfileIcon(true); // Show icon for admin too (can be different later)
        }
        else { // Not logged in
            navbarPanel.setGreeting("");
            navbarPanel.setPageTitle(pageTitle);
            navbarPanel.showProfileIcon(false);
        }

        // Update Sidebar visibility and button state
        if (currentUser != null) {
            sidebarPanel.setVisible(true);
            sidebarPanel.updateSidebarForUser();
             switch(intendedPanelName) {
                 case "Dashboard":    sidebarPanel.highlightDashboardButton(); break;
                 case "Cart":         sidebarPanel.highlightCartButton(); break;
                 case "Billing":      sidebarPanel.highlightYourOrdersButton(); break;
                 case "User Profile": sidebarPanel.highlightUserButton(); break;
             }

        } else if (isAdmin) {
             sidebarPanel.setVisible(true);
             sidebarPanel.updateSidebarForAdmin();
             switch(intendedPanelName) {
                 case "Dashboard":    sidebarPanel.highlightDashboardButton(); break;
                 case "Manage Games": sidebarPanel.highlightManageGamesButton(); break;
                 case "Manage Users": sidebarPanel.highlightManageUsersButton(); break;
             }
        }
        else { // Not logged in
            sidebarPanel.setVisible(false);
             sidebarPanel.hideAllButtons();
             sidebarPanel.resetButtonColors();
        }

        revalidate();
        repaint();
     }

     /**
      * Re-fetches the current user's data from the database and refreshes
      * the UI elements that display user-specific information (User Panel, Navbar greeting, etc.).
      * This should be called after any transaction that affects the user's balance or data.
      */
     public void refreshCurrentUserAndUI() {
         if (currentUser != null) {
             try {
                 // Re-fetch the user from the database
                 this.currentUser = userManagement.getUser(currentUser.getUserId());

                 // Refresh UI elements that display user data
                 // Note: Panels loaded via showPanel already call load... methods.
                 // This ensures that even if the User Profile panel isn't currently visible,
                 // the currentUser object *in the frame* is updated for the next time it IS shown.
                 // However, if we *are* currently on the User Profile panel, we should explicitly refresh it.
                 String currentPanel = getCurrentPanelName();
                 if ("User Profile".equals(currentPanel)) {
                     userPanel.loadUserInfo(this.currentUser);
                 }
                 // Also update the Navbar greeting which uses currentUser
                 updateUIState(currentPanel); // Refresh the UI state for the current panel

             } catch (UserNotFoundException e) {
                 // This indicates a serious issue where the logged-in user was deleted from the DB
                 // It might be best to force a logout in this scenario.
                 System.err.println("Logged-in user not found during refresh! Forcing logout.");
                 e.printStackTrace();
                 logout(); // Force logout
             } catch (Exception e) {
                  System.err.println("An error occurred while refreshing user data: " + e.getMessage());
                  e.printStackTrace();
                  // Log error, but don't necessarily force logout for general errors
             }
         }
         // If currentUser is null (admin or logged out), there's nothing to refresh.
     }


     // Method to handle user selection from RoleSelection -> now shows Login
    public void selectUserPerspective() {
         showPanel("Login");
    }

    // Method to handle admin selection from RoleSelection -> goes to admin dashboard
    public void selectAdminPerspective() {
        currentUser = null;
        isAdmin = true;
        showPanel("Dashboard");
    }

    /**
     * Attempts to log in a user with the given email and password.
     * Called from LoginPanel.
     * @param email The user's email.
     * @param password The user's password.
     */
    public void attemptLogin(String email, String password) {
        try {
            User user = userManagement.login(email, password);
            this.currentUser = user;
            this.isAdmin = false;

            // Optional: JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + user.getUsername(), "Success", JOptionPane.INFORMATION_MESSAGE);

            showPanel("Dashboard");

        } catch (UserNotFoundException e) {
            loginPanel.displayErrorMessage("Invalid email or password.");
        } catch (Exception e) {
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
                 null,
                 null, // orderId is null for top-up
                 currentUser.getUserId(),
                 "Top-up",
                 amount,
                 LocalDateTime.now()
             );
             transactionManagement.addTransaction(topupTransaction);

             // !!! Refresh the user data and UI after the balance update and transaction !!!
             refreshCurrentUserAndUI();

             JOptionPane.showMessageDialog(this, String.format("$%.2f added to your wallet!", amount), "Balance Updated", JOptionPane.INFORMATION_MESSAGE);


         } catch (UserNotFoundException e) {
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
            if (newUsername == null || newUsername.trim().isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            // Create a new User object with updated username, keeping other fields the same
            User updatedUser = new User(
                currentUser.getUserId(),
                currentUser.getEmail(),
                currentUser.getPassword(),
                newUsername.trim(),
                currentUser.getWalletBalance(),
                currentUser.getCreatedAt()
            );

            userManagement.updateUser(updatedUser);
            // Update the currentUser object in the frame's state
            this.currentUser = updatedUser;

            JOptionPane.showMessageDialog(this, "Username updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the User Profile panel display
            userPanel.loadUserInfo(this.currentUser);
            // Update navbar greeting (which uses currentUser)
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
            return;
        }
        gameVaultManagement.logout();
        currentUser = null;
        isAdmin = false;
        JOptionPane.showMessageDialog(this, "Logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
        // Navigate back to the Role Selection screen
        showPanel("RoleSelection");
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
                      if (comp.equals(roleSelectionPanel)) return "RoleSelection";
                      if (comp.equals(loginPanel)) return "Login";
                      if (comp.equals(signupPanel)) return "Signup";
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
             BorderFactory.createLineBorder(new Color(0, 123, 255), 2, true),
             BorderFactory.createEmptyBorder(15, 30, 15, 30)
         ));
         button.setBackground(new Color(0, 123, 255));
         button.setForeground(Color.WHITE);
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