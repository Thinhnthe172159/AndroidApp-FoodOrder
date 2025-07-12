package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.Notification.SignalRClient;
import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.StatusOrder;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.adapter.OrderItemAdapter2;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.PaymentGate;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.annotations.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends BaseActivity {

    private RecyclerView recyclerOrderItems;
    private ProgressBar progressBar;

    private TextView txtOrderTitle, txtOrderAddress, txtOrderTime, totalPrice;
    private Button notifyStaffButton, paymentButton;

    private OrderDto order;
    private CountDownTimer countDownTimer;
    private int orderId;

    private TokenManager tokenManager;
    private OrderService orderService;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        recyclerOrderItems = findViewById(R.id.recyclerOrderItems);
        txtOrderTitle = findViewById(R.id.txtOrderTitle);
        txtOrderAddress = findViewById(R.id.txtOrderAddress);
        txtOrderTime = findViewById(R.id.txtOrderTime);
        totalPrice = findViewById(R.id.txtTotalPrice);
        notifyStaffButton = findViewById(R.id.btnNotifyStaff);
        paymentButton = findViewById(R.id.btnCheckout);

        order = (OrderDto) getIntent().getSerializableExtra("ORDER_DTO");
        recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));

        tokenManager = new TokenManager(this);
        orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            fetchOrderDetail(orderId);
        } else {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }

        notifyStaffButton.setOnClickListener(v -> {
            if (order.getStatus().equalsIgnoreCase(StatusOrder.PendingStatus)) {
                String title = "Thông báo xác nhận đặt bàn: " + order.getTableName() + " | CODE=PE_" + order.getId() + "/" + order.getTableId();
                String message = "Khách hàng " + order.getCustomerName() + " vừa đặt bàn " + order.getTableName() + " lúc " + order.getCreatedAt();
                sendNotificationToStaff(title, message);
            } else if (order.getStatus().equalsIgnoreCase(StatusOrder.Update)) {
                String title = "Thông báo cập nhật đơn từ bàn: " + order.getTableName() + " | CODE=UP_" + order.getId() + "/" + order.getTableId();
                String message = "Khách hàng " + order.getCustomerName() + " vừa cập nhật đơn từ bàn " + order.getTableName();
                sendNotificationToStaff(title, message);
            }
        });

        paymentButton.setOnClickListener(v -> showPaymentOptions());
    }

    private void fetchOrderDetail(int orderId) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        orderService.getOrderById(orderId).enqueue(new Callback<OrderDto>() {
            @Override
            public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    order = response.body();

                    txtOrderTitle.setText("Đơn số: " + order.getId());
                    txtOrderAddress.setText(order.getTableName());
                    txtOrderTime.setText(order.getCreatedAt());

                    if (order.getTotalAmount() == 0 || order.getItems() == null || order.getItems().isEmpty()) {
                        notifyStaffButton.setVisibility(View.GONE);
                        paymentButton.setVisibility(View.GONE);
                        totalPrice.setText("Chưa chọn món");
                    } else {
                        // Logic trạng thái paid / cancelled
                        if (order.getStatus().equalsIgnoreCase(StatusOrder.PaidStatus) ||
                                order.getStatus().equalsIgnoreCase(StatusOrder.CancelledStatus)) {
                            notifyStaffButton.setVisibility(View.GONE);
                            paymentButton.setVisibility(View.GONE);
                            totalPrice.setVisibility(View.GONE);
                        } else {
                            totalPrice.setVisibility(View.VISIBLE);
                            totalPrice.setText(formatVND(order.getTotalAmount()));

                            paymentButton.setVisibility(View.VISIBLE);

                            if (order.getStatus().equalsIgnoreCase(StatusOrder.PendingStatus)) {
                                notifyStaffButton.setVisibility(View.VISIBLE);
                                notifyStaffButton.setText("Gửi menu");
                            } else if (order.getStatus().equalsIgnoreCase(StatusOrder.Update)) {
                                notifyStaffButton.setVisibility(View.VISIBLE);
                                notifyStaffButton.setText("Cập nhật menu");
                            } else {
                                notifyStaffButton.setVisibility(View.GONE);
                            }
                        }
                    }

                    displayOrderItems(order.getItems());

                    long lastNotify = getSharedPreferences("order_prefs", MODE_PRIVATE)
                            .getLong("last_notify_time_" + orderId, 0);
                    long elapsed = System.currentTimeMillis() - lastNotify;
                    if (elapsed < 60000) {
                        startCountdown(60000 - elapsed);
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không thể tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDto> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                Log.e("OrderDetail", "Error: " + t.getMessage());
            }
        });
    }

    private void displayOrderItems(List<OrderItemDto> items) {
        OrderItemAdapter2 adapter = new OrderItemAdapter2(orderId, items, this, tokenManager,
                () -> fetchOrderDetail(orderId)); // Lambda đúng
        recyclerOrderItems.setAdapter(adapter);
    }

    private void sendNotificationToStaff(String title, String message) {
        SignalRClient.sendToAllStaff(title, message);
        Toast.makeText(this, "Đã gửi yêu cầu tới nhân viên", Toast.LENGTH_SHORT).show();

        long now = System.currentTimeMillis();
        getSharedPreferences("order_prefs", MODE_PRIVATE)
                .edit()
                .putLong("last_notify_time_" + orderId, now)
                .apply();

        startCountdown(60000);
    }

    private void startCountdown(long millisLeft) {
        notifyStaffButton.setEnabled(false);

        countDownTimer = new CountDownTimer(millisLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                notifyStaffButton.setText("Vui lòng chờ " + secondsLeft + "s...");
            }

            @Override
            public void onFinish() {
                notifyStaffButton.setEnabled(true);
                notifyStaffButton.setText("Gửi yêu cầu xác nhận");

                getSharedPreferences("order_prefs", MODE_PRIVATE)
                        .edit()
                        .remove("last_notify_time_" + orderId)
                        .apply();
            }
        }.start();
    }

    private void showPaymentOptions() {
        String[] options = {"Thanh toán COD", "Quét mã QR", "Dùng cổng thanh toán"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Chọn phương thức thanh toán")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            sendPaymentNotifyToStaff("COD");
                            break;
                        case 1:
                            sendPaymentNotifyToStaff("QR Code");
                            break;
                        case 2:
                            openPaymentGateway();
                            break;
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void sendPaymentNotifyToStaff(String method) {
        if (order == null) return;
        String title = "Khách yêu cầu thanh toán";
        String message = "Bàn: " + order.getTableName() + "\nMã đơn: " + order.getId() + "\nPhương thức: " + method;
        SignalRClient.sendToAllStaff(title, message);
        Toast.makeText(this, "Đã gửi yêu cầu thanh toán đến nhân viên", Toast.LENGTH_SHORT).show();
    }

    private void openPaymentGateway() {
        Intent intent = new Intent(this, PaymentGate.class);
        intent.putExtra("ORDER_ID", order.getId());
        intent.putExtra("ORDER_DTO", order);
        startActivity(intent);
    }

    private String formatVND(Double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " đ";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            fetchOrderDetail(orderId);
        }
    }
}
