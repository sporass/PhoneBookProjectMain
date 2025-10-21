package org.example;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private JLabel errorLabel;

    public LoginFrame() {
        setTitle("📒 PhoneBook - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Logowanie do PhoneBook");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        // Użytkownik
        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Użytkownik:"), gbc);

        userField = new JTextField();
        gbc.gridx = 1;
        add(userField, gbc);

        // Hasło
        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Hasło:"), gbc);

        passField = new JPasswordField();
        gbc.gridx = 1;
        add(passField, gbc);

        // Przycisk logowania
        JButton loginButton = new JButton("Zaloguj");
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        add(loginButton, gbc);

        // Etykieta błędu
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

        boolean isAdmin = "Anna".equalsIgnoreCase(username) && "Kowalska".equals(password);
        boolean isJan = "Jan".equalsIgnoreCase(username) && "Kowalski".equals(password);

        if (isAdmin || isJan) {
            dispose();
            new PhonebookFrame(isAdmin).setVisible(true);
        } else {
            errorLabel.setText("Niepoprawny login lub hasło");
        }
    }
}
