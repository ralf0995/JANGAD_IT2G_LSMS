package com.scc;

import com.scc.Model.User;
import com.scc.dao.DatabaseInitializer;
import com.scc.ui.AdminMenu;
import com.scc.ui.Authentication;
import com.scc.ui.CustomerMenu;
import com.scc.ui.StaffMenu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Laundry Shop Management System is running...");

        // 1. Initialize tables and verify database setup
        try {
            DatabaseInitializer.initialize();
        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(System.in);

        // 2. Session loop so users can log out and log back in
        while (true) {
            User loggedInUser = Authentication.showLoginMenu(scanner);
            if (loggedInUser == null) {
                System.out.println("Exiting Laundry Shop Management System. Goodbye!");
                break;
            }

            // 3. Route user to their respective menu based on role
            String role = loggedInUser.getRole();
            if ("Admin".equalsIgnoreCase(role)) {
                AdminMenu.show(scanner, loggedInUser);
            } else if ("Staff".equalsIgnoreCase(role) || "Cashier".equalsIgnoreCase(role)) {
                StaffMenu.show(scanner, loggedInUser);
            } else if ("Customer".equalsIgnoreCase(role)) {
                CustomerMenu.show(scanner, loggedInUser);
            } else {
                System.out.println("Unknown user role: " + role);
            }
        }
    }
}