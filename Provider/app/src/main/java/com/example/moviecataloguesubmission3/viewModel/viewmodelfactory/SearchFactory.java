package com.example.moviecataloguesubmission3.viewModel.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.moviecataloguesubmission3.viewModel.search.SearchViewModel;

public class SearchFactory implements ViewModelProvider.Factory {

    private String itemType;

    public SearchFactory(String itemType) {
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SearchViewModel(itemType);
    }

}
