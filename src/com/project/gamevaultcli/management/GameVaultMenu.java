package com.project.gamevaultcli.management;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.entities.Order;
import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.exceptions.CartEmptyException;
import com.project.gamevaultcli.exceptions.GameNotFoundException;
import com.project.gamevaultcli.exceptions.UserNotFoundException;
import com.project.gamevaultcli.helpers.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class GameVaultMenu {

    private final UserManagement userManagement;
    private final GameManagement gameManagement;
    private final CartManagement cartManagement;
    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;
    private final GameVaultManagement vaultManager; // Reference to the manager
    private final Scanner scanner = new Scanner(System.in);

    private User currentUser = null; // Track the currently logged-in user.

    public GameVaultMenu(UserManagement userManagement, GameManagement gameManagement, CartManagement cartManagement, OrderManagement orderManagement, TransactionManagement transactionManagement, GameVaultManagement vaultManager) {
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
        this.cartManagement = cartManagement;
        this.orderManagement = orderManagement;
        this.transactionManagement = transactionManagement;
        this.vaultManager = vaultManager;
    }

    public void run() {
        boolean running = true;
        while (running) {
            displayMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        createGame();
                    case 3:
                         addGameToCart();
                         break;
                     case 4:
                         placeOrder();
                         break;
                     case 5:
                         viewCart();
                         break;
                     case 6:
                         listGames();
                         break;
                     case 7:
                         listUsers();
                         break;
                     case 8:
                         viewOrders();
                         break;
                     case 9:
                         viewTransactions();
                         break;
                     case 10:
                         login();
                         break;
                     case 11:
                         logout();
                         break;
                     case 0:
                         running = false;
                         System.out.println("Exiting Game Vault CLI...");
                         break;
                     default:
                         System.out.println("Invalid choice. Please try again.");
                 }
             } catch (InputMismatchException e) {
                 System.out.println("Invalid input. Please enter a number.");
                 scanner.nextLine(); // Clear the invalid input
             } catch (Exception e) {
                 System.out.println("An unexpected error occurred: " + e.getMessage());
             }
         }
     }

     private void displayMenu() {
         System.out.println("\nGame Vault CLI Menu:");
         System.out.println("Current User: " + (currentUser != null ? currentUser.getUsername() : "Not logged in")); // Show current user
         System.out.println("1. Create User");
         System.out.println("2. Create Game");
         System.out.println("3. Add Game to Cart");
         System.out.println("4. Place Order");
         System.out.println("5. View Cart");
         System.out.println("6. List Games");
         System.out.println("7. List Users");
         System.out.println("8. View Orders");
         System.out.println("9. View Transactions");
         System.out.println("10. Login");
         System.out.println("11. Logout");
         System.out.println("0. Exit");
         System.out.print("Enter your choice: ");
     }

     private void createUser() {
         try {
             System.out.print("Enter email: ");
             String email = scanner.nextLine();
             System.out.print("Enter password: ");
             String password = scanner.nextLine();
             System.out.print("Enter username: ");
             String username = scanner.nextLine();
             System.out.print("Enter wallet balance: ");
             float walletBalance = scanner.nextFloat();
             scanner.nextLine(); // Consume newline

             User user = new User(email, password, username, walletBalance);
             userManagement.addUser(user);
             System.out.println("User created: " + user.getUsername() + " with ID: " + user.getUserId());
         } catch (Exception e) {
             System.out.println("Error creating user: " + e.getMessage());
         }
     }

     private void createGame() {
         try {
             System.out.print("Enter game title: ");
             String title = scanner.nextLine();
             System.out.print("Enter game description: ");
             String description = scanner.nextLine();
             System.out.print("Enter game developer: ");
             String developer = scanner.nextLine();
             System.out.print("Enter game platform: ");
             String platform = scanner.nextLine();
             System.out.print("Enter game price: ");
             float price = scanner.nextFloat();
             scanner.nextLine(); // Consume newline

             Game game = new Game(title, description, developer, platform, price, new java.util.Date());
             gameManagement.addGame(game);
             System.out.println("Game created: " + game.getTitle() + " with ID: " + game.getGameId());
         } catch (Exception e) {
             System.out.println("Error creating game: " + e.getMessage());
         }
     }
   private void addGameToCart() {
        if (currentUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        try {
            System.out.print("Enter Game ID to add to cart: ");
            int gameId = scanner.nextInt();
            scanner.nextLine();

            Game game = gameManagement.getGame(gameId);
            cartManagement.addGameToCart(currentUser.getUserId(), gameId);
            System.out.println("Added " + game.getTitle() + " to cart for user " + currentUser.getUsername());
        } catch (GameNotFoundException e) {
            System.out.println("Game not found with ID: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error adding game to cart: " + e.getMessage());
        }
    }

    private void placeOrder() {
        if (currentUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        try {
            orderManagement.placeOrder(currentUser.getUserId());
            System.out.println("Order placed for user " + currentUser.getUsername());
        } catch (CartEmptyException e) {
            System.out.println("Cart is empty: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error placing order: " + e.getMessage());
        }
    }
    private void viewCart() {
           if (currentUser == null) {
               System.out.println("Please log in first.");
               return;
           }

           try {
               List<Game> gamesInCart = cartManagement.getGamesInCart(currentUser.getUserId());
               System.out.println("\n--- Games in Cart ---");

               if (gamesInCart.isEmpty()) {
                   System.out.println("Your cart is empty.");
               } else {
                   List<String> columnNames = Arrays.asList("gameId", "title", "developer", "platform", "price", "description");
                   Helper.printTable(gamesInCart, columnNames);
               }
           } catch (CartEmptyException e) {
               System.out.println("Your cart is empty.");
           }
           catch (Exception e) {
               System.out.println("Error viewing cart: " + e.getMessage());
           }
       }
    private void listGames() {
        try {
            List<Game> games = gameManagement.getAllGames();
            System.out.println("\n--- Game List ---");
            List<String> columnNames = Arrays.asList("gameId", "title", "developer", "platform", "price", "description");
            Helper.printTable(games, columnNames);
        } catch (Exception e) {
            System.out.println("Error listing games: " + e.getMessage());
        }
    }

    private void listUsers() {
        try {
            List<User> users = userManagement.getAllUsers();
            System.out.println("\n--- User List ---");
            List<String> columnNames = Arrays.asList("userId", "username", "email", "walletBalance");
            Helper.printTable(users, columnNames);
        } catch (Exception e) {
            System.out.println("Error listing users: " + e.getMessage());
        }
    }

    private void viewOrders() {
        if (currentUser == null) {
            System.out.println("Please log in first.");
            return;
        }

        try {
            List<Order> allOrders = orderManagement.getAllOrders();
            System.out.println("\n--- Orders List ---");

            List<String> columnNames = Arrays.asList("orderId", "userId", "totalAmount", "orderDate");
            List<Order> userOrders = new ArrayList<>();

            for (Order order : allOrders) {
                if (order.getUserId() == currentUser.getUserId()) {
                    userOrders.add(order);
                }
            }
            Helper.printTable(userOrders, columnNames);

        } catch (Exception e) {
            System.out.println("Error viewing orders: " + e.getMessage());
        }
    }

    private void viewTransactions() {
        if (currentUser == null) {
            System.out.println("Please log in first.");
            return;
        }
        try {
            List<Transaction> allTransactions = transactionManagement.getAllTransactions();
            System.out.println("\n--- Transactions List ---");
            List<String> columnNames = Arrays.asList("transactionId", "orderId", "userId", "transactionType", "amount", "transactionDate");
            List<Transaction> userTransactions = new ArrayList<>();

            for (Transaction transaction : allTransactions) {
                if (transaction.getUserId() == currentUser.getUserId()) {
                    userTransactions.add(transaction);
                }
            }
            Helper.printTable(userTransactions, columnNames);

        } catch (Exception e) {
            System.out.println("Error viewing transactions: " + e.getMessage());
        }
    }

    private void login() {
        try {
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            User user = vaultManager.login(email, password); // Use the login method in GameVaultManager
            if (user != null) {
                currentUser = user;
                System.out.println("Logged in as: " + user.getUsername());
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid email and password.");
            scanner.nextLine(); // Clear the invalid input
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage()); // User not found message
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void logout() {
        vaultManager.logout();
        currentUser = null;
        System.out.println("Logged out.");
    }
}