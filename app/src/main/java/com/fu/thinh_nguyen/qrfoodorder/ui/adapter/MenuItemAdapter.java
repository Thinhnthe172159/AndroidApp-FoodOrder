// MenuItemAdapter.java
package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.MenuItemDto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

    private List<MenuItemDto> menuItems;
    private OnMenuItemClickListener listener;
    private Context context;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItemDto menuItem);
    }

    public MenuItemAdapter(OnMenuItemClickListener listener) {
        this.menuItems = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItemDto menuItem = menuItems.get(position);
        holder.bind(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void updateItems(List<MenuItemDto> newItems) {
        this.menuItems.clear();
        if (newItems != null) {
            this.menuItems.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView descriptionTextView;
        private TextView priceTextView;
        private TextView categoryTextView;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.menuItemImage);
            nameTextView = itemView.findViewById(R.id.menuItemName);
            descriptionTextView = itemView.findViewById(R.id.menuItemDescription);
            priceTextView = itemView.findViewById(R.id.menuItemPrice);
            categoryTextView = itemView.findViewById(R.id.menuItemCategory);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onMenuItemClick(menuItems.get(getAdapterPosition()));
                }
            });
        }

        public void bind(MenuItemDto menuItem) {
            nameTextView.setText(menuItem.getName());

            if (menuItem.getDescription() != null && !menuItem.getDescription().isEmpty()) {
                descriptionTextView.setText(menuItem.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            } else {
                descriptionTextView.setVisibility(View.GONE);
            }

            // Format price
            if (menuItem.getPrice() != null) {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                priceTextView.setText(formatter.format(menuItem.getPrice()));
            }

            if (menuItem.getCategoryName() != null) {
                categoryTextView.setText(menuItem.getCategoryName());
                categoryTextView.setVisibility(View.VISIBLE);
            } else {
                categoryTextView.setVisibility(View.GONE);
            }

            // Load image - ĐƠN GIẢN HƠN NHIỀU
            loadMenuItemImage(menuItem);
        }

        private void loadMenuItemImage(MenuItemDto menuItem) {
            String imageUrl = menuItem.getImageUrl();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                // URL đã đầy đủ, không cần tạo full URL
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_food_placeholder)
                        .error(R.drawable.ic_restaurant)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(imageView);
            } else {
                // Use default placeholder
                imageView.setImageResource(R.drawable.ic_restaurant);
            }
        }

    }
}