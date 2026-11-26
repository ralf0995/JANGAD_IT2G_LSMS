package com.scc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    // Added to support calls looking for .initialize()
    public static void initialize() {
        initializeDatabase();
    }

    public static void initializeDatabase() {
        // 1. Create the Users table (Parent table)
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
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
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL);";

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
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT);";

        // 4. Create the Activity_logs table (Links to Users)
        String createActivityLogsTable = "CREATE TABLE IF NOT EXISTS activity_logs (" +
                "log_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "action_type TEXT CHECK(action_type IN ('INSERT', 'UPDATE', 'DELETE', 'LOGIN')) NOT NULL, " +
                "table_affected TEXT NOT NULL, " +
                "record_id INTEGER, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL);";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Enforce foreign keys in SQLite
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Execute table creations
            stmt.execute(createUsersTable);
            stmt.execute(createCustomersTable);
            stmt.execute(createTransactionsTable);
            stmt.execute(createActivityLogsTable);

            // Ensure action_type exists if activity_logs was created by an older schema
            ensureActionTypeColumn(conn);

            // Insert default admin if not exists
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE role = 'Admin';";
            try (ResultSet rs = stmt.executeQuery(checkAdmin)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertAdmin = "INSERT INTO users (username, password_hash, role, full_name, status) " +
                            "VALUES ('admin', ?, 'Admin', 'System Administrator', 'Active');";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertAdmin)) {
                        pstmt.setString(1, SecurityUtil.hashPassword("admin123"));
                        pstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    private static void ensureActionTypeColumn(Connection conn) throws SQLException {
        boolean hasActionType = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(activity_logs);")) {
            while (rs.next()) {
                if ("action_type".equalsIgnoreCase(rs.getString("name"))) {
                    hasActionType = true;
                    break;
                }
            }
        }

        if (!hasActionType) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE activity_logs ADD COLUMN action_type TEXT;");
            }
        }
    }
}