package ui;

import model.Equipment;
import model.Rental;
import model.RentalBill;
import model.User;
import service.EquipmentService;
import service.RentalObserver;
import service.RentalService;
import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReturnBillingPanel extends JPanel implements RentalObserver {
    private final RentalService rentalService;
    private final EquipmentService equipmentService;
    private final UserService userService;

    private JTable historyTable;
    private DefaultTableModel historyModel;
    private JTextArea billArea;

    public ReturnBillingPanel(RentalService rentalService, EquipmentService equipmentService,
                               UserService userService) {
        this.rentalService = rentalService;
        this.equipmentService = equipmentService;
        this.userService = userService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshHistory();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton returnButton = new JButton("Return Equipment");
        returnButton.addActionListener(e -> showReturnDialog());
        topPanel.add(returnButton);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Rental ID", "Equipment", "User", "Return Date",
                "Late?", "Damaged?", "Base Fee", "Discount", "Penalty", "Net Payable"};
        historyModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        historyTable = new JTable(historyModel);
        historyTable.setFillsViewportHeight(true);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        billArea = new JTextArea(4, 40);
        billArea.setEditable(false);
        billArea.setBorder(BorderFactory.createTitledBorder("Last Bill Detail"));
        add(billArea, BorderLayout.SOUTH);

        rentalService.addObserver(this);
    }

    public void refreshHistory() {
        historyModel.setRowCount(0);
        List<Rental> returned = rentalService.getReturnedRentals();
        for (Rental r : returned) {
            Equipment eqp = equipmentService.getById(r.getEquipmentId());
            User user = userService.getById(r.getUserId());
            RentalBill bill = r.getBill();
            historyModel.addRow(new Object[]{
                r.getRentalId(),
                eqp != null ? eqp.toString() : r.getEquipmentId(),
                user != null ? user.toString() : r.getUserId(),
                r.getReturnDate().toString(),
                r.isLate() ? "Yes" : "No",
                r.isDamaged() ? "Yes" : "No",
                bill != null ? String.format("RM%.2f", bill.getBaseFee()) : "-",
                bill != null ? String.format("RM%.2f", bill.getDiscount()) : "-",
                bill != null ? String.format("RM%.2f", bill.getPenalty()) : "-",
                bill != null ? String.format("RM%.2f", bill.getNetPayable()) : "-"
            });
        }
    }

    private void showReturnDialog() {
        List<Rental> active = rentalService.getActiveRentals();
        if (active.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no active rentals to return.",
                    "Nothing to Return", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<String> rentalCombo = new JComboBox<>();
        for (Rental r : active) {
            Equipment eqp = equipmentService.getById(r.getEquipmentId());
            User user = userService.getById(r.getUserId());
            rentalCombo.addItem(r.getRentalId() + " - "
                    + (eqp != null ? eqp.getName() : r.getEquipmentId()) + " -> "
                    + (user != null ? user.getName() : r.getUserId())
                    + " (due " + r.getDueDate() + ")");
        }
        JCheckBox damagedCheck = new JCheckBox("Equipment returned damaged");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Rental:"), gbc);
        gbc.gridx = 1; panel.add(rentalCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(damagedCheck, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Return Equipment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Rental selected = active.get(rentalCombo.getSelectedIndex());
            boolean damaged = damagedCheck.isSelected();

            String error = rentalService.returnEquipment(selected.getRentalId(), damaged);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onRented(Rental rental, Equipment equipment) {
        
    }

    @Override
    public void onReturned(Rental rental, Equipment equipment, RentalBill bill) {
        refreshHistory();
        billArea.setText(
                "Rental " + rental.getRentalId() + " - " + equipment.getName() + "\n" + bill.toString());
    }
}
