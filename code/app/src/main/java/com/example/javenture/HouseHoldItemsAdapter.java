package com.example.javenture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class HouseHoldItemsAdapter extends RecyclerView.Adapter<HouseHoldItemsAdapter.HouseHoldItemsViewHolder>{

    private HouseHoldItemViewModel houseHoldItemViewModel;
    private Context context;
    private OnItemClickListener listener;

    public HouseHoldItemsAdapter(Context context, HouseHoldItemViewModel houseHoldItemViewModel) {
        this.houseHoldItemViewModel = houseHoldItemViewModel;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HouseHoldItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false);
        return new HouseHoldItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseHoldItemsViewHolder holder, int position) {
        HouseHoldItem houseHoldItem = houseHoldItemViewModel.getHouseHoldItems().getValue().get(position);
        holder.description.setText(houseHoldItem.getDescription());
        holder.make.setText(houseHoldItem.getMake());
        holder.datePurchased.setText(houseHoldItem.getDatePurchased().toString());
        holder.price.setText(String.format("%.2f", houseHoldItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(houseHoldItemViewModel.getHouseHoldItems().getValue()).size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class HouseHoldItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView description;
        TextView make;
        TextView datePurchased;
        TextView price;

        HouseHoldItemsViewHolder(View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            make = itemView.findViewById(R.id.make);
            datePurchased = itemView.findViewById(R.id.date_purchased);
            price = itemView.findViewById(R.id.price);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(pos);
            }
        }
    }


}

