package ui;

import model.User;
import model.UserType;
import service.UserService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class UserPanel extends JPanel {
    private final UserService userService;
    private JTable table;
    private DefaultTableModel tableModel;

    public UserPanel(UserService userService) {
        this.userService = userService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        String[] columns = {"User ID", "Name", "Type", "Final Year", "Discount Eligible"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add User");
        addButton.addActionListener(e -> showAddDialog());
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(e -> showDeleteDialog());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAll();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getUserId(),
                user.getName(),
                user.getUserType().toString(),
                user.isFinalYear() ? "Yes" : "No",
                user.isEligibleForDiscount() ? "Yes" : "No"
            });
        }
    }

    private void showDeleteDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        String userName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + userName + " (" + userId + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String error = userService.deleteUser(userId);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                refreshTable();
            }
        }
    }

    private void showAddDialog() {
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JComboBox<UserType> typeCombo = new JComboBox<>(UserType.values());
        JCheckBox finalYearCheck = new JCheckBox("Final Year Student");

        typeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean isStudent = typeCombo.getSelectedItem() == UserType.STUDENT;
                finalYearCheck.setEnabled(isStudent);
                if (!isStudent) finalYearCheck.setSelected(false);
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Final Year:"), gbc);
        gbc.gridx = 1;
        panel.add(finalYearCheck, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            UserType type = (UserType) typeCombo.getSelectedItem();
            boolean finalYear = finalYearCheck.isSelected();

            String error = userService.addUser(id, name, type, finalYear);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                refreshTable();
            }
        }
    }
}
