package org.example;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        Color primaryBlue = new Color(0, 102, 204);
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);

        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.NONE;

        JLabel titleLabel = new JLabel("Zaloguj się");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(primaryBlue);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        JLabel userLabel = new JLabel("Użytkownik:");
        userLabel.setFont(baseFont);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        loginPanel.add(userLabel, gbc);

        JTextField userField = new JTextField();
        userField.setFont(baseFont);
        gbc.gridx = 1;
        userField.setPreferredSize(new Dimension(200, 30));
        loginPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Hasło:");
        passLabel.setFont(baseFont);
        gbc.gridx = 0;
        gbc.gridy++;
        loginPanel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField();
        passField.setFont(baseFont);
        gbc.gridx = 1;
        passField.setPreferredSize(new Dimension(200, 30));
        loginPanel.add(passField, gbc);

        JButton loginButton = new JButton("Zaloguj");
        loginButton.setFont(baseFont);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(primaryBlue);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(baseFont);
        errorLabel.setForeground(Color.RED);
        gbc.gridy++;
        loginPanel.add(errorLabel, gbc);

        add(loginPanel);
        setVisible(true);

        // --- logika logowania ---
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            boolean isAdmin = "Anna".equalsIgnoreCase(username) && "Kowalska".equals(password);
            boolean isJan = "Jan".equalsIgnoreCase(username) && "Kowalski".equals(password);

            if (isAdmin || isJan) {
                dispose(); // zamyka okno logowania
                new PhonebookFrame(isAdmin).setVisible(true); // otwiera aplikację
            } else {
                errorLabel.setText("Niepoprawny login lub hasło");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
