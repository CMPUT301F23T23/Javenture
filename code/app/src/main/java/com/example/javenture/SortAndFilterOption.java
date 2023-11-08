package com.example.javenture;

import androidx.core.util.Pair;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * This class is used to store the sort and filter options that the user has selected.
 */
public class SortAndFilterOption {
    private String sortType;
    private String sortOption;
    private String filterType;
    private ArrayList<String> descriptionKeywords;
    private String makeKeyword;
    private ArrayList<String> tags;

    private Pair<LocalDate, LocalDate> dateRange;

    public SortAndFilterOption() {
        this.sortType = null;
        this.sortOption = null;
        this.filterType = null;
        this.descriptionKeywords = null;
        this.makeKeyword = null;
        this.tags = null;
        this.dateRange = null;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getSortOption() {
        return sortOption;
    }

    public void setSortOption(String sortOption) {
        this.sortOption = sortOption;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public ArrayList<String> getDescriptionKeywords() {
        return descriptionKeywords;
    }

    public void setDescriptionKeywords(ArrayList<String> descriptionKeywords) {
        this.descriptionKeywords = descriptionKeywords;
    }

    public String getMakeKeyword() {
        return makeKeyword;
    }

    public void setMakeKeyword(String makeKeyword) {
        this.makeKeyword = makeKeyword;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Pair<LocalDate, LocalDate> getDateRange() {
        return dateRange;
    }

    public void setDateRange(Pair<LocalDate, LocalDate> dateRange) {
        this.dateRange = dateRange;
    }

}
