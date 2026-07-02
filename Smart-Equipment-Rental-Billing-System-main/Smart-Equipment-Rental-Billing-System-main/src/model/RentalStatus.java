package model;

public enum RentalStatus {
    ACTIVE,   
    RETURNED; 

    @Override
    public String toString() {
        return this == ACTIVE ? "Active" : "Returned";
    }
}
