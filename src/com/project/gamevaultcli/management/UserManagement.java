package com.project.gamevaultcli.management;

import com.project.gamevaultcli.entities.User;
import com.project.gamevaultcli.exceptions.InvalidUserDataException;
import com.project.gamevaultcli.exceptions.UserNotFoundException;
import com.project.gamevaultcli.storage.UserStorage;

import java.util.List;

public class UserManagement {

    private final UserStorage userStorage;

    public UserManagement(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(int userId) throws UserNotFoundException {
        User user = userStorage.findById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        return user;
    }

    public User login(String email, String password) throws UserNotFoundException {
        User user = userStorage.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new UserNotFoundException("Invalid email or password");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public void addUser(User user) throws InvalidUserDataException {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new InvalidUserDataException("username", "Username cannot be empty");
        }
        // Optional: Check for duplicate email or username before saving
        if (userStorage.findByEmail(user.getEmail()) != null) {
             throw new InvalidUserDataException("email", "Email address is already registered.");
        }
        // Add check for duplicate username if needed, requires findByUsername in storage

        userStorage.save(user);
    }

     /**
      * Updates an existing user's information in the database.
      * Includes basic validation for the username.
      * @param user The User object with updated information (userId must be set).
      * @throws InvalidUserDataException If the updated user data is invalid.
      * @throws UserNotFoundException If the user with the given ID does not exist.
      */
    public void updateUser(User user) throws InvalidUserDataException, UserNotFoundException {
         if (user.getUserId() <= 0) {
             throw new InvalidUserDataException("userId", "User ID is required for update.");
         }
         if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new InvalidUserDataException("username", "Username cannot be empty");
        }
         // You might want to check if the new username is already taken by another user.
         // This requires fetching by username and checking if the found user has a different ID.

        // The UserStorage.update method updates all fields, so we don't need to
        // fetch the existing user's details if the calling code provides the full User object.
        // However, if you only passed partial data (like just userId and new username),
        // you'd need to fetch the full user, update it, and then pass to storage.
        // Assuming the GUI passes the full User object with the updated username:
         userStorage.update(user);
         // If userStorage.update could fail silently when ID not found, add a check:
         // if (userStorage.findById(user.getUserId()) == null) {
         //      throw new UserNotFoundException("User with ID " + user.getUserId() + " not found after attempting update.");
         // }
    }

    /**
     * Updates the wallet balance for a specific user.
     * @param userId The ID of the user.
     * @param amount The amount to add (must be positive).
     * @throws UserNotFoundException If the user is not found.
     * @throws InvalidUserDataException If the amount is invalid (should be positive).
     */
     public void updateWalletBalance(int userId, float amount) throws UserNotFoundException, InvalidUserDataException {
         if (amount <= 0) {
             throw new InvalidUserDataException("amount", "Amount to add must be positive.");
         }

         User user = userStorage.findById(userId);
         if (user == null) {
             throw new UserNotFoundException("User with ID " + userId + " not found for balance update.");
         }

         float newBalance = user.getWalletBalance() + amount;
         user.setWalletBalance(newBalance); // Update the user object

         userStorage.update(user); // Save the updated user object to the database
     }


    public void deleteUser(int userId) {
        userStorage.delete(userId);
    }
}