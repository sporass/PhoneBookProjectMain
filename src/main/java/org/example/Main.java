package org.example;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Ustawienie FlatLaf jako Look & Feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Dodatkowe ustawienia stylu
            UIManager.put("Button.arc", 20);           // Zaokrąglone przyciski
            UIManager.put("TextComponent.arc", 15);    // Zaokrąglone pola tekstowe
            UIManager.put("Component.focusWidth", 2);  // Wskaźnik focusFF
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Inicjalizacja bazy danych (tworzy tabele USERS i EMPLOYEES jeśli nie istnieją)
        Database.initialize();

        // Uruchomienie GUI
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
