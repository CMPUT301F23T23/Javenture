package com.example.javenture;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditItemTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES);

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
    public void editItemTest() {
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
        onView(withId(R.id.household_item_list)).check(ViewAssertions.matches(hasDescendant(withText(item.getDescription()))));
        onView(withId(R.id.total_estimated_value)).check(ViewAssertions.matches(withText(String.valueOf(item.getPrice()))));

        HouseHoldItem editedItem = new HouseHoldItem(
                "",
                "80-Inch Smart TV",
                "LG",
                LocalDate.of(2020, 1, 1),
                1699.99,
                "LG23859GB",
                "This is a 80 inch TV",
                "CSD23859GB",
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("tv", "electronics", "samsung"))
        );
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, click()));
        editItem(item, editedItem);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.confirm_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).check(ViewAssertions.matches(hasDescendant(withText(editedItem.getDescription()))));
        onView(withId(R.id.total_estimated_value)).check(ViewAssertions.matches(withText(String.valueOf(editedItem.getPrice()))));
    }

    @Test
    public void deleteItemTest() {
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
        onView(withId(R.id.household_item_list)).check(ViewAssertions.matches(hasDescendant(withText(item.getDescription()))));
        onView(withId(R.id.total_estimated_value)).check(ViewAssertions.matches(withText(String.valueOf(item.getPrice()))));
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, click()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.delete_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.total_estimated_value)).check(ViewAssertions.matches(withText("0.00")));

    }

    @Test
    public void viewItemTest() {
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
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(0, click()));
        // check texts are correct
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.make_edit_text)).check(ViewAssertions.matches(withText(item.getMake())));
        onView(withId(R.id.model_edit_text)).check(ViewAssertions.matches(withText(item.getModel())));
        onView(withId(R.id.serial_number_edit_text)).check(ViewAssertions.matches(withText(item.getSerialNumber())));
        onView(withId(R.id.description_edit_text)).check(ViewAssertions.matches(withText(item.getDescription())));
        onView(withId(R.id.value_edit_text)).check(ViewAssertions.matches(withText(String.valueOf(item.getPrice()))));
        onView(withId(R.id.comment_edit_text)).check(ViewAssertions.matches(withText(item.getComment())));
        onView(withId(R.id.date_purchased_edit_text)).check(ViewAssertions.matches(withText(item.getFormattedDatePurchased())));
        // get chip group and check all the children are correct
        onView(withId(R.id.chip_group)).check(ViewAssertions.matches(hasChildCount(item.getTags().size())));

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

    private void editItem(HouseHoldItem originalItem, HouseHoldItem item) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // delete an image
        try {
            onView(withId(R.id.image_list_recycler_view)).perform(actionOnItemAtPosition(0, click()));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            onView(withId(R.id.delete_image_button)).perform(click());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            // do nothing
        }
        if (!originalItem.getMake().equals(item.getMake())) {
            onView(withId(R.id.make_edit_text)).perform(scrollTo(), clearText(), ViewActions.typeText(item.getMake()), closeSoftKeyboard());
        }
        if (!originalItem.getModel().equals(item.getModel())) {
            onView(withId(R.id.model_edit_text)).perform(scrollTo(), clearText(), ViewActions.typeText(item.getModel()), closeSoftKeyboard());
        }
        if (!originalItem.getSerialNumber().equals(item.getSerialNumber())) {
            onView(withId(R.id.serial_number_edit_text)).perform(scrollTo(), clearText(), ViewActions.typeText(item.getSerialNumber()), closeSoftKeyboard());
        }
        if (!originalItem.getDescription().equals(item.getDescription())) {
            onView(withId(R.id.description_edit_text)).perform(scrollTo(), clearText(), ViewActions.typeText(item.getDescription()), closeSoftKeyboard());
        }
        if (originalItem.getPrice() != item.getPrice()) {
            onView(withId(R.id.value_edit_text)).perform(scrollTo(), clearText(), ViewActions.typeText(String.valueOf(item.getPrice())), closeSoftKeyboard());
        }
        if (!originalItem.getComment().equals(item.getComment())) {
            onView(withId(R.id.comment_edit_text)).perform(scrollTo(), clearText(), ViewActions.typeText(item.getComment()), closeSoftKeyboard());
        }
        try {
            Thread.sleep(500);
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
