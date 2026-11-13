package com.scc.dao;

import com.scc.Model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public boolean registerUser(String username, String password, String role, String fullName) {
        String sql = "INSERT INTO users(username, password_hash, role, full_name, status) VALUES(?, ?, ?, ?, 'Active')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, SecurityUtil.hashPassword(password));
            pstmt.setString(3, role);
            pstmt.setString(4, fullName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, SecurityUtil.hashPassword(password));
            ResultSet rs = pstmt.executeQuery();
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
                        rs.getString("full_name") // Added 6th parameter
                );
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
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
                        rs.getString("full_name") // Added 6th parameter
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public boolean updateStatus(int userId, String newStatus) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Failed to update status: " + e.getMessage());
            return false;
        }
    }
}