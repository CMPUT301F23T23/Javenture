package com.example.javenture;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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
 * This class tests whether the total estimated value is up to date
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class TotalEstimatedValueTest {

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
    public void totalEstimatedValueTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Double value1 = 748.99;
        Double value2 = 1249.99;
        HouseHoldItem item = new HouseHoldItem(
                "",
                "55-Inch Class Crystal UHD Smart TV",
                "Samsung",
                LocalDate.of(2020, 1, 1),
                value1,
                "SD23859GB",
                "This is a 55 inch TV",
                "CU7000",
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("tv", "electronics", "samsung"))
        );
        fillData(item);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).check(matches(hasDescendant(withText(item.getDescription()))));

        floatingActionButton.perform(click());
        HouseHoldItem item2 = new HouseHoldItem(
                "",
                "65-Inch Class Crystal UHD Smart TV",
                "Samsung",
                LocalDate.of(2021, 1, 1),
                value2,
                "SD143289D3",
                "This is a 65 inch TV",
                "CU9000",
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("tv", "electronics", "samsung"))
        );

        fillData(item2);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).check(matches(hasDescendant(withText(item2.getDescription()))));

        onView(withId(R.id.total_estimated_value)).check(matches(withText(String.valueOf(value1 + value2))));
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

