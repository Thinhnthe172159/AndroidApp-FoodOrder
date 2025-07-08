package com.fu.thinh_nguyen.qrfoodorder.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.TableService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.TableStatusUpdateDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QrTableStatusActivity extends BaseActivity {

    private TextView tvName, tvStatus;
    private Button btnReserve, btnCancel;
    private int tableId;
    private String tableStatus;

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

        //btnReserve.setOnClickListener(v -> reserveTable());

        btnCancel.setOnClickListener(v -> finish());
    }

//    private void reserveTable() {
//        TokenManager tm = new TokenManager(this);
//        TableService service = RetrofitClient.getInstance(tm).create(TableService.class);
//
//        TableStatusUpdateDto dto = new TableStatusUpdateDto("reserved");
//
//        service.updateTableStatus(tableId, dto).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(QrTableStatusActivity.this,
//                            "Đặt bàn thành công!", Toast.LENGTH_SHORT).show();
//                    finish(); // hoặc chuyển màn hình khác nếu muốn
//                } else {
//                    Toast.makeText(QrTableStatusActivity.this,
//                            "Không thể đặt bàn!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(QrTableStatusActivity.this,
//                        "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
