package com.example.javenture;

import static com.google.firebase.firestore.core.UserData.Source.Set;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.w3c.dom.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class HouseHoldItemRepository {
    private FirebaseFirestore db;
    private FirebaseUser user;
    private AuthenticationService authService;

    public HouseHoldItemRepository() {
        db = FirebaseFirestore.getInstance();
        authService = new AuthenticationService();
        user = authService.getCurrentUser();
    }

    public interface OnItemsFetchedListener {
        void onItemsFetched(ArrayList<HouseHoldItem> items);
    }

    /**
     * Observe changes to the items collection in the db
     * @param listener listener to be called when the items collection changes
     */
    public ListenerRegistration observeItems(OnItemsFetchedListener listener, @Nullable String filterType, @Nullable ArrayList<String> keywords) {
        final CollectionReference itemsRef = db.collection("users").document(user.getUid()).collection("items");
        return itemsRef.addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("db", "Listen failed.", error);
                        listener.onItemsFetched(new ArrayList<>());
                        return;
                    }
                    List<DocumentSnapshot> filteredDocuments = filterDocuments(filterType, keywords, value.getDocuments());
                    ArrayList<HouseHoldItem> items = new ArrayList<>();
                    for (DocumentSnapshot document : filteredDocuments) {
                        HouseHoldItem item = feedDataToItem(document);
                        items.add(item);
                    }
                    Log.d("db", "Items fetched: " + items.size());
                    listener.onItemsFetched(items);
                }
        );
    }

    /**
     * Filter documents by given filter type and keywords
     * @param filterType String representing the filter type
     * @param keywords List of keywords to filter by
     * @param itemDocs List of documents to be filtered
     * @return List of filtered documents
     */
    private List<DocumentSnapshot> filterDocuments(@Nullable String filterType, @Nullable ArrayList<String> keywords, List<DocumentSnapshot> itemDocs) {
        if (filterType == null) {
            return itemDocs;
        }
        List<DocumentSnapshot> filteredItemDocs = new ArrayList<>();
        Log.d("db", "filterType: " + filterType);
        Log.d("db", "keywords: " + keywords);
        switch (filterType) {
            case "tags":
//                filteredItemDocs = filterByTags(itemDocs, keywords);
                break;
            case "description_keywords":
                filteredItemDocs = filterByDescriptionsKeywords(itemDocs, keywords);
                break;
        }
        return filteredItemDocs;
    }

    /**
     * Filter documents by description keywords.
     * If a document's description contains all of the keywords, it will be included in the filtered list.
     * @param itemDocs List of documents to be filtered
     * @param keywords List of keywords to filter by
     * @return List of filtered documents
     */
    private List<DocumentSnapshot> filterByDescriptionsKeywords(List<DocumentSnapshot> itemDocs, @Nullable ArrayList<String> keywords) {
        List<DocumentSnapshot> filteredItemDocs = new ArrayList<>();
        if (keywords == null) {
            return filteredItemDocs;
        }
        for (DocumentSnapshot itemDoc : itemDocs) {
            String description = itemDoc.getString("description");
            if (description != null) {
                HashSet<String> descriptionWords = new HashSet<>();
                for (String word : description.split(" ")) {
                    descriptionWords.add(word.toLowerCase());
                }
                boolean containsAllKeywords = true;
                for (String keyword : keywords) {
                    if (!descriptionWords.contains(keyword.toLowerCase())) {
                        containsAllKeywords = false;
                        break;
                    }
                }
                if (containsAllKeywords) {
                    filteredItemDocs.add(itemDoc);
                }
            }
        }
        return filteredItemDocs;
    }


    /**
     * Given a document, create an item object
     * @param document document from the db
     * @return item object
     */
    private HouseHoldItem feedDataToItem(DocumentSnapshot document) {
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
        List<String> tagNames = (List<String>) document.get("tags");
        List<Tag> tags = new ArrayList<>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                tags.add(new Tag(tagName));
            }
        }
        item.setTags(tags);
        return item;
    }

    /**
     * Add an item to the db
     * @param item item to be added
     */
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

    /**
     * Delete an item from the db
     * @param item item to be deleted
     */
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
    public void deleteItems(List<HouseHoldItem> items) {
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error deleting documents", e);
                    }
                });
    }

    /**
     * Edit multiple items in the db
     * @param items list of items to edit
     */
    public void editItems(List<HouseHoldItem> items) {
        if (user == null) {
            return;
        }
        CollectionReference itemsRef = db.collection("users").document(user.getUid()).collection("items");
        WriteBatch batch = db.batch();
        for (HouseHoldItem item : items) {
            batch.set(itemsRef.document(item.getId()), item.toMap(), SetOptions.merge());
        }
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db", "Documents updated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("db", "Error updating documents", e);
                    }
                });
    }
}
