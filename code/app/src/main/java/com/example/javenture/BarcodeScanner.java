package com.example.javenture;

import android.util.Log;

import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

/**
 * Scans barcodes from an image
 */
public class BarcodeScanner {
    private static final String TAG = "BarcodeScanner";
    private InputImage image;
    private com.google.mlkit.vision.barcode.BarcodeScanner scanner;

    public BarcodeScanner(InputImage image) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                        .build();
        this.image = image;
        scanner = BarcodeScanning.getClient(options);
    }

    /**
     * Scans the image for a barcode and returns the barcode value
     * @param listener callback for when the barcode is found or not found
     */
    public void scan(OnCompleteListener listener) {
        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes.size() == 0) {
                        listener.onSuccess("");
                        return;
                    }
                    for (Barcode barcode : barcodes) {
                        Log.d(TAG, "scan: " + barcode.getRawValue());
                        Log.d(TAG, "type: " + barcode.getValueType());
                    }
                    Barcode barcode = barcodes.get(0);
                    listener.onSuccess(barcode.getRawValue());
                })
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * Callback for when the barcode is found or not found
     */
    public interface OnCompleteListener {
        void onSuccess(String barcodeValue);
        void onFailure(Exception e);
    }
}
