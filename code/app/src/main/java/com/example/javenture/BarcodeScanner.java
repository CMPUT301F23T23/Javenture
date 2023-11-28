package com.example.javenture;

import android.util.Log;

import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

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

    public interface OnCompleteListener {
        void onSuccess(String barcodeValue);
        void onFailure(Exception e);
    }
}
