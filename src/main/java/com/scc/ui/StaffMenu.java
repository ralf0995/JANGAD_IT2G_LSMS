package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.Model.User;
import java.util.Scanner;

public class StaffMenu {
    private static final ActivityLogDao logDao = new ActivityLogDao();

    public static void show(Scanner scanner, User staff) {
        while (true) {
            System.out.println("\n=== STAFF MENU ===");
            System.out.println("1. Perform Staff Task");
            System.out.println("2. View Activity Logs");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("Executing staff task...");
                    logDao.logAction(staff.id(), "STAFF_TASK", "Performed routine staff task.");
                    break;
                case "2":
                    ActivityLogViewer.displayLogs();
                    logDao.logAction(staff.id(), "VIEW_LOGS", "Staff viewed activity logs.");
                    break;
                case "3":
                    logDao.logAction(staff.id(), "LOGOUT", "Staff logged out.");
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}