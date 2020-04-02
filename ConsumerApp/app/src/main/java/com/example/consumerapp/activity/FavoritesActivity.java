package com.example.consumerapp.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.consumerapp.R;
import com.example.consumerapp.adapter.PageAdapter;
import com.example.consumerapp.tab.FavoritesFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import static com.example.consumerapp.activity.MainActivity.ITEM_MOVIES;
import static com.example.consumerapp.activity.MainActivity.TV_SHOWS;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.favorites);

        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FavoritesFragment(ITEM_MOVIES), getResources().getString(R.string.movies));
        adapter.addFragment(new FavoritesFragment(TV_SHOWS), getResources().getString(R.string.tv_shows));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }
}
