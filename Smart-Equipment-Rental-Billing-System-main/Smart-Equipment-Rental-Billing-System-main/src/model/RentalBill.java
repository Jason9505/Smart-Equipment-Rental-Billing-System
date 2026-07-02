package model;

public class RentalBill {
    private final double baseFee;
    private final double discount;
    private final double penalty;
    private final double netPayable;

    public RentalBill(double baseFee, double discount, double penalty) {
        this.baseFee = baseFee;
        this.discount = discount;
        this.penalty = penalty;
        this.netPayable = baseFee - discount + penalty;
    }

    public double getBaseFee() { return baseFee; }
    public double getDiscount() { return discount; }
    public double getPenalty() { return penalty; }
    public double getNetPayable() { return netPayable; }

    @Override
    public String toString() {
        return String.format(
            "Base Fee: RM%.2f | Discount: -RM%.2f | Penalty: +RM%.2f | Net Payable: RM%.2f",
            baseFee, discount, penalty, netPayable);
    }
}
