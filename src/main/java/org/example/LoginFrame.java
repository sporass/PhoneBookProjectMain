package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JLabel errorLabel;

    public LoginFrame() {
        setTitle("📒Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,15,10,15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        add(title, gbc);

        gbc.gridwidth=1; gbc.gridy++;
        add(new JLabel("Login:"), gbc);

        userField = new JTextField();
        userField.setPreferredSize(new Dimension(200, userField.getPreferredSize().height));
        gbc.gridx=1;
        add(userField, gbc);

        gbc.gridx=0; gbc.gridy++;
        add(new JLabel("Password:"), gbc);

        passField = new JPasswordFieldWithEye(20);
        passField.setPreferredSize(new Dimension(200, passField.getPreferredSize().height));
        gbc.gridx=1;
        add(passField, gbc);

        JButton loginButton = new JButton("Log in");
        gbc.gridx=0; gbc.gridy++; gbc.gridwidth=2;
        add(loginButton, gbc);

        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        add(errorLabel, gbc);

        loginButton.addActionListener(e -> login());
    }

    private void login() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM USERS WHERE USERNAME=?")) { // <- TUTAJ

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                String storedHash = rs.getString("PASSWORDHASH");
                String storedSalt = rs.getString("SALT");
                String role = rs.getString("ROLE");

                if(Database.verifyPassword(password, storedHash, storedSalt)) {
                    dispose();
                    boolean isAdmin = role.equals("ADMIN");
                    new PhonebookFrame(isAdmin, username, role).setVisible(true);
                } else {
                    errorLabel.setText("Invalid username or password");
                }
            } else {
                errorLabel.setText("Invalid username or password");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            errorLabel.setText("Database connection error");
        }
    }



}

