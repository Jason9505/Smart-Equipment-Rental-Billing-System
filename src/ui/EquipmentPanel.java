package ui;

import model.Equipment;
import model.EquipmentCategory;
import service.EquipmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EquipmentPanel extends JPanel {
    private final EquipmentService equipmentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public EquipmentPanel(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        String[] columns = {"Equipment ID", "Name", "Category", "Daily Rate (RM)", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Equipment");
        addButton.addActionListener(e -> showAddDialog());
        buttonPanel.add(addButton);

        JButton editButton = new JButton("Edit Equipment");
        editButton.addActionListener(e -> showEditDialog());
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete Equipment");
        deleteButton.addActionListener(e -> showDeleteDialog());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Equipment> items = equipmentService.getAll();
        for (Equipment eqp : items) {
            tableModel.addRow(new Object[]{
                eqp.getEquipmentId(),
                eqp.getName(),
                eqp.getCategory().getDisplayName(),
                String.format("%.2f", eqp.getDailyRate()),
                eqp.isAvailable() ? "Yes" : "No"
            });
        }
    }

    private void showDeleteDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select equipment to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + name + " (" + id + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String error = equipmentService.deleteEquipment(id);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                refreshTable();
            }
        }
    }

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select equipment to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        Equipment eqp = equipmentService.getById(id);
        if (eqp == null) return;

        JTextField idField = new JTextField(eqp.getEquipmentId(), 10);
        idField.setEditable(false);
        JTextField nameField = new JTextField(eqp.getName(), 20);
        JComboBox<EquipmentCategory> categoryCombo = new JComboBox<>(EquipmentCategory.values());
        categoryCombo.setSelectedItem(eqp.getCategory());
        JTextField rateField = new JTextField(String.valueOf(eqp.getDailyRate()), 10);

        JPanel panel = buildFormPanel(idField, nameField, categoryCombo, rateField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Edit Equipment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double rate = Double.parseDouble(rateField.getText().trim());
                String error = equipmentService.updateEquipment(id, nameField.getText().trim(),
                        (EquipmentCategory) categoryCombo.getSelectedItem(), rate);
                if (error != null) {
                    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    refreshTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Daily rate must be a valid number.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddDialog() {
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JComboBox<EquipmentCategory> categoryCombo = new JComboBox<>(EquipmentCategory.values());
        JTextField rateField = new JTextField(10);

        JPanel panel = buildFormPanel(idField, nameField, categoryCombo, rateField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add Equipment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double rate = Double.parseDouble(rateField.getText().trim());
                String error = equipmentService.addEquipment(idField.getText().trim(),
                        nameField.getText().trim(),
                        (EquipmentCategory) categoryCombo.getSelectedItem(), rate);
                if (error != null) {
                    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    refreshTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Daily rate must be a valid number.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel buildFormPanel(JTextField idField, JTextField nameField,
                                   JComboBox<EquipmentCategory> categoryCombo, JTextField rateField) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Equipment ID:"), gbc);
        gbc.gridx = 1; panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1; panel.add(categoryCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Daily Rate (RM):"), gbc);
        gbc.gridx = 1; panel.add(rateField, gbc);

        return panel;
    }
}
