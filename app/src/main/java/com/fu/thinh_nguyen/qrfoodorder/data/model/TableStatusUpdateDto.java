package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class TableStatusUpdateDto {
    private int tableId;
    private String newStatus;

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}
