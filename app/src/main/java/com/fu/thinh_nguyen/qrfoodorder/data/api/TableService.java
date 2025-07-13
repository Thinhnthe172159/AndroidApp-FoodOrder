package com.fu.thinh_nguyen.qrfoodorder.data.api;

import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderSearchDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.TableDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.TableStatusUpdateDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
public interface TableService {
    // TABLE APIs
    @POST("api/table")
    Call<TableDto> createTable(@Body TableDto dto);
    @GET("api/table/{id}")
    Call<TableDto> getTableById(@Path("id") int id);
    @GET("api/table/qrcode/{code}")
    Call<TableDto> getTableByQrCode(@Path("code") String code);
    @PUT("api/table/{id}/status")
    Call<Void> updateTableStatus(@Path("id") int id, @Body TableStatusUpdateDto dto);

    // -----------------------------------------------------

    @GET("api/table")
    Call<List<TableDto>> getTables();
    @GET("api/order/table/{tableId}")
    Call<List<OrderDto>> getOrdersByTableId(@Path("tableId") int tableId);
    @POST("api/order/SearchOrder")
    Call<List<OrderDto>> searchOrdersByTable(@Body OrderSearchDto searchDto);
}
