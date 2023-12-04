package com.example.javenture;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.view.View;
import android.widget.FrameLayout;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Matcher;

/**
 * This helper class is used to click on the end icon inside a TextInputLayout
 */
public class ClickEndIconViewAction implements ViewAction {
    @Override
    public Matcher<View> getConstraints() {
        return isDisplayed(); // Only execute on a displayed view
    }

    @Override
    public String getDescription() {
        return "click on the end icon inside TextInputLayout";
    }

    @Override
    public void perform(UiController uiController, View view) {
        if (view instanceof TextInputLayout) {
            // Find the end icon inside the TextInputLayout
            TextInputLayout textInputLayout = (TextInputLayout) view;
            View endIconFrame = (View) textInputLayout.findViewById(com.google.android.material.R.id.text_input_end_icon);

            if (endIconFrame != null && endIconFrame.isShown()) {
                endIconFrame.performClick();
            }
        }
    }
}
