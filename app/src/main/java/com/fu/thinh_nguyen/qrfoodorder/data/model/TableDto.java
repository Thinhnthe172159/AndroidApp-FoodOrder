package com.fu.thinh_nguyen.qrfoodorder.data.model;

import com.google.gson.annotations.SerializedName;

public class TableDto {
    @SerializedName("id")
    private int id;

    @SerializedName("tableNumber")
    private String tableNumber;

    @SerializedName("qrCode")
    private String qrCode;

    @SerializedName("status")
    private String status;

    public int getId() { return id; }
    public String getTableNumber() { return tableNumber; }
    public String getQrCode() { return qrCode; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public void setStatus(String status) { this.status = status; }
}
