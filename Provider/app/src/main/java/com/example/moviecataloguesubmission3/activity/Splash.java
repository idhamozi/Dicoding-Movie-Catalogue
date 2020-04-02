package com.example.moviecataloguesubmission3.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.moviecataloguesubmission3.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        ImageView imgBackground = findViewById(R.id.img_background);

        Glide.with(this)
                .load(R.drawable.splash)
                .into(imgBackground);

        int waktu_loading = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashscreen = new Intent(Splash.this, MainActivity.class);
                startActivity(splashscreen);
                finish();
            }
        }, waktu_loading);
    }
}
