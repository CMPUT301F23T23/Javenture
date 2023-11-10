package com.example.javenture;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditHouseHoldItemFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void editHouseHoldItemFragmentTest() {
        ViewInteraction floatingActionButton = onView(withId(R.id.add_fab));
        floatingActionButton.perform(click());

        ViewInteraction textInputEditText = onView(withId(R.id.make_edit_text));
        textInputEditText.perform(scrollTo(), replaceText("a"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(withId(R.id.model_edit_text));
        textInputEditText2.perform(scrollTo(), replaceText("a"), closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(withId(R.id.serial_number_edit_text));
        textInputEditText3.perform(scrollTo(), replaceText("a"), closeSoftKeyboard());

        ViewInteraction textInputEditText4 = onView(withId(R.id.description_edit_text));
        textInputEditText4.perform(scrollTo(), replaceText("a"), closeSoftKeyboard());

        ViewInteraction textInputEditText5 = onView(withId(R.id.value_edit_text));
        textInputEditText5.perform(scrollTo(), replaceText("123.23"), closeSoftKeyboard());

        ViewInteraction textInputEditText6 = onView(withId(R.id.date_purchased_edit_text));
        textInputEditText6.perform(scrollTo(), click());

        DataInteraction materialTextView = onData(anything())
                .inAdapterView(allOf(withId(com.google.android.material.R.id.month_grid),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1)))
                .atPosition(11);
        materialTextView.perform(click());

        ViewInteraction materialButton = onView(allOf(withId(R.id.confirm_button), withText("OK")));
        materialButton.perform(click());

        ViewInteraction textInputEditText7 = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText7.perform(replaceText("a"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(allOf(withId(R.id.add_fab), withText("Add")));
        materialButton2.perform(scrollTo(), click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.household_item_list),
                        childAtPosition(
                                withId(R.id.fragment_household_items_constraint_layout),
                                0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction textInputEditText8 = onView(allOf(withId(R.id.make_edit_text), withText("a")));
        textInputEditText8.perform(scrollTo(), replaceText("b"));

        ViewInteraction textInputEditText9 = onView(allOf(withId(R.id.make_edit_text), withText("b")));
        textInputEditText9.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText10 = onView(allOf(withId(R.id.model_edit_text), withText("a")));
        textInputEditText10.perform(scrollTo(), replaceText("b"));

        ViewInteraction textInputEditText11 = onView(allOf(withId(R.id.model_edit_text), withText("b")));
        textInputEditText11.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText12 = onView(allOf(withId(R.id.serial_number_edit_text), withText("a")));
        textInputEditText12.perform(scrollTo(), replaceText("b"));

        ViewInteraction textInputEditText13 = onView(allOf(withId(R.id.serial_number_edit_text), withText("b")));
        textInputEditText13.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText14 = onView(allOf(withId(R.id.description_edit_text), withText("a")));
        textInputEditText14.perform(scrollTo(), replaceText("b"));

        ViewInteraction textInputEditText15 = onView(allOf(withId(R.id.description_edit_text), withText("b")));
        textInputEditText15.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText16 = onView(allOf(withId(R.id.value_edit_text), withText("123.00")));
        textInputEditText16.perform(scrollTo(), click());

        ViewInteraction textInputEditText17 = onView(allOf(withId(R.id.value_edit_text), withText("123.00")));
        textInputEditText17.perform(scrollTo(), replaceText("234"));

        ViewInteraction textInputEditText18 = onView(allOf(withId(R.id.value_edit_text), withText("234")));
        textInputEditText18.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText19 = onView(
                allOf(childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText19.perform(replaceText("b"), closeSoftKeyboard());

        ViewInteraction materialButton3 = onView(allOf(withId(R.id.confirm_button), withText("Confirm")));
        materialButton3.perform(scrollTo(), click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
