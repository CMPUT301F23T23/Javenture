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

/**
 * This class is used to display the list of HouseHoldItem objects in a RecyclerView.
 */
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

    /**
     * Set the listener for the item click event
     * @param listener listener for the item click event
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * Set the listener for the multi selection mode
     * @param listener listener for the multi selection mode
     */
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

    /**
     * Toggle the selection of an item
     * @param position position of the item
     */
    private void toggleSelection(int position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
        notifyItemChanged(position);
    }

    /**
     * Check if an item is selected
     * @param position position of the item
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) {
        return selectedItems.contains(position);
    }

    /**
     * Enter multi selection mode
     */
    public void enterMultiSelectMode() {
        isInMultiSelectMode = true;
        selectedItems.clear();
        notifyDataSetChanged();
        multiSelectionModeListener.onMultiSelectionModeEnter();
    }

    /**
     * Get the list of selected items
     * @return list of selected items
     */
    public List<HouseHoldItem> getSelectedItems() {
        ArrayList<HouseHoldItem> items = new ArrayList<>();
        for (int i : selectedItems) {
            items.add(houseHoldItemViewModel.getHouseHoldItem(i));
        }
        return items;
    }

    /**
     * Exit multi selection mode
     */
    public void exitMultiSelectionMode() {
        isInMultiSelectMode = false;
        selectedItems.clear();
        notifyDataSetChanged();
        multiSelectionModeListener.onMultiSelectionModeExit();
    }

    /**
     * Interface for the item click event
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Interface for the multi selection mode
     */
    public interface MultiSelectionModeListener {
        void onMultiSelectionModeEnter();
        void onMultiSelectionModeExit();
    }

    /**
     * This class is used to store the views of the item in the RecyclerView
     */
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

            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        toggleSelection(position);
                    }
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

