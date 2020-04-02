package com.example.moviecataloguesubmission3.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDbHelper {

    private static final String FAVORITES_TABLE = FavoritesDbContract.TABLE_NAME;
    private static final String GENRES_TABLE = GenresDbContract.TABLE_NAME;
    private static DatabaseHelper databaseHelper;
    private static AppDbHelper INSTANCE;

    private static SQLiteDatabase database;

    private AppDbHelper(Context context){
        databaseHelper = new DatabaseHelper(context);
    }

    public static AppDbHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (INSTANCE == null)
                    INSTANCE = new AppDbHelper(context);
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public Cursor getFavoritesByItemTypeProvider(String[] itemType) {
        Cursor cursor;
        if (itemType == null) {
            cursor = database.query(
                    FAVORITES_TABLE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    FavoritesDbContract.FavoritesColumns._ID + " ASC",
                    null
            );
        } else {
            cursor = database.query(
                    FAVORITES_TABLE,
                    null,
                    FavoritesDbContract.FavoritesColumns.ITEM_TYPE + " = ?",
                    itemType,
                    null,
                    null,
                    FavoritesDbContract.FavoritesColumns._ID + " ASC",
                    null
            );
        }
        return cursor;
    }

    public Cursor getFavoriteByIdProvider(String id) {
        return database.query(
                FAVORITES_TABLE,
                null,
                FavoritesDbContract.FavoritesColumns._ID + " = ?",
                new String[] {id},
                null,
                null,
                null,
                null
        );
    }

    public Cursor getGenresProvider(String[] id) {
        Cursor cursor;
        if (id == null) {
            cursor = database.query(
                    GENRES_TABLE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        } else {
            cursor = database.query(
                    GENRES_TABLE,
                    null,
                    GenresDbContract.GenresColumns._ID + " = ?",
                    id,
                    null,
                    null,
                    null,
                    null
            );
        }

        return cursor;
    }

    public long insertFavoriteProvider(ContentValues values) {
        return database.insert(FAVORITES_TABLE, null, values);
    }

    public long deleteFavoriteProvider(String id) {
        return database.delete(FAVORITES_TABLE, FavoritesDbContract.FavoritesColumns._ID + " = ?", new String[] {id});
    }

    public long insertGenreProvider(ContentValues values) {
        return database.insert(GENRES_TABLE, null, values);
    }
}
