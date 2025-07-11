package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresPermission;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderCreateDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemCreateDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMenuItem_Cus_Activity extends BaseActivity {
    private int quantity = 1;
    private MenuItemDto menuItem;

    private ImageView imgMenu;
    private TextView tvName, tvDescription, tvCategory, tvTotalPrice, tvQuantity;

    private EditText edtNote;
    private ImageButton btnPlus, btnMinus;
    private TokenManager tokenManager;
    private OrderService orderService;
    private List<OrderDto> orderDtoList;

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_menu_item_cus);

        menuItem = (MenuItemDto) getIntent().getSerializableExtra("menuDetail");

        if (menuItem == null) {
            Toast.makeText(this, "Không có dữ liệu món ăn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tokenManager = new TokenManager(this);
        imgMenu = findViewById(R.id.imgMenu);
        tvName = findViewById(R.id.tvName);
        tvDescription = findViewById(R.id.tvDescription);
        tvCategory = findViewById(R.id.tvCategory);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        edtNote = findViewById(R.id.edtNote);

        tvName.setText(menuItem.getName());
        tvDescription.setText(menuItem.getDescription());
        tvCategory.setText(menuItem.getCategoryName());
        tvQuantity.setText(String.valueOf(quantity));
        updateTotalPrice();

        if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(menuItem.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30))) // bo góc 30px
                    .placeholder(R.drawable.ic_food_placeholder)
                    .into(imgMenu);
        }

        ImageButton btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(v -> AddOrder());


        // Kiểm tra còn hàng
        if (menuItem.getIsAvailable() != null && !menuItem.getIsAvailable()) {
            Toast.makeText(this, "Món ăn hiện không có sẵn", Toast.LENGTH_LONG).show();
        }

        // Tăng/giảm số lượng
        btnPlus.setOnClickListener(v -> {
            quantity++;
            if (quantity == 50) {
                Toast.makeText(this, "Số lượng tối đa là 50", Toast.LENGTH_SHORT).show();
                quantity--;
            }
            tvQuantity.setText(String.valueOf(quantity));
            updateTotalPrice();
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        EditText edtNote = findViewById(R.id.edtNote);
        ScrollView scrollView = findViewById(R.id.scrollView);

        edtNote.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.postDelayed(() -> scrollView.smoothScrollTo(0, edtNote.getBottom()), 200);
            }
        });



        getOrderList();
    }

    private void updateTotalPrice() {
        BigDecimal total = menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(format.format(total));
    }

    private void getOrderList() {
        orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);
        String token = "Bearer " + tokenManager.get();
        orderService.getMyCurrentOrder(token).enqueue(new Callback<List<OrderDto>>() {
            String token = tokenManager.get();
            @Override
            public void onResponse(Call<List<OrderDto>> call, Response<List<OrderDto>> response) {
                Log.d("DetailMenuItem_Cus_Activity", "getOrderList: " + tokenManager.get());
                Log.d("DetailMenuItem_Cus_Activity", "getOrderList: " + response.toString());
                Log.d("DetailMenuItem_Cus_Activity", "getOrderList: " + response.body().toString());
                if (response.isSuccessful() && response.body() != null) {
                    orderDtoList = response.body();
                } else {
                    Toast.makeText(DetailMenuItem_Cus_Activity.this, "Không lấy được đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderDto>> call, Throwable throwable) {
                Toast.makeText(DetailMenuItem_Cus_Activity.this, "Lỗi: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void AddOrder() {
        if (orderDtoList == null || orderDtoList.isEmpty()) {
            Toast.makeText(this, "Không có đơn hàng nào để thêm món", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo DTO món ăn
        OrderItemCreateDto dto = new OrderItemCreateDto();
        dto.setMenuItemId(menuItem.getId());
        dto.setQuantity(quantity);
        dto.setNote(edtNote.getText().toString());

        if (orderDtoList.size() == 1) {
            // Trường hợp chỉ có 1 order
            dto.setOrderId(orderDtoList.get(0).getId());
            sendOrderItemToServer(dto);
        } else {
            // Có nhiều order → cho người dùng chọn
            showOrderSelectionDialog(dto);
        }
    }

    private void showOrderSelectionDialog(OrderItemCreateDto dto) {
        String[] orderNames = new String[orderDtoList.size()];
        for (int i = 0; i < orderDtoList.size(); i++) {
            OrderDto order = orderDtoList.get(i);
            // Hiển thị tên bàn và mã đơn (nếu có)
            orderNames[i] = "Bàn: " + order.getTableName() + " - Mã đơn: #" + order.getId();
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn đơn hàng để thêm món")
                .setItems(orderNames, (dialog, which) -> {
                    dto.setOrderId(orderDtoList.get(which).getId());
                    sendOrderItemToServer(dto);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    public void sendOrderItemToServer(OrderItemCreateDto dto) {
        orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);

        orderService.addItem(dto.getOrderId(), dto).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d("DetailMenuItem_Cus_Activity", "Status code: " + response.code());
                Log.d("DetailMenuItem_Cus_Activity", "Body: " + response.body());

                if (response.isSuccessful() && Boolean.TRUE.equals(response.body())) {
                    Toast.makeText(DetailMenuItem_Cus_Activity.this,
                            "Đã thêm món vào đơn #" + dto.getOrderId(),
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DetailMenuItem_Cus_Activity.this,
                            "Thêm món thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(DetailMenuItem_Cus_Activity.this,
                        "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}