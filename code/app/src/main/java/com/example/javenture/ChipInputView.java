package com.example.javenture;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ChipInputView extends ConstraintLayout {

    private ChipGroup chipGroup;

    public ChipInputView(Context context) {
        this(context, null);
    }

    public ChipInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChipInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.chip_input_layout, this, true);

        TextInputLayout textInputLayout = findViewById(R.id.text_input_layout);
        EditText editText = textInputLayout.getEditText();
        chipGroup = findViewById(R.id.chip_group);

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if (" ".equals(editText.getText().toString())) {
                        editText.setText("");
                    }
                } else {
                    if (editText.getText().length() == 0 && chipGroup.getChildCount() > 0) {
                        editText.setText(" ");
                    }
                    if (editText.getText().toString().trim().length() > 0 && !editText.getText().toString().endsWith(",")) {
                        addNewChip(editText.getText().toString());
                        editText.setText(" ");
                    }
                }
            }
        });

        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (chipGroup.getChildCount() <= 0) {
                        return false;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DEL && editText.getText().length() == 0) {
                        Chip lastChip = (Chip) chipGroup.getChildAt(chipGroup.getChildCount() - 1);
                        editText.append(lastChip.getText());
                        chipGroup.removeView(lastChip);
                        return true;
                    }
                }
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (!text.isEmpty() && text.endsWith(",")) {
                    addNewChip(text.substring(0, text.length() - 1));
                    editable.clear();
                }
            }
        });
    }

    private void addNewChip(String text) {
        if (isWordDuplicate(text)) {
            return;
        }
        Chip newChip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.standalone_chip, chipGroup, false);
        newChip.setId(ViewCompat.generateViewId());
        newChip.setText(text);
        newChip.setOnCloseIconClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                chipGroup.removeView(newChip);
            }
        });
        chipGroup.addView(newChip);
    }

    /**
     * Get the number of Chips in the ChipInputView
     * @return number of Chips
     */
    public int getChipCount() {
        return chipGroup.getChildCount();
    }

    /**
     * Get the tags in the TagInputView
     * @return ArrayList of tags
     */
    public ArrayList<String> getChipWords() {
        ArrayList<String> words = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            words.add(chip.getText().toString());
        }
        return words;
    }

    /**
     * Check if a word is already in the ChipInputView
     * @param word text to be checked
     * @return true if the word is already in the ChipInputView, false otherwise
     */
    private boolean isWordDuplicate(String word) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a list of words, add them to the ChipInputView
     * @param words list of words
     */
    public void addChipsToChipGroup(List<String> words) {
        for (String word : words) {
            Chip chip = (Chip) LayoutInflater.from(getContext()).inflate(R.layout.standalone_chip, chipGroup, false);
            chip.setId(ViewCompat.generateViewId());
            chip.setText(word);
            chip.setOnCloseIconClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    chipGroup.removeView(chip);
                }
            });
            chipGroup.addView(chip);
        }
    }

    /**
     * Set the hint of the ChipInputView
     * @param hint hint
     */
    public void setHint(String hint) {
        TextInputLayout textInputLayout = findViewById(R.id.text_input_layout);
        textInputLayout.setHint(hint);
    }
}

