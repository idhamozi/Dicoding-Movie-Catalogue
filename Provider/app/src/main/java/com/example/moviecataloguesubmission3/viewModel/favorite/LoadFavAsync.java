package com.example.moviecataloguesubmission3.viewModel.favorite;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.moviecataloguesubmission3.database.FavoritesDbContract;

import java.lang.ref.WeakReference;

public class LoadFavAsync extends AsyncTask<Void, Void, Cursor> {

    private final String itemType;
    private final WeakReference<Context> weakContext;
    private final WeakReference<LoadFavoriteMovieCallback> weakCallback;

    LoadFavAsync(Context context, LoadFavoriteMovieCallback callback, String itemType) {
        this.itemType = itemType;
        this.weakCallback = new WeakReference<>(callback);
        this.weakContext = new WeakReference<>(context);
    }

    @Override
    protected Cursor doInBackground(Void... voids) {
        Context context = weakContext.get();
        return context.getContentResolver().query(
                FavoritesDbContract.CONTENT_URI,
                null,
                null,
                new String[] {itemType},
                null
        );
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        super.onPostExecute(cursor);
        weakCallback.get().postExecute(cursor);
    }
}
