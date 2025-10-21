package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class PhonebookFrame extends JFrame {

    private final EmployeeTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private final boolean isAdmin;

    public PhonebookFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setTitle("📒 PhoneBook");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 480);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        add(mainPanel);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchField = new JTextField();
        searchPanel.add(new JLabel("🔍 Szukaj:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        tableModel = new EmployeeTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(33, 150, 243));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton addButton = new JButton("➕ Dodaj");
        JButton editButton = new JButton("✏️ Edytuj");
        JButton deleteButton = new JButton("🗑️ Usuń");

        addButton.setEnabled(isAdmin);
        editButton.setEnabled(isAdmin);
        deleteButton.setEnabled(isAdmin);

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Demo data
        tableModel.addEmployee(new Employee("Anna", "Kowalska", "anna.kowalska@example.com", "123456789"));
        tableModel.addEmployee(new Employee("Jan", "Kowalski", "jan.kowalski@example.com", "987654321"));
        tableModel.addEmployee(new Employee("Piotr", "Wiśniewski", "piotr.wisniewski@example.com", "555123456"));
        tableModel.addEmployee(new Employee("Katarzyna", "Zielińska", "katarzyna.zielinska@example.com", "600789123"));

        // Sorting / Searching
        TableRowSorter<EmployeeTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void search() {
                String text = searchField.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });

        // Button actions (add/edit/delete) – używamy tych samych funkcji jak wcześniej
        addButton.addActionListener(e -> addEmployee());
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
    }

    // Funkcje add/edit/delete – pozostają takie same jak wcześniej
    // Funkcje obsługi przycisków

    private void addEmployee() {
        JTextField firstField = new JTextField();
        JTextField lastField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        JPanel panel = createFormPanel(firstField, lastField, emailField, phoneField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, panel, "Add New Employee", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) break;

            Employee emp = new Employee(
                    firstField.getText().trim(),
                    lastField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
            );

            String error = emp.validate();
            if (error != null) {
                JOptionPane.showMessageDialog(this, error);
            } else {
                tableModel.addEmployee(emp);
                break;
            }
        }
    }

    private void editEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        Employee emp = tableModel.getEmployee(modelRow);

        JTextField firstField = new JTextField(emp.getFirstName());
        JTextField lastField = new JTextField(emp.getLastName());
        JTextField emailField = new JTextField(emp.getEmail());
        JTextField phoneField = new JTextField(emp.getPhone());

        JPanel panel = createFormPanel(firstField, lastField, emailField, phoneField);

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Employee", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) break;

            emp.setFirstName(firstField.getText().trim());
            emp.setLastName(lastField.getText().trim());
            emp.setEmail(emailField.getText().trim());
            emp.setPhone(phoneField.getText().trim());

            String error = emp.validate();
            if (error != null) {
                JOptionPane.showMessageDialog(this, error);
            } else {
                tableModel.fireTableRowsUpdated(modelRow, modelRow);
                break;
            }
        }
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected employee?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.removeEmployee(modelRow);
        }
    }

    // Pomocnicza funkcja do formularzy
    private JPanel createFormPanel(JTextField first, JTextField last, JTextField email, JTextField phone) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("First Name:"));
        panel.add(first);
        panel.add(new JLabel("Last Name:"));
        panel.add(last);
        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(new JLabel("Phone (9 digits):"));
        panel.add(phone);
        return panel;
    }

}
