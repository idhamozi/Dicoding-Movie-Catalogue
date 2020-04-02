package com.example.consumerapp.viewModel.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.consumerapp.viewModel.favorite.FavoritesViewModel;

public class FavoriteFactory implements ViewModelProvider.Factory {

    private String itemType;

    public FavoriteFactory(String itemType){this.itemType = itemType;}

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavoritesViewModel(itemType);
    }
}
