package com.project.gamevaultcli;

import com.project.gamevaultcli.management.GameVaultManagement;
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
import com.project.gamevaultcli.helpers.DBUtil;
import com.project.gamevaultgui.GameVaultFrame;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.SwingUtilities;

public class GameVaultCLI {

    public static void main(String[] args) {
        try {
            // Initialize Storages (but don't connect to DB yet)
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
            OrderManagement orderManagement = new OrderManagement(orderStorage, cartStorage, userStorage,
                    transactionManagement);

            // Create GameVaultManagement but DO NOT initialize data yet (which would
            // trigger DB connection)
            GameVaultManagement vaultManager = new GameVaultManagement(userManagement, gameManagement, orderManagement,
                    transactionManagement);

            // Launch the GUI first, which will handle the database connection through its
            // connection panel
            SwingUtilities.invokeLater(() -> {
                GameVaultFrame frame = new GameVaultFrame(
                        vaultManager,
                        userManagement,
                        gameManagement,
                        cartManagement,
                        orderManagement,
                        transactionManagement);
                frame.setVisible(true);

                // Start with the database connection panel
                frame.showPanel("DatabaseConnection");
            });

        } finally {
            // Connection closing is now handled by the GUI
        }
    }
}