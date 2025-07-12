package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.StatusOrder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.OrderViewHolder> {

    private final boolean readonly;
    private List<OrderDto> orderList;
    private final OnOrderClickListener listener;
    private final OrderService orderService;
    private final Context context;
    private final Runnable refreshList;

    public interface OnOrderClickListener {
        void onOrderClick(OrderDto order);
    }

    public CustomerOrderAdapter(List<OrderDto> orderList,
                                OnOrderClickListener listener,
                                Context context,
                                OrderService orderService,
                                Runnable refreshList,
                                boolean readonly) {
        this.orderList = orderList;
        this.listener = listener;
        this.context = context;
        this.orderService = orderService;
        this.refreshList = refreshList;
        this.readonly = readonly;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_customer, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDto order = orderList.get(position);
        holder.bind(order, listener, orderService, context, refreshList, readonly);
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void setOrderList(List<OrderDto> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderStatus;
        Button btnCancelOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            btnCancelOrder = itemView.findViewById(R.id.btnCancelOrder);
        }

        public void bind(OrderDto order,
                         OnOrderClickListener listener,
                         OrderService orderService,
                         Context context,
                         Runnable refreshList,
                         boolean readonly) {

            txtOrderId.setText("Đơn đặt bàn số: " + order.getId());
            txtOrderStatus.setText("Trạng thái: " + StatusOrder.getStatus(order.getStatus()));
            itemView.setOnClickListener(v -> listener.onOrderClick(order));

            if (readonly) {
                btnCancelOrder.setVisibility(View.GONE);
                return;
            }

            if (!"pending".equalsIgnoreCase(order.getStatus())) {
                btnCancelOrder.setVisibility(View.GONE);
            } else {
                btnCancelOrder.setVisibility(View.VISIBLE);
                btnCancelOrder.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Xác nhận huỷ đơn")
                            .setMessage("Bạn có chắc muốn huỷ đơn #" + order.getId() + " không?")
                            .setPositiveButton("Đồng ý", (dialog, which) -> {
                                orderService.cancelOrder(order.getId()).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Đã huỷ đơn thành công", Toast.LENGTH_SHORT).show();
                                            if (refreshList != null) refreshList.run();
                                        } else {
                                            Toast.makeText(context, "Không thể huỷ đơn", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(context, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .setNegativeButton("Huỷ", null)
                            .show();
                });
            }
        }
    }
}
