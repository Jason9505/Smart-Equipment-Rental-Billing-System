package model;

public class User {
    private String userId;
    private String name;
    private UserType userType;
    private boolean finalYear;

    public User(String userId, String name, UserType userType, boolean finalYear) {
        this.userId = userId;
        this.name = name;
        this.userType = userType;
        this.finalYear = finalYear;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public boolean isFinalYear() { return finalYear; }
    public void setFinalYear(boolean finalYear) { this.finalYear = finalYear; }

    public boolean isEligibleForDiscount() {
        return userType == UserType.STAFF || (userType == UserType.STUDENT && finalYear);
    }

    @Override
    public String toString() {
        return name + " (" + userId + ")";
    }
}
