package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.Model.User;
import java.util.Scanner;

public class CustomerMenu {
    private static final ActivityLogDao logDao = new ActivityLogDao();
    public static void show(Scanner scanner, User customer) {
        while (true) {
            System.out.println("\n=== CUSTOMER MENU ===");
            System.out.println("1. View Profile");
            System.out.println("2. Place Order / Request Service");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println(customer.toString());
                    // Updated to use getId()
                    logDao.logAction(customer.getId(),
                            "VIEW_PROFILE",
                            "Customer viewed profile info.");
                    break;
                case "2":
                    System.out.println("Order placed successfully!");
                    // Updated to use getId()
                    logDao.logAction(customer.getId(), "PLACE_ORDER", "Customer placed a new service/order request.");
                    break;
                case "3":
                    // Updated to use getId()
                    logDao.logAction(customer.getId(), "LOGOUT", "Customer logged out.");
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}