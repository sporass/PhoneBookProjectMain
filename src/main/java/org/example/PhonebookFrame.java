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
        setTitle("Employee PhoneBook");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top panel: Search ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(173, 216, 230));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        add(searchPanel, BorderLayout.NORTH);

        // --- Table ---
        tableModel = new EmployeeTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);

        TableRowSorter<EmployeeTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Buttons ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(173, 216, 230));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton addButton = createButton("Add");
        JButton editButton = createButton("Edit Selected");
        JButton deleteButton = createButton("Delete Selected");

        addButton.setEnabled(isAdmin);
        editButton.setEnabled(isAdmin);
        deleteButton.setEnabled(isAdmin);

        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Demo data ---
        tableModel.addEmployee(new Employee("Anna", "Kowalska", "anna.kowalska@example.com", "123456789"));
        tableModel.addEmployee(new Employee("Jan", "Kowalski", "jan.kowalski@example.com", "987654321"));

        // --- Add listeners ---
        addButton.addActionListener(e -> addEmployee());
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());

        // --- Search ---
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void search() {
                String text = searchField.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

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
