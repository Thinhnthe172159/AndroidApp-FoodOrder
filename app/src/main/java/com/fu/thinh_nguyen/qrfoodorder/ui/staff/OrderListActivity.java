package com.fu.thinh_nguyen.qrfoodorder.ui.staff;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.data.repository.OrderStaffRepository;
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
    private OrderStaffRepository orderStaffRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        setContentView(R.layout.activity_order_list);

        tokenManager = new TokenManager(this);

        tableRepository = new TableRepository(tokenManager);
        orderStaffRepository = new OrderStaffRepository(tokenManager);

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

        tvTableTitle.setText("Đơn hàng - " + tableNumber);
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
        tableRepository.getOrdersByTableIdUsingSearch(tableId, new Callback<List<OrderDto>>() {
            @Override
            public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderTableAdapter.updateOrders(orderList);

                    if (orderList.isEmpty()) {
                        Toast.makeText(OrderListActivity.this, "Chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderDto>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onConfirmOrder(int orderId) {
        orderStaffRepository.confirmOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderListActivity.this, "Xác nhận đơn hàng #" + orderId + " thành công", Toast.LENGTH_SHORT).show();
                    loadOrders();
                } else {
                    Toast.makeText(OrderListActivity.this, "Xác nhận thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPayment(int orderId) {
        orderStaffRepository.markOrderAsPaid(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderListActivity.this, "Đơn hàng đã được thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    loadOrders();
                } else {
                    Toast.makeText(OrderListActivity.this, "Lỗi khi cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
