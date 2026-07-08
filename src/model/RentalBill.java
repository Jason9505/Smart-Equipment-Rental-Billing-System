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

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Base Fee:      RM%8.2f%n", baseFee));
        if (discount > 0) {
            sb.append(String.format("Discount:     -RM%8.2f%n", discount));
        }
        if (penalty > 0) {
            sb.append(String.format("Penalty:      +RM%8.2f%n", penalty));
        }
        sb.append("------------------------------\n");
        sb.append(String.format("Net Payable:   RM%8.2f", netPayable));
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format(
            "Base Fee: RM%.2f | Discount: -RM%.2f | Penalty: +RM%.2f | Net Payable: RM%.2f",
            baseFee, discount, penalty, netPayable);
    }
}
