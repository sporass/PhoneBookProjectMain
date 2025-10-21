package org.example;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTableModel extends AbstractTableModel {

    private final List<Employee> employees = new ArrayList<>();
    private final String[] columns = {"First Name", "Last Name", "Email", "Phone"};

    public void addEmployee(Employee e) {
        employees.add(e);
        fireTableRowsInserted(employees.size()-1, employees.size()-1);
    }

    public void removeEmployee(int index) {
        employees.remove(index);
        fireTableRowsDeleted(index, index);
    }

    public Employee getEmployee(int index) {
        return employees.get(index);
    }

    @Override
    public int getRowCount() { return employees.size(); }

    @Override
    public int getColumnCount() { return columns.length; }

    @Override
    public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Employee e = employees.get(rowIndex);
        return switch(columnIndex) {
            case 0 -> e.getFirstName();
            case 1 -> e.getLastName();
            case 2 -> e.getEmail();
            case 3 -> e.getPhone();
            default -> null;
        };
    }
}
