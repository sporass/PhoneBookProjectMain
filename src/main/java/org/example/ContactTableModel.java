package com.projectmain;

import javax.swing.table.DefaultTableModel;

public class ContactTableModel extends DefaultTableModel {

    public ContactTableModel(Object[][] data, String[] columns) {
        super(data, columns);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
