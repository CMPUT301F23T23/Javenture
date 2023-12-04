package com.example.javenture;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

import android.Manifest;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddItemTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Before
    public void setUp() {
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
    }

    @Test
    public void addItemTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HouseHoldItem item = new HouseHoldItem(
                "",
                "55-Inch Class Crystal UHD Smart TV",
                "Samsung",
                LocalDate.of(2020, 1, 1),
                748.99,
                "SD23859GB",
                "This is a 55 inch TV",
                "CU7000",
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("tv", "electronics", "samsung"))
        );
        takePhoto();
        fillData(item);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).check(matches(hasDescendant(withText(item.getDescription()))));
    }

    @Test
    public void addingWithoutInfoTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HouseHoldItem item = new HouseHoldItem(
                "",
                "",
                "",
                LocalDate.of(2020, 1, 1),
                10,
                "",
                "",
                "",
                new ArrayList<>(),
                new ArrayList<>()
        );
        fillData(item);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.add_item_button)).check(matches(isDisplayed()));
    }

    @Test
    public void defineTagTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HouseHoldItem item = new HouseHoldItem(
                "",
                "55-Inch Class Crystal UHD Smart TV",
                "Samsung",
                LocalDate.of(2020, 1, 1),
                748.99,
                "SD23859GB",
                "This is a 55 inch TV",
                "CU7000",
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("tv", "electronics", "samsung"))
        );
        fillData(item);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.chip_group)).check(new ChipGroupItemCountAssertion(3));

    }

    private void fillData(HouseHoldItem item) {
        onView(withId(R.id.make_edit_text)).perform(scrollTo(), ViewActions.typeText(item.getMake()), closeSoftKeyboard());
        onView(withId(R.id.model_edit_text)).perform(scrollTo(), ViewActions.typeText(item.getModel()), closeSoftKeyboard());
        onView(withId(R.id.serial_number_edit_text)).perform(scrollTo(), ViewActions.typeText(item.getSerialNumber()), closeSoftKeyboard());
        onView(withId(R.id.description_edit_text)).perform(scrollTo(), ViewActions.typeText(item.getDescription()), closeSoftKeyboard());
        onView(withId(R.id.value_edit_text)).perform(scrollTo(), ViewActions.typeText(String.valueOf(item.getPrice())), closeSoftKeyboard());
        List<String> tags = item.getTags();
        String tagString = String.join(",", tags);
        onView(withId(R.id.chip_text_input_edit_text)).perform(scrollTo(), ViewActions.typeText(tagString), closeSoftKeyboard());
        onView(withId(R.id.comment_edit_text)).perform(scrollTo(), ViewActions.typeText(item.getComment()), closeSoftKeyboard());
        onView(withId(R.id.date_purchased_edit_text)).perform(scrollTo(), replaceText(item.getFormattedDatePurchased()), closeSoftKeyboard());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

}
