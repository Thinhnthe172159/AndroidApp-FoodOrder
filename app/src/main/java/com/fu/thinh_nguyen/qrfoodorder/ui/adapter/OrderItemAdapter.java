package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;

import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ItemViewHolder> {

    private final List<OrderItemDto> itemList;

    public OrderItemAdapter(List<OrderItemDto> itemList) {
        this.itemList = itemList != null ? itemList : new ArrayList<>();
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
        holder.txtPrice.setText(item.getPrice() + "đ");
        holder.txtQuantity.setText(item.getQuantity() + " món");


        // Nếu có ảnh, load ảnh vào imgFood, nếu không thì giữ placeholder
        // Glide.with(holder.imgFood.getContext()).load(item.getImageUrl()).into(holder.imgFood);

        // Nếu có ghi chú thì xử lý nếu muốn hiển thị thêm

//        holder.btnCancel.setOnClickListener(v -> {
//            // TODO: xử lý huỷ món
//        });
//
//        holder.btnTrack.setOnClickListener(v -> {
//            // TODO: cập nhật trạng thái món hoặc mở chi tiết
//        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtQuantity, txtTime;

        //Button btnCancel, btnTrack;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtItemName);
            txtPrice = itemView.findViewById(R.id.txtItemPrice);
            txtQuantity = itemView.findViewById(R.id.txtItemQuantity);
            txtTime = itemView.findViewById(R.id.txtItemTime);
//            btnCancel = itemView.findViewById(R.id.btnCancel);
//            btnTrack = itemView.findViewById(R.id.btnTrack);
        }
    }
}

