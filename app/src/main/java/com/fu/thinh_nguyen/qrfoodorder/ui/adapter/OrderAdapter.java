package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderDto;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderDto> orderList;
    private Context context;
    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderDto order);
    }

    public OrderAdapter(Context context, List<OrderDto> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.onOrderClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDto order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void updateOrders(List<OrderDto> newOrders) {
        this.orderList = newOrders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId;
        private TextView tvCustomerName;
        private TextView tvTableName;
        private TextView tvStaffName;
        private TextView tvCreatedAt;
        private TextView tvPaidAt;
        private TextView tvStatus;
        private TextView tvTotalAmount;
        private TextView tvItemCount;
        private RecyclerView rvOrderItems;
        private OrderItemAdapter orderItemAdapter;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvTableName = itemView.findViewById(R.id.tv_table_name);
            tvStaffName = itemView.findViewById(R.id.tv_staff_name);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            tvPaidAt = itemView.findViewById(R.id.tv_paid_at);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            rvOrderItems = itemView.findViewById(R.id.rv_order_items);

            // Setup RecyclerView for order items
            rvOrderItems.setLayoutManager(new LinearLayoutManager(context));
            orderItemAdapter = new OrderItemAdapter(context);
            rvOrderItems.setAdapter(orderItemAdapter);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onOrderClickListener != null) {
                    onOrderClickListener.onOrderClick(orderList.get(position));
                }
            });
        }

        public void bind(OrderDto order) {
            tvOrderId.setText("Đơn #" + order.getId());

            // Customer info
            if (order.getCustomerName() != null && !order.getCustomerName().isEmpty()) {
                tvCustomerName.setText(order.getCustomerName());
                tvCustomerName.setVisibility(View.VISIBLE);
            } else {
                tvCustomerName.setVisibility(View.GONE);
            }

            // Table info
            if (order.getTableName() != null && !order.getTableName().isEmpty()) {
                tvTableName.setText("Bàn: " + order.getTableName());
                tvTableName.setVisibility(View.VISIBLE);
            } else {
                tvTableName.setVisibility(View.GONE);
            }

            // Staff info
            if (order.getStaffName() != null && !order.getStaffName().isEmpty()) {
                tvStaffName.setText("NV: " + order.getStaffName());
                tvStaffName.setVisibility(View.VISIBLE);
            } else {
                tvStaffName.setVisibility(View.GONE);
            }

            // Dates
            tvCreatedAt.setText("Tạo: " + formatDate(order.getCreatedAt()));

            if (order.getPaidAt() != null && !order.getPaidAt().isEmpty()) {
                tvPaidAt.setText("Thanh toán: " + formatDate(order.getPaidAt()));
                tvPaidAt.setVisibility(View.VISIBLE);
            } else {
                tvPaidAt.setVisibility(View.GONE);
            }

            // Status
            tvStatus.setText(getStatusText(order.getStatus()));
            tvStatus.setBackgroundColor(getStatusColor(order.getStatus()));

            // Total amount
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvTotalAmount.setText(formatter.format(order.getTotalAmount() != null ? order.getTotalAmount() : 0));

            // Order items
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                tvItemCount.setText(order.getItems().size() + " món");
                orderItemAdapter.updateItems(order.getItems());
                rvOrderItems.setVisibility(View.VISIBLE);
            } else {
                tvItemCount.setText("0 món");
                rvOrderItems.setVisibility(View.GONE);
            }
        }

        private String formatDate(String dateString) {
            try {
                // Assuming the date format from API is ISO 8601
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateString; // Return original if parsing fails
            }
        }

        private String getStatusText(String status) {
            if (status == null) return "Không xác định";

            switch (status.toLowerCase()) {
                case "pending": return "Chờ xử lý";
                case "confirmed": return "Đã xác nhận";
                case "preparing": return "Đang chuẩn bị";
                case "ready": return "Sẵn sàng";
                case "completed": return "Hoàn thành";
                case "cancelled": return "Đã hủy";
                case "paid": return "Đã thanh toán";
                default: return status;
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return context.getResources().getColor(android.R.color.darker_gray);

            switch (status.toLowerCase()) {
                case "pending":
                    return context.getResources().getColor(android.R.color.holo_orange_light);
                case "confirmed":
                    return context.getResources().getColor(android.R.color.holo_blue_light);
                case "preparing":
                    return context.getResources().getColor(android.R.color.holo_purple);
                case "ready":
                    return context.getResources().getColor(android.R.color.holo_green_light);
                case "completed":
                case "paid":
                    return context.getResources().getColor(android.R.color.holo_green_dark);
                case "cancelled":
                    return context.getResources().getColor(android.R.color.holo_red_light);
                default:
                    return context.getResources().getColor(android.R.color.darker_gray);
            }
        }
    }
}
