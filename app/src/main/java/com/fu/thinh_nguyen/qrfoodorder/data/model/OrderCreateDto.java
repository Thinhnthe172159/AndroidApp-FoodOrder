package com.fu.thinh_nguyen.qrfoodorder.data.model;

import java.util.List;

public class OrderCreateDto {
    private int customerId;
    private int tableId;
    private List<OrderItemCreateDto> items;

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    public List<OrderItemCreateDto> getItems() { return items; }
    public void setItems(List<OrderItemCreateDto> items) { this.items = items; }
}
