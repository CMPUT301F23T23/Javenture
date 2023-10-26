package com.example.javenture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class HouseHoldItemsAdapter extends RecyclerView.Adapter<HouseHoldItemsAdapter.HouseHoldItemsViewHolder>{

    private HouseHoldItemViewModel houseHoldItemViewModel;
    private Context context;
    private OnItemClickListener itemClickListener;
    private MultiSelectionModeListener multiSelectionModeListener;
    private Set<Integer> selectedItems = new HashSet<>();
    private boolean isInMultiSelectMode = false;

    public HouseHoldItemsAdapter(Context context, HouseHoldItemViewModel houseHoldItemViewModel) {
        this.houseHoldItemViewModel = houseHoldItemViewModel;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setMultiSelectionModeListener(MultiSelectionModeListener listener) {
        this.multiSelectionModeListener = listener;
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

        if (isInMultiSelectMode) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(isSelected(position));
        } else {
            holder.checkbox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(houseHoldItemViewModel.getHouseHoldItems().getValue()).size();
    }

    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
    }

    public boolean isSelected(int position) {
        return selectedItems.contains(position);
    }

    public void enterMultiSelectMode() {
        isInMultiSelectMode = true;
        selectedItems.clear();
        notifyDataSetChanged();
        multiSelectionModeListener.onMultiSelectionModeEnter();
    }

    public List<HouseHoldItem> getSelectedItems() {
        ArrayList<HouseHoldItem> items = new ArrayList<>();
        for (int i : selectedItems) {
            items.add(houseHoldItemViewModel.getHouseHoldItem(i));
        }
        return items;
    }

    public void exitMultiSelectionMode() {
        isInMultiSelectMode = false;
        selectedItems.clear();
        notifyDataSetChanged();
        multiSelectionModeListener.onMultiSelectionModeExit();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface MultiSelectionModeListener {
        void onMultiSelectionModeEnter();
        void onMultiSelectionModeExit();
    }

    class HouseHoldItemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView description;
        TextView make;
        TextView datePurchased;
        TextView price;
        CheckBox checkbox;

        HouseHoldItemsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            description = itemView.findViewById(R.id.description);
            make = itemView.findViewById(R.id.make);
            datePurchased = itemView.findViewById(R.id.date_purchased);
            price = itemView.findViewById(R.id.price);
            checkbox = itemView.findViewById(R.id.checkbox);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        enterMultiSelectMode();
                        toggleSelection(position);
                    }
                    return true;
                }
            });
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && itemClickListener != null) {
                if (isInMultiSelectMode) {
                    toggleSelection(pos);
                } else {
                    itemClickListener.onItemClick(pos);
                }
            }
        }
    }


}

