package model;

public class Equipment {
    private String equipmentId;
    private String name;
    private EquipmentCategory category;
    private double dailyRate;
    private boolean available;

    public Equipment(String equipmentId, String name, EquipmentCategory category,
                      double dailyRate, boolean available) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.category = category;
        this.dailyRate = dailyRate;
        this.available = available;
    }

    public String getEquipmentId() { return equipmentId; }
    public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public EquipmentCategory getCategory() { return category; }
    public void setCategory(EquipmentCategory category) { this.category = category; }

    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return equipmentId + " - " + name + " (" + category.getDisplayName() + ")";
    }
}
