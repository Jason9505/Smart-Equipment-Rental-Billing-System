package ui;

import repository.UserRepository;
import service.UserService;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Smart Equipment Rental Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        UserRepository userRepo = new UserRepository();
        userRepo.loadFromFile();

        UserService userService = new UserService(userRepo);
        UserPanel userPanel = new UserPanel(userService);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Users", userPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
