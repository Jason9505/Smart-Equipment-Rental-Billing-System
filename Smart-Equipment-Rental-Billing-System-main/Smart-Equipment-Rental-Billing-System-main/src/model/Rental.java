package model;

import java.time.LocalDate;

public class Rental {
    private String rentalId;
    private String equipmentId;
    private String userId;
    private LocalDate rentDate;
    private LocalDate dueDate;
    private LocalDate returnDate; 
    private boolean damaged;
    private RentalStatus status;
    private RentalBill bill;   

    public Rental(String rentalId, String equipmentId, String userId,
                   LocalDate rentDate, LocalDate dueDate) {
        this.rentalId = rentalId;
        this.equipmentId = equipmentId;
        this.userId = userId;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.damaged = false;
        this.status = RentalStatus.ACTIVE;
        this.bill = null;
    }

    public Rental(String rentalId, String equipmentId, String userId,
                   LocalDate rentDate, LocalDate dueDate, LocalDate returnDate,
                   boolean damaged, RentalStatus status, RentalBill bill) {
        this.rentalId = rentalId;
        this.equipmentId = equipmentId;
        this.userId = userId;
        this.rentDate = rentDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.damaged = damaged;
        this.status = status;
        this.bill = bill;
    }

    public String getRentalId() { return rentalId; }
    public String getEquipmentId() { return equipmentId; }
    public String getUserId() { return userId; }
    public LocalDate getRentDate() { return rentDate; }
    public LocalDate getDueDate() { return dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isDamaged() { return damaged; }
    public void setDamaged(boolean damaged) { this.damaged = damaged; }

    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }

    public RentalBill getBill() { return bill; }
    public void setBill(RentalBill bill) { this.bill = bill; }

    public boolean isLate() {
        return returnDate != null && returnDate.isAfter(dueDate);
    }

    @Override
    public String toString() {
        return rentalId + " [" + equipmentId + " -> " + userId + "] " + status;
    }
}
