package com.fu.thinh_nguyen.qrfoodorder.data.model;

import java.io.Serializable;

public class OrderItemDto  {
    private Integer menuItemId;
    private String menuItemName;
    private int quantity;
    private String note;
    private Double price;

    // Constructors
    public OrderItemDto() {
    }

    // Getters & Setters
    public Integer getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Integer menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}