package com.example.moviecataloguesubmission3.viewModel.search;

import com.example.moviecataloguesubmission3.model.Movie;

import java.util.ArrayList;

public interface SearchCallback {
    void onPostExecute(ArrayList<Movie> movies);

}
