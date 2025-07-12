package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.api.OrderService;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemStatus;
import com.fu.thinh_nguyen.qrfoodorder.data.network.RetrofitClient;
import com.fu.thinh_nguyen.qrfoodorder.data.prefs.TokenManager;
import com.fu.thinh_nguyen.qrfoodorder.ui.customer.ViewOrderItemDetailActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderItemAdapter2 extends RecyclerView.Adapter<OrderItemAdapter2.ItemViewHolder> {

    public interface OnOrderItemChangedListener {
        void onItemChanged();
    }

    private final int orderId;
    private final List<OrderItemDto> itemList;
    private final Activity parentActivity;
    private final TokenManager tokenManager;
    private final OrderService orderService;
    private final OnOrderItemChangedListener itemChangedListener;

    public OrderItemAdapter2(int orderId, List<OrderItemDto> itemList, Activity parentActivity,
                             TokenManager tokenManager, OnOrderItemChangedListener itemChangedListener) {
        this.orderId = orderId;
        this.itemList = itemList != null ? itemList : new ArrayList<>();
        this.parentActivity = parentActivity;
        this.tokenManager = tokenManager;
        this.itemChangedListener = itemChangedListener;
        this.orderService = RetrofitClient.getInstance(tokenManager).create(OrderService.class);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        OrderItemDto item = itemList.get(position);

        holder.txtName.setText(item.getMenuItemName());

        // Hiển thị số lượng
        holder.txtQuantity.setText("Số lượng: x" + item.getQuantity());

        holder.txtPrice.setText("Giá: " + formatVND((double)item.getPrice()));

        holder.txtTime.setText(OrderItemStatus.getStatusText(item.getStatus()));

        // Load ảnh
        Glide.with(holder.imgFood.getContext())
                .load(item.getImage())
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .into(holder.imgFood);

        // Ẩn hoặc hiển thị nút hủy/track theo trạng thái
        boolean isPending = true;
        if(item.getStatus().equalsIgnoreCase(OrderItemStatus.PENDING) || item.getStatus().equalsIgnoreCase(OrderItemStatus.Update)) {
            isPending = true;
        } else {
            isPending = false;
        }

            holder.btnCancel.setVisibility(isPending ? View.VISIBLE : View.GONE);
            holder.btnTrack.setVisibility(isPending ? View.VISIBLE : View.GONE);

        holder.btnCancel.setOnClickListener(v -> doDelete(orderId, item));

        holder.btnTrack.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ViewOrderItemDetailActivity.class);
            intent.putExtra("ORDER_ITEM", item);
            parentActivity.startActivityForResult(intent, 1001);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    private String formatVND(Double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " đ";
    }

    private void doDelete(int orderId, OrderItemDto item) {
        new AlertDialog.Builder(parentActivity)
                .setTitle("Xác nhận huỷ món")
                .setMessage("Bạn có chắc muốn huỷ món '" + item.getMenuItemName() + "' không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    orderService.removeItem(orderId, item.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                int position = itemList.indexOf(item);
                                if (position != -1) {
                                    itemList.remove(position);
                                    notifyItemRemoved(position);
                                }
                                Toast.makeText(parentActivity, "Đã huỷ món", Toast.LENGTH_SHORT).show();
                                if (itemChangedListener != null) {
                                    itemChangedListener.onItemChanged(); // cập nhật lại total
                                }
                            } else {
                                Toast.makeText(parentActivity, "Huỷ món thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(parentActivity, "Lỗi kết nối. Huỷ món thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtQuantity, txtTime;
        ImageView imgFood;
        Button btnCancel, btnTrack;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtItemName);
            txtPrice = itemView.findViewById(R.id.txtItemPrice);
            txtQuantity = itemView.findViewById(R.id.txtItemQuantity);
            txtTime = itemView.findViewById(R.id.txtItemTime);
            imgFood = itemView.findViewById(R.id.imgFood);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnTrack = itemView.findViewById(R.id.btnTrack);
        }
    }
}
