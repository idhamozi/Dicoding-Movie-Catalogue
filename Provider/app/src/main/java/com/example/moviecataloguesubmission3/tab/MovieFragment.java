package com.example.moviecataloguesubmission3.tab;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.example.moviecataloguesubmission3.database.GenresDbContract;
import com.example.moviecataloguesubmission3.model.Movie;
import com.example.moviecataloguesubmission3.preference.AppPreference;
import com.example.moviecataloguesubmission3.viewModel.movie.MyViewModel;
import com.example.moviecataloguesubmission3.viewModel.viewmodelfactory.MovieFactory;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.moviecataloguesubmission3.viewModel.movie.MyViewModel.GENRES_NOT_RECEIVED;
import static com.example.moviecataloguesubmission3.viewModel.movie.MyViewModel.GENRES_RECEIVED;
import static com.example.moviecataloguesubmission3.viewModel.movie.MyViewModel.ITEMS_NOT_RECEIVED;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    private String itemType;
    private MovieAdapter movieAdapter;
    private MyViewModel myViewModel;
    private ProgressBar progressBar;

    private AppPreference appPreference;

    SwipeRefreshLayout swipeRefreshLayout;

    public MovieFragment() {
        // Required empty public constructor
    }

    public MovieFragment(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreference = new AppPreference(Objects.requireNonNull(getContext()));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_movie, container, false);

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

        myViewModel = ViewModelProviders.of(this, new MovieFactory(itemType)).get(MyViewModel.class);
        myViewModel.getMoviesLiveData().observe(this, getMovie);
        myViewModel.getMoviesRequestStatus().observe(this, getMoviesRequestStatus);
        myViewModel.getGenresRequestStatus().observe(this, getGenresRequestStatus);

        setMovie();

        movieAdapter.setOnItemClickCallback(new MovieAdapter.OnItemClickCallback() {

            @Override
            public void onItemClicked(Movie movie, MovieAdapter.MovieViewHolder holder, int position) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Uri uriItemId = Uri.parse(FavoritesDbContract.CONTENT_URI + "/" + movieAdapter.getListMovie().get(position).getId());
                intent.setData(uriItemId);
                intent.putExtra(DetailActivity.KEY_EXTRA, movie);

                startActivity(intent);
            }
        });

        return view;
    }

    private void setMovie() {
        Boolean firstRun = appPreference.getAppFirstRun();

        if (firstRun){
            myViewModel.requestGenres(getContext(), GenresDbContract.CONTENT_URI);
            myViewModel.requestMovie();
        }
        else {
            myViewModel.requestMovie();
        }

        Loading(true);
    }

    private Observer<ArrayList<Movie>> getMovie =  new Observer<ArrayList<Movie>>() {
        @Override
        public void onChanged(ArrayList<Movie> movies) {
            if (movies != null){
                movieAdapter.setData(movies);
                Loading(false);
            }
        }
    };

    private Observer<Integer> getMoviesRequestStatus = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            if (integer == ITEMS_NOT_RECEIVED) {
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
                Loading(false);
            }
        }
    };

    private void Loading(boolean state){
        if (state)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    private Observer<Integer> getGenresRequestStatus = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            switch (integer) {
                case GENRES_RECEIVED:
                    appPreference.setAppFirstRun(false);
                    break;
                case GENRES_NOT_RECEIVED:
                    Toast.makeText(getActivity(), R.string.request_genres_failed, Toast.LENGTH_SHORT).show();
                    appPreference.setAppFirstRun(true);
                    break;
            }
        }
    };
}
