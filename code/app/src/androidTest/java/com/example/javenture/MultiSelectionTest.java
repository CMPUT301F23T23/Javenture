package com.example.javenture;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.javenture.EditItemTest.hasItemCount;

import static org.junit.Assert.assertEquals;

import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class tests the multi selection functionality
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MultiSelectionTest {

    private HouseHoldItem item = new HouseHoldItem(
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

    private HouseHoldItem item2 = new HouseHoldItem(
            "",
            "4K Monitor",
            "LG",
            LocalDate.of(2023, 11, 5),
            1099.99,
            "L1233DE",
            "This is a LG 4K Monitor",
            "LDF123",
            new ArrayList<>(),
            new ArrayList<>(Arrays.asList("monitor", "4k", "electronics"))
    );

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);


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
    public void multiDeletionTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        floatingActionButton.perform(click());
        fillData(item);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        floatingActionButton.perform(click());
        fillData(item2);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.multi_delete_button)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("CONFIRM")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).check(matches(hasItemCount(0)));
    }

    @Test
    public void multiTagAssignmentTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        floatingActionButton.perform(click());
        fillData(item);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        floatingActionButton.perform(click());
        fillData(item2);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.multi_tag_assign_button)).perform(click());

        ArrayList<String> uniqueTags = new ArrayList<>(Arrays.asList("tag1", "tag2", "tag3"));
        onView(withId(R.id.chip_text_input_edit_text)).perform(ViewActions.typeText(String.join(",", uniqueTags)));
        onView(withText("ADD")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, click()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String tag : uniqueTags) {
            // check whether tag is displayed in the screen
            onView(withText(tag)).check(matches(isDisplayed()));
        }

        onView(withId(R.id.confirm_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // add the tags again
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.multi_tag_assign_button)).perform(click());
        onView(withId(R.id.chip_text_input_edit_text)).perform(ViewActions.typeText(String.join(",", uniqueTags)));
        onView(withText("ADD")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, click()));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // the tags should not be added
        onView(withId(R.id.chip_group)).perform(scrollTo()).check(new ChipGroupItemCountAssertion(item.getTags().size() + uniqueTags.size()));
        onView(withId(R.id.confirm_button)).perform(scrollTo(), click());
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.chip_group)).perform(scrollTo()).check(new ChipGroupItemCountAssertion(item.getTags().size() + uniqueTags.size()));


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
}
