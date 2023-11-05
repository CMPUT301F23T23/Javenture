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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
     * @param sortAndFilterOption sort and filter options
     */
    public ListenerRegistration observeItems(OnItemsFetchedListener listener, SortAndFilterOption sortAndFilterOption) {
        final CollectionReference itemsRef = db.collection("users").document(user.getUid()).collection("items");
        return itemsRef.addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("db", "Listen failed.", error);
                        listener.onItemsFetched(new ArrayList<>());
                        return;
                    }
                    List<DocumentSnapshot> filteredDocuments = filterDocuments(sortAndFilterOption, value.getDocuments());
                    List<DocumentSnapshot> sortedDocuments = sortDocuments(sortAndFilterOption, filteredDocuments);
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
     * @param sortAndFilterOption sort and filter options
     * @param itemDocs List of documents to be filtered
     * @return List of filtered documents
     */
    private List<DocumentSnapshot> filterDocuments(SortAndFilterOption sortAndFilterOption, List<DocumentSnapshot> itemDocs) {
        if (sortAndFilterOption.getFilterType() == null) {
            return itemDocs;
        }
        List<DocumentSnapshot> filteredItemDocs = new ArrayList<>();
        switch (sortAndFilterOption.getFilterType()) {
            case "tags":
//                filteredItemDocs = filterByTags(itemDocs, keywords);
                break;
            case "description_keywords":
                filteredItemDocs = filterByDescriptionsKeywords(itemDocs, sortAndFilterOption.getDescriptionKeywords());
                break;
        }
        return filteredItemDocs;
    }

    /**
     * Sort documents by given sort type and option
     * @param sortAndFilterOption sort and filter options
     * @param itemDocs List of documents to be sorted
     * @return List of sorted documents
     */
    private List<DocumentSnapshot> sortDocuments(SortAndFilterOption sortAndFilterOption, List<DocumentSnapshot> itemDocs) {
        if (sortAndFilterOption.getSortType() == null || sortAndFilterOption.getSortOption() == null) {
            return itemDocs;
        }
        List<DocumentSnapshot> sortedItemDocs = new ArrayList<>();
        switch (sortAndFilterOption.getSortType()) {
            case "date":
                sortedItemDocs = sortByDate(itemDocs, sortAndFilterOption.getSortOption());
                break;
            case "description":
                sortedItemDocs = sortByDescription(itemDocs, sortAndFilterOption.getSortOption());
                break;
            case "make":
                sortedItemDocs = sortByMake(itemDocs, sortAndFilterOption.getSortOption());
                break;
            case "value":
//                sortedItemDocs = sortByValue(itemDocs, sortOption);
//                break;
            case "tags":
//                sortedItemDocs = sortByTags(itemDocs, sortOption);
//                break;
        }
        return sortedItemDocs;
    }

    /**
     * Sort documents by description
     * @param itemDocs List of documents to be sorted
     * @param sortOption "ascending" or "descending"
     * @return List of sorted documents
     */
    private List<DocumentSnapshot> sortByDescription(List<DocumentSnapshot> itemDocs, String sortOption) {
        Comparator<DocumentSnapshot> comparator = (doc1, doc2) -> {
            String description1 = doc1.getString("description");
            String description2 = doc2.getString("description");

            // null checks if necessary
            if (description1 == null) return (description2 == null) ? 0 : -1;
            if (description2 == null) return 1;

            return description1.compareToIgnoreCase(description2); // Ascending
        };

        // Sort ascending or descending based on the sortOption
        if ("ascending".equalsIgnoreCase(sortOption)) {
            Collections.sort(itemDocs, comparator);
        } else if ("descending".equalsIgnoreCase(sortOption)) {
            Collections.sort(itemDocs, Collections.reverseOrder(comparator));
        } else {
            throw new IllegalArgumentException("Invalid sort option");
        }
        return itemDocs;
    }

    /**
     * Sort documents by make
     * @param itemDocs List of documents to be sorted
     * @param sortOption "ascending" or "descending"
     * @return List of sorted documents
     */
    private List<DocumentSnapshot> sortByMake(List<DocumentSnapshot> itemDocs, String sortOption) {
        Comparator<DocumentSnapshot> comparator = (doc1, doc2) -> {
            String make1 = doc1.getString("make");
            String make2 = doc2.getString("make");

            // null checks if necessary
            if (make1 == null) return (make2 == null) ? 0 : -1;
            if (make2 == null) return 1;

            return make1.compareToIgnoreCase(make2); // Ascending
        };

        // Sort ascending or descending based on the sortOption
        if ("ascending".equalsIgnoreCase(sortOption)) {
            Collections.sort(itemDocs, comparator);
        } else if ("descending".equalsIgnoreCase(sortOption)) {
            Collections.sort(itemDocs, Collections.reverseOrder(comparator));
        } else {
            throw new IllegalArgumentException("Invalid sort option");
        }
        return itemDocs;
    }

    /**
     * Sort documents by date purchased
     * @param itemDocs List of documents to be sorted
     * @param sortOption "ascending" or "descending"
     * @return List of sorted documents
     */
    private List<DocumentSnapshot> sortByDate(List<DocumentSnapshot> itemDocs, String sortOption) {
        Comparator<DocumentSnapshot> comparator = (doc1, doc2) -> {
            LocalDate date1 = parseDate(doc1.getString("datePurchased"));
            LocalDate date2 = parseDate(doc2.getString("datePurchased"));

            // null checks if necessary
            if (date1 == null) return (date2 == null) ? 0 : -1;
            if (date2 == null) return 1;

            return date1.compareTo(date2); // Ascending
        };

        // Sort ascending or descending based on the sortOption
        if ("ascending".equalsIgnoreCase(sortOption)) {
            Collections.sort(itemDocs, comparator);
        } else if ("descending".equalsIgnoreCase(sortOption)) {
            Collections.sort(itemDocs, Collections.reverseOrder(comparator));
        } else {
            throw new IllegalArgumentException("Invalid sort option");
        }
        return itemDocs;
    }

    /**
     * Sort documents by description
     * @param dateString List of documents to be sorted
     * @return List of sorted documents
     */
    private LocalDate parseDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
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
