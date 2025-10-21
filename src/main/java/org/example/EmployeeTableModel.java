package org.example;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTableModel extends DefaultTableModel {
    private final List<Employee> employees;
    private static final String[] COLUMNS = {"First Name", "Last Name", "Email", "Phone"};

    public EmployeeTableModel() {
        super(COLUMNS, 0);
        employees = new ArrayList<>();
    }

    public void addEmployee(Employee emp) {
        employees.add(emp);
        addRow(new Object[]{emp.getFirstName(), emp.getLastName(), emp.getEmail(), emp.getPhone()});
    }

    public Employee getEmployee(int index) {
        return employees.get(index);
    }

    public void removeEmployee(int index) {
        employees.remove(index);
        removeRow(index);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // brak edycji bezpośredniej
    }
}
