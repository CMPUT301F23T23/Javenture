package com.example.javenture;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<ImageItem> imageItems;
    private Context context;

    public ImageAdapter(Context context, List<ImageItem> imageItems) {
        this.context = context;
        this.imageItems = imageItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                                  .inflate(R.layout.image_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem imageItem = imageItems.get(position);
        if (imageItem.isLocal()) {
            holder.imageView.setImageURI(imageItem.getLocalUri());
            return;
        }
        Glide.with(context)
             .load(imageItem.getRemoteUrl())
             .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_view);
            imageView.setOnClickListener(v -> showImageDialog(getAdapterPosition()));
        }
    }

    private void showImageDialog(int pos) {
        ImageItem imageItem = imageItems.get(pos);

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image_enlarged);
        ImageView dialogImageView = dialog.findViewById(R.id.dialog_image_view);
        if (imageItem.isLocal()) {
            dialogImageView.setImageURI(imageItem.getLocalUri());
        } else {
            Glide.with(context)
                 .load(imageItem.getRemoteUrl())
                 .into(dialogImageView);
        }

        Button deleteButton = dialog.findViewById(R.id.delete_image_button);
        deleteButton.setOnClickListener(v -> {
            imageItems.remove(pos);
            notifyItemRemoved(pos);
            dialog.dismiss();
        });

        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }
}
