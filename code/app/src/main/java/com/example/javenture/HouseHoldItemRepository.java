package com.example.javenture;

import static com.google.firebase.firestore.core.UserData.Source.Set;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

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
import java.util.Map;

/**
 * Handles interactions with the Firestore db
 */
public class HouseHoldItemRepository {
    private FirebaseFirestore db;
    private AuthenticationService authService;

    public HouseHoldItemRepository() {
        db = FirebaseFirestore.getInstance();
        authService = new AuthenticationService();
    }

    public HouseHoldItemRepository(FirebaseFirestore db, AuthenticationService authService) {
        this.db = db;
        this.authService = authService;
    }

    /**
     * Listener for when items are fetched from the db
     */
    public interface OnItemsFetchedListener {
        void onItemsFetched(ArrayList<HouseHoldItem> items);
    }

    /**
     * Observe changes to the items collection in the db
     * @param listener listener to be called when the items collection changes
     * @param sortAndFilterOption sort and filter options
     */
    public ListenerRegistration observeItems(OnItemsFetchedListener listener, SortAndFilterOption sortAndFilterOption) {
        if (authService.getCurrentUser() == null) {
            return null;
        }
        final CollectionReference itemsRef = db.collection("users").document(authService.getCurrentUser().getUid()).collection("items");
        return itemsRef.addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("db", "Listen failed.", error);
                        listener.onItemsFetched(new ArrayList<>());
                        return;
                    }
                    List<DocumentSnapshot> itemsDoc = filterDocuments(sortAndFilterOption, value.getDocuments());
                    itemsDoc = sortDocuments(sortAndFilterOption, itemsDoc);
                    ArrayList<HouseHoldItem> items = new ArrayList<>();
                    for (DocumentSnapshot document : itemsDoc) {
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
    public List<DocumentSnapshot> filterDocuments(SortAndFilterOption sortAndFilterOption, List<DocumentSnapshot> itemDocs) {
        if (sortAndFilterOption.getFilterType() == null) {
            return itemDocs;
        }
        List<DocumentSnapshot> filteredItemDocs = new ArrayList<>();
        switch (sortAndFilterOption.getFilterType()) {
            case "description_keywords":
                filteredItemDocs = filterByDescriptionsKeywords(itemDocs, sortAndFilterOption.getDescriptionKeywords());
                break;
            case "date_range":
                filteredItemDocs = filterByDateRange(itemDocs, sortAndFilterOption.getDateRange());
                break;
            case "tags":
                filteredItemDocs = filterByTags(itemDocs, sortAndFilterOption.getTags());
                break;
            case "make":
                filteredItemDocs = filterByMake(itemDocs, sortAndFilterOption.getMakeKeyword());
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
    public List<DocumentSnapshot> sortDocuments(SortAndFilterOption sortAndFilterOption, List<DocumentSnapshot> itemDocs) {
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
                sortedItemDocs = sortByValue(itemDocs, sortAndFilterOption.getSortOption());
                break;
            case "tags":
                sortedItemDocs = sortByTags(itemDocs, sortAndFilterOption.getSortOption());
                break;
        }
        return sortedItemDocs;
    }

    /**
     * Filter documents by date range.
     * @param itemDocs List of documents to be filtered
     * @param dateRange Pair of start and end dates
     * @return List of filtered documents
     */
    private List<DocumentSnapshot> filterByDateRange(List<DocumentSnapshot> itemDocs, Pair<LocalDate, LocalDate> dateRange) {
        List<DocumentSnapshot> filteredItemDocs = new ArrayList<>();
        if (dateRange == null) {
            return filteredItemDocs;
        }
        for (DocumentSnapshot itemDoc : itemDocs) {
            LocalDate date = parseDate(itemDoc.getString("datePurchased"));
            if (date != null && date.compareTo(dateRange.first) >= 0 && date.compareTo(dateRange.second) <= 0) {
                filteredItemDocs.add(itemDoc);
            }
        }
        return filteredItemDocs;
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
     * Sort documents by value
     * @param itemDocs List of documents to be sorted
     * @param sortOption "ascending" or "descending"
     * @return List of sorted documents
     */
    private List<DocumentSnapshot> sortByValue(List<DocumentSnapshot> itemDocs, String sortOption) {
        Comparator<DocumentSnapshot> comparator = (doc1, doc2) -> {
            Double value1 = doc1.getDouble("price");
            Double value2 = doc2.getDouble("price");

            // null checks if necessary
            if (value1 == null) return (value2 == null) ? 0 : -1;
            if (value2 == null) return 1;

            return value1.compareTo(value2); // Ascending
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
     * Filter documents by make
     * If a document's make contains the make keyword, it will be included in the filtered list.
     * Ignore case when comparing make keyword and make.
     *
     * @param itemDocs List of documents to be filtered
     * @param makeKeyword make keyword to filter by
     * @return
     */
    private List<DocumentSnapshot> filterByMake(List<DocumentSnapshot> itemDocs, @Nullable String makeKeyword) {
        List<DocumentSnapshot> filteredItemDocs = new ArrayList<>();
        if (makeKeyword == null) {
            return filteredItemDocs;
        }
        for (DocumentSnapshot itemDoc : itemDocs) {
            String make = itemDoc.getString("make");
            if (make != null) {
                if (make.compareToIgnoreCase(makeKeyword) == 0) {
                    filteredItemDocs.add(itemDoc);
                }
            }
        }
        return filteredItemDocs;
    }

    /**
     * Filter documents by tags.
     * If a document's tags contains all of the tags, it will be included in the filtered list.
     * @param itemDocs List of documents to be filtered
     * @param filterTags List of tags to filter by
     * @return List of filtered documents
     */
    private List<DocumentSnapshot> filterByTags(List<DocumentSnapshot> itemDocs, @Nullable ArrayList<String> filterTags) {
        List<DocumentSnapshot> filteredItemDocs = new ArrayList<>();
        if (filterTags == null) {
            return filteredItemDocs;
        }
        for (DocumentSnapshot itemDoc : itemDocs) {
            List<String> tags = (List<String>) itemDoc.get("tags");
            if (tags != null) {
                HashSet<String> tagSet = new HashSet<>();
                for (String tag : tags) {
                    tagSet.add(tag.toLowerCase());
                }
                boolean containsAllTags = true;
                for (String t : filterTags) {
                    if (!tagSet.contains(t.toLowerCase())) {
                        containsAllTags = false;
                        break;
                    }
                }
                if (containsAllTags) {
                    filteredItemDocs.add(itemDoc);
                }
            }
        }
        return filteredItemDocs;

    }

    /**
     * Sorts a list of DocumentSnapshot items by their associated tags.
     *
     * This function sorts documents based on the lexicographical order of their first tag.
     * Documents are grouped by their tags, and the order is determined by the 'sortOption' parameter.
     * If a document has multiple tags, the first tag in alphabetical order is used for sorting.
     * Documents that do not contain any tags are placed at the end of the list.
     *
     * @param itemDocs   The list of DocumentSnapshot items to be sorted.
     * @param sortOption A string indicating the sort order: 'ascending' or 'descending'.
     * @return A sorted list of DocumentSnapshot items based on the specified sorting option.
     */
    private List<DocumentSnapshot> sortByTags(List<DocumentSnapshot> itemDocs, String sortOption) {
        Comparator<DocumentSnapshot> tagComparator = (doc1, doc2) -> {
            List<String> tags1 = (List<String>) doc1.get("tags");
            List<String> tags2 = (List<String>) doc2.get("tags");

            String firstTag1 = (tags1 != null && !tags1.isEmpty()) ? Collections.min(tags1) : null;
            String firstTag2 = (tags2 != null && !tags2.isEmpty()) ? Collections.min(tags2) : null;

            // Items without tags go at the end
            if (firstTag1 == null) return 1;
            if (firstTag2 == null) return -1;

            // Compare the first tags
            return firstTag1.compareToIgnoreCase(firstTag2);
        };

        // Sort ascending or descending based on the sortOption
        if ("ascending".equalsIgnoreCase(sortOption)) {
            Collections.sort(itemDocs, tagComparator);
        } else if ("descending".equalsIgnoreCase(sortOption)) {
            Collections.sort(itemDocs, Collections.reverseOrder(tagComparator));
        } else {
            throw new IllegalArgumentException("Invalid sort option");
        }
        return itemDocs;
    }


    /**
     * Given a document, create an item object
     * @param document document from the db
     * @return item object
     */
    private HouseHoldItem feedDataToItem(DocumentSnapshot document) {
        HouseHoldItem item = new HouseHoldItem();
        item.setId(document.getId());
        item.setDescription(document.getString("description"));
        item.setMake(document.getString("make"));
        item.setDatePurchased(parseDate(document.getString("datePurchased")));
        item.setPrice(document.getDouble("price"));
        item.setSerialNumber(document.getString("serialNumber"));
        item.setComment(document.getString("comment"));
        item.setModel(document.getString("model"));
        item.setTags((List<String>) document.get("tags"));
        return item;
    }

    /**
     * Add an item to the db
     * @param item item to be added
     */
    public void addItem(Map<String, Object> item) {
        if (authService.getCurrentUser() == null) {
            return;
        }
        CollectionReference itemsRef = db.collection("users").document(authService.getCurrentUser().getUid()).collection("items");
        // add item to db
        itemsRef.add(item)
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
     * @param id id of item to be edited
     * @param item target item with updated values
     */
    public void editItem(String id, Map<String, Object> item) {
        if (authService.getCurrentUser() == null) {
            return;
        }
        DocumentReference docRef = db.collection("users").document(authService.getCurrentUser().getUid()).collection("items").document(id);
        docRef.set(item, SetOptions.merge())
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
     * @param id id of item to be deleted
     */
    public void deleteItem(String id) {
        if (authService.getCurrentUser() == null) {
            return;
        }
        DocumentReference docRef = db.collection("users").document(authService.getCurrentUser().getUid()).collection("items").document(id);
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
     * @param ids list of ids of items to be deleted
     */
    public void deleteItems(List<String> ids) {
        if (authService.getCurrentUser() == null) {
            return;
        }
        CollectionReference itemsRef = db.collection("users").document(authService.getCurrentUser().getUid()).collection("items");
        WriteBatch batch = db.batch();
        for (String id : ids) {
            batch.delete(itemsRef.document(id));
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
     * @param items list of pairs of ids and target items with updated values
     */
    public void editItems(List<Pair<String, Map<String, Object>>> items) {
        if (authService.getCurrentUser() == null) {
            return;
        }
        CollectionReference itemsRef = db.collection("users").document(authService.getCurrentUser().getUid()).collection("items");
        WriteBatch batch = db.batch();
        for (Pair<String, Map<String, Object>> item : items) {
            String id = item.first;
            batch.set(itemsRef.document(id), item.second, SetOptions.merge());
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
