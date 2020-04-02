package com.example.moviecataloguesubmission3.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviecataloguesubmission3.R;
import com.example.moviecataloguesubmission3.adapter.MovieAdapter;
import com.example.moviecataloguesubmission3.database.FavoritesDbContract;
import com.example.moviecataloguesubmission3.model.Movie;
import com.example.moviecataloguesubmission3.viewModel.search.SearchViewModel;
import com.example.moviecataloguesubmission3.viewModel.viewmodelfactory.SearchFactory;

import java.util.ArrayList;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_SEARCH = "extra_search";
    public static final String EXTRA_TYPE = "extra_type";

    private String itemType;
    private MovieAdapter movieAdapter;
    private ProgressBar progressBar;
    private SearchViewModel searchViewModel;
    private TextView NotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

        itemType = getIntent().getStringExtra(EXTRA_TYPE);

        movieAdapter = new MovieAdapter(this);

        NotFound = findViewById(R.id.not_match);
        progressBar = findViewById(R.id.progress_bar);
        RecyclerView recyclerView = findViewById(R.id.rv_items);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(movieAdapter);

        searchViewModel = ViewModelProviders.of(this, new SearchFactory(itemType)).get(SearchViewModel.class);
        searchViewModel.getMoviesLiveData().observe(this, getMovie);

        movieAdapter.setOnItemClickCallback(new MovieAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Movie movie, MovieAdapter.MovieViewHolder holder, int position) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                Uri uriItemId = Uri.parse(FavoritesDbContract.CONTENT_URI + "/" + movieAdapter.getListMovie().get(position).getId());
                intent.setData(uriItemId);
                intent.putExtra(DetailActivity.KEY_EXTRA, movie);

                startActivity(intent);
            }
        });
    }

    private Observer<ArrayList<Movie>> getMovie = new Observer<ArrayList<Movie>>() {
        @Override
        public void onChanged(ArrayList<Movie> movies) {
            if (movies.size() == 0) {
                NotFound(true);
            }
            else {
                NotFound(false);
                movieAdapter.setData(movies);
                movieAdapter.notifyDataSetChanged();
            }
            Loading(false);
        }
    };

    private void Loading(boolean state){
        if (state)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    private void NotFound(boolean state) {
        if (state)
            NotFound.setVisibility(View.VISIBLE);
        else
            NotFound.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchItem.expandActionView();
        searchView.setQuery(getIntent().getStringExtra(EXTRA_SEARCH), true);
        searchView.setQueryHint("Search " + itemType);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                NotFound(false);
                movieAdapter.clearItems();
                query = query.toLowerCase().trim();
                searchViewModel.search(query);
                Loading(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                finish();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}
