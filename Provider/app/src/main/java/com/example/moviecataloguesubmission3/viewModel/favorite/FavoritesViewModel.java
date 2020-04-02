package com.example.moviecataloguesubmission3.viewModel.favorite;

import android.content.Context;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviecataloguesubmission3.mapping.Mapping;
import com.example.moviecataloguesubmission3.model.Movie;

import java.util.ArrayList;

public class FavoritesViewModel extends ViewModel implements LoadFavoriteMovieCallback {

    private String itemType;

    private MutableLiveData<ArrayList<Movie>> favoritesLiveData = new MutableLiveData<>();

    public FavoritesViewModel(String itemType) {
        this.itemType = itemType;
    }

    public void loadFavoritesFromDb(Context context) {
        new LoadFavAsync(context, this, itemType).execute();
    }

    @Override
    public void postExecute(Cursor cursorFavorites) {
        ArrayList<Movie> favoritesList = Mapping.mapItemsCursorToArrayList(cursorFavorites);
        favoritesLiveData.postValue(favoritesList);
    }

    public LiveData<ArrayList<Movie>> getFavoritesLiveData() {
        return favoritesLiveData;
    }
}
