package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class OrderItemStatus {
    public static String PENDING = "Pending";
    public static String Serving = "Serving";
    public static String Canncel = "Canncel";
    public static String Paid = "Paid";
    public static String Update = "Update";

    public static String getStatusText(String status) {
        if (status == null) return "Không xác định";

        switch (status) {
            case "Pending": return "Chờ xử lý";
            case "Serving": return "Đang phục vụ";
            case "Update": return "Cập nhật món";
            case "Canncel": return "Đã hủy";
            case "Paid": return "Đã thanh toán";
            default: return status;
        }
    }
}
