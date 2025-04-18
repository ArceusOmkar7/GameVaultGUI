package com.project.gamevaultcli;

import com.project.gamevaultcli.management.GameVaultManagement;
import com.project.gamevaultcli.management.GameVaultMenu;
import com.project.gamevaultcli.management.CartManagement;
import com.project.gamevaultcli.management.GameManagement;
import com.project.gamevaultcli.management.OrderManagement;
import com.project.gamevaultcli.management.TransactionManagement;
import com.project.gamevaultcli.management.UserManagement;
import com.project.gamevaultcli.storage.CartStorage;
import com.project.gamevaultcli.storage.GameStorage;
import com.project.gamevaultcli.storage.OrderStorage;
import com.project.gamevaultcli.storage.TransactionStorage;
import com.project.gamevaultcli.storage.UserStorage;
import com.project.gamevaultcli.helpers.DBUtil; // Import DBUtil
import java.io.IOException;
import java.sql.SQLException;

public class GameVaultCLI {

    public static void main(String[] args) {
        try {
            // Initialize Storages
            UserStorage userStorage = new UserStorage();
            GameStorage gameStorage = new GameStorage();
            CartStorage cartStorage = new CartStorage(gameStorage);
            OrderStorage orderStorage = new OrderStorage();
            TransactionStorage transactionStorage = new TransactionStorage();

            // Initialize Managements
            UserManagement userManagement = new UserManagement(userStorage);
            GameManagement gameManagement = new GameManagement(gameStorage);
            CartManagement cartManagement = new CartManagement(cartStorage);
            TransactionManagement transactionManagement = new TransactionManagement(transactionStorage);
            OrderManagement orderManagement = new OrderManagement(orderStorage, cartStorage, userStorage, transactionManagement); // Modified

            // Initialize and load predefined data using the GameVaultManager
            GameVaultManagement vaultManager = new GameVaultManagement(userManagement, gameManagement, orderManagement, transactionManagement);
            vaultManager.initializeData();

            // Create and run the menu
            GameVaultMenu menu = new GameVaultMenu(userManagement, gameManagement, cartManagement, orderManagement, transactionManagement, vaultManager);
            menu.run();

        } finally {
            // Ensure database connection is closed when the application exits
            DBUtil.closeConnection();
        }
    }
}