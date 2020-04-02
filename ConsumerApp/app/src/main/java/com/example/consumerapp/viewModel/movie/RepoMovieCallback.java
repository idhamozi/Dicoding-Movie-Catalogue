package com.example.consumerapp.viewModel.movie;

import com.example.consumerapp.model.Movie;

import java.util.ArrayList;

public interface RepoMovieCallback {

    void onGenresReceived(int genresReceivedStatus);
    void onItemsReceived(ArrayList<Movie> movies, int itemsReceivedStatus);

}
