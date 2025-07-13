package com.fu.thinh_nguyen.qrfoodorder.Notification;

import android.Manifest;
import android.content.Context;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.Map;

import io.reactivex.rxjava3.core.Single;

public class SignalRClient {

    private static HubConnection hubConnection;
    private static final String TAG = "SignalR";

    // Khởi tạo kết nối đến SignalR Hub
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void start(Context context, TokenManager tokenManager) {
        String jwtToken = tokenManager.get(); // Lấy JWT từ SharedPreferences

        hubConnection = HubConnectionBuilder.create("http://thinhnguyen5k-001-site1.ltempurl.com/notificationHub")
                .withAccessTokenProvider(Single.defer(() -> Single.just(jwtToken)))
                .build();

        // Nhận thông báo từ server với dữ liệu bổ sung
        hubConnection.on("ReceiveNotificationWithData", (title, message, senderId, senderName, orderId, tableId) -> {
            Log.d(TAG, "Nhận thông báo từ: " + senderName + " - " + title + " - OrderID: " + orderId);

            // Tạo Intent với dữ liệu để mở OrderDetailActivity
            NotificationHelper.showNotificationWithData(context, senderName + " - " + title, message, orderId, tableId);
        }, String.class, String.class, String.class, String.class, String.class, String.class);

        hubConnection.on("ReceiveNotification", (title, message, senderId, senderName) -> {
            Log.d(TAG, "Nhận thông báo từ: " + senderName + " - " + title);
            NotificationHelper.showNotification(context, senderName + " - " + title, message);
        }, String.class, String.class, String.class, String.class);



        hubConnection.onClosed(error -> {
            Log.e("SignalR", "Kết nối đã đóng: " + (error != null ? error.getMessage() : "không có lỗi"));
        });



        // Kết nối tới hub
        hubConnection.start()
                .doOnComplete(() -> Log.d(TAG, "Kết nối SignalR thành công"))
                .doOnError(error -> Log.e(TAG, "Lỗi kết nối SignalR: " + error.getMessage()))
                .subscribe();
    }

    // Ngắt kết nối hub khi không cần nữa (ví dụ trong onDestroy)
    public static void stop() {
        if (hubConnection != null) {
            hubConnection.stop();
        }
    }

    // Kiểm tra đã kết nối thành công hay chưa
    public static boolean isConnected() {
        return hubConnection != null &&
                hubConnection.getConnectionState().name().equals("CONNECTED");
    }

    // Gửi thông báo đến tất cả nhân viên
    public static void sendToAllStaff(String title, String message) {
        if (isConnected()) {
            hubConnection.send("SendToAllStaff", title, message);
        }
    }


    // Gửi phản hồi từ nhân viên đến khách hàng cụ thể
    public static void replyToCustomer(String customerId, String title, String message, String senderId, String senderName) {
        if (isConnected()) {
            hubConnection.send("ReplyToCustomer", customerId, title, message);
        } else {
            Log.w(TAG, "Chưa kết nối SignalR, không thể gửi phản hồi");
        }
    }

    // Gửi thông báo đến tất cả nhân viên với dữ liệu bổ sung
    public static void sendToAllStaffWithData(String title, String message, String orderId, String tableId) {
        if (isConnected()) {
            hubConnection.send("SendToAllStaffWithData", title, message, orderId, tableId);
        }
    }

}
