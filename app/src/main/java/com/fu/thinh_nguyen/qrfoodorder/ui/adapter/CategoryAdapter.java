package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.CategoryDto;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryDto> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = -1;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryDto category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.categories = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryDto category = categories.get(position);
        holder.bind(category, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(List<CategoryDto> newCategories) {
        this.categories.clear();
        if (newCategories != null) {
            this.categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int previousPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousPosition);
        notifyItemChanged(selectedPosition);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private View categoryContainer;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryContainer = itemView.findViewById(R.id.categoryContainer);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    setSelectedPosition(getAdapterPosition());
                    listener.onCategoryClick(categories.get(getAdapterPosition()));
                }
            });
        }

        public void bind(CategoryDto category, boolean isSelected) {
            categoryName.setText(category.getName());

            // Highlight selected category
            if (isSelected) {
                categoryContainer.setBackgroundResource(R.drawable.category_selected_background);
            } else {
                categoryContainer.setBackgroundResource(R.drawable.category_normal_background);
            }
        }
    }
}
