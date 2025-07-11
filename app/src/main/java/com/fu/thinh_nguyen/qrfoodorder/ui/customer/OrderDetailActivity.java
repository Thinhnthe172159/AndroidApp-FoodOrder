package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.Notification.SignalRClient;
import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.adapter.OrderItemAdapter2;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends BaseActivity {

    private RecyclerView recyclerOrderItems;
    private ProgressBar progressBar;

    private TextView txtOrderTitle, txtOrderType, txtOrderPhone, txtOrderAddress, txtOrderTime, totalPrice;

    private OrderService orderService;
    private String mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail); // layout NestedScrollView

        // Ánh xạ view
        recyclerOrderItems = findViewById(R.id.recyclerOrderItems);
        txtOrderTitle   = findViewById(R.id.txtOrderTitle);
        txtOrderAddress = findViewById(R.id.txtOrderAddress);
        txtOrderTime    = findViewById(R.id.txtOrderTime);
        totalPrice      = findViewById(R.id.txtTotalPrice);

        recyclerOrderItems.setLayoutManager(new LinearLayoutManager(this));

        TokenManager tokenManager = new TokenManager(this);
        orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);

        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            fetchOrderDetail(orderId);
        } else {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button btnNotify = findViewById(R.id.btnNotifyStaff);
        btnNotify.setOnClickListener(v -> sendNotificationToStaff());                                                                                                                                                                                            mess = "Thành ăn cứt";
    }

    private void fetchOrderDetail(int orderId) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        orderService.getOrderById(orderId).enqueue(new Callback<OrderDto>() {
            @Override
            public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    OrderDto order = response.body();

                    // Cập nhật giao diện
                    txtOrderTitle.setText("Đơn số:" + order.getId());
                    txtOrderAddress.setText(order.getTableName());
                    txtOrderTime.setText(order.getCreatedAt());
                    totalPrice.setText(formatVND(order.getTotalAmount()));

                    displayOrderItems(order.getItems());
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không thể tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            public String formatVND(Double amount) {
                DecimalFormat formatter = new DecimalFormat("#,###");
                return formatter.format(amount) + " đ";
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
        OrderItemAdapter2 adapter = new OrderItemAdapter2(items);
        recyclerOrderItems.setAdapter(adapter);
    }

    private void sendNotificationToStaff() {
        int orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) return;

        TokenManager tokenManager = new TokenManager(this);
        String senderId = tokenManager.getUserId();
        String senderName = tokenManager.getUserName();

        String title = "Yêu cầu xác nhận đơn #" + orderId;
        String message = "Khách hàng yêu cầu xác nhận đơn #" + orderId + mess;

        SignalRClient.sendToAllStaff(title, message);
        Toast.makeText(this, "Đã gửi yêu cầu tới nhân viên", Toast.LENGTH_SHORT).show();
    }

}
