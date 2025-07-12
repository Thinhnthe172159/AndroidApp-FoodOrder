package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.adapter.CustomerOrderAdapter;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView txtEmpty;
    private CustomerOrderAdapter adapter;
    private OrderService orderService;
    private TokenManager tokenManager;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        recyclerView = findViewById(R.id.recyclerViewOrderHistory);
        txtEmpty = findViewById(R.id.txtEmptyOrder); // Anh nhớ thêm TextView này vào layout

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        tokenManager = new TokenManager(this);
        orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);

        adapter = new CustomerOrderAdapter(
                null,
                order -> {
                    Intent intent = new Intent(OrderHistoryActivity.this, OrderDetailActivity.class);
                    intent.putExtra("ORDER_ID", order.getId());
                    intent.putExtra("ORDER_DTO", order);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                },
                this,
                orderService,
                null,
                true // readonly
        );

        recyclerView.setAdapter(adapter);

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        OrderDto request = new OrderDto();
        request.setItems(new ArrayList<>());
                request.setCustomerId(Integer.parseInt(tokenManager.getUserId()));
        orderService.searchOrder(request).enqueue(new Callback<List<OrderDto>>() {
            @Override
            public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                Log.d("OrderHistory", "Response: " + response.toString());
                Log.d("OrderHistory", "Body: " + response.body());
                Log.d("OrderHistory", "Code: " + response.code());
                Log.d("OrderHistory", "Message: " + response.message());
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDto> list = response.body();
                    if (list.isEmpty()) {
                        txtEmpty.setText("Bạn chưa có lịch sử đơn hàng.");
                        txtEmpty.setVisibility(TextView.VISIBLE);
                    } else {
                        txtEmpty.setVisibility(TextView.GONE);
                    }
                    adapter.setOrderList(list);
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Không lấy được lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                Toast.makeText(OrderHistoryActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderHistory", "Lỗi khi gọi API", t);
            }
        });
    }
}
