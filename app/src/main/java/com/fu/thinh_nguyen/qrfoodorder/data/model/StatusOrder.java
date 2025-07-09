package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class StatusOrder {
    public static final String PendingStatus = "pending";
    public static final String PreparingStatus = "preparing";
    public static final String CancelledStatus = "cancelled";
    public static final String PaidStatus = "paid";

    public static String getStatus(String status) {
        if (status.equals(PendingStatus)) {
            return "Đang chờ";
        }
        if (status.equals(PreparingStatus)) {
            return "Đang chuẩn bị";
        }
        if (status.equals(CancelledStatus)) {
            return "Đã hủy";
        }
        if (status.equals(PaidStatus)) {
            return "Đã thanh toán";
        }
        return null;
    }
}
