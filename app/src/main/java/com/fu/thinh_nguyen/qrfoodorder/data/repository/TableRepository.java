package com.fu.thinh_nguyen.qrfoodorder.data.repository;

import android.util.Log;

import com.fu.thinh_nguyen.qrfoodorder.data.api.TableService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderSearchDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.TableDto;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class TableRepository {
    private final TableService tableService;

    public TableRepository(TokenManager tokenManager) {
        this.tableService = RetrofitClient.getInstance(tokenManager).create(TableService.class);
    }

    public void getTables(Callback<List<TableDto>> callback) {
        Call<List<TableDto>> call = tableService.getTables();
        call.enqueue(callback);
    }

    public void getTableById(int id, Callback<TableDto> callback) {
        Call<TableDto> call = tableService.getTableById(id);
        call.enqueue(callback);
    }

    public void getTableByQrCode(String code, Callback<TableDto> callback) {
        Call<TableDto> call = tableService.getTableByQrCode(code);
        call.enqueue(callback);
    }

    public void getOrdersByTableId(int tableId, Callback<List<OrderDto>> callback) {
        Call<List<OrderDto>> call = tableService.getOrdersByTableId(tableId);
        call.enqueue(callback);
    }

    public void getOrdersByTableIdUsingSearch(int tableId, Callback<List<OrderDto>> callback) {

        OrderSearchDto searchDto = new OrderSearchDto();
        searchDto.setTableId(tableId);
        searchDto.setCreatedAt("today");
        searchDto.setItems(new ArrayList<>());

        Call<List<OrderDto>> call = tableService.searchOrdersByTable(searchDto);

        call.enqueue(callback);
    }

}
