package org.example;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField userField;
    private final JPasswordField passField;
    private final JButton loginButton;

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 5, 5));
        getContentPane().setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        userField = new JTextField();
        passField = new JPasswordField();
        loginButton = new JButton("Login");

        loginButton.setBackground(new Color(30, 144, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        add(userLabel);
        add(userField);
        add(passLabel);
        add(passField);
        add(new JLabel());
        add(loginButton);

        loginButton.addActionListener(e -> login());
    }

    private void login() {
        String login = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        boolean isAdmin = login.equalsIgnoreCase("Anna") && password.equals("Kowalska");
        boolean isJan = login.equalsIgnoreCase("Jan") && password.equals("Kowalski");

        if (isAdmin || isJan) {
            dispose();
            new PhonebookFrame(isAdmin).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
        }
    }
}
