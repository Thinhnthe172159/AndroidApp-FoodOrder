package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

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
import com.fu.thinh_nguyen.qrfoodorder.data.model.OrderItemDto;

import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter2 extends RecyclerView.Adapter<OrderItemAdapter2.ItemViewHolder> {

    private final List<OrderItemDto> itemList;

    public OrderItemAdapter2(List<OrderItemDto> itemList) {
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
        Glide.with(holder.imgFood.getContext())
                .load(item.getImage()) // URL ảnh từ API
                .placeholder(R.drawable.ic_food_placeholder)
                .error(R.drawable.ic_food_placeholder)
                .into(holder.imgFood);

        if(!item.getStatus().equals("Pending")){
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnTrack.setVisibility(View.GONE);
        }

        holder.btnCancel.setOnClickListener(v -> {
            Toast.makeText(holder.btnCancel.getContext(), "clicked: "+item.getMenuItemName(), Toast.LENGTH_SHORT).show();
        });
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
