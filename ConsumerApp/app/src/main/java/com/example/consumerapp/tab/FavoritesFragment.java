package com.example.consumerapp.tab;


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

import com.example.consumerapp.R;
import com.example.consumerapp.activity.DetailActivity;
import com.example.consumerapp.adapter.MovieAdapter;
import com.example.consumerapp.database.FavoritesDbContract;
import com.example.consumerapp.model.Movie;
import com.example.consumerapp.viewModel.favorite.FavoritesViewModel;
import com.example.consumerapp.viewModel.viewmodelfactory.FavoriteFactory;

import java.util.ArrayList;

import static com.example.consumerapp.activity.DetailActivity.REMOVE_RESULT_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    private String itemType;
    private MovieAdapter movieAdapter;
    private FavoritesViewModel favoritesViewModel;
    private ProgressBar progressBar;
    private TextView tvDataEmpty;

    private static final int REQUEST_CODE = 100;

    private int selectedMoviePosition;

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

        progressBar = view.findViewById(R.id.progress_bar);
        tvDataEmpty = view.findViewById(R.id.tv_data_empty);

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
        tvDataEmpty.setText(getString(R.string.empty_favorites, itemType));
        tvDataEmpty.setVisibility(View.VISIBLE);
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
