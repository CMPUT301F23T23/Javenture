package com.example.javenture;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraFragment";
    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private ImageCapture imageCapture;

    private PreviewView previewView;
    private ImageButton captureButton;
    private ImageButton flipButton;
    private ImageButton galleryButton;
    private ImageButton backButton;
    private CameraSelector cameraSelector ;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.preview_view);
        captureButton = findViewById(R.id.capture_button);
        flipButton = findViewById(R.id.flip_button);
        galleryButton = findViewById(R.id.gallery_button);
        backButton = findViewById(R.id.back_button);

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d(TAG, "result: " + uri.toString());
                returnResult(uri);
            } else {
                Log.e(TAG, "result: null");
            }
        });

        captureButton.setOnClickListener(v -> {
            imageCapture.takePicture(
                    createOutputFileOptions(),
                    ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            // Handle success
                            Log.d(TAG, outputFileResults.getSavedUri().toString());
                            returnResult(outputFileResults.getSavedUri());
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e(TAG, "Failed to save image", exception);
                        }
                    }
            );
        });
        flipButton.setOnClickListener(v -> {
            // flip camera from back to front
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
            } else {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
            }
            startCamera();
        });
        galleryButton.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        backButton.setOnClickListener(v -> {
            finish();
        });

        checkPermissions();

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
            requestPermissions();
        } else {
            startCamera();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES},
                REQUEST_PERMISSIONS_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0) {
                boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readExternalStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (cameraPermission && readExternalStoragePermission) {
                    // Permissions are granted.
                    Log.d(TAG, "Permissions are granted");
                    startCamera();
                } else {
                    // Permissions are denied.
                    Log.d(TAG, "Permissions are not granted");
                }
            }
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                imageCapture = new ImageCapture.Builder().build();

                cameraProvider.unbindAll();     // unbind use cases before rebinding
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (Exception e) {
                Log.e(TAG, "Failed to get camera provider", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private ImageCapture.OutputFileOptions createOutputFileOptions() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = timeStamp;
        File photoFile = new File(storageDir, imageFileName + ".jpg");

        return new ImageCapture.OutputFileOptions.Builder(photoFile).build();
    }

    private void returnResult(Uri imageUri) {
        ImageItem imageItem = new ImageItem(imageUri);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("imageItem", imageItem);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
