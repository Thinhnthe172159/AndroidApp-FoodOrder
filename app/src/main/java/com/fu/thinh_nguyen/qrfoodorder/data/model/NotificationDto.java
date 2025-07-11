package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class NotificationDto {
    private String title;
    private String message;
    private String senderId;
    private String senderName;
    private String targetCustomerId;

    public NotificationDto() {}

    public NotificationDto(String title, String message) {
        this.title = title;
        this.message = message;
    }

    // Getter + Setter
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getTargetCustomerId() { return targetCustomerId; }

    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setTargetCustomerId(String targetCustomerId) { this.targetCustomerId = targetCustomerId; }
}
