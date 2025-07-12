package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class OrderIdRequest {
    private int orderId;

    public OrderIdRequest(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
