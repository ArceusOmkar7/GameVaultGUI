package com.project.gamevaultgui;

import com.project.gamevaultcli.management.GameVaultManagement;
import com.project.gamevaultcli.management.CartManagement;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;
import com.project.gamevaultcli.management.UserManagement;
import com.project.gamevaultcli.entities.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import com.project.gamevaultgui.ManageGamesPanel;
import com.project.gamevaultgui.ManageUsersPanel;


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
    private JPanel roleSelectionPanel; // Panel for choosing role

    // Management classes
    private final GameVaultManagement gameVaultManagement;
    private final UserManagement userManagement;
    private final GameManagement gameManagement;
    private final CartManagement cartManagement;
    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;

    private User currentUser;
    private boolean isAdmin = false; // Flag to track if the current perspective is admin

    private ManageGamesPanel manageGamesPanel;
    private ManageUsersPanel manageUsersPanel;
    
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
                com.project.gamevaultcli.helpers.DBUtil.closeConnection();
            }
        });
    }

     private void initComponents() {
        sidebarPanel = new SidebarPanel(this);
        navbarPanel = new NavbarPanel("Welcome", this);

        centerPanel = new JPanel();
        cardLayout = new CardLayout();
        centerPanel.setLayout(cardLayout);

        dashboardPanel = new DashboardPanel(userManagement, gameManagement, orderManagement, transactionManagement);
        cartPanel = new CartPanel(cartManagement, gameManagement, this);
        billingPanel = new BillingPanel(orderManagement, transactionManagement, this);
        userPanel = new UserPanel();
        roleSelectionPanel = createRoleSelectionPanel();

        // Initialize Admin panels
        manageGamesPanel = new ManageGamesPanel();
        manageUsersPanel = new ManageUsersPanel();
    }

    private void addComponentsToFrame() {
        add(sidebarPanel, BorderLayout.WEST);
        add(navbarPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        centerPanel.add(roleSelectionPanel, "RoleSelection");
        centerPanel.add(dashboardPanel, "Dashboard");
        centerPanel.add(cartPanel, "Cart");
        centerPanel.add(billingPanel, "Billing");
        centerPanel.add(userPanel, "User");
        // Add Admin panels
        centerPanel.add(manageGamesPanel, "Games"); 
        centerPanel.add(manageUsersPanel, "Users"); 
    }

    private void setupEventHandlers() {
        // Event handlers for sidebar buttons are handled in SidebarPanel
        // Role selection handled in the roleSelectionPanel
    }

    public void showPanel(String panelName) {
        cardLayout.show(centerPanel, panelName);
        // Update content of panels when shown
        if ("Dashboard".equals(panelName)) {
            if (currentUser != null) {
                dashboardPanel.loadDashboardData(currentUser.getUserId());
            } else {
                // Handle admin dashboard view (if different) or show all game data
                 dashboardPanel.loadDashboardData(-1); // Use a dummy ID for admin view
            }
        } else if ("Cart".equals(panelName) && currentUser != null) {
            cartPanel.loadCart(currentUser.getUserId());
        } else if ("Billing".equals(panelName) && currentUser != null) {
            billingPanel.loadBills(currentUser.getUserId());
        } else if ("User".equals(panelName) && currentUser != null) {
             userPanel.loadUserInfo(currentUser);
        }
        // No specific load logic for admin panels in this basic implementation yet
        updateNavbar(panelName); // Update navbar title when showing panels
    }

    public void updateNavbar(String pageTitle) {
        if (currentUser != null) {
            navbarPanel.setGreeting("Hello, " + currentUser.getUsername());
            navbarPanel.setPageTitle(pageTitle);
            sidebarPanel.setVisible(true); // Show sidebar when logged in (as a user)
            sidebarPanel.updateSidebarForUser(); // Show user-specific buttons
            navbarPanel.showProfileIcon(true); // Show profile icon for user
        } else if (isAdmin) {
             navbarPanel.setGreeting("Hello, Admin");
             navbarPanel.setPageTitle(pageTitle);
             sidebarPanel.setVisible(true); // Show sidebar for admin
             sidebarPanel.updateSidebarForAdmin(); // Show admin-specific buttons
             navbarPanel.showProfileIcon(false); // Hide profile icon for admin (or show an admin icon)
        }
        else {
            navbarPanel.setGreeting(""); // No greeting on the initial screen
            navbarPanel.setPageTitle("Game Vault"); // Or a suitable initial title
            sidebarPanel.setVisible(false); // Hide sidebar initially
            navbarPanel.showProfileIcon(false); // Hide profile icon initially
        }
        revalidate(); // Revalidate the frame to reflect changes
        repaint(); // Repaint the frame
    }

     // Method to handle user selection
    public void selectUserPerspective() {
        // For simplicity, we'll create a dummy user for the user perspective
        // In a real application, you would handle user login here
        try {
             // Attempt to find an existing user (you might want to add a simple user selection dialog)
            java.util.List<User> allUsers = userManagement.getAllUsers();
            if (!allUsers.isEmpty()) {
                currentUser = allUsers.get(0); // Select the first user for demonstration
                isAdmin = false;
                updateNavbar("Dashboard");
                showPanel("Dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "No users found. Please create a user first.", "User Not Found", JOptionPane.INFORMATION_MESSAGE);
                 // Optionally go back to the role selection or provide a "Create User" option
                 showPanel("RoleSelection");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error selecting user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            showPanel("RoleSelection");
        }
    }

    // Method to handle admin selection
    public void selectAdminPerspective() {
        // No specific user for admin in this scenario (no auth)
        currentUser = null;
        isAdmin = true;
        updateNavbar("Dashboard (Admin)");
        showPanel("Dashboard"); // Admin might also see the dashboard initially
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return isAdmin;
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
        JLabel selectRoleLabel = new JLabel("Select Your Role:");
        selectRoleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JButton userButton = new JButton("User");
        JButton adminButton = new JButton("Admin");

        userButton.addActionListener(e -> selectUserPerspective());
        adminButton.addActionListener(e -> selectAdminPerspective());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(selectRoleLabel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(userButton);
        buttonPanel.add(adminButton);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    public static void main(String[] args) {
        // Initialize management classes (similar to your CLI)
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
        vaultManager.initializeData(); // Initialize some data

        SwingUtilities.invokeLater(() -> {
            new GameVaultFrame(vaultManager, userManagement, gameManagement, cartManagement, orderManagement, transactionManagement).setVisible(true);
        });
    }
}