package com.example.consumerapp.viewModel.favorite;

import android.database.Cursor;

public interface LoadFavoriteMovieCallback {
    void postExecute(Cursor cursorFavorites);
}
