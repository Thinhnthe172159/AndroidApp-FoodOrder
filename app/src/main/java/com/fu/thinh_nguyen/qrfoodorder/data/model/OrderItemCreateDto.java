package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class OrderItemCreateDto {
    private int menuItemId;
    private int quantity;

    public int getMenuItemId() { return menuItemId; }
    public void setMenuItemId(int menuItemId) { this.menuItemId = menuItemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
