package com.example.moviecataloguesubmission3.viewModel.movie;

import com.example.moviecataloguesubmission3.model.Movie;

import java.util.ArrayList;

public interface RepoMovieCallback {

    void onGenresReceived(int genresReceivedStatus);
    void onItemsReceived(ArrayList<Movie> movies, int itemsReceivedStatus);

}
