package com.example.moviecataloguesubmission3.tab;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.moviecataloguesubmission3.R;
import com.example.moviecataloguesubmission3.activity.DetailActivity;
import com.example.moviecataloguesubmission3.adapter.MovieAdapter;
import com.example.moviecataloguesubmission3.database.FavoritesDbContract;
import com.example.moviecataloguesubmission3.model.Movie;
import com.example.moviecataloguesubmission3.viewModel.favorite.FavoritesViewModel;
import com.example.moviecataloguesubmission3.viewModel.viewmodelfactory.FavoriteFactory;

import java.util.ArrayList;

import static com.example.moviecataloguesubmission3.activity.DetailActivity.REMOVE_RESULT_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    private String itemType;
    private MovieAdapter movieAdapter;
    private FavoritesViewModel favoritesViewModel;
    private ProgressBar progressBar;
    private TextView EmptyMovie;

    private static final int REQUEST_CODE = 100;

    private int selectedMoviePosition;

    SwipeRefreshLayout swipeRefreshLayout;


    public FavoritesFragment() {
        // Required empty public constructor
    }

    public FavoritesFragment(String itemType){
        this.itemType = itemType;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        movieAdapter = new MovieAdapter(getContext());
        movieAdapter.notifyDataSetChanged();

        RecyclerView recyclerView = view.findViewById(R.id.rv_movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(movieAdapter);

        swipeRefreshLayout = view.findViewById(R.id.Swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                movieAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        progressBar = view.findViewById(R.id.progress_bar);
        EmptyMovie = view.findViewById(R.id.data_empty);

        favoritesViewModel = ViewModelProviders.of(this, new FavoriteFactory(itemType)).get(FavoritesViewModel.class);
        favoritesViewModel.getFavoritesLiveData().observe(this, getFavorite);

        setMovie();

        movieAdapter.setOnItemClickCallback(new MovieAdapter.OnItemClickCallback() {

            @Override
            public void onItemClicked(Movie movie, MovieAdapter.MovieViewHolder holder, int position) {
                selectedMoviePosition = position;

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Uri uriFavoriteId = Uri.parse(FavoritesDbContract.CONTENT_URI + "/" + movieAdapter.getListMovie().get(position).getId());
                intent.setData(uriFavoriteId);
                intent.putExtra(DetailActivity.KEY_EXTRA, movie);

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == REMOVE_RESULT_CODE) {
                movieAdapter.removeItem(selectedMoviePosition);

                if (movieAdapter.getItemCount() == 0)
                    showEmpty();
            }
        }
    }

    private void setMovie() {
        favoritesViewModel.loadFavoritesFromDb(getContext());
        Loading(true);
    }

    private void Loading(boolean state){
        if (state)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    private void showEmpty() {
        EmptyMovie.setText(getString(R.string.empty_favorites, itemType));
        EmptyMovie.setVisibility(View.VISIBLE);
    }

    private Observer<ArrayList<Movie>> getFavorite = new Observer<ArrayList<Movie>>() {
        @Override
        public void onChanged(ArrayList<Movie> movies) {
            if (movies != null) {
                movieAdapter.setData(movies);
                Loading(false);

                if (movieAdapter.getItemCount() == 0)
                    showEmpty();
            }
        }
    };

}
