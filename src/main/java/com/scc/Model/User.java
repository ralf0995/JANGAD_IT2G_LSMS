package com.scc.Model;

import java.util.Objects;

public class User {
    private final int id;
    private final String username;
    private final String password;
    private final String role;
    private final String status;
    private final String fullName;

    public User(int id, String username, String password, String role, String status, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.fullName = fullName;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getFullName() { return fullName; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - status: %s", id, username, role, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(role, user.role) &&
                Objects.equals(status, user.status) &&
                Objects.equals(fullName, user.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role, status, fullName);
    }
}