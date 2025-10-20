package com.mycompany.swingapp;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SearchSortTableApp extends JFrame {

    // -------------------- POLA KLASY --------------------
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> sortBox;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JPanel loginPanel;
    private JTextField userField;
    private JPasswordField passField;

    // -------------------- KONSTRUKTOR --------------------
    public SearchSortTableApp() {
        setTitle("Phonebook");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.WHITE);
        initLoginPanel();
    }

    // -------------------- PANEL LOGOWANIA --------------------
    private void initLoginPanel() {
        Color primaryBlue = new Color(0, 102, 204);
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);

        loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Zaloguj się");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(primaryBlue);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        JLabel userLabel = new JLabel("Użytkownik:");
        userLabel.setFont(baseFont);
        gbc.gridy++;
        gbc.gridwidth = 1;
        loginPanel.add(userLabel, gbc);

        userField = new JTextField();
        userField.setFont(baseFont);
        userField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        loginPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Hasło:");
        passLabel.setFont(baseFont);
        gbc.gridx = 0;
        gbc.gridy++;
        loginPanel.add(passLabel, gbc);

        passField = new JPasswordField();
        passField.setFont(baseFont);
        passField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        loginPanel.add(passField, gbc);

        JButton loginButton = new JButton("Zaloguj");
        loginButton.setFont(baseFont);
        loginButton.setBackground(primaryBlue);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(baseFont);
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        loginPanel.add(errorLabel, gbc);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            // -------------------- LOGIKA LOGOWANIA --------------------
            if ("admin".equals(username) && "1234".equals(password)) {
                getContentPane().removeAll();
                initTablePanel();
                revalidate();
                repaint();
            } else {
                errorLabel.setText("Niepoprawny login lub hasło");
            }
        });

        add(loginPanel);
    }

    // -------------------- PANEL Z TABELĄ --------------------
    private void initTablePanel() {
        Color primaryBlue = new Color(0, 102, 204);
        Color selectionBlue = new Color(51, 153, 255);
        Color lightGray = new Color(220, 220, 220);
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);

        // -------------------- DANE TABELI --------------------
        String[] columns = {"Imię", "Nazwisko", "Numer"};
        Object[][] data = {
                {"Jan", "Kowalski", "123456789"},
                {"Anna", "Nowak", "987654321"},
                {"Piotr", "Wiśniewski", "019283746"}
        };

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // -------------------- KONFIGURACJA TABELI --------------------
        table = new JTable(tableModel);
        table.setFont(baseFont);
        table.setRowHeight(30);
        table.setGridColor(lightGray);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(selectionBlue);
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row,
                                                           int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(primaryBlue);
                label.setForeground(Color.WHITE);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                return label;
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // -------------------- SEARCH BAR --------------------
        searchField = new JTextField(20);
        searchField.setFont(baseFont);
        searchField.setBorder(BorderFactory.createLineBorder(primaryBlue, 1));
        searchField.setBackground(Color.WHITE);
        searchField.putClientProperty("JTextField.placeholderText", "Szukaj...");

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateFilter() {
                String text = searchField.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateFilter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateFilter();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateFilter();
            }
        });

        // -------------------- SORTOWANIE --------------------
        sortBox = new JComboBox<>(new String[]{"Imię", "Nazwisko", "Wiek"});
        sortBox.setFont(baseFont);
        sortBox.setBackground(Color.WHITE);
        sortBox.setBorder(BorderFactory.createLineBorder(Color.white, 1, true));
        sortBox.addActionListener(e -> {
            int index = sortBox.getSelectedIndex();
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();
            sortKeys.add(new RowSorter.SortKey(index, SortOrder.ASCENDING));
            sorter.setSortKeys(sortKeys);
            sorter.sort();
        });

        JLabel sortLabel = new JLabel("Sortuj po:");
        sortLabel.setFont(baseFont);

        // -------------------- PANEL KONTROLNY --------------------
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.add(searchField);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(sortLabel);
        controlPanel.add(sortBox);

        // -------------------- SCROLLPANE --------------------
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // -------------------- DODANIE DO RAMKI --------------------
        getContentPane().setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // -------------------- MAIN --------------------
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new SearchSortTableApp().setVisible(true));
    }
}
