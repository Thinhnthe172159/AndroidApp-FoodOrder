package com.fu.thinh_nguyen.qrfoodorder.data.model;

public class StatusOrder {
    public static final String PendingStatus = "pending";
    public static final String PreparingStatus = "preparing";
    public static final String CancelledStatus = "cancelled";
    public static final String PaidStatus = "paid";

    public static final String Update = "update";

    public static String getStatus(String status) {
        switch (status) {
            case PendingStatus:
                return "Đang chờ";
            case PreparingStatus:
                return "Đang chuẩn bị";
            case CancelledStatus:
                return "Đã hủy";
            case PaidStatus:
                return "Đã thanh toán";
            case Update:
                return "Cập nhật Đơn";
        }
        return null;
    }
}