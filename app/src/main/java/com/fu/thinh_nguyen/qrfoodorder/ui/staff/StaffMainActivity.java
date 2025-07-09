package com.fu.thinh_nguyen.qrfoodorder.ui.staff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
        setContentView(R.layout.activity_staff_main);

        // Initialize repository
        tokenManager = new TokenManager(this);
        tableRepository = new TableRepository(tokenManager);

        initViews();
        setupRecyclerView();
        loadTables();
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
