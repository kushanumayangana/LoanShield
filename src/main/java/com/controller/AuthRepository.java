package com.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// ABSTRACTION: This class demonstrates abstraction by:

public class AuthRepository {

    private static final String DB_URL = "jdbc:sqlite:database/loanshield.db?busy_timeout=5000";
    public static final String REGISTRATION_SECRET = "1122448";

    private static void ensureDbDir() {
        try {
            Path dir = Paths.get("database");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (Exception ignored) {}
    }

    private static void ensureDriverLoaded() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) {}
    }

    private static void ensureUsersTable(Connection conn) throws SQLException {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL
            );
        """;
        conn.createStatement().execute(createUsers);
    }

    private static Connection open() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA journal_mode=WAL");
            s.execute("PRAGMA synchronous=NORMAL");
            s.execute("PRAGMA busy_timeout=5000");
        }
        return conn;
    }

    // ABSTRACTION: This method provides a simple interface for user registration
    
    // Complex operations like password hashing, database setup, and SQL execution are hidden
    public static boolean register(String username, String password, String secret) {
        if (!REGISTRATION_SECRET.equals(secret)) return false;
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        ensureDbDir();
        ensureDriverLoaded();
        try (Connection conn = open()) {
            ensureUsersTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, sha256(password));
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ABSTRACTION: This method provides a simple interface for user login
    // Complex operations like password hashing, database queries, and security checks are abstracted
    public static boolean login(String username, String password) {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        ensureDbDir();
        ensureDriverLoaded();
        try (Connection conn = open()) {
            ensureUsersTable(conn);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String expected = rs.getString(1);
                    return expected != null && expected.equals(sha256(password));
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ABSTRACTION: This method provides a simple interface for checking username existence
    // Database connection and query logic are hidden from the caller
    public static boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        ensureDbDir();
        ensureDriverLoaded();
        try (Connection conn = open();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ensureUsersTable(conn);
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ABSTRACTION: This private method encapsulates the complex SHA-256 hashing algorithm
    // The hashing implementation details are hidden from other methods
    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

