package com.example.javenture;

import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

/**
 * Scans an image for a serial number
 */
public class SerialNumberScanner {
    private static final String TAG = "SerialNumberScanner";
    private TextRecognizer recognizer;
    private InputImage image;

    public SerialNumberScanner(InputImage image) {
        this.image = image;
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    /**
     * Scans the image for a serial number
     * @param listener callback for when the scan is complete
     */
    public void scan(OnCompleteListener listener) {
        recognizer.process(image).addOnSuccessListener(visionText -> {
            String serialNumber = parseFromResult(visionText);
            Log.d(TAG, "serial number: " + serialNumber);
            listener.onSuccess(serialNumber);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "failed to recognize text");
            listener.onFailure(e);
        });
    }

    /**
     * Parses the serial number from the result of the text recognition
     * @param resultText the result of the text recognition
     * @return the serial number
     */
    private String parseFromResult(Text resultText) {
        for (Text.TextBlock block : resultText.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                Log.d(TAG, "line: " + line.getText());
                for (Text.Element element : line.getElements()) {
                    String text = element.getText();
                    Log.d(TAG, "text: " + text);
                    if (text.matches("^(?=(?:[^A-Za-z]*[A-Za-z]){3})(?=(?:\\D*\\d){3})[A-Za-z\\d]+$")) {
                        // at least 3 letters and 3 digits
                        return text;
                    }
                }
            }
        }
        return "";
    }

    /**
     * Listener for the scan event
     */
    public interface OnCompleteListener {
        void onSuccess(String serialNumber);
        void onFailure(Exception e);
    }

}
