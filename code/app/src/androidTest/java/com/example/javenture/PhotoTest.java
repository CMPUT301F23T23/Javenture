package com.example.javenture;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class PhotoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);


    @Before
    public void setUp() {
        init();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        FirebaseAuth.getInstance().signInAnonymously();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
        release();
    }

    @Test
    public void attachImageFromCameraTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        takePhoto();
        onView(withId(R.id.image_list_recycler_view)).check(matches(hasItemCount(1)));
    }

    @Test
    public void deleteImagesFromItem() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        takePhoto();
        onView(withId(R.id.image_list_recycler_view)).check(matches(hasItemCount(1)));
        onView(withId(R.id.image_card_view)).perform(scrollTo(), click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.delete_image_button)).perform(click());
        onView(withId(R.id.image_list_recycler_view)).check(matches(hasItemCount(0)));
    }

    @Test
    public void attachImageFromGalleryTest() {
        Intent resultData = new Intent();
        resultData.setData(Uri.parse("android.resource://com.example.javenture/" + R.raw.test_image));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(hasAction(MediaStore.ACTION_PICK_IMAGES)).respondWith(result);

        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.add_image_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.gallery_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        intended(hasAction(MediaStore.ACTION_PICK_IMAGES));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.image_list_recycler_view)).check(matches(hasItemCount(1)));
    }


    private void takePhoto() {
        onView(withId(R.id.add_image_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.capture_button)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Matcher<View> hasItemCount(final int count) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                return recyclerView.getAdapter().getItemCount() == count;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView should have " + count + " items");
            }
        };
    }
}
