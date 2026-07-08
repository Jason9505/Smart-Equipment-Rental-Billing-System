package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.*;
import repository.EquipmentRepository;
import repository.RentalRepository;
import repository.UserRepository;

public class RentalService {
    //for billing calculations, rental management, and observer notifications
    private final BillingService billingService;
    private final RentalRepository rentalRepo;
    private final EquipmentRepository equipmentRepo;
    private final UserRepository userRepo;
    private final List<RentalObserver> observers = new ArrayList<>();
    private int rentalCounter;

    public RentalService(RentalRepository rentalRepo, EquipmentRepository equipmentRepo,
                          UserRepository userRepo) {
        this.rentalRepo = rentalRepo;
        this.equipmentRepo = equipmentRepo;
        this.userRepo = userRepo;
        this.billingService = new BillingService();
        this.rentalCounter = computeNextCounter();
    }

    private int computeNextCounter() {
        int max = 0;
        for (Rental r : rentalRepo.getAll()) {
            try {
                int n = Integer.parseInt(r.getRentalId().replaceAll("[^0-9]", ""));
                if (n > max) max = n;
            } catch (NumberFormatException ignored) { }
        }
        return max;
    }

    public void addObserver(RentalObserver observer) { observers.add(observer); }
    public void removeObserver(RentalObserver observer) { observers.remove(observer); }

    private void notifyRented(Rental rental, Equipment equipment) {
        for (RentalObserver o : observers) o.onRented(rental, equipment);
    }

    private void notifyReturned(Rental rental, Equipment equipment, RentalBill bill) {
        for (RentalObserver o : observers) o.onReturned(rental, equipment, bill);
    }

    public String rentEquipment(String equipmentId, String userId, int rentalDays) {
        if (equipmentId == null || equipmentId.trim().isEmpty()) return "Please select equipment.";
        if (userId == null || userId.trim().isEmpty()) return "Please select a user.";
        if (rentalDays <= 0) return "Rental duration must be at least 1 day.";

        Equipment equipment = equipmentRepo.getById(equipmentId);
        if (equipment == null) return "Equipment not found.";
        if (!equipment.isAvailable()) return "This equipment is not available for rental.";

        User user = userRepo.getById(userId);
        if (user == null) return "User not found.";

        String rentalId = "R" + String.format("%04d", ++rentalCounter);
        LocalDate rentDate = LocalDate.now();
        LocalDate dueDate = rentDate.plusDays(rentalDays);

        Rental rental = new Rental(rentalId, equipmentId, userId, rentDate, dueDate);

        equipment.setAvailable(false);
        equipmentRepo.update(equipment); 
        rentalRepo.add(rental);         

        notifyRented(rental, equipment);
        return null;
    }

    public String returnEquipment(String rentalId, boolean damaged) {
        if (rentalId == null || !rentalRepo.exists(rentalId)) return "Rental record not found.";

        Rental rental = rentalRepo.getById(rentalId);
        if (rental.getStatus() != RentalStatus.ACTIVE) return "This rental has already been returned.";

        Equipment equipment = equipmentRepo.getById(rental.getEquipmentId());
        if (equipment == null) return "Associated equipment record not found.";

        User user = userRepo.getById(rental.getUserId());

        LocalDate returnDate = LocalDate.now();

        // Calculate the bill for the rental, considering any damages
        RentalBill bill = billingService.calculateBill(
                equipment, user, rental.getRentDate(), rental.getDueDate(), returnDate, damaged);

        rental.setReturnDate(returnDate);
        rental.setDamaged(damaged);
        rental.setStatus(RentalStatus.RETURNED);
        rental.setBill(bill);

        equipment.setAvailable(true);
        equipmentRepo.update(equipment); 
        rentalRepo.update(rental);       

        notifyReturned(rental, equipment, bill);
        return null;
    }

    public List<Rental> getAllRentals() { return rentalRepo.getAll(); }
    public List<Rental> getActiveRentals() { return rentalRepo.getByStatus(RentalStatus.ACTIVE); }
    public List<Rental> getReturnedRentals() { return rentalRepo.getByStatus(RentalStatus.RETURNED); }
    public Rental getById(String rentalId) { return rentalRepo.getById(rentalId); }
}
