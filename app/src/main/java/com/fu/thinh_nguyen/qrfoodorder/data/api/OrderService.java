package com.fu.thinh_nguyen.qrfoodorder.data.api;

import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderCreateDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemCreateDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface OrderService {

    /* ------------------------------------------------------------------
     * 1.   ORDER
     * ---------------------------------------------------------------- */

    @GET("api/order")
    Call<List<OrderDto>> getOrders(
            @Query("customerId") Integer customerId,
            @Query("status") String status
    );

    @GET("api/order/{id}")
    Call<OrderDto> getOrderById(@Path("id") int id);

    @POST("api/order")
    Call<OrderDto> createOrder(@Body OrderCreateDto dto);

    @PUT("api/order/{id}/status")
    Call<Void> updateOrderStatus(@Path("id") int id, @Body String status);

    @POST("api/order/Cancel/{id}")
    Call<Void> cancelOrder(@Path("id") int id);

    @POST("api/order/SearchOrder")
    Call<List<OrderDto>> searchOrder(@Body OrderDto dto);

    @POST("api/order/getMyCurrentOrder")
    Call<List<OrderDto>> getMyCurrentOrder(@Header("Authorization") String token);

    @POST("api/order/Confirm_order")
    Call<Void> confirmOrder(
            @Query("id") int orderId
    );

    @POST("api/order/MarkAsPaid/{id}")
    Call<Void> markOrderAsPaid(@Path("id") int orderId);


    /* ------------------------------------------------------------------
     * 2.   ORDER ITEM
     * ---------------------------------------------------------------- */

    @GET("api/order/{orderId}/items")
    Call<List<OrderItemCreateDto>> getItemsByOrderId(@Path("orderId") int orderId);

    @POST("api/order/{orderId}/items")
    Call<Boolean> addItem(
            @Path("orderId") int orderId,
            @Body OrderItemCreateDto dto
    );

    @POST("api/order/UpdateQuantityOrderItem")
    Call<Boolean> updateQuantityOrderItem(@Body OrderItemDto dto);

    @DELETE("api/order/{orderId}/items/{itemId}")
    Call<Void> removeItem(
            @Path("orderId") int orderId,
            @Path("itemId") int itemId
    );

    @GET("api/order/CheckPaid/{id}")
    Call<String> checkPaid(@Path("id") int orderId);
}
