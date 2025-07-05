package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class OrderUpdateStatusDto {
    private int orderId;
    private String newStatus;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}
