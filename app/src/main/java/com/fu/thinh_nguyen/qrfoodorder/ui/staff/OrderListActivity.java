package com.fu.thinh_nguyen.qrfoodorder.ui.staff;

import android.Manifest;
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
    private OrderService orderService;

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

        // Initialize repository
        tokenManager = new TokenManager(this);
        tableRepository = new TableRepository(tokenManager);
        orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);
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

    // **THÊM 2 METHOD NÀY**
    @Override
    public void onConfirmOrder(int orderId) {
        // Xử lý xác nhận đơn hàng (pending/update -> preparing)
        //

        orderService.confirmOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderListActivity.this, "Xác nhận đơn hàng #" + orderId + " thành công", Toast.LENGTH_SHORT).show();
                    // Có thể load lại danh sách đơn hàng
                    loadOrders();
                } else {
                    Toast.makeText(OrderListActivity.this, "Xác nhận thất bại!", Toast.LENGTH_SHORT).show();
                    Log.e("OrderAction", "Confirm order failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(OrderListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderAction", "Confirm order error", t);
            }
        });
    }

    @Override
    public void onPayment(OrderDto order) {
        // Xử lý thanh toán (preparing -> paid)
        Toast.makeText(this, "Thanh toán đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();

        // Log để debug
        Log.d("OrderAction", "Payment for order: " + order.getId() + " - Amount: " + order.getTotalAmount());

        // TODO: Thay thế bằng API call thực tế
        processPayment(order);
    }

    // **THÊM CÁC METHOD XỬ LÝ API**
    private void updateOrderStatus(String orderId, String newStatus) {
        // TODO: Implement API call để update order status
        // Tạm thời hiển thị message
        Toast.makeText(this, "Đang cập nhật trạng thái đơn #" + orderId + " thành " + newStatus, Toast.LENGTH_SHORT).show();

        // Sau khi API thành công, refresh lại danh sách
        // loadOrders();
    }

    private void processPayment(OrderDto order) {
        // TODO: Implement API call để xử lý thanh toán
        // Tạm thời hiển thị message
        Toast.makeText(this, "Đang xử lý thanh toán cho đơn #" + order.getId(), Toast.LENGTH_SHORT).show();

        // Sau khi payment thành công, refresh lại danh sách
        // loadOrders();
    }



}
