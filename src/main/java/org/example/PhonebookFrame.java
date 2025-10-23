package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PhonebookFrame extends JFrame {

    private final EmployeeTableModel tableModel;
    private final JTable table;
    private final JTextField searchField;
    private final boolean isAdmin;
    private final List<Integer> employeeIds = new ArrayList<>();
    private String currentUsername;
    private final String currentRole;

    public PhonebookFrame(boolean isAdmin, String currentUsername, String currentRole) {
        this.isAdmin = isAdmin;
        this.currentUsername = currentUsername;
        this.currentRole = currentRole;// ustawiamy poprawnie
        setTitle("📒 PhoneBook - " + currentUsername);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        add(mainPanel);

        // Panel wyszukiwania z przyciskiem Logout
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchField = new JTextField();
        searchPanel.add(new JLabel("🔍 Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

// Dodanie przycisku Logout po prawej stronie
        JButton logoutButton = new JButton("Logout");
        searchPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

// Obsługa wylogowania
        logoutButton.addActionListener(e -> {
            dispose(); // zamyka aktualne okno PhonebookFrame
            new LoginFrame().setVisible(true); // otwiera panel logowania
        });

        // Tabela
        tableModel = new EmployeeTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(33, 150, 243));
        table.setSelectionForeground(Color.WHITE);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        JButton addButton = new JButton("➕ Add");
        JButton editButton = new JButton("✏️ Edit");
        JButton deleteButton = new JButton("🗑️ Delete");
        JButton manageUsersButton = new JButton("👥");

        // Ustawienia wyglądu przycisków
        JButton[] buttons = {addButton, editButton, deleteButton};
        for (JButton btn : buttons) {
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(5, 10, 5, 10));
            btn.setEnabled(currentRole.equalsIgnoreCase("ADMIN") || currentRole.equalsIgnoreCase("EDITOR"));
            buttonPanel.add(btn);
        }

        // manageUsersButton osobno
        manageUsersButton.setFocusPainted(false);
        manageUsersButton.setMargin(new Insets(5, 10, 5, 10));
        manageUsersButton.setEnabled(true); // każdy USER/EDITOR/ADMIN może otworzyć okno
        buttonPanel.add(manageUsersButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        manageUsersButton.addActionListener(e -> {
            showUsersWindow();
        });

        // Skróty klawiaturowe dla tabeli
        InputMap im = buttonPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = buttonPanel.getActionMap();
        im.put(KeyStroke.getKeyStroke("control N"), "add");
        im.put(KeyStroke.getKeyStroke("control E"), "edit");
        im.put(KeyStroke.getKeyStroke("control D"), "delete");
        im.put(KeyStroke.getKeyStroke("control U"), "addUser");
        im.put(KeyStroke.getKeyStroke("control X"), "deleteUser");

        am.put("add", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (addButton.isEnabled()) addButton.doClick();
            }
        });
        am.put("edit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editButton.isEnabled()) editButton.doClick();
            }
        });
        am.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (deleteButton.isEnabled()) deleteButton.doClick();
            }
        });

        // Wczytanie danych z bazy
        loadEmployeesFromDB();

        // Sorting / Searching
        TableRowSorter<EmployeeTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void search() {
                String text = searchField.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                search();
            }
        });

        // Akcje przycisków
        addButton.addActionListener(e -> addEmployee());
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
    }

    private void showUsersWindow() {
        JDialog dialog = new JDialog(this, "User list", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel userTableModel = new DefaultTableModel(new Object[]{"Username", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(userTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Załaduj użytkowników z DB
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT USERNAME, ROLE FROM USERS ORDER BY USERNAME");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("USERNAME");
                String role = rs.getString("ROLE");
                if (role.equalsIgnoreCase("ADMIN") && !username.equalsIgnoreCase("Admin")) {
                    role = "EDITOR";
                }
                userTableModel.addRow(new Object[]{username, role});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error displaying users!");
        }

        // Panel przycisków
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("➕ Add User");
        JButton editButton = new JButton("✏️ Edit");
        JButton removeButton = new JButton("❌ Delete");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Uprawnienia przycisków dla ADD
        addButton.setEnabled(currentRole.equalsIgnoreCase("ADMIN") || currentRole.equalsIgnoreCase("EDITOR"));

        // Listener wyboru w tabeli
        userTable.getSelectionModel().addListSelectionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) {
                editButton.setEnabled(false);
                removeButton.setEnabled(false);
                return;
            }
            String selectedUser = (String) userTable.getValueAt(row, 0);
            boolean isMainAdmin = selectedUser.equalsIgnoreCase("Admin");
            boolean isCurrentUser = selectedUser.equalsIgnoreCase(currentUsername);

            // Edit button
            if (currentRole.equalsIgnoreCase("ADMIN")) {
                if (isMainAdmin && isCurrentUser) {
                    editButton.setEnabled(true); // może edytować tylko swoje hasło
                } else {
                    editButton.setEnabled(true); // może edytować innych użytkowników normalnie
                }
            } else if (currentRole.equalsIgnoreCase("EDITOR")) {
                editButton.setEnabled(!isMainAdmin); // editor nie może edytować Admina
            } else if (currentRole.equalsIgnoreCase("USER")) {
                editButton.setEnabled(isCurrentUser); // user może edytować tylko siebie
            }


            // Delete button
            removeButton.setEnabled(
                    (currentRole.equalsIgnoreCase("ADMIN") && !isCurrentUser && !isMainAdmin) ||
                            (currentRole.equalsIgnoreCase("EDITOR") && !isCurrentUser && !isMainAdmin)
            );
        });

        // Dodawanie użytkownika
        addButton.addActionListener(e -> {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            JComboBox<String> roleBox = new JComboBox<>(new String[]{"EDITOR", "USER"});

            JPanel addPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            addPanel.add(new JLabel("Username:")); addPanel.add(usernameField);
            addPanel.add(new JLabel("Password:")); addPanel.add(passwordField);
            addPanel.add(new JLabel("Role:")); addPanel.add(roleBox);

            int result = JOptionPane.showConfirmDialog(dialog, addPanel, "Add User", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username and password cannot be empty!");
                return;
            }

            try (Connection conn = Database.getConnection()) {
                try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM USERS WHERE USERNAME=?")) {
                    check.setString(1, username);
                    ResultSet rs = check.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "A user with this username already exists!");
                        return;
                    }
                }
                Database.insertUserIfNotExists(conn, username, password, role);
                userTableModel.addRow(new Object[]{username, role});
                JOptionPane.showMessageDialog(dialog, "User added: " + username);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding user!");
            }
        });

        editButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Select a user to edit.");
                return;
            }

            String selectedUsername = (String) userTable.getValueAt(row, 0);
            String selectedRole = (String) userTable.getValueAt(row, 1);
            boolean isMainAdmin = selectedUsername.equalsIgnoreCase("Admin");
            boolean isCurrentUser = selectedUsername.equalsIgnoreCase(currentUsername);

            // Blokada edycji Admina dla EDITOR/USER
            if ((currentRole.equalsIgnoreCase("EDITOR") || currentRole.equalsIgnoreCase("USER")) && isMainAdmin) {
                JOptionPane.showMessageDialog(dialog, "Cannot edit the main Admin!");
                return;
            }

            JTextField usernameField = new JTextField(selectedUsername);
            JPasswordField passwordField = new JPasswordField();
            JComboBox<String> roleBox = new JComboBox<>(new String[]{"EDITOR", "USER"});
            roleBox.setSelectedItem(selectedRole.equalsIgnoreCase("ADMIN") ? "EDITOR" : selectedRole);

            // Uprawnienia pól w edycji
            if (currentRole.equalsIgnoreCase("ADMIN")) {
                if (isMainAdmin && isCurrentUser) {
                    usernameField.setEnabled(false);   // nie zmienia nazwy
                    roleBox.setEnabled(false);         // nie zmienia roli
                    passwordField.setEnabled(true);    // może zmieniać tylko hasło
                } else {
                    usernameField.setEnabled(true);
                    roleBox.setEnabled(!isCurrentUser);
                    passwordField.setEnabled(true);
                }
            } else if (currentRole.equalsIgnoreCase("EDITOR")) {
                roleBox.setEnabled(!isCurrentUser && !isMainAdmin); // EDITOR nie może zmienić swojej roli ani Admina
            } else { // USER
                roleBox.setEnabled(false); // USER nie może zmieniać roli
            }

            JPanel editPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            editPanel.add(new JLabel("Username:")); editPanel.add(usernameField);
            editPanel.add(new JLabel("New Password:")); editPanel.add(passwordField);
            editPanel.add(new JLabel("Role:")); editPanel.add(roleBox);

            int result = JOptionPane.showConfirmDialog(dialog, editPanel, "Edit User: " + selectedUsername, JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            String newRole = (String) roleBox.getSelectedItem();

            if (newUsername.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username cannot be empty!");
                return;
            }

            try (Connection conn = Database.getConnection()) {
                String originalUsername = selectedUsername; // zachowujemy starą nazwę

                // Zmiana nazwy
                if (!newUsername.equals(originalUsername)) {
                    try (PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM USERS WHERE USERNAME=?")) {
                        check.setString(1, newUsername);
                        ResultSet rs = check.executeQuery();
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(dialog, "Username already exists!");
                            return;
                        }
                    }
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE USERS SET USERNAME=? WHERE USERNAME=?")) {
                        ps.setString(1, newUsername);
                        ps.setString(2, originalUsername);
                        ps.executeUpdate();
                    }
                    userTableModel.setValueAt(newUsername, row, 0);

                    if (isCurrentUser) currentUsername = newUsername;
                }

                // 🔒 Bezpieczna aktualizacja roli
                if ((currentRole.equalsIgnoreCase("EDITOR") && isCurrentUser) ||
                        (currentRole.equalsIgnoreCase("USER"))) {
                    // USER nie może zmieniać roli w ogóle
                    // EDITOR nie może zmieniać swojej roli
                    newRole = selectedRole; // wymuszenie starej roli
                }

                try (PreparedStatement ps = conn.prepareStatement("UPDATE USERS SET ROLE=? WHERE USERNAME=?")) {
                    ps.setString(1, newRole);
                    ps.setString(2, originalUsername);
                    ps.executeUpdate();
                }
                userTableModel.setValueAt(newRole, row, 1);

                // Zmiana hasła (jeśli podano)
                if (!newPassword.isEmpty()) {
                    String salt = "";
                    try (PreparedStatement ps = conn.prepareStatement("SELECT SALT FROM USERS WHERE USERNAME=?")) {
                        ps.setString(1, originalUsername);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) salt = rs.getString("SALT");
                    }
                    String hash = Database.hashPassword(newPassword, salt);
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE USERS SET PASSWORDHASH=? WHERE USERNAME=?")) {
                        ps.setString(1, hash);
                        ps.setString(2, originalUsername);
                        ps.executeUpdate();
                    }
                }

                JOptionPane.showMessageDialog(dialog, "User updated successfully.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error updating user!");
            }
        });




        // Usuwanie użytkownika
        removeButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(dialog,"Select a user to delete."); return; }

            String username = (String) userTable.getValueAt(row,0);
            boolean isMainAdmin = username.equalsIgnoreCase("Admin");
            boolean isCurrentUser = username.equalsIgnoreCase(currentUsername);

            if ((currentRole.equalsIgnoreCase("ADMIN") || currentRole.equalsIgnoreCase("EDITOR")) &&
                    !isCurrentUser && !isMainAdmin) {
                int confirm = JOptionPane.showConfirmDialog(dialog,"Delete user: "+username+"?","Confirm",JOptionPane.YES_NO_OPTION);
                if(confirm==JOptionPane.YES_OPTION){
                    try(Connection conn=Database.getConnection();
                        PreparedStatement ps=conn.prepareStatement("DELETE FROM USERS WHERE USERNAME=?")){
                        ps.setString(1,username);
                        int deleted = ps.executeUpdate();
                        if(deleted>0){ userTableModel.removeRow(row); JOptionPane.showMessageDialog(dialog,"User deleted."); }
                        else JOptionPane.showMessageDialog(dialog,"User not found!");
                    }catch(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(dialog,"Error deleting user!"); }
                }
            }
        });

        dialog.setVisible(true);
    }



    private void loadEmployeesFromDB() {
        tableModel.clear();
        employeeIds.clear();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees")) {

            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                tableModel.addEmployee(emp);
                employeeIds.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                try (Connection conn = Database.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO employees (firstName, lastName, email, phone) VALUES (?, ?, ?, ?)",
                             Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, emp.getFirstName());
                    ps.setString(2, emp.getLastName());
                    ps.setString(3, emp.getEmail());
                    ps.setString(4, emp.getPhone());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) employeeIds.add(keys.getInt(1));
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableModel.addEmployee(emp);
                break;
            }
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

    private void editEmployee() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit!");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        Employee emp = tableModel.getEmployee(modelRow);
        int empId = employeeIds.get(modelRow);

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
                try (Connection conn = Database.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE employees SET firstName=?, lastName=?, email=?, phone=? WHERE id=?")) {

                    ps.setString(1, emp.getFirstName());
                    ps.setString(2, emp.getLastName());
                    ps.setString(3, emp.getEmail());
                    ps.setString(4, emp.getPhone());
                    ps.setInt(5, empId);
                    ps.executeUpdate();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

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
        int empId = employeeIds.get(modelRow);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected employee?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM employees WHERE id=?")) {

                ps.setInt(1, empId);
                ps.executeUpdate();
                employeeIds.remove(modelRow);
                tableModel.removeEmployee(modelRow);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
