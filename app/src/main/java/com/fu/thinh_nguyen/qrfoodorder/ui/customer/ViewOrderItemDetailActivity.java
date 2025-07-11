package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.Manifest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.bumptech.glide.Glide;
import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrderItemDetailActivity extends BaseActivity {
    private ImageView imgFood;
    private TextView txtItemName, txtItemPrice, txtNote;
    private EditText edtQuantity;
    private Button btnUpdateQuantity;
    private OrderItemDto orderItem;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_item_detail);

        // Ánh xạ view
        imgFood = findViewById(R.id.imgFood);
        txtItemName = findViewById(R.id.txtItemName);
        txtItemPrice = findViewById(R.id.txtItemPrice);
        txtNote = findViewById(R.id.txtNote);
        edtQuantity = findViewById(R.id.edtQuantity);
        btnUpdateQuantity = findViewById(R.id.btnUpdateQuantity);

        // Nhận dữ liệu từ intent
        if (getIntent() != null && getIntent().hasExtra("ORDER_ITEM")) {
            orderItem = (OrderItemDto) getIntent().getSerializableExtra("ORDER_ITEM");

            if (orderItem != null) {
                txtItemName.setText(orderItem.getMenuItemName());
                NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                txtItemPrice.setText("Giá: " + vnFormat.format(orderItem.getPrice()));
                txtNote.setText(orderItem.getNote() != null && !orderItem.getNote().isEmpty()
                        ? orderItem.getNote()
                        : "Không có ghi chú");
                edtQuantity.setText(String.valueOf(orderItem.getQuantity()));

                // Load ảnh nếu có URL (nếu chỉ là placeholder thì giữ nguyên)
                if (orderItem.getImage() != null && !orderItem.getImage().isEmpty()) {
                    Glide.with(this).load(orderItem.getImage()).into(imgFood);
                }
            }
        }

        btnUpdateQuantity.setOnClickListener(v -> {
            String quantityStr = edtQuantity.getText().toString().trim();

            if (quantityStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
                return;
            }

            int newQuantity = Integer.parseInt(quantityStr);

            if (newQuantity <= 0) {
                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // GÁN quantity mới vào orderItem
            orderItem.setQuantity(newQuantity);

            updateOrderItem(orderItem);
        });

    }

    private void updateOrderItem(OrderItemDto dto) {
        TokenManager tokenManager = new TokenManager(this);
        OrderService orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);

        orderService.updateQuantityOrderItem(orderItem).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = response.body();
                    if (success) {
                        Toast.makeText(ViewOrderItemDetailActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(ViewOrderItemDetailActivity.this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewOrderItemDetailActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(ViewOrderItemDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
