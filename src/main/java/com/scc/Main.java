package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.Model.User;
import com.scc.ui.Authentication;
import com.scc.ui.AdminMenu;
import com.scc.ui.CustomerMenu;
import com.scc.ui.StaffMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Laundry Shop Management System is running...");

        // 1. Initialize tables and handle potential database errors
        try {
            DatabaseInitializer.initialize();
        } catch (Exception e) {
            System.out.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            return; // Exit if database setup fails
        }

        Scanner scanner = new Scanner(System.in);

        // 2. Launch login menu and get the authenticated user
        User loggedInUser = Authentication.showLoginMenu(scanner);

        if (loggedInUser != null) {
            // 3. Route user to their respective menu based on their role
            // (Use loggedInUser.getRole() if using POJO, or loggedInUser.role() if using a record)
            String role = loggedInUser.getRole();

            if ("Admin".equalsIgnoreCase(role)) {
                AdminMenu.show(scanner, loggedInUser);
            } else if ("Staff".equalsIgnoreCase(role)) {
                StaffMenu.show(scanner, loggedInUser);
            } else if ("Customer".equalsIgnoreCase(role)) {
                CustomerMenu.show(scanner, loggedInUser);
            } else {
                System.out.println("Unknown user role.");
            }
        }

        scanner.close();
    }
}