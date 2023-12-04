package com.example.javenture;

import static org.junit.Assert.assertEquals;

import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;

import com.google.android.material.chip.ChipGroup;

public class ChipGroupItemCountAssertion implements ViewAssertion {
    private final int expectedCount;

    public ChipGroupItemCountAssertion(int expectedCount) {
        this.expectedCount = expectedCount;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }

        ChipGroup chipGroup = (ChipGroup) view;
        int actualCount = chipGroup.getChildCount();
        assertEquals("ChipGroup item count", expectedCount, actualCount);
    }
}

