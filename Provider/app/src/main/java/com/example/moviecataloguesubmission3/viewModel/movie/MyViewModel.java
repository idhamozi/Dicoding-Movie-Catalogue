package com.example.moviecataloguesubmission3.viewModel.movie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviecataloguesubmission3.BuildConfig;
import com.example.moviecataloguesubmission3.database.GenresDbContract;
import com.example.moviecataloguesubmission3.model.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class MyViewModel extends ViewModel implements RepoMovieCallback {

    private String itemType;

    private static final String API_KEY = BuildConfig.TMDB_API_KEY;

    private MutableLiveData<ArrayList<Movie>> moviesLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> moviesReceivedLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> genresReceivedLiveData = new MutableLiveData<>();

    private static final int ITEMS_RECEIVED = 1;
    public static final int ITEMS_NOT_RECEIVED = -1;
    public static final int GENRES_RECEIVED = 2;
    public static final int GENRES_NOT_RECEIVED = -1;

    public MyViewModel(String itemType) {
        this.itemType = itemType;
    }

    public void requestGenres(Context context, Uri uriGenre) {
        RepoGenre(context, this, uriGenre, itemType);
    }

    public void requestMovie() {
        RepoMovie(this, itemType);
    }

    @Override
    public void onGenresReceived(int genresReceivedStatus) {
        genresReceivedLiveData.postValue(genresReceivedStatus);
    }

    @Override
    public void onItemsReceived(ArrayList<Movie> movies, int itemsReceivedStatus) {
        switch (itemsReceivedStatus) {
            case ITEMS_RECEIVED:
                moviesLiveData.postValue(movies);
                moviesReceivedLiveData.postValue(ITEMS_RECEIVED);
                break;
            case ITEMS_NOT_RECEIVED:
                moviesReceivedLiveData.postValue(ITEMS_NOT_RECEIVED);
                break;
        }
    }

    public LiveData<ArrayList<Movie>> getMoviesLiveData() {
        return moviesLiveData;
    }

    public LiveData<Integer> getGenresRequestStatus() {
        return genresReceivedLiveData;
    }

    public LiveData<Integer> getMoviesRequestStatus() {
        return moviesReceivedLiveData;
    }

    private void RepoMovie(RepoMovieCallback callback, final String itemType) {
        final WeakReference<RepoMovieCallback> weakItemsViewModel = new WeakReference<>(callback);
        final ArrayList<Movie> listMovie = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.themoviedb.org/3/discover/" + itemType + "?api_key=" + API_KEY + "&language=en-US";

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("results");

                    for (int i = 0; i < list.length(); ++i) {
                        JSONObject itemData = list.getJSONObject(i);
                        Movie movie = new Movie(itemData, itemType);
                        listMovie.add(movie);
                    }

                    weakItemsViewModel.get().onItemsReceived(listMovie, ITEMS_RECEIVED);
                } catch (JSONException e) {
                    Log.d("JSONException", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure", Objects.requireNonNull(error.getMessage()));
                weakItemsViewModel.get().onItemsReceived(new ArrayList<Movie>(), ITEMS_NOT_RECEIVED);
            }
        });
    }

    private void RepoGenre(final Context context, RepoMovieCallback callback, final Uri uriGenre, String itemType) {
        final WeakReference<RepoMovieCallback> weakItemsViewModel = new WeakReference<>(callback);
        String url = "https://api.themoviedb.org/3/genre/" + itemType + "/list?api_key=" + API_KEY + "&language=en-US";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("genres");

                    for (int i = 0; i < list.length(); ++i) {
                        JSONObject genreObject = list.getJSONObject(i);
                        int id = genreObject.getInt("id");
                        String name = genreObject.getString("name");
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(GenresDbContract.GenresColumns._ID, id);
                        contentValues.put(GenresDbContract.GenresColumns.NAME, name);
                        if (!isGenreInDatabase(context, id))
                            context.getContentResolver().insert(uriGenre, contentValues);
                    }

                    weakItemsViewModel.get().onGenresReceived(GENRES_RECEIVED);
                } catch (JSONException e) {
                    Log.d("JSONException", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure", Objects.requireNonNull(error.getMessage()));
                weakItemsViewModel.get().onGenresReceived(GENRES_NOT_RECEIVED);
            }
        });
    }

    private boolean isGenreInDatabase(Context context, int id) {
        boolean hasInserted = false;
        Cursor genreCursor = context.getContentResolver().query(
                GenresDbContract.CONTENT_URI, null, null, new String[] {String.valueOf(id)}, null
        );

        if (genreCursor != null) {
            if (genreCursor.moveToFirst()) {
                int idFromDb = genreCursor.getInt(genreCursor.getColumnIndexOrThrow(GenresDbContract.GenresColumns._ID));
                if (idFromDb == id)
                    hasInserted = true;
            }
            genreCursor.close();
        }

        return hasInserted;
    }

}
