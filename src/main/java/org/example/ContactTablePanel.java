package com.projectmain;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContactTablePanel extends JPanel {

    private JTable table;
    private ContactTableModel model;
    private TableRowSorter<ContactTableModel> sorter;

    public ContactTablePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        Color primaryBlue = new Color(33, 150, 243);

        String[] columns = {"Imię", "Nazwisko", "Numer"};
        Object[][] data = {
                {"Jan", "Kowalski", "123456789"},
                {"Anna", "Nowak", "987654321"},
                {"Piotr", "Wiśniewski", "019283746"}
        };

        model = new ContactTableModel(data, columns);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(primaryBlue);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(primaryBlue);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Wyrównanie tekstu do środka
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
    }

    public void filterTable(String text) {
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public void sortTableByColumn(int columnIndex) {
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }
}
