package com.example.moviecataloguesubmission3.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moviecataloguesubmission3.activity.MainActivity;
import com.example.moviecataloguesubmission3.database.AppDbHelper;
import com.example.moviecataloguesubmission3.database.FavoritesDbContract;
import com.example.moviecataloguesubmission3.database.GenresDbContract;

import java.util.Objects;

import static com.example.moviecataloguesubmission3.database.FavoritesDbContract.AUTHORITY;

public class MovieCatalogueProvider extends ContentProvider {

    private static final int FAVORITES = 1;
    private static final int FAVORITES_ID = 2;
    private static final int GENRES = 3;

    private AppDbHelper appDbHelper;
    private static final UriMatcher stringUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        stringUriMatcher.addURI(AUTHORITY, FavoritesDbContract.TABLE_NAME, FAVORITES);

        stringUriMatcher.addURI(AUTHORITY, FavoritesDbContract.TABLE_NAME + "/#", FAVORITES_ID);

        stringUriMatcher.addURI(AUTHORITY, GenresDbContract.TABLE_NAME, GENRES);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        appDbHelper.open();
        Cursor cursor;
        switch (stringUriMatcher.match(uri)) {
            case FAVORITES:
                cursor = appDbHelper.getFavoritesByItemTypeProvider(strings1);
                break;
            case FAVORITES_ID:
                cursor = appDbHelper.getFavoriteByIdProvider(uri.getLastPathSegment());
                break;
            case GENRES:
                cursor = appDbHelper.getGenresProvider(strings1);
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        appDbHelper.open();
        long added;
        switch (stringUriMatcher.match(uri)) {
            case FAVORITES:
                added = appDbHelper.insertFavoriteProvider(contentValues);
                Objects.requireNonNull(getContext()).getContentResolver().notifyChange(
                        FavoritesDbContract.CONTENT_URI,
                        new MainActivity.DataObserver(new Handler(), getContext())
                );
                break;
            case GENRES:
                added = appDbHelper.insertGenreProvider(contentValues);
                break;
            default:
                added = 0;
                break;
        }
        return Uri.parse(FavoritesDbContract.CONTENT_URI + "/" + added);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        appDbHelper.open();
        int deleted;

        if (stringUriMatcher.match(uri) == FAVORITES_ID) {
            deleted = (int) appDbHelper.deleteFavoriteProvider(uri.getLastPathSegment());
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(
                    FavoritesDbContract.CONTENT_URI,
                    new MainActivity.DataObserver(new Handler(), getContext())
            );
        }
        else {
            deleted = 0;
        }

        return deleted;
    }

    @Override
    public boolean onCreate() {
        appDbHelper = AppDbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
