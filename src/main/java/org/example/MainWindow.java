package com.projectmain;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private final ContactTablePanel tablePanel;
    private final ControlPanel controlPanel;

    public MainWindow() {
        setTitle("Phonebook");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);

        tablePanel = new ContactTablePanel();
        controlPanel = new ControlPanel(tablePanel);

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }
}
