package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class PaymentCreateDto {
    private int orderId;
    private double amount;
    private String paymentMethod;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
