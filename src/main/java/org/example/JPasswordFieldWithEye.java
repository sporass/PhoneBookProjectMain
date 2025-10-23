package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JPasswordFieldWithEye extends JPasswordField {

    private boolean showPassword = false;
    private final int iconSize = 16; // rozmiar ikony

    public JPasswordFieldWithEye(int columns) {
        super(columns);
        setEchoChar((Character) UIManager.get("PasswordField.echoChar"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // jeśli kliknięto w obszar oka po prawej stronie
                if (e.getX() >= getWidth() - iconSize - 4) {
                    togglePassword();
                }
            }
        });
    }

    private void togglePassword() {
        showPassword = !showPassword;
        setEchoChar(showPassword ? (char) 0 : (Character) UIManager.get("PasswordField.echoChar"));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Rysowanie ikonki oka
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = getWidth() - iconSize - 4;
        int y = (getHeight() - iconSize) / 2;

        g2.setColor(Color.GRAY);
        g2.drawString(showPassword ? "🙈" : "👁️", x, y + iconSize - 2);

        g2.dispose();
    }
}
