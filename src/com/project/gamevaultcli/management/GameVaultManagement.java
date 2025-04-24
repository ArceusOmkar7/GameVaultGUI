package com.project.gamevaultcli.management;

import com.project.gamevaultcli.entities.Game;
import com.project.gamevaultcli.entities.Transaction;
import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.exceptions.InvalidUserDataException;
import com.project.gamevaultcli.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Date;

public class GameVaultManagement {

    private final UserManagement userManagement;
    private final GameManagement gameManagement;
    private final OrderManagement orderManagement;
    private final TransactionManagement transactionManagement;
    private User currentUser = null;

    public GameVaultManagement(UserManagement userManagement, GameManagement gameManagement,
            OrderManagement orderManagement, TransactionManagement transactionManagement) {
        this.userManagement = userManagement;
        this.gameManagement = gameManagement;
        this.orderManagement = orderManagement;
        this.transactionManagement = transactionManagement;
    }

    public void initializeData() {
        try {
            // Predefined Users
            User user1 = new User("sasuke@gmail.com", "is this my password?", "Sasuke", 50.0f);
            userManagement.addUser(user1);
            User user2 = new User("naruto@gmail.com", "believeit", "Naruto", 100.0f);
            userManagement.addUser(user2);
            // Add the default user shown in the login panel
            User user3 = new User("user@user.com", "1234", "DefaultUser", 200.0f);
            userManagement.addUser(user3);

            // Predefined Games
            Game game1 = new Game("Spider-Man Remastered", "Game created ?? lol lorem ipsum or wot", "Insomniac Games",
                    "PC", 52.3f, new Date());
            gameManagement.addGame(game1);
            Game game2 = new Game("God of War", "A great game", "Santa Monica Studio", "PS4", 49.99f, new Date());
            gameManagement.addGame(game2);
            Game game3 = new Game("The Last of Us Part II", "A controversial masterpiece", "Naughty Dog", "PS4", 59.99f,
                    new Date());
            gameManagement.addGame(game3);
            Game game4 = new Game("Cyberpunk 2077", "A buggy mess... or is it?", "CD Projekt Red", "PC", 39.99f,
                    new Date());
            gameManagement.addGame(game4);

            // Predefined Orders - will need to manually create cart and place order
            // Predefined Transactions
            // Transaction transaction1 = new Transaction(1, user1.getUserId(),
            // game1.getGameId(), "Purchase", 52.3f, LocalDateTime.now());
            // transactionManagement.addTransaction(transaction1);

        } catch (InvalidUserDataException e) {
            System.out.println("Error initializing data: " + e.getMessage());
        }
    }

    // Add login method to GameVaultManager
    public User login(String email, String password) throws UserNotFoundException {
        User user = userManagement.login(email, password);
        this.currentUser = user;
        return user;
    }

    // Add logout method to GameVaultManager
    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}