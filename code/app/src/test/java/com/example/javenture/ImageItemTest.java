package com.example.javenture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.net.Uri;

import org.junit.Test;

/**
 * Unit tests for ImageItem class
 */
public class ImageItemTest {

    @Test
    public void testSetLocalUri() {
        Uri mockUri = mock(Uri.class);

        ImageItem item = new ImageItem("url");
        assertNull(item.getLocalUri());
        item.setLocalUri(mockUri);
        assertNull(item.getRemoteUrl());
        assertEquals(mockUri, item.getLocalUri());
    }

    @Test
    public void testSetRemoteUrl() {
        Uri mockUri = mock(Uri.class);

        ImageItem item = new ImageItem(mockUri);
        assertNull(item.getRemoteUrl());
        item.setRemoteUrl("url");
        assertNull(item.getLocalUri());
        assertEquals("url", item.getRemoteUrl());
    }

    @Test
    public void testIsLocal() {
        Uri mockUri = mock(Uri.class);
        ImageItem localItem = new ImageItem(mockUri);
        assertTrue(localItem.isLocal());
        ImageItem remoteItem = new ImageItem("url");
        assertFalse(remoteItem.isLocal());
    }
}
