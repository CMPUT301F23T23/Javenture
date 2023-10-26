package com.example.javenture;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
                    item.setId(document.getId());
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

    /**
     * Edit an item in the db
     * @param item target item with updated values
     */
    public void editItem(HouseHoldItem item) {
        if (user == null) {
            return;
        }
        DocumentReference docRef = db.collection("users").document(user.getUid()).collection("items").document(item.getId());
        docRef.set(item.toMap(), SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db", "Document updated");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("db", "Error updating document", e);
                });
    }

    public void deleteItem(HouseHoldItem item) {
        if (user == null) {
            return;
        }
        DocumentReference docRef = db.collection("users").document(user.getUid()).collection("items").document(item.getId());
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db", "Document deleted");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("db", "Error deleting document", e);
                });
    }

    /**
     * Delete multiple items from the db
     * @param items list of items to delete
     */
    public void deleteItems(List<HouseHoldItem> items, OnSuccessListener<Void> onSuccessListener) {
        if (user == null) {
            return;
        }
        CollectionReference itemsRef = db.collection("users").document(user.getUid()).collection("items");
        WriteBatch batch = db.batch();
        for (HouseHoldItem item : items) {
            batch.delete(itemsRef.document(item.getId()));
        }
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db", "Documents deleted");
                        onSuccessListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error deleting documents", e);
                    }
                });
    }
}
