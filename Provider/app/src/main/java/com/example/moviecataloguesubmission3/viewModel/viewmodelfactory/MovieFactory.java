package com.example.moviecataloguesubmission3.viewModel.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.moviecataloguesubmission3.viewModel.movie.MyViewModel;

public class MovieFactory implements ViewModelProvider.Factory {

    private String itemType;

    public MovieFactory(String itemType) {
        this.itemType = itemType;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MyViewModel(itemType);
    }

}
