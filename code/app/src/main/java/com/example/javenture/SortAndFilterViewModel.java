package com.example.javenture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SortAndFilterViewModel extends ViewModel {
    private MutableLiveData<String> sortType = new MutableLiveData<>();
    private MutableLiveData<String> sortOption = new MutableLiveData<>();
    private MutableLiveData<String> filterType = new MutableLiveData<>();

    public LiveData<String> getSortType() {
        return sortType;
    }
    public LiveData<String> getSortOption() {
        return sortOption;
    }
    public LiveData<String> getFilterType() {
        return filterType;
    }

    public void setSortType(String sortType) {
        this.sortType.postValue(sortType);
    }
    public void setSortOption(String sortOption) {
        this.sortOption.postValue(sortOption);
    }
    public void setFilterType(String filterType) {
        this.filterType.postValue(filterType);
    }
}
