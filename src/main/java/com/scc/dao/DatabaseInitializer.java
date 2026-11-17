package com.scc.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DatabaseInitializer {

    // Added to support calls looking for .initialize()
    public static void initialize() {
        initializeDatabase();
    }

    public static void initializeDatabase() {
        // 1. Create the Users table (Parent table) - Changed table name from 'user' to 'users'
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "role TEXT CHECK(role IN ('Admin', 'Cashier', 'Customer')) NOT NULL, " +
                "full_name TEXT NOT NULL, " +
                "status TEXT DEFAULT 'Active', " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP);";

        // 2. Create the Customers table (Links to Users)
        String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers (" +
                "customer_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "full_name TEXT NOT NULL, " +
                "contact_no TEXT, " +
                "address TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL);";

        // 3. Create the Transactions table (Links to Customers and Users)
        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "service_id INTEGER, " +
                "weight_kg REAL, " +
                "total_amount REAL NOT NULL, " +
                "status TEXT DEFAULT 'Pending', " +
                "date_created DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT);";

        // 4. Create the Activity_logs table (Links to Users)
        String createActivityLogsTable = "CREATE TABLE IF NOT EXISTS activity_logs (" +
                "log_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "action TEXT CHECK(action IN ('INSERT', 'UPDATE', 'DELETE', 'LOGIN')) NOT NULL, " +
                "table_affected TEXT NOT NULL, " +
                "record_id INTEGER, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL);";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Execute table creations
            stmt.execute(createUsersTable);
            stmt.execute(createCustomersTable);
            stmt.execute(createTransactionsTable);
            stmt.execute(createActivityLogsTable);

            // Insert default admin if not exists (matching schema constraints)
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE role = 'Admin';";
            ResultSet rs = stmt.executeQuery(checkAdmin);
            if (rs.next() && rs.getInt(1) == 0) {
                String defaultAdmin = "INSERT INTO users (username, password_hash, role, full_name, status) " +
                        "VALUES ('admin', '" + SecurityUtil.hashPassword("admin123") + "', 'Admin', 'System Administrator', 'Active');";
                stmt.execute(defaultAdmin);
            }
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
}