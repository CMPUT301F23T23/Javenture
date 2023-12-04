package com.example.javenture;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for HouseHoldItem class
 */
public class HouseHoldItemTest {
    @Test
    public void testToMap() {
        ArrayList<ImageItem> imageItems = new ArrayList<>(Arrays.asList(new ImageItem("url1"), new ImageItem("url2")));
        HouseHoldItem item = new HouseHoldItem("id1", "desc", "make", LocalDate.of(2020, 1, 1), 100.0, "serial", "comment", "model", imageItems, Arrays.asList("tag1", "tag2"));
        Map<String, Object> map = item.toMap();

        assertEquals("desc", map.get("description"));
        assertEquals("make", map.get("make"));
        assertEquals("Jan. 1, 2020", map.get("datePurchased"));
        assertEquals(100.0, map.get("price"));
        assertEquals("serial", map.get("serialNumber"));
        assertEquals("comment", map.get("comment"));
        assertEquals("model", map.get("model"));
        assertTrue(map.get("tags") instanceof List);
        assertEquals(Arrays.asList("url1", "url2"), (List<String>) map.get("photoUrls"));
    }

    @Test
    public void testGetFormattedDatePurchased() {
        HouseHoldItem item = new HouseHoldItem();
        item.setDatePurchased(LocalDate.of(2020, 1, 1));
        String formattedDate = item.getFormattedDatePurchased();
        assertEquals("Jan. 1, 2020", formattedDate);
    }

    @Test
    public void testHasTag() {
        HouseHoldItem item = new HouseHoldItem();
        item.addTag("tag1");
        assertTrue(item.hasTag("tag1"));
        assertFalse(item.hasTag("tag2"));
    }

    @Test
    public void testAddTag() {
        HouseHoldItem item = new HouseHoldItem();
        item.addTag("tag1");
        assertTrue(item.hasTag("tag1"));
        item.addTag("tag2");
        assertTrue(item.hasTag("tag2"));
        item.addTag("tag1"); // Should not add duplicate
        assertEquals(2, item.getTags().size());
    }
}
