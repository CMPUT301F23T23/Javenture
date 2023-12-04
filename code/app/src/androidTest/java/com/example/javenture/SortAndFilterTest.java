package com.example.javenture;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.annotation.ExperimentalTestApi;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
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
 * This class tests the sorting and filtering functionality
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class SortAndFilterTest {

    private HouseHoldItem item1 = new HouseHoldItem(
            "",
            "Crystal UHD Smart TV",
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
            "OLED Monitor",
            "LG",
            LocalDate.of(2023, 11, 5),
            1099.99,
            "L1233DE",
            "This is a LG 4K Monitor",
            "LDF123",
            new ArrayList<>(),
            new ArrayList<>(Arrays.asList("monitor"))
    );

    private HouseHoldItem item3 = new HouseHoldItem(
            "",
            "Microwave",
            "Panasonic",
            LocalDate.of(2023, 10, 1),
            299.99,
            "PA1234",
            "A descent microwave",
            "PDSF437",
            new ArrayList<>(),
            new ArrayList<>(Arrays.asList("microwave", "kitchen"))
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

        onView(withId(R.id.add_fab)).perform(click());
        fillData(item1);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.add_fab)).perform(click());
        fillData(item2);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.add_fab)).perform(click());
        fillData(item3);
        onView(withId(R.id.add_item_button)).perform(scrollTo(), click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() {
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void sortByDescriptionDescendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_description)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_descending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item2, item3, item1));
    }

    @Test
    public void sortByDescriptionAscendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_description)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_ascending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item1, item3, item2));
    }

    @Test
    public void sortByDateDescendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_date)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_descending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item2, item3, item1));
    }

    @Test
    public void sortByDateAscendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_date)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_ascending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item1, item3, item2));
    }

    @Test
    public void sortByMakeDescendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_make)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_descending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item1, item3, item2));
    }

    @Test
    public void sortByMakeAscendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_make)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_ascending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item2, item3, item1));
    }

    @Test
    public void sortByValueDescendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_value)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_descending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item2, item1, item3));
    }

    @Test
    public void sortByValueAscendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_value)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_ascending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item3, item1, item2));
    }

    @Test
    public void sortByTagsDescendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_tags)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_descending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item2, item3, item1));
    }

    @Test
    public void sortByTagsAscendingTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_sort_tags)).perform(new ConditionalClickAction());
        onView(withId(R.id.chip_ascending)).perform(new ConditionalClickAction());
        onView(withText("APPLY")).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isItemInOrder(Arrays.asList(item1, item3, item2));
    }

    @Test
    public void filterByDescriptionTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_filter_description_keywords)).perform(new ConditionalClickAction());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_text_input_edit_text)).perform(typeText("tv"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());
        onView(withText("APPLY")).perform(click());
        onView(withId(R.id.household_item_list)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.household_item_list)).check(matches(hasDescendant(withText(item1.getDescription()))));

        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_filter_description_keywords)).perform(click());
        onView(withId(R.id.chip_filter_description_keywords)).perform(new ConditionalClickAction());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_text_input_edit_text)).perform(typeText("non-existing"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());
        onView(withText("APPLY")).perform(click());
        onView(withId(R.id.household_item_list)).check(new RecyclerViewItemCountAssertion(0));
    }

    @Test
    public void filterByMakeTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_filter_make)).perform(new ConditionalClickAction());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.dialog_text_input_edit_text)).perform(typeText("samsung"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());
        onView(withText("APPLY")).perform(click());
        onView(withId(R.id.household_item_list)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.household_item_list)).check(matches(hasDescendant(withText(item1.getDescription()))));

        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_filter_make)).perform(click());
        onView(withId(R.id.chip_filter_make)).perform(new ConditionalClickAction());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.dialog_text_input_edit_text)).perform(typeText("non-existing"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());
        onView(withText("APPLY")).perform(click());
        onView(withId(R.id.household_item_list)).check(new RecyclerViewItemCountAssertion(0));
    }

    @Test
    public void filterByTagsTest() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_filter_tags)).perform(new ConditionalClickAction());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_text_input_edit_text)).perform(typeText("electronics"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());
        onView(withText("APPLY")).perform(click());
        onView(withId(R.id.household_item_list)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.household_item_list)).check(matches(hasDescendant(withText(item1.getDescription()))));

        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_filter_tags)).perform(click());
        onView(withId(R.id.chip_filter_tags)).perform(new ConditionalClickAction());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_text_input_edit_text)).perform(typeText("non-existing"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());
        onView(withText("APPLY")).perform(click());
        onView(withId(R.id.household_item_list)).check(new RecyclerViewItemCountAssertion(0));
    }

    @Test
    public void filterByDateRange() {
        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_filter_tags)).perform(new ConditionalClickAction());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_text_input_edit_text)).perform(typeText("electronics"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());
        onView(withText("APPLY")).perform(click());
        onView(withId(R.id.household_item_list)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.household_item_list)).check(matches(hasDescendant(withText(item1.getDescription()))));

        onView(withId(R.id.action_sort_and_filter)).perform(click());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_filter_tags)).perform(click());
        onView(withId(R.id.chip_filter_tags)).perform(new ConditionalClickAction());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.chip_text_input_edit_text)).perform(typeText("non-existing"), closeSoftKeyboard());
        onView(withText("ADD")).perform(click());
        onView(withText("APPLY")).perform(click());
        onView(withId(R.id.household_item_list)).check(new RecyclerViewItemCountAssertion(0));
    }

    private void isItemInOrder(List<HouseHoldItem> items) {
        for (int i = 0; i < items.size(); i++) {
            onView(withId(R.id.household_item_list)).perform(actionOnItemAtPosition(i, click()));
            onView(withId(R.id.description_edit_text)).check(matches(withText(items.get(i).getDescription())));
            pressBack();
        }
    }

    private void fillData(HouseHoldItem item) {
        onView(withId(R.id.make_edit_text)).perform(scrollTo(), typeText(item.getMake()), closeSoftKeyboard());
        onView(withId(R.id.model_edit_text)).perform(scrollTo(), typeText(item.getModel()), closeSoftKeyboard());
        onView(withId(R.id.serial_number_edit_text)).perform(scrollTo(), typeText(item.getSerialNumber()), closeSoftKeyboard());
        onView(withId(R.id.description_edit_text)).perform(scrollTo(), typeText(item.getDescription()), closeSoftKeyboard());
        onView(withId(R.id.value_edit_text)).perform(scrollTo(), typeText(String.valueOf(item.getPrice())), closeSoftKeyboard());
        List<String> tags = item.getTags();
        String tagString = String.join(",", tags);
        onView(withId(R.id.chip_text_input_edit_text)).perform(scrollTo(), typeText(tagString), closeSoftKeyboard());
        onView(withId(R.id.comment_edit_text)).perform(scrollTo(), typeText(item.getComment()), closeSoftKeyboard());
        onView(withId(R.id.date_purchased_edit_text)).perform(scrollTo(), replaceText(item.getFormattedDatePurchased()), closeSoftKeyboard());
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
