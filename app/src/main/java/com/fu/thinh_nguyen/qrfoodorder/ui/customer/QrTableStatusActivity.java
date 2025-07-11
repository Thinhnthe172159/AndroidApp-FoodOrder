package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderCreateDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrTableStatusActivity extends BaseActivity {

    private TextView tvName, tvStatus;
    private Button btnReserve, btnCancel;
    private int tableId;
    private String tableStatus;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_table_status);

        tvName = findViewById(R.id.tv_table_name);
        tvStatus = findViewById(R.id.tv_table_status);
        btnReserve = findViewById(R.id.btn_reserve);
        btnCancel = findViewById(R.id.btn_cancel);

        // Nhận dữ liệu từ intent
        tableId = getIntent().getIntExtra("TABLE_ID", -1);
        String tableName = getIntent().getStringExtra("TABLE_NAME");
        tableStatus = getIntent().getStringExtra("TABLE_STATUS");

        // Gán hiển thị
        tvName.setText("Bàn số: " + tableName);
        tvStatus.setText("Trạng thái: " + tableStatus);

        // Nếu không phải available thì ẩn nút đặt bàn
        if (!"available".equalsIgnoreCase(tableStatus)) {
            btnReserve.setVisibility(View.GONE);
            Toast.makeText(this, "Bàn đang được sử dụng", Toast.LENGTH_SHORT).show();
        }

        btnReserve.setOnClickListener(v -> reserveTable(tableId));

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomerMainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void reserveTable(int tableId) {
        TokenManager tm = new TokenManager(this);
        OrderService orderService = RetrofitClient.getInstance(tm).create(OrderService.class);

        OrderCreateDto createDto = new OrderCreateDto();
        createDto.setTableId(tableId);
        createDto.setCustomerId(0);
        createDto.setItems(new ArrayList<>());

        orderService.createOrder(createDto).enqueue(new Callback<OrderDto>() {
            @Override
            public void onResponse(Call<OrderDto> call, Response<OrderDto> response) {
                Log.d(TAG, "onResponse: " + response.code() + " " + response.message());
                Log.d("TOKEN", "Bearer " + tm.get());

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(QrTableStatusActivity.this, "Đặt bàn thành công", Toast.LENGTH_SHORT).show();

                    int orderId = response.body().getId();

                    Intent intent = new Intent(QrTableStatusActivity.this, OrderDetailActivity.class);
                    intent.putExtra("ORDER_ID", orderId);
                    startActivity(intent);

                    finish();
                } else {
                    Toast.makeText(QrTableStatusActivity.this, "Đặt bàn thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderDto> call, Throwable t) {
                Toast.makeText(QrTableStatusActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
