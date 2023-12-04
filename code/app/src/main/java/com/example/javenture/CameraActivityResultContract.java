package com.example.javenture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Starts CameraActivity and returns the image taken
 */
public class CameraActivityResultContract extends ActivityResultContract<Void, ImageItem> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void input) {
        // Create an Intent that starts CameraActivity
        return new Intent(context, CameraActivity.class);
    }

    @Override
    public ImageItem parseResult(int resultCode, @Nullable Intent intent) {
        // Check if the result was OK and the Intent is not null
        if (resultCode == Activity.RESULT_OK && intent != null) {
            // Extract the Uri from the Intent
            return intent.getParcelableExtra("imageItem", ImageItem.class);
        }
        return null;
    }
}
