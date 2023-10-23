package com.example.javenture;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class HouseHoldItemRepository {
    private FirebaseFirestore db;
    private FirebaseUser user;

    public HouseHoldItemRepository(FirebaseUser user) {
        db = FirebaseFirestore.getInstance();
        this.user = user;
    }

    public ArrayList<HouseHoldItem> fetchItems(OnSuccessListener<ArrayList<HouseHoldItem>> onSuccessListener) {
        if (user == null) {
            return new ArrayList<>();
        }
        CollectionReference itemsRef = db.collection("users").document(user.getUid()).collection("items");
        // fetch items from db
        ArrayList<HouseHoldItem> items = new ArrayList<>();
        itemsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("db", "Fetching documents");
                for (QueryDocumentSnapshot document : task.getResult()) {
                    HouseHoldItem item = new HouseHoldItem();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
                    item.setDescription(document.getString("description"));
                    item.setMake(document.getString("make"));
                    item.setDatePurchased(LocalDate.parse(document.getString("datePurchased"), formatter));
                    item.setPrice(document.getDouble("price"));
                    item.setSerialNumber(document.getString("serialNumber"));
                    item.setComment(document.getString("comment"));
                    item.setModel(document.getString("model"));
                    items.add(item);
                }
                onSuccessListener.onSuccess(items);
            } else {
                Log.d("db", "Error getting documents: ", task.getException());
            }
        });

        return items;
    }

    public void addItem(HouseHoldItem item) {
        if (user == null) {
            return;
        }
        CollectionReference itemsRef = db.collection("users").document(user.getUid()).collection("items");
        // add item to db
        itemsRef.add(item.toMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("db", "Document added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("db", "Error adding document", e);
                });
    }
}
