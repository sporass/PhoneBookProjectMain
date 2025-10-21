package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // --- Login window ---
            JFrame loginFrame = new JFrame("Login");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(300, 150);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setLayout(new GridLayout(3, 2, 5, 5));
            loginFrame.getContentPane().setBackground(Color.WHITE);

            JLabel userLabel = new JLabel("Username:");
            JTextField userField = new JTextField();
            JLabel passLabel = new JLabel("Password:");
            JPasswordField passField = new JPasswordField();
            JButton loginButton = new JButton("Login");
            loginButton.setBackground(new Color(30, 144, 255));
            loginButton.setForeground(Color.WHITE);
            loginButton.setFocusPainted(false);
            loginButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            loginFrame.add(userLabel);
            loginFrame.add(userField);
            loginFrame.add(passLabel);
            loginFrame.add(passField);
            loginFrame.add(new JLabel());
            loginFrame.add(loginButton);
            loginFrame.setVisible(true);

            loginButton.addActionListener(e -> {
                String login = userField.getText().trim();
                String password = new String(passField.getPassword()).trim();

                boolean isAdmin = login.equalsIgnoreCase("Anna") && password.equals("Kowalska");
                boolean isJan = login.equalsIgnoreCase("Jan") && password.equals("Kowalski");

                if (isAdmin || isJan) {

                    loginFrame.dispose();

                    JFrame frame = new JFrame("Employee PhoneBook");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(800, 450);
                    frame.setLocationRelativeTo(null);
                    frame.setLayout(new BorderLayout());

                    // --- Top panel: search bar ---
                    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                    searchPanel.setBackground(new Color(173, 216, 230));
                    searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    JLabel searchLabel = new JLabel("Search:");
                    JTextField searchField = new JTextField(20);
                    searchPanel.add(searchLabel);
                    searchPanel.add(searchField);
                    frame.add(searchPanel, BorderLayout.NORTH);

                    // --- Table: tylko do odczytu ---
                    String[] columnNames = {"First Name", "Last Name", "Email", "Phone"};
                    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false; // blokuje edycję komórek po dwukliku
                        }
                    };
                    JTable table = new JTable(tableModel);
                    table.setBackground(Color.WHITE);
                    table.setForeground(Color.BLACK);
                    table.setRowHeight(25);
                    table.setFillsViewportHeight(true);

                    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                    table.setRowSorter(sorter);

                    JScrollPane scrollPane = new JScrollPane(table);
                    frame.add(scrollPane, BorderLayout.CENTER);

                    // --- Bottom panel: Add/Edit/Delete buttons ---
                    JPanel bottomPanel = new JPanel();
                    bottomPanel.setBackground(new Color(173, 216, 230));
                    bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                    JButton addButton = new JButton("Add");
                    JButton editButton = new JButton("Edit Selected");
                    JButton deleteButton = new JButton("Delete Selected");

                    addButton.setBackground(new Color(30, 144, 255));
                    addButton.setForeground(Color.WHITE);
                    addButton.setFocusPainted(false);
                    addButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                    addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    editButton.setBackground(new Color(30, 144, 255));
                    editButton.setForeground(Color.WHITE);
                    editButton.setFocusPainted(false);
                    editButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                    editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    deleteButton.setBackground(new Color(30, 144, 255));
                    deleteButton.setForeground(Color.WHITE);
                    deleteButton.setFocusPainted(false);
                    deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                    deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    bottomPanel.add(addButton);
                    bottomPanel.add(editButton);
                    bottomPanel.add(deleteButton);
                    frame.add(bottomPanel, BorderLayout.SOUTH);

                    // --- Ustawienie dostępności przycisków w zależności od użytkownika ---
                    addButton.setEnabled(isAdmin);
                    editButton.setEnabled(isAdmin);
                    deleteButton.setEnabled(isAdmin);

                    // --- Przykładowi pracownicy ---
                    tableModel.addRow(new Object[]{"Anna", "Kowalska", "anna.kowalska@example.com", "123456789"});
                    tableModel.addRow(new Object[]{"Jan", "Kowalski", "jan.kowalski@example.com", "987654321"});

                    // --- Add button with persistent fields on error ---
                    addButton.addActionListener(ev -> {
                        JTextField firstNameField = new JTextField();
                        JTextField lastNameField = new JTextField();
                        JTextField emailField = new JTextField();
                        JTextField phoneField = new JTextField();

                        JPanel addPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                        addPanel.add(new JLabel("First Name:"));
                        addPanel.add(firstNameField);
                        addPanel.add(new JLabel("Last Name:"));
                        addPanel.add(lastNameField);
                        addPanel.add(new JLabel("Email:"));
                        addPanel.add(emailField);
                        addPanel.add(new JLabel("Phone (9 digits):"));
                        addPanel.add(phoneField);

                        while (true) {
                            int result = JOptionPane.showConfirmDialog(frame, addPanel,
                                    "Add New Employee", JOptionPane.OK_CANCEL_OPTION);

                            if (result != JOptionPane.OK_OPTION) break;

                            String first = firstNameField.getText().trim();
                            String last = lastNameField.getText().trim();
                            String email = emailField.getText().trim();
                            String phone = phoneField.getText().trim();

                            if (first.isEmpty() || last.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                                JOptionPane.showMessageDialog(frame, "Please fill in all fields!");
                            } else if (!first.matches("[a-zA-Z]+") || !last.matches("[a-zA-Z]+")) {
                                JOptionPane.showMessageDialog(frame, "First and Last Name must contain only letters!");
                            } else if (!phone.matches("\\d{9}")) {
                                JOptionPane.showMessageDialog(frame, "Phone must contain exactly 9 digits!");
                            } else {
                                tableModel.addRow(new Object[]{first, last, email, phone});
                                break;
                            }
                        }
                    });

                    // --- Edit button with persistent fields on error ---
                    editButton.addActionListener(ev -> {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow >= 0) {
                            int modelRow = table.convertRowIndexToModel(selectedRow);
                            String selectedFirst = (String) tableModel.getValueAt(modelRow, 0);
                            String selectedLast = (String) tableModel.getValueAt(modelRow, 1);

                            if (!isAdmin && selectedFirst.equalsIgnoreCase("Jan") && selectedLast.equalsIgnoreCase("Kowalski")) {
                                JOptionPane.showMessageDialog(frame, "You can only view this employee!");
                                return;
                            }

                            JTextField editFirst = new JTextField((String) tableModel.getValueAt(modelRow, 0));
                            JTextField editLast = new JTextField((String) tableModel.getValueAt(modelRow, 1));
                            JTextField editEmail = new JTextField((String) tableModel.getValueAt(modelRow, 2));
                            JTextField editPhone = new JTextField((String) tableModel.getValueAt(modelRow, 3));

                            JPanel editPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                            editPanel.add(new JLabel("First Name:"));
                            editPanel.add(editFirst);
                            editPanel.add(new JLabel("Last Name:"));
                            editPanel.add(editLast);
                            editPanel.add(new JLabel("Email:"));
                            editPanel.add(editEmail);
                            editPanel.add(new JLabel("Phone (9 digits):"));
                            editPanel.add(editPhone);

                            while (true) {
                                int result = JOptionPane.showConfirmDialog(frame, editPanel,
                                        "Edit Employee", JOptionPane.OK_CANCEL_OPTION);

                                if (result != JOptionPane.OK_OPTION) break;

                                String newFirst = editFirst.getText().trim();
                                String newLast = editLast.getText().trim();
                                String newEmail = editEmail.getText().trim();
                                String newPhone = editPhone.getText().trim();

                                if (newFirst.isEmpty() || newLast.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                                    JOptionPane.showMessageDialog(frame, "Please fill in all fields!");
                                } else if (!newFirst.matches("[a-zA-Z]+") || !newLast.matches("[a-zA-Z]+")) {
                                    JOptionPane.showMessageDialog(frame, "First and Last Name must contain only letters!");
                                } else if (!newPhone.matches("\\d{9}")) {
                                    JOptionPane.showMessageDialog(frame, "Phone must contain exactly 9 digits!");
                                } else {
                                    tableModel.setValueAt(newFirst, modelRow, 0);
                                    tableModel.setValueAt(newLast, modelRow, 1);
                                    tableModel.setValueAt(newEmail, modelRow, 2);
                                    tableModel.setValueAt(newPhone, modelRow, 3);
                                    break;
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Please select an employee to edit!");
                        }
                    });

                    // --- Delete button with confirmation ---
                    deleteButton.addActionListener(ev -> {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow >= 0) {
                            int modelRow = table.convertRowIndexToModel(selectedRow);
                            String selectedFirst = (String) tableModel.getValueAt(modelRow, 0);
                            String selectedLast = (String) tableModel.getValueAt(modelRow, 1);

                            if (!isAdmin && selectedFirst.equalsIgnoreCase("Jan") && selectedLast.equalsIgnoreCase("Kowalski")) {
                                JOptionPane.showMessageDialog(frame, "You cannot delete this employee!");
                                return;
                            }

                            int confirm = JOptionPane.showConfirmDialog(frame,
                                    "Are you sure you want to delete this employee?",
                                    "Confirm Deletion",
                                    JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                tableModel.removeRow(modelRow);
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Please select an employee to delete!");
                        }
                    });

                    // --- Search ---
                    searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                        public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
                        public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
                        public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }

                        private void search() {
                            String text = searchField.getText();
                            if (text.trim().length() == 0) {
                                sorter.setRowFilter(null);
                            } else {
                                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                            }
                        }
                    });


                    frame.setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid username or password!");
                }
            });
        });
    }
}
