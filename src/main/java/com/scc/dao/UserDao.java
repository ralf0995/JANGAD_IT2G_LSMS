package com.scc.dao;

import com.scc.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean usernameExists(String username) {
        if (isNullOrBlank(username)) {
            return false;
        }
        String sql = "SELECT 1 FROM users WHERE LOWER(username) = LOWER(?) LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public boolean registerUser(String username, String password, String role, String fullName) {
        if (isNullOrBlank(username) || isNullOrBlank(password) ||
                isNullOrBlank(role) || isNullOrBlank(fullName)) {
            System.out.println("Registration failed: All fields are required.");
            return false;
        }

        String trimmedUsername = username.trim();
        if (usernameExists(trimmedUsername)) {
            System.out.println("Username '" + trimmedUsername + "' is already taken. Please choose another.");
            return false;
        }

        String sql = "INSERT INTO users(username, password_hash, role, full_name, status) VALUES(?, ?, ?, ?, 'Active')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, trimmedUsername);
            pstmt.setString(2, SecurityUtil.hashPassword(password));
            pstmt.setString(3, role.trim());
            pstmt.setString(4, fullName.trim());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("SQLITE_CONSTRAINT_UNIQUE") || msg.contains("UNIQUE constraint failed: users.username"))) {
                System.out.println("Username '" + trimmedUsername + "' is already taken. Please choose another.");
            } else {
                System.err.println("Registration failed: " + msg);
            }
            return false;
        }
    }

    public User authenticate(String username, String password) {
        if (isNullOrBlank(username) || password == null) {
            return null;
        }
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) AND password_hash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            pstmt.setString(2, SecurityUtil.hashPassword(password));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    if ("Inactive".equalsIgnoreCase(status)) {
                        System.out.println("Account is inactive. Contact Administrator.");
                        return null;
                    }
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role"),
                            status,
                            rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getString("full_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public boolean updateStatus(int userId, String newStatus) {
        if (isNullOrBlank(newStatus)) {
            return false;
        }
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.trim());
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to update status: " + e.getMessage());
            return false;
        }
    }
}