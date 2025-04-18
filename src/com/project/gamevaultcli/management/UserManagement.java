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
        // Basic validation
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new InvalidUserDataException("username", "Username cannot be empty");
        }
        userStorage.save(user);
    }

    public void updateUser(User user) {
        userStorage.update(user);
    }

    public void deleteUser(int userId) {
        userStorage.delete(userId);
    }
}