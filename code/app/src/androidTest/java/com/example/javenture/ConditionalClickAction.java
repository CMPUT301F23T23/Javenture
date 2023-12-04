package com.example.javenture;

import static org.hamcrest.Matchers.any;

import android.view.View;
import android.widget.Checkable;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

/**
 * This helper class is used to click on a view if it is not checked
 */
public class ConditionalClickAction implements ViewAction {
    @Override
    public Matcher<View> getConstraints() {
        // This action can be applied to any view
        return any(View.class);
    }

    @Override
    public String getDescription() {
        return "Clicks the view if it is not checked";
    }

    @Override
    public void perform(UiController uiController, View view) {
        if (view instanceof Checkable) {
            Checkable checkableView = (Checkable) view;
            if (!checkableView.isChecked()) {
                view.performClick();
            }
        }
    }
}