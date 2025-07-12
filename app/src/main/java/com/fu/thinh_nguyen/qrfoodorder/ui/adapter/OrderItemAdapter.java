package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private List<OrderItemDto> itemList;
    private Context context;

    public OrderItemAdapter(Context context) {
        this.context = context;
        this.itemList = new ArrayList<>();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItemDto item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public void updateItems(List<OrderItemDto> newItems) {
        this.itemList = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName;
        private TextView tvQuantity;
        private TextView tvUnitPrice;
        private TextView tvTotalPrice;
        private TextView tvNotes;
        private TextView tvStatus;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvUnitPrice = itemView.findViewById(R.id.tv_unit_price);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            tvNotes = itemView.findViewById(R.id.tv_notes);
            tvStatus = itemView.findViewById(R.id.tv_status_badge);
        }

        public void bind(OrderItemDto item) {
            // Null check cho product name - sử dụng getMenuItemName()
            tvProductName.setText(item.getMenuItemName() != null ? item.getMenuItemName() : "Món ăn");
            tvQuantity.setText("SL: " + item.getQuantity());

            // Format currency
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

            // Price - chỉ có một giá (price), không có unitPrice và totalPrice riêng
            double price = item.getPrice() != null ? item.getPrice() : 0;
            tvUnitPrice.setText(formatter.format(price) + "đ");

            // Total price = price * quantity
            double totalPrice = price * item.getQuantity();
            tvTotalPrice.setText(formatter.format(totalPrice) + "đ");

            if (item.getNote() != null && !item.getNote().trim().isEmpty()) {
                tvNotes.setText("Ghi chú: " + item.getNote());
                tvNotes.setVisibility(View.VISIBLE);
            } else {
                tvNotes.setVisibility(View.GONE);
            }

            // Status
            tvStatus.setText(getStatusText(item.getStatus()));
            tvStatus.setBackgroundColor(getStatusColor(item.getStatus()));
        }

        private String getStatusText(String status) {
            if (status == null) return "Không xác định";

            switch (status.toLowerCase()) {
                case "pending": return "Chờ xử lý";
                case "serving": return "Chuẩn bị món";
                case "update": return "Cập nhật số lượng";
                case "cancelled": return "Đã huỷ";
                case "paid" : return "Đã thanh toán";
                default: return status;
            }
        }

        private int getStatusColor(String status) {
            if (status == null) return context.getResources().getColor(android.R.color.darker_gray);

            switch (status.toLowerCase()) {
                case "pending":
                    return context.getResources().getColor(android.R.color.holo_orange_light);
                case "preparing":
                    return context.getResources().getColor(android.R.color.holo_blue_light);
                case "update":
                    return context.getResources().getColor(android.R.color.holo_purple);
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
