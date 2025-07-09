package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.adapter.OrderAdapter;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrderActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        recyclerView = findViewById(R.id.RecyclerViewOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Chỉ khởi tạo adapter 1 lần duy nhất
        adapter = new OrderAdapter(null, order -> {
            Intent intent = new Intent(ViewOrderActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", order.getId());
            startActivity(intent);
        });
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        loadOrders();
    }


    private void loadOrders() {
        TokenManager tokenManager = new TokenManager(this);
        OrderService orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);
        String token = "Bearer " + tokenManager.get();

        orderService.getMyCurrentOrder(token).enqueue(new Callback<List<OrderDto>>() {
            @Override
            public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setOrderList(response.body());
                } else {
                    Toast.makeText(ViewOrderActivity.this, "Không lấy được đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                Toast.makeText(ViewOrderActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderAPI", "Error: ", t);
            }
        });
    }
}
