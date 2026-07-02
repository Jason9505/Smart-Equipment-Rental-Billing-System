package service;

import model.Equipment;
import model.Rental;
import model.RentalBill;

public interface RentalObserver {

    void onRented(Rental rental, Equipment equipment);

    void onReturned(Rental rental, Equipment equipment, RentalBill bill);
}
