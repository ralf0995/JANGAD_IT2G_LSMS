package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.Model.User;

import java.util.Scanner;

public class Authentication {
    private static final UserDao userDao = new UserDao();
    private static final ActivityLogDao logDao = new ActivityLogDao();

    public static User showLoginMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n=== LOGIN SYSTEM ===");
            System.out.println("1. Login");
            System.out.println("2. Register Customer Account");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    User user = userDao.authenticate(username, password);
                    if (user != null) {
                        // Use user.getRole() / user.getId() / user.getUsername() if using POJO,
                        // or user.role() / user.id() / user.username() if using a Record.
                        System.out.println("Login successful! Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
                        logDao.logAction(user.getId(), "LOGIN", "User logged into the system.");
                        return user;
                    } else {
                        System.out.println("Invalid username or password, or account inactive.");
                    }
                    break;
                case "2":
                    System.out.print("Choose new username: ");
                    String regUser = scanner.nextLine();
                    System.out.print("Choose password: ");
                    String regPass = scanner.nextLine();
                    System.out.print("Enter full name: "); // Added prompt to match 4-argument requirement
                    String regFullName = scanner.nextLine();

                    // Updated to pass 4 arguments: username, password, role, fullName
                    boolean success = userDao.registerUser(regUser, regPass, "Customer", regFullName);
                    if (success) {
                        System.out.println("Registration successful! You can now log in.");
                    }
                    break;
                case "3":
                    System.out.println("Exiting application...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}