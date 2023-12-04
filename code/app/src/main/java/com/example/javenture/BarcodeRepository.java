package com.example.javenture;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Interacts with the database to search for items that match with the barcodes
 */
public class BarcodeRepository {
    private FirebaseFirestore db;

    public BarcodeRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Given a string of barcode, get an item that match with the barcode
     * @param barcode barcode string
     * @param listener callback for when the item is found or not found
     */
    public void getHouseHoldItem(String barcode, OnCompleteListener listener) {
        db.collection("barcodes")
                .document(barcode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        listener.onSuccess(feedDataToItem(documentSnapshot));
                    } else {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }

    /**
     * Given a document, create an item object
     * @param document document from the db
     * @return item object
     */
    private HouseHoldItem feedDataToItem(DocumentSnapshot document) {
        HouseHoldItem item = new HouseHoldItem();
        item.setDescription(document.getString("description"));
        item.setMake(document.getString("make"));
        item.setPrice(document.getDouble("price"));
        item.setModel(document.getString("model"));

        return item;
    }

    /**
     * Callback for when the item is found or not found
     */
    public interface OnCompleteListener {
        void onSuccess(HouseHoldItem item);
        void onFailure(Exception e);
    }
}
