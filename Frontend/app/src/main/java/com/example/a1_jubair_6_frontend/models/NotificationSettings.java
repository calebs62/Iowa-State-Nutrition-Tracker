package com.example.a1_jubair_6_frontend.models;

public class NotificationSettings {
    private int uid;
    private User user;
    private boolean timeNotification;
    private boolean systemNotification;
    private boolean smsNotification;
    private boolean emailNotification;

    public boolean isTimeNotification() { return timeNotification; }
    public void setTimeNotification(boolean timeNotification) { this.timeNotification = timeNotification; }
    public boolean isSystemNotification() { return systemNotification; }
    public void setSystemNotification(boolean systemNotification) { this.systemNotification = systemNotification; }
    public boolean isSmsNotification() { return smsNotification; }
    public void setSmsNotification(boolean smsNotification) { this.smsNotification = smsNotification; }
    public boolean isEmailNotification() { return emailNotification; }
    public void setEmailNotification(boolean emailNotification) { this.emailNotification = emailNotification; }
}
