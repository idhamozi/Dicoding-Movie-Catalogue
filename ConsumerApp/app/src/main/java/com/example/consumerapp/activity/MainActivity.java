package com.example.consumerapp.activity;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.consumerapp.R;
import com.example.consumerapp.adapter.PageAdapter;
import com.example.consumerapp.database.FavoritesDbContract;
import com.example.consumerapp.tab.MovieFragment;
import com.example.consumerapp.widget.FavoritesWidget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    public static final String ITEM_MOVIES = "movie";
    public static final String TV_SHOWS = "tv";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver dataObserver = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(FavoritesDbContract.CONTENT_URI, true, dataObserver);

        FloatingActionButton floatingActionButton = findViewById(R.id.show_favorite);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFavorite = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intentFavorite);
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MovieFragment(ITEM_MOVIES), getResources().getString(R.string.movies));
        adapter.addFragment(new MovieFragment(TV_SHOWS), getResources().getString(R.string.tv_shows));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.change_language:
                Intent intentLanguage = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intentLanguage);
                break;

//            case R.id.show_favorite:
//                Intent intentFavorite = new Intent(this, FavoritesActivity.class);
//                startActivity(intentFavorite);
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DataObserver extends ContentObserver {

        final Context context;

        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            FavoritesWidget.notifyWidgetDataChanged(context);
        }
    }
}
