package com.example.consumerapp.mapping;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.util.Log;

import com.example.consumerapp.database.FavoritesDbContract;
import com.example.consumerapp.database.GenresDbContract;
import com.example.consumerapp.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;

public class Mapping {

    public static ArrayList<Movie> mapItemsCursorToArrayList(Cursor itemsCursor) {
        if (itemsCursor == null)
            return new ArrayList<>();

        ArrayList<Movie> itemsList = new ArrayList<>();

        while (itemsCursor.moveToNext()) {
            int id = itemsCursor.getInt(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns._ID));
            String itemType = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.ITEM_TYPE));
            String photo = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.PHOTO));
            String title = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.TITLE));
            String genres = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.GENRES));
            String description = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.DESCRIPTION));
            String year = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.YEAR));
            String language = itemsCursor.getString(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.LANGUAGE));
            float rating = itemsCursor.getFloat(itemsCursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns.RATING));
            Log.d("Mapping", "mapItemsCursorToArrayList: " + itemType);
            itemsList.add(new Movie(id, itemType, photo, title, genres, description, year, language, rating));
        }

        return itemsList;
    }

    public static HashMap<Integer, String> mapGenresCursorToHashMap(Cursor genresCursor) {
        @SuppressLint("UseSparseArrays")
        HashMap<Integer, String> genresList = new HashMap<>();

        while (genresCursor.moveToNext()) {
            int id = genresCursor.getInt(genresCursor.getColumnIndexOrThrow(GenresDbContract.GenresColumns._ID));
            String name = genresCursor.getString(genresCursor.getColumnIndexOrThrow(GenresDbContract.GenresColumns.NAME));
            genresList.put(id, name);
        }

        return genresList;
    }

}
