package com.fu.thinh_nguyen.qrfoodorder.ui.staff;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.TableDto;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.data.repository.TableRepository;
import com.fu.thinh_nguyen.qrfoodorder.ui.adapter.TableAdapter;
import com.fu.thinh_nguyen.qrfoodorder.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffMainActivity extends BaseActivity implements TableAdapter.OnTableClickListener {

    private RecyclerView recyclerView;
    private TableAdapter tableAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<TableDto> tableList;

    // Repository
    private TableRepository tableRepository;
    private TokenManager tokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
        setContentView(R.layout.activity_staff_main);

        // Initialize repository
        tokenManager = new TokenManager(this);
        tableRepository = new TableRepository(tokenManager);

        initViews();
        setupRecyclerView();
        loadTables();

        handleNotificationIntent();
    }

    private void handleNotificationIntent() {
        Intent intent = getIntent();
        String orderIdFromNotification = intent.getStringExtra("orderId");
        String tableIdFromNotification = intent.getStringExtra("tableId");

        if (orderIdFromNotification != null && tableIdFromNotification != null) {
            try {
                int tableId = Integer.parseInt(tableIdFromNotification);

                Toast.makeText(this, "Mở từ thông báo - Đơn: " + orderIdFromNotification + ", Bàn: " + tableId, Toast.LENGTH_SHORT).show();

                // Tìm table trong danh sách và gọi onTableClick
                findTableAndOpenOrderList(tableId);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Dữ liệu thông báo không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void findTableAndOpenOrderList(int targetTableId) {
        // Nếu danh sách table đã load xong
        if (tableList != null && !tableList.isEmpty()) {
            for (TableDto table : tableList) {
                if (table.getId() == targetTableId) {
                    // Gọi onTableClick với table tìm được
                    onTableClick(table);
                    return;
                }
            }
            // Nếu không tìm thấy table, tạo table object tạm thời
            createTemporaryTableAndOpen(targetTableId);
        } else {
            // Nếu danh sách chưa load, đợi load xong rồi tìm
            waitForTablesLoadedThenOpen(targetTableId);
        }
    }

    private void createTemporaryTableAndOpen(int tableId) {
        // Tạo table object tạm thời để gọi onTableClick
        TableDto tempTable = new TableDto();
        tempTable.setId(tableId);
        tempTable.setTableNumber("Bàn " + tableId); // Tên tạm thời

        onTableClick(tempTable);
    }

    private void waitForTablesLoadedThenOpen(int targetTableId) {
        // Override lại loadTables để xử lý sau khi load xong
        swipeRefreshLayout.setRefreshing(true);

        tableRepository.getTables(new Callback<List<TableDto>>() {
            @Override
            public void onResponse(Call<List<TableDto>> call, Response<List<TableDto>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    tableList.clear();
                    tableList.addAll(response.body());
                    tableAdapter.updateTables(tableList);

                    // Sau khi load xong, tìm table và mở
                    findTableAndOpenOrderList(targetTableId);
                } else {
                    Toast.makeText(StaffMainActivity.this, "Lỗi tải dữ liệu Table", Toast.LENGTH_SHORT).show();
                    // Vẫn mở với thông tin tạm thời
                    createTemporaryTableAndOpen(targetTableId);
                }
            }

            @Override
            public void onFailure(Call<List<TableDto>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(StaffMainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Vẫn mở với thông tin tạm thời
                createTemporaryTableAndOpen(targetTableId);
            }
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_tables);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this::loadTables);
    }

    private void setupRecyclerView() {
        tableList = new ArrayList<>();
        tableAdapter = new TableAdapter(this, tableList, this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tableAdapter);
    }

    private void loadTables() {
        // Chỉ load nếu không phải từ notification
        Intent intent = getIntent();
        String orderIdFromNotification = intent.getStringExtra("orderId");

        if (orderIdFromNotification != null) {
            // Đã xử lý trong handleNotificationIntent()
            return;
        }

        swipeRefreshLayout.setRefreshing(true);

        tableRepository.getTables(new Callback<List<TableDto>>() {
            @Override
            public void onResponse(Call<List<TableDto>> call, Response<List<TableDto>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    tableList.clear();
                    tableList.addAll(response.body());
                    tableAdapter.updateTables(tableList);
                } else {
                    Toast.makeText(StaffMainActivity.this, "Lỗi tải dữ liệu Table", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TableDto>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(StaffMainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onTableClick(TableDto table) {
        Intent intent = new Intent(this, OrderListActivity.class);
        intent.putExtra("table_id", table.getId());
        intent.putExtra("table_number", table.getTableNumber());
        startActivity(intent);
    }
}
