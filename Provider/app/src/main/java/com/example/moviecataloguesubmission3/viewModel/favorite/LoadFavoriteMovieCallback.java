package com.example.moviecataloguesubmission3.viewModel.favorite;

import android.database.Cursor;

public interface LoadFavoriteMovieCallback {
    void postExecute(Cursor cursorFavorites);
}
