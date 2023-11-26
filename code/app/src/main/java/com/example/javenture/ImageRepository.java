package com.example.javenture;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles the upload and deletion of images to/from storage
 */
public class ImageRepository {
    private FirebaseStorage storage;
    private AuthenticationService authService;
    private final String TAG = "ImageRepository";

    public ImageRepository() {
        storage = FirebaseStorage.getInstance();
        authService = new AuthenticationService();
    }

    public ImageRepository(FirebaseStorage storage, AuthenticationService authService) {
        this.storage = storage;
        this.authService = authService;
    }

    public interface OnUploadListener {
        void onUpload(List<ImageItem> remoteImageItems);
        void onFailure();
    }

    /**
     * Upload a list of images to storage
     * @param imageItems list of ImageItem objects to be uploaded
     * @param listener listener for the upload event
     */
    public void uploadImages(List<ImageItem> imageItems, OnUploadListener listener) {
        FirebaseUser user = authService.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "user is null");
            listener.onFailure();
            return;
        }
        List<ImageItem> remoteImageItems = new ArrayList<>();
        if (imageItems.size() == 0) {
            listener.onUpload(remoteImageItems);
            return;
        }
        AtomicInteger uploadCount = new AtomicInteger(0);
        for (ImageItem image : imageItems) {
            if (!image.isLocal()) {
                remoteImageItems.add(image);
                if (uploadCount.incrementAndGet() == imageItems.size()) {
                    // all images uploaded
                    listener.onUpload(remoteImageItems);
                }
                continue;
            }
            StorageReference imageRef = storage.getReference().child(user.getUid()).child(UUID.randomUUID().toString());
            imageRef.putFile(image.getLocalUri())
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d(TAG, "image uploaded");
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Log.d(TAG, "download url: " + uri.toString());
                                    image.setRemoteUrl(uri.toString());
                                    remoteImageItems.add(image);
                                    if (uploadCount.incrementAndGet() == imageItems.size()) {
                                        // all images uploaded
                                        listener.onUpload(remoteImageItems);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "failed to get download url");
                                    listener.onFailure();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "failed to upload image");
                        listener.onFailure();
                    });
        }

    }

    /**
     * Delete a list of images from storage
     * @param remotePhotoUrls list of remote photo urls to be deleted from storage
     */
    public void deleteImagesUsingUrls(List<String> remotePhotoUrls) {
        FirebaseUser user = authService.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "user is null");
            return;
        }
        for (String url : remotePhotoUrls) {
            StorageReference imageRef = storage.getReferenceFromUrl(url);
            imageRef.delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "image deleted"))
                    .addOnFailureListener(e -> Log.e(TAG, "failed to delete image"));
        }
    }

    /**
     * Delete a list of images from storage
     * @param imageItems list of ImageItem objects to be deleted from storage
     */
    public void deleteImages(List<ImageItem> imageItems) {
        List<String> remotePhotoUrls = new ArrayList<>();
        for (ImageItem imageItem : imageItems) {
            if (!imageItem.isLocal()) {
                remotePhotoUrls.add(imageItem.getRemoteUrl());
            }
        }
        this.deleteImagesUsingUrls(remotePhotoUrls);
    }
}
