package model;

public enum EquipmentCategory {
    ELECTRONICS("Electronics", 1.5, 2.0),
    MEDIA_EQUIPMENT("Media Equipment", 1.3, 2.5),
    LABORATORY_EQUIPMENT("Laboratory Equipment", 2.0, 3.0);

    private final String displayName;
    private final double lateFeeMultiplier;
    private final double damageFeeMultiplier;

    EquipmentCategory(String displayName, double lateFeeMultiplier, double damageFeeMultiplier) {
        this.displayName = displayName;
        this.lateFeeMultiplier = lateFeeMultiplier;
        this.damageFeeMultiplier = damageFeeMultiplier;
    }

    public String getDisplayName() { return displayName; }
    public double getLateFeeMultiplier() { return lateFeeMultiplier; }
    public double getDamageFeeMultiplier() { return damageFeeMultiplier; }

    @Override
    public String toString() { return displayName; }
}
