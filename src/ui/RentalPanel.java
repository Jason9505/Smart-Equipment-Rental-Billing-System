package ui;

import model.Equipment;
import model.EquipmentCategory;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RentalPanel extends JPanel implements RentalObserver {
    private static final String ALL_CATEGORIES = "All Categories";
    private static final String SORT_DUE_ASC = "Due Date: Early -> Late";
    private static final String SORT_DUE_DESC = "Due Date: Late -> Early";

    private final RentalService rentalService;
    private final EquipmentService equipmentService;
    private final UserService userService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryFilterCombo;
    private JComboBox<String> sortOrderCombo;

    public RentalPanel(RentalService rentalService, EquipmentService equipmentService,
                        UserService userService) {
        this.rentalService = rentalService;
        this.equipmentService = equipmentService;
        this.userService = userService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Filter by Category:"));
        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.addItem(ALL_CATEGORIES);
        for (EquipmentCategory cat : EquipmentCategory.values()) {
            categoryFilterCombo.addItem(cat.getDisplayName());
        }
        categoryFilterCombo.addActionListener(e -> refreshTable());
        filterPanel.add(categoryFilterCombo);

        filterPanel.add(Box.createHorizontalStrut(15));

        filterPanel.add(new JLabel("Sort by Expected Return Date:"));
        sortOrderCombo = new JComboBox<>(new String[]{SORT_DUE_ASC, SORT_DUE_DESC});
        sortOrderCombo.addActionListener(e -> refreshTable());
        filterPanel.add(sortOrderCombo);

        add(filterPanel, BorderLayout.NORTH);

        String[] columns = {"Rental ID", "Equipment", "User", "Rent Date", "Due Date",
                "Days", "Base Price (RM)", "Est. Payment (RM)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());

        JLabel captionLabel = new JLabel(
                "Est. Payment assumes on-time, undamaged return. Final bill (with any late/damage penalty) is shown after Return.");
        captionLabel.setFont(captionLabel.getFont().deriveFont(Font.ITALIC, 11f));
        captionLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        southPanel.add(captionLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton rentButton = new JButton("Rent Equipment");
        rentButton.addActionListener(e -> showRentDialog());
        buttonPanel.add(rentButton);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        rentalService.addObserver(this);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);

        List<Rental> active = new ArrayList<>(rentalService.getActiveRentals());

        String selectedCategory = categoryFilterCombo != null
                ? (String) categoryFilterCombo.getSelectedItem() : ALL_CATEGORIES;
        if (selectedCategory != null && !selectedCategory.equals(ALL_CATEGORIES)) {
            List<Rental> filtered = new ArrayList<>();
            for (Rental r : active) {
                Equipment eqp = equipmentService.getById(r.getEquipmentId());
                if (eqp != null && eqp.getCategory().getDisplayName().equals(selectedCategory)) {
                    filtered.add(r);
                }
            }
            active = filtered;
        }

        String selectedSort = sortOrderCombo != null
                ? (String) sortOrderCombo.getSelectedItem() : SORT_DUE_ASC;
        Comparator<Rental> byDueDate = Comparator.comparing(Rental::getDueDate);
        if (SORT_DUE_DESC.equals(selectedSort)) {
            byDueDate = byDueDate.reversed();
        }
        active.sort(byDueDate);

        for (Rental r : active) {
            Equipment eqp = equipmentService.getById(r.getEquipmentId());
            User user = userService.getById(r.getUserId());

            long days = ChronoUnit.DAYS.between(r.getRentDate(), r.getDueDate());
            if (days < 1) days = 1;

            String basePriceStr = "-";
            String estPaymentStr = "-";
            if (eqp != null) {
                double basePrice = eqp.getDailyRate() * days;
                double discount = (user != null && user.isEligibleForDiscount()) ? basePrice * 0.10 : 0;
                double estPayment = basePrice - discount;
                basePriceStr = String.format("%.2f", basePrice);
                estPaymentStr = String.format("%.2f", estPayment);
            }

            tableModel.addRow(new Object[]{
                r.getRentalId(),
                eqp != null ? eqp.toString() : r.getEquipmentId(),
                user != null ? user.toString() : r.getUserId(),
                r.getRentDate().toString(),
                r.getDueDate().toString(),
                days,
                basePriceStr,
                estPaymentStr
            });
        }
    }

    private void showRentDialog() {
        List<Equipment> available = equipmentService.getAvailable();
        List<User> users = userService.getAll();

        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No equipment is currently available to rent.",
                    "Nothing Available", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one user first.",
                    "No Users", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Equipment> equipmentCombo = new JComboBox<>(available.toArray(new Equipment[0]));
        JComboBox<User> userCombo = new JComboBox<>(users.toArray(new User[0]));
        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));

        JLabel dailyRateValue = new JLabel();
        JLabel basePriceValue = new JLabel();
        JLabel discountValue = new JLabel();
        JLabel estPaymentValue = new JLabel();
        estPaymentValue.setFont(estPaymentValue.getFont().deriveFont(Font.BOLD));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Equipment:"), gbc);
        gbc.gridx = 1; panel.add(equipmentCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("User:"), gbc);
        gbc.gridx = 1; panel.add(userCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Rental Duration (days):"), gbc);
        gbc.gridx = 1; panel.add(daysSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Daily Rate:"), gbc);
        gbc.gridx = 1; panel.add(dailyRateValue, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Base Price:"), gbc);
        gbc.gridx = 1; panel.add(basePriceValue, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Discount:"), gbc);
        gbc.gridx = 1; panel.add(discountValue, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Est. Payment:"), gbc);
        gbc.gridx = 1; panel.add(estPaymentValue, gbc);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        JLabel noteLabel = new JLabel(
                "<html><i>Estimate assumes on-time, undamaged return.<br>Late or damage penalties are added when the item is actually returned.</i></html>");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.PLAIN, 11f));
        panel.add(noteLabel, gbc);
        gbc.gridwidth = 1;

        Runnable updatePreview = () -> {
            Equipment eqp = (Equipment) equipmentCombo.getSelectedItem();
            User user = (User) userCombo.getSelectedItem();
            int days = (Integer) daysSpinner.getValue();

            if (eqp == null) return;
            double basePrice = eqp.getDailyRate() * days;
            double discount = (user != null && user.isEligibleForDiscount()) ? basePrice * 0.10 : 0;
            double estPayment = basePrice - discount;

            dailyRateValue.setText(String.format("RM%.2f / day", eqp.getDailyRate()));
            basePriceValue.setText(String.format("RM%.2f  (RM%.2f x %d day%s)",
                    basePrice, eqp.getDailyRate(), days, days == 1 ? "" : "s"));
            discountValue.setText(discount > 0
                    ? String.format("-RM%.2f (10%% staff/final-year rate)", discount)
                    : "RM0.00");
            estPaymentValue.setText(String.format("RM%.2f", estPayment));
        };

        equipmentCombo.addItemListener(e -> updatePreview.run());
        userCombo.addItemListener(e -> updatePreview.run());
        daysSpinner.addChangeListener(e -> updatePreview.run());
        updatePreview.run(); 

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Rent Equipment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Equipment selectedEquipment = (Equipment) equipmentCombo.getSelectedItem();
            User selectedUser = (User) userCombo.getSelectedItem();
            int days = (Integer) daysSpinner.getValue();

            String error = rentalService.rentEquipment(
                    selectedEquipment.getEquipmentId(), selectedUser.getUserId(), days);

            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onRented(Rental rental, Equipment equipment) {
        refreshTable();
    }

    @Override
    public void onReturned(Rental rental, Equipment equipment, RentalBill bill) {
        refreshTable(); 
    }
}
