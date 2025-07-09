package com.fu.thinh_nguyen.qrfoodorder.ui.staff;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.data.repository.TableRepository;
import com.fu.thinh_nguyen.qrfoodorder.ui.adapter.OrderTableAdapter;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListActivity extends BaseActivity implements OrderTableAdapter.OnOrderClickListener {
    private RecyclerView recyclerView;
    private OrderTableAdapter orderTableAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvTableTitle;
    private List<OrderDto> orderList;
    private int tableId;
    private String tableNumber;

    // Repository
    private TableRepository tableRepository;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // Initialize repository
        tokenManager = new TokenManager(this);
        tableRepository = new TableRepository(tokenManager);

        getIntentData();
        initViews();
        setupRecyclerView();
        loadOrders();
    }

    private void getIntentData() {
        tableId = getIntent().getIntExtra("table_id", -1);
        tableNumber = getIntent().getStringExtra("table_number");
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_orders);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        tvTableTitle = findViewById(R.id.tv_table_title);

        tvTableTitle.setText("Đơn hàng - Bàn " + tableNumber);
        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderTableAdapter = new OrderTableAdapter(this, orderList);
        orderTableAdapter.setOnOrderClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderTableAdapter);
    }

    private void loadOrders() {
        swipeRefreshLayout.setRefreshing(true);

        // Log thông tin request
        Log.d("OrderListDebug", "Loading orders for tableId: " + tableId);

        tableRepository.getOrdersByTableIdUsingSearch(tableId, new Callback<List<OrderDto>>() {
            @Override
            public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                swipeRefreshLayout.setRefreshing(false);

                // Log response details
                Log.d("OrderListDebug", "Response code: " + response.code());
                Log.d("OrderListDebug", "Response successful: " + response.isSuccessful());
                Log.d("OrderListDebug", "Response body null: " + (response.body() == null));

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("OrderListDebug", "Orders count: " + response.body().size());

                    orderList.clear();
                    orderList.addAll(response.body());
                    orderTableAdapter.updateOrders(orderList);

                    if (orderList.isEmpty()) {
                        Toast.makeText(OrderListActivity.this, "Chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log chi tiết lỗi
                    Log.e("OrderListError", "Response failed - Code: " + response.code());

                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("OrderListError", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("OrderListError", "Error reading errorBody", e);
                    }

                    // Hiển thị thông tin lỗi chi tiết hơn
                    String errorMessage = "Lỗi tải dữ liệu - Code: " + response.code();
                    Toast.makeText(OrderListActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);

                // Log chi tiết lỗi network
                Log.e("OrderListError", "Network failure: " + t.getMessage(), t);
                Log.e("OrderListError", "Request URL: " + call.request().url());

                Toast.makeText(OrderListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onOrderClick(OrderDto order) {
        // Handle order click - có thể mở chi tiết đơn hàng
        Toast.makeText(this, "Clicked order #" + order.getId(), Toast.LENGTH_SHORT).show();
    }
}
