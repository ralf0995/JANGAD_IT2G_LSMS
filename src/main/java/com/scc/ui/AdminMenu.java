package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.dao.ActivityLogDao;
import com.scc.Model.*;

import java.util.Scanner;

public class AdminMenu {
    private static final UserDao userDao = new UserDao();
    private static final ActivityLogDao logDao = new ActivityLogDao();

    public static void show(Scanner scanner, User admin) {
        while (true) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All Users");
            System.out.println("2. Register New Admin/Cashier Account");
            System.out.println("3. Change User Status (Active/Inactive)");
            System.out.println("4. View Activity Logs");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n--- USER LIST ---");
                    for (User u : userDao.getAllUsers()) {
                        System.out.println(u.toString());
                    }
                    logDao.logAction(admin.id(), "VIEW_USERS", "Admin viewed all system users.");
                    break;
                case "2":
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    System.out.print("Enter role (Admin/Cashier): "); // Updated role options to match DB constraint
                    String role = scanner.nextLine();
                    System.out.print("Enter full name: "); // Added prompt for full name
                    String fullName = scanner.nextLine();

                    // Updated to pass 4 arguments matching UserDao.registerUser
                    if (userDao.registerUser(username, password, role, fullName)) {
                        System.out.println("Account created successfully.");
                        logDao.logAction(admin.id(), "CREATE_USER", "Created new " + role + " account: " + username);
                    }
                    break;
                case "3":
                    System.out.print("Enter User ID to modify status: ");
                    try {
                        int targetId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter new status (Active/Inactive): ");
                        String newStatus = scanner.nextLine();
                        if (userDao.updateStatus(targetId, newStatus)) {
                            System.out.println("User status updated successfully.");
                            logDao.logAction(admin.id(), "UPDATE_STATUS", "Updated user ID " + targetId + " status to " + newStatus);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format.");
                    }
                    break;
                case "4":
                    ActivityLogViewer.displayLogs();
                    logDao.logAction(admin.id(), "VIEW_LOGS", "Admin viewed activity logs.");
                    break;
                case "5":
                    logDao.logAction(admin.id(), "LOGOUT", "Admin logged out.");
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}