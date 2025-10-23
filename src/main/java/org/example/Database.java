package org.example;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Database {
    private static final String DB_URL = "jdbc:derby:phonebookDB;create=true";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tworzenie tabeli users
            try {
                stmt.executeUpdate("""
    CREATE TABLE USERS (
        ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        USERNAME VARCHAR(50) UNIQUE,
        PASSWORDHASH VARCHAR(256),
        SALT VARCHAR(50),
        ROLE VARCHAR(20)
    )
""");

            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) e.printStackTrace();
            }

            // Tworzenie tabeli employees
            try {
                stmt.executeUpdate("""
                    CREATE TABLE employees (
                        ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        FIRSTNAME VARCHAR(50),
                        LASTNAME VARCHAR(50),
                        EMAIL VARCHAR(100),
                        PHONE VARCHAR(20)
                    )
                """);
            } catch (SQLException e) {
                if (!"X0Y32".equals(e.getSQLState())) e.printStackTrace();
            }

            // Dodaj admina i użytkownika domyślnego
            insertUserIfNotExists(conn, "Admin", "1234", "ADMIN");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertUserIfNotExists(Connection conn, String username, String password, String role) {
        try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM USERS WHERE USERNAME = ?")) {
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                // generowanie salt + hash
                String salt = generateSalt();
                String hash = hashPassword(password, salt);
                try (PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO USERS (USERNAME, PASSWORDHASH, SALT, ROLE) VALUES (?, ?, ?, ?)")) {
                    insert.setString(1, username);
                    insert.setString(2, hash);
                    insert.setString(3, salt);
                    insert.setString(4, role);
                    insert.executeUpdate();
                }
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private static String generateSalt() {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String enteredPassword, String storedHash, String storedSalt) {
        try {
            String hash = hashPassword(enteredPassword, storedSalt);
            return hash.equals(storedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
