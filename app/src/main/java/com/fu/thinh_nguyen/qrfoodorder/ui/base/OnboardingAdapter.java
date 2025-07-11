package com.fu.thinh_nguyen.qrfoodorder.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.OnboardingItem;
import com.fu.thinh_nguyen.qrfoodorder.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private Context context;
    private List<OnboardingItem> items;

    public OnboardingAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
        items.add(new OnboardingItem(R.drawable.img1, "Order For Food", "Lorem ipsum dolor..."));
        items.add(new OnboardingItem(R.drawable.img2, "Easy Payment", "Lorem ipsum dolor..."));
        items.add(new OnboardingItem(R.drawable.img3, "Fast Delivery", "Lorem ipsum dolor..."));
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnboardingItem item = items.get(position);
        holder.imageView.setImageResource(item.getImage());
        holder.title.setText(item.getTitle());
        holder.desc.setText(item.getDescription());

        if (position == items.size() - 1) {
            holder.nextBtn.setText("Get Started");
            holder.nextBtn.setOnClickListener(v -> {
                context.startActivity(new Intent(context, MainActivity.class));
                ((Activity) context).finish();
            });
        } else {
            holder.nextBtn.setOnClickListener(v -> {
                ((OnboardingActivity) context).viewPager.setCurrentItem(position + 1);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, desc;
        Button nextBtn;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            nextBtn = itemView.findViewById(R.id.btnNext);
        }
    }
}
