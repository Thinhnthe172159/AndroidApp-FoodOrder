package com.fu.thinh_nguyen.qrfoodorder.data.api.table;

import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderSearchDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.TableDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TableService {
    @GET("api/table")
    Call<List<TableDto>> getTables();

    @GET("api/table/{id}")
    Call<TableDto> getTableById(@Path("id") int id);

    @GET("api/table/qrcode/{code}")
    Call<TableDto> getTableByQrCode(@Path("code") String code);

    @GET("api/order/table/{tableId}")
    Call<List<OrderDto>> getOrdersByTableId(@Path("tableId") int tableId);


    // Option 2: Sử dụng SearchOrder endpoint hiện có
    @POST("api/order/SearchOrder")
    Call<List<OrderDto>> searchOrdersByTable(@Body OrderSearchDto searchDto);
}
