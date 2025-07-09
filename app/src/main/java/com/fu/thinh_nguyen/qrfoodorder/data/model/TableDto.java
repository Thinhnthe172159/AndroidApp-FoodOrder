package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class TableDto {
    private int id;
    private String tableNumber;
    private String qrCode;
    private String status;

    // Constructors
    public TableDto() {}

    public TableDto(int id, String tableNumber, String qrCode, String status) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.qrCode = qrCode;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

