package com.example.moviecataloguesubmission3.activity;

import android.animation.Animator;
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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.moviecataloguesubmission3.R;
import com.example.moviecataloguesubmission3.adapter.PageAdapter;
import com.example.moviecataloguesubmission3.database.FavoritesDbContract;
import com.example.moviecataloguesubmission3.tab.MovieFragment;
import com.example.moviecataloguesubmission3.widget.FavoritesWidget;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import static com.example.moviecataloguesubmission3.activity.SearchActivity.EXTRA_TYPE;

public class MainActivity extends AppCompatActivity {

    public static final String ITEM_MOVIES = "movie";
    public static final String TV_SHOWS = "tv";

    private TabLayout tabLayout;

    FloatingActionButton fabSetting, actionFavorite, actionAlarm, actionLanguage;
    LinearLayout favLayout, alarmLayout, languageLayout;
    View backgroundLayout;

    boolean isFABOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        favLayout = findViewById(R.id.favorite_layout);
        alarmLayout = findViewById(R.id.alarm_layout);
        languageLayout = findViewById(R.id.language_layout);

        fabSetting = findViewById(R.id.setting);
        actionFavorite = findViewById(R.id.action_favorite);
        actionAlarm = findViewById(R.id.action_alarm);
        actionLanguage = findViewById(R.id.action_language);

        backgroundLayout = findViewById(R.id.fabBGLayout);

        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver dataObserver = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(FavoritesDbContract.CONTENT_URI, true, dataObserver);

        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFAB();
                } else {
                    closeFAB();
                }
            }
        });

        actionFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFavorite = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intentFavorite);
            }
        });

        actionAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAlarm = new Intent(MainActivity.this, Notification.class);
                startActivity(intentAlarm);
            }
        });

        actionLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLanguage = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intentLanguage);
            }
        });

        backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFAB();
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
        final int MOVIES_TAB = 0;
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            String selectedTab = tabLayout.getSelectedTabPosition() == MOVIES_TAB ? "movie" : "tv";
            intent.putExtra(EXTRA_TYPE, selectedTab);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFAB();
        } else {
            super.onBackPressed();
        }
    }

    private void showFAB() {
        isFABOpen = true;
        favLayout.setVisibility(View.VISIBLE);
        alarmLayout.setVisibility(View.VISIBLE);
        languageLayout.setVisibility(View.VISIBLE);
        backgroundLayout.setVisibility(View.VISIBLE);
        fabSetting.animate().rotationBy(180);
        favLayout.animate().translationY(-getResources().getDimension(R.dimen.s_55));
        alarmLayout.animate().translationY(-getResources().getDimension(R.dimen.s_100));
        languageLayout.animate().translationY(-getResources().getDimension(R.dimen.s_145));
    }

    private void closeFAB() {
        isFABOpen = false;
        backgroundLayout.setVisibility(View.GONE);
        fabSetting.animate().rotation(0);
        favLayout.animate().translationY(0);
        alarmLayout.animate().translationY(0);
        languageLayout.animate().translationY(0);
        languageLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    favLayout.setVisibility(View.GONE);
                    alarmLayout.setVisibility(View.GONE);
                    languageLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
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
            FavoritesWidget.WidgetDataChanged(context);
        }
    }
}
