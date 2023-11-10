package com.example.javenture;

import static org.junit.Assert.assertEquals;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import androidx.core.util.Pair;

@RunWith(MockitoJUnitRunner.class)
public class SortAndFilterTest {

    private FirebaseAuth mockedAuth;
    private FirebaseFirestore mockedDb;

    @Before
    public void setUp() {
        mockedAuth = Mockito.mock(FirebaseAuth.class);
        mockedDb = Mockito.mock(FirebaseFirestore.class);
    }

    @Test
    public void testSortByDescription() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.getString("description")).thenReturn("aAa");
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.getString("description")).thenReturn("BbB");
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.getString("description")).thenReturn("ccC");

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setSortType("description");

        ArrayList<DocumentSnapshot> expected = new ArrayList<>();
        expected.add(doc1);
        expected.add(doc2);
        expected.add(doc3);
        sortAndFilterOptions.setSortOption("ascending");
        List<DocumentSnapshot> result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);

        expected = new ArrayList<>();
        expected.add(doc3);
        expected.add(doc2);
        expected.add(doc1);
        sortAndFilterOptions.setSortOption("descending");
        result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);
    }

    @Test
    public void testSortByMake() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.getString("make")).thenReturn("aAa");
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.getString("make")).thenReturn("BbB");
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.getString("make")).thenReturn("ccC");

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setSortType("make");

        ArrayList<DocumentSnapshot> expected = new ArrayList<>();
        expected.add(doc1);
        expected.add(doc2);
        expected.add(doc3);
        sortAndFilterOptions.setSortOption("ascending");
        List<DocumentSnapshot> result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);

        expected = new ArrayList<>();
        expected.add(doc3);
        expected.add(doc2);
        expected.add(doc1);
        sortAndFilterOptions.setSortOption("descending");
        result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);
    }

    @Test
    public void testSortByValue() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.getDouble("price")).thenReturn(100.0);
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.getDouble("price")).thenReturn(200.0);
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.getDouble("price")).thenReturn(300.0);

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setSortType("value");

        ArrayList<DocumentSnapshot> expected = new ArrayList<>();
        expected.add(doc1);
        expected.add(doc2);
        expected.add(doc3);
        sortAndFilterOptions.setSortOption("ascending");
        List<DocumentSnapshot> result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);

        expected = new ArrayList<>();
        expected.add(doc3);
        expected.add(doc2);
        expected.add(doc1);
        sortAndFilterOptions.setSortOption("descending");
        result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);
    }

    @Test
    public void testSortByDate() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.getString("datePurchased")).thenReturn("Oct 1, 2020");
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.getString("datePurchased")).thenReturn("Oct 15, 2020");
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.getString("datePurchased")).thenReturn("Oct 30, 2020");

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setSortType("date");

        ArrayList<DocumentSnapshot> expected = new ArrayList<>();
        expected.add(doc1);
        expected.add(doc2);
        expected.add(doc3);
        sortAndFilterOptions.setSortOption("ascending");
        List<DocumentSnapshot> result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);

        expected = new ArrayList<>();
        expected.add(doc3);
        expected.add(doc2);
        expected.add(doc1);
        sortAndFilterOptions.setSortOption("descending");
        result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);
    }

    @Test
    public void testSortBySingleTag() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.get("tags")).thenReturn(Collections.singletonList("aAa"));
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.get("tags")).thenReturn(Collections.singletonList("BbB"));
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.get("tags")).thenReturn(Collections.singletonList("ccC"));

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setSortType("tags");

        ArrayList<DocumentSnapshot> expected = new ArrayList<>();
        expected.add(doc1);
        expected.add(doc2);
        expected.add(doc3);
        sortAndFilterOptions.setSortOption("ascending");
        List<DocumentSnapshot> result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);

        expected = new ArrayList<>();
        expected.add(doc3);
        expected.add(doc2);
        expected.add(doc1);
        sortAndFilterOptions.setSortOption("descending");
        result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);
    }

    @Test
    public void testSortByMultipleTags() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.get("tags")).thenReturn(Arrays.asList("bbB", "AaA"));
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.get("tags")).thenReturn(Arrays.asList("Bbb", "cCc"));
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.get("tags")).thenReturn(Arrays.asList("cCc", "dDd"));

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setSortType("tags");

        ArrayList<DocumentSnapshot> expected = new ArrayList<>();
        expected.add(doc1);
        expected.add(doc2);
        expected.add(doc3);
        sortAndFilterOptions.setSortOption("ascending");
        List<DocumentSnapshot> result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);

        expected = new ArrayList<>();
        expected.add(doc3);
        expected.add(doc2);
        expected.add(doc1);
        sortAndFilterOptions.setSortOption("descending");
        result = repo.sortDocuments(sortAndFilterOptions, docs);
        assertEquals(expected, result);
    }

    @Test
    public void testFilterByDateRange() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.getString("datePurchased")).thenReturn("Oct 1, 2020");
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.getString("datePurchased")).thenReturn("Oct 15, 2020");
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.getString("datePurchased")).thenReturn("Oct 30, 2020");

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setFilterType("date_range");
        sortAndFilterOptions.setDateRange(new Pair<>(parseDate("Oct 1, 2020"), parseDate("Oct 15, 2020")));

        HashSet<DocumentSnapshot> expected = new HashSet<>();
        expected.add(doc1);
        expected.add(doc2);

        HashSet<DocumentSnapshot> result = new HashSet<>(repo.filterDocuments(sortAndFilterOptions, docs));
        assertEquals(expected, result);
    }

    private LocalDate parseDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        try {
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format", e);
        }
    }

    @Test
    public void testFilterByMake() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.getString("make")).thenReturn("aaa");
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.getString("make")).thenReturn("bbb");
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.getString("make")).thenReturn("ccc");

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setFilterType("make");
        sortAndFilterOptions.setMakeKeyword("aaa");

        HashSet<DocumentSnapshot> expected = new HashSet<>();
        expected.add(doc1);

        HashSet<DocumentSnapshot> result = new HashSet<>(repo.filterDocuments(sortAndFilterOptions, docs));
        assertEquals(expected, result);
    }

    @Test
    public void testFilterByTags() {
        DocumentSnapshot doc1 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc1.get("tags")).thenReturn(Arrays.asList("bbb", "aaa"));
        DocumentSnapshot doc2 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc2.get("tags")).thenReturn(Arrays.asList("bbb", "ccc"));
        DocumentSnapshot doc3 = Mockito.mock(DocumentSnapshot.class);
        Mockito.when(doc3.get("tags")).thenReturn(Arrays.asList("ccc", "ddd"));

        ArrayList<DocumentSnapshot> docs = new ArrayList<>();
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc1);

        AuthenticationService authServ = new AuthenticationService(mockedAuth, mockedDb);
        HouseHoldItemRepository repo = new HouseHoldItemRepository(mockedDb, authServ);
        SortAndFilterOption sortAndFilterOptions = new SortAndFilterOption();
        sortAndFilterOptions.setFilterType("tags");
        sortAndFilterOptions.setTags(new ArrayList<>(Arrays.asList("bbb")));

        HashSet<DocumentSnapshot> expected = new HashSet<>();
        expected.add(doc1);
        expected.add(doc2);

        HashSet<DocumentSnapshot> result = new HashSet<>(repo.filterDocuments(sortAndFilterOptions, docs));
        assertEquals(expected, result);

        sortAndFilterOptions.setTags(new ArrayList<>(Arrays.asList("bbb", "ccc")));
        expected = new HashSet<>();
        expected.add(doc2);
        result = new HashSet<>(repo.filterDocuments(sortAndFilterOptions, docs));
        assertEquals(expected, result);

        sortAndFilterOptions.setTags(new ArrayList<>(Arrays.asList("bbb", "ccc")));
        expected = new HashSet<>();
        expected.add(doc2);
        result = new HashSet<>(repo.filterDocuments(sortAndFilterOptions, docs));
        assertEquals(expected, result);
    }
}
