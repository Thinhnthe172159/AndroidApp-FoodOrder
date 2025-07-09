package com.fu.thinh_nguyen.qrfoodorder.data.model;

import java.util.List;

public class OrderSearchDto {
    private Integer customerId;
    private String customerName;
    private Integer tableId;
    private String status;
    private List<OrderItemDto> items; // Thêm field này

    // Constructors
    public OrderSearchDto() {}

    public OrderSearchDto(Integer tableId) {
        this.tableId = tableId;
    }

    // Getters & Setters
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getTableId() { return tableId; }
    public void setTableId(Integer tableId) { this.tableId = tableId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Thêm getter/setter cho Items
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
