package com.example.javenture;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddHouseHoldItemFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addHouseHoldItemFragmentTests() {
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

        ViewInteraction addBtn = onView(allOf(withId(R.id.add_fab), withText("Add")));
        addBtn.perform(scrollTo(), click());

        ViewInteraction textView = onView(
                allOf(withId(com.google.android.material.R.id.textinput_error), withText("Date of Purchased is required"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView.check(matches(withText("Date of Purchased is required")));
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
