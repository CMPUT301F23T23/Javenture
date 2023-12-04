package com.example.javenture;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.actionWithAssertions;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.javenture.PhotoTest.hasItemCount;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScanningTest {

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
    public void scanSerialNumberTest() {
        Intent resultData = new Intent();
        resultData.setData(Uri.parse("android.resource://com.example.javenture/" + R.raw.test_serial_number));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(hasAction(MediaStore.ACTION_PICK_IMAGES)).respondWith(result);

        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.serial_number_text_input_layout)).perform(actionWithAssertions(new ClickEndIconViewAction()));
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
        onView(withId(R.id.serial_number_edit_text)).check(matches(withText("4CE0460DOG")));
    }

    @Test
    public void scanInvalidSerialNumberTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.serial_number_text_input_layout)).perform(actionWithAssertions(new ClickEndIconViewAction()));
        takePhoto();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.serial_number_edit_text)).check(matches(withText("")));
    }

    @Test
    public void scanBarcodeTest() {
        Intent resultData = new Intent();
        resultData.setData(Uri.parse("android.resource://com.example.javenture/" + R.raw.test_barcode));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        Intents.intending(hasAction(MediaStore.ACTION_PICK_IMAGES)).respondWith(result);

        onView(withId(R.id.action_scan_barcode)).perform(click());
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
        onView(withId(R.id.description_edit_text)).check(matches(withText("Google Nest Mini (2nd Gen) Smart Speaker - Chalk")));
        onView(withId(R.id.make_edit_text)).check(matches(withText("Google")));
        onView(withId(R.id.model_edit_text)).check(matches(withText("GA00638-US")));
        onView(withId(R.id.value_edit_text)).check(matches(withText("69.99")));
    }

    @Test
    public void scanInvalidBarcodeTest() {
        onView(withId(R.id.action_scan_barcode)).perform(click());
        takePhoto();
        onView(withId(R.id.description_edit_text)).check(doesNotExist());
        onView(withId(R.id.make_edit_text)).check(doesNotExist());
        onView(withId(R.id.model_edit_text)).check(doesNotExist());
        onView(withId(R.id.value_edit_text)).check(doesNotExist());
    }

    private void takePhoto() {
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
}
