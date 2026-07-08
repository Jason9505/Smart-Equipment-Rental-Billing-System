package service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import model.*;

public class BillingService {
    private static final double DISCOUNT_RATE = 0.10;

    public RentalBill calculateBill(Equipment equipment, User user,
                                     LocalDate rentDate, LocalDate dueDate,
                                     LocalDate returnDate, boolean damaged) {
        long heldDays = ChronoUnit.DAYS.between(rentDate, returnDate);
        if (heldDays < 1) heldDays = 1;

        double dailyRate = equipment.getDailyRate();
        EquipmentCategory category = equipment.getCategory();

        double baseFee = dailyRate * heldDays;

        double discount = 0.0;
        if (user != null && user.isEligibleForDiscount()) {
            discount = baseFee * DISCOUNT_RATE;
        }

        double penalty = 0.0;
        if (returnDate.isAfter(dueDate)) {
            long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
            penalty += lateDays * dailyRate * category.getLateFeeMultiplier();
        }
        if (damaged) {
            penalty += dailyRate * category.getDamageFeeMultiplier();
        }

        return new RentalBill(baseFee, discount, penalty);
    }
}
