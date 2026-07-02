package ui;

import repository.EquipmentRepository;
import repository.RentalRepository;
import repository.UserRepository;
import service.EquipmentService;
import service.RentalService;
import service.UserService;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Smart Equipment Rental Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        UserRepository userRepo = new UserRepository();
        userRepo.loadFromFile();

        EquipmentRepository equipmentRepo = new EquipmentRepository();
        equipmentRepo.loadFromFile();

        RentalRepository rentalRepo = new RentalRepository();
        rentalRepo.loadFromFile();

        UserService userService = new UserService(userRepo);
        EquipmentService equipmentService = new EquipmentService(equipmentRepo);
        RentalService rentalService = new RentalService(rentalRepo, equipmentRepo, userRepo);

        UserPanel userPanel = new UserPanel(userService);
        EquipmentPanel equipmentPanel = new EquipmentPanel(equipmentService);
        RentalPanel rentalPanel = new RentalPanel(rentalService, equipmentService, userService);
        ReturnBillingPanel returnBillingPanel =
                new ReturnBillingPanel(rentalService, equipmentService, userService);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Users", userPanel);
        tabbedPane.addTab("Equipment", equipmentPanel);
        tabbedPane.addTab("Rentals", rentalPanel);
        tabbedPane.addTab("Returns & Billing", returnBillingPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
