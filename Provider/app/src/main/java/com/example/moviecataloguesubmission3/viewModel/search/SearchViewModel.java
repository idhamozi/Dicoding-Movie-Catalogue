package com.example.moviecataloguesubmission3.viewModel.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviecataloguesubmission3.BuildConfig;
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

public class SearchViewModel extends ViewModel implements SearchCallback {

    private static final String API_KEY = BuildConfig.TMDB_API_KEY;


    private MutableLiveData<ArrayList<Movie>> moviesLiveData = new MutableLiveData<>();
    private String itemType;

    public SearchViewModel(String itemType) {
        this.itemType = itemType;
    }

    public void search(String query) {
        RepoSearch(this, itemType, query);
    }

    @Override
    public void onPostExecute(ArrayList<Movie> movies) {
        moviesLiveData.postValue(movies);
    }

    public LiveData<ArrayList<Movie>> getMoviesLiveData() {
        return moviesLiveData;
    }

    private void RepoSearch(SearchCallback searchCallback, final String itemType, String query) {
        final ArrayList<Movie> listMovie = new ArrayList<>();
        final WeakReference<SearchCallback> weakSearchViewModel = new WeakReference<>(searchCallback);
        String url = "https://api.themoviedb.org/3/search/" + itemType + "?api_key=" + API_KEY  + "&language=en-US&query=" + query;
        AsyncHttpClient client = new AsyncHttpClient();

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

                    weakSearchViewModel.get().onPostExecute(listMovie);
                } catch (JSONException e) {
                    Log.d("JSONException", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure", Objects.requireNonNull(error.getMessage()));
                weakSearchViewModel.get().onPostExecute(new ArrayList<Movie>());
            }
        });
    }
}
