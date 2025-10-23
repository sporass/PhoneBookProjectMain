package com.projectmain;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private final JTextField searchField;
    private final JComboBox<String> sortBox;
    private final ContactTablePanel tablePanel;

    public ControlPanel(ContactTablePanel tablePanel) {
        this.tablePanel = tablePanel;
        setBackground(Color.WHITE);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        Color primaryBlue = new Color(33, 150, 243);
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);

        searchField = new JTextField(20);
        searchField.setFont(baseFont);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primaryBlue, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setBackground(Color.WHITE);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateFilter() {
                tablePanel.filterTable(searchField.getText());
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFilter(); }
        });

        sortBox = new JComboBox<>(new String[]{"Imię", "Nazwisko", "Numer"});
        sortBox.setFont(baseFont);
        sortBox.setBackground(Color.WHITE);
        sortBox.setBorder(BorderFactory.createLineBorder(primaryBlue, 1, true));
        sortBox.addActionListener(e -> tablePanel.sortTableByColumn(sortBox.getSelectedIndex()));

        JLabel sortLabel = new JLabel("Sortuj po:");
        sortLabel.setFont(baseFont);

        add(searchField);
        add(Box.createHorizontalStrut(20));
        add(sortLabel);
        add(sortBox);
    }
}
