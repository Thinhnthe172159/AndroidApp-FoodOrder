package com.fu.thinh_nguyen.qrfoodorder.data.repository;

import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;

import retrofit2.Call;

public class OrderStaffRepository {
    private final OrderService orderService;

    public OrderStaffRepository(TokenManager tokenManager) {
        orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);
    }

    public Call<Void> confirmOrder(int orderId) {
        return orderService.confirmOrder(orderId);
    }

    public Call<Void> markOrderAsPaid(int orderId) {
        return orderService.markOrderAsPaid(orderId);
    }
}
