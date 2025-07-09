package com.fu.thinh_nguyen.qrfoodorder.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fu.thinh_nguyen.qrfoodorder.R;
import com.fu.thinh_nguyen.qrfoodorder.data.model.TableDto;

import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private List<TableDto> tableList;
    private Context context;
    private OnTableClickListener onTableClickListener;

    public interface OnTableClickListener {
        void onTableClick(TableDto table);
    }

    public TableAdapter(Context context, List<TableDto> tableList, OnTableClickListener listener) {
        this.context = context;
        this.tableList = tableList;
        this.onTableClickListener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        TableDto table = tableList.get(position);
        holder.bind(table);
    }

    @Override
    public int getItemCount() {
        return tableList != null ? tableList.size() : 0;
    }

    public void updateTables(List<TableDto> newTables) {
        this.tableList = newTables;
        notifyDataSetChanged();
    }

    class TableViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTableNumber;
        private TextView tvStatus;
        private View statusIndicator;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTableNumber = itemView.findViewById(R.id.tv_table_number);
            tvStatus = itemView.findViewById(R.id.tv_status);
            statusIndicator = itemView.findViewById(R.id.status_indicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onTableClickListener != null) {
                    onTableClickListener.onTableClick(tableList.get(position));
                }
            });
        }

        public void bind(TableDto table) {
            tvTableNumber.setText("Bàn " + table.getTableNumber());
            tvStatus.setText(getStatusText(table.getStatus()));

            // Đổi màu theo trạng thái
            int statusColor = getStatusColor(table.getStatus());
            statusIndicator.setBackgroundColor(statusColor);
        }

        private String getStatusText(String status) {
            switch (status.toLowerCase()) {
                case "available": return "Trống";
                case "occupied": return "Có khách";
                case "reserved": return "Đã đặt";
                default: return status;
            }
        }

        private int getStatusColor(String status) {
            switch (status.toLowerCase()) {
                case "available": return context.getResources().getColor(android.R.color.holo_green_light);
                case "occupied": return context.getResources().getColor(android.R.color.holo_red_light);
                case "reserved": return context.getResources().getColor(android.R.color.holo_orange_light);
                default: return context.getResources().getColor(android.R.color.darker_gray);
            }
        }
    }
}

