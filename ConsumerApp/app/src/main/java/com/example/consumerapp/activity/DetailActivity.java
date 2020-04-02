package com.example.consumerapp.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.consumerapp.R;
import com.example.consumerapp.database.FavoritesDbContract;
import com.example.consumerapp.database.GenresDbContract;
import com.example.consumerapp.mapping.Mapping;
import com.example.consumerapp.model.Movie;

import java.util.HashMap;
import java.util.Objects;

import static android.provider.BaseColumns._ID;
import static com.example.consumerapp.database.FavoritesDbContract.FavoritesColumns.DESCRIPTION;
import static com.example.consumerapp.database.FavoritesDbContract.FavoritesColumns.GENRES;
import static com.example.consumerapp.database.FavoritesDbContract.FavoritesColumns.ITEM_TYPE;
import static com.example.consumerapp.database.FavoritesDbContract.FavoritesColumns.LANGUAGE;
import static com.example.consumerapp.database.FavoritesDbContract.FavoritesColumns.PHOTO;
import static com.example.consumerapp.database.FavoritesDbContract.FavoritesColumns.RATING;
import static com.example.consumerapp.database.FavoritesDbContract.FavoritesColumns.TITLE;
import static com.example.consumerapp.database.FavoritesDbContract.FavoritesColumns.YEAR;

public class DetailActivity extends AppCompatActivity {

    public static final String KEY_EXTRA = "key_extra";
    public static final int REMOVE_RESULT_CODE = 101;
    private Movie movie;

    private boolean isRemoved = false;
    private Uri uriId;

    public static HashMap<Integer, String> genresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView Photo = findViewById(R.id.Photo);
        ImageView PhotoCover = findViewById(R.id.PhotoCover);
        TextView Title = findViewById(R.id.Title);
        TextView Genre = findViewById(R.id.Genre);
        RatingBar RatingBar = findViewById(R.id.Rating);
        TextView Year = findViewById(R.id.Year);
        TextView Language = findViewById(R.id.Language);
        TextView Description = findViewById(R.id.Description);
        final ToggleButton favoriteButton = findViewById(R.id.fav_button);

        movie = getIntent().getParcelableExtra(KEY_EXTRA);
        uriId = getIntent().getData();

        if (isInDatabase()){
            favoriteButton.setChecked(true);
            favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red));
        } else {
            favoriteButton.setChecked(false);
            favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_red));
        }

        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_red));
                    insertFavorite();
                    isRemoved = false;
                    Toast.makeText(DetailActivity.this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
                } else {
                    favoriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_red));
                    deleteFavorite();
                    isRemoved = true;
                    Toast.makeText(DetailActivity.this, R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();

                }
            }
        });

        parseGenre();

        Glide.with(this).load(this.movie.getPhoto()).into(Photo);
        Glide.with(this).load(this.movie.getPhoto()).into(PhotoCover);
        Title.setText(this.movie.getTitle());
        Genre.setText(parseGenre());
        RatingBar.setRating(this.movie.getRating());
        Year.setText(this.movie.getYear());
        Language.setText(this.movie.getLanguage());
        Description.setText(this.movie.getDescription());

    }

    @Override
    public void finish() {
        if (isRemoved){
            Intent resultIntent = new Intent();
            setResult(REMOVE_RESULT_CODE, resultIntent);
        }
        super.finish();
    }

    public void insertFavorite() {
        ContentValues values = new ContentValues();
        values.put(_ID, movie.getId());
        values.put(ITEM_TYPE, movie.getItemType());
        values.put(PHOTO, movie.getPhoto());
        values.put(TITLE, movie.getTitle());
        values.put(GENRES, movie.getGenres());
        values.put(DESCRIPTION, movie.getDescription());
        values.put(YEAR, movie.getYear());
        values.put(LANGUAGE, movie.getLanguage());
        values.put(RATING, movie.getRating());
        getContentResolver().insert(FavoritesDbContract.CONTENT_URI, values);
    }

    public void deleteFavorite() {
        getContentResolver().delete(uriId, null, null);
    }

    public boolean isInDatabase() {
        boolean hasInserted = false;
        Cursor cursor = getContentResolver().query(uriId,  null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FavoritesDbContract.FavoritesColumns._ID));
                int idFromUri = Integer.valueOf(Objects.requireNonNull(uriId.getLastPathSegment()));
                if (id == idFromUri)
                    hasInserted = true;
            }
            cursor.close();
        }

        return hasInserted;
    }

    public String parseGenre() {
        if (genresList == null) {
            Cursor genresCursor = getContentResolver().query(GenresDbContract.CONTENT_URI, null, null, null, null);
            genresList = Mapping.mapGenresCursorToHashMap(Objects.requireNonNull(genresCursor));
        }

        if (movie.getGenres() != null) {
            String[] genresId = movie.getGenres().split("_");

            StringBuilder stringBuilder = new StringBuilder();
            for (String genreId : genresId) {
                if (genreId.length() != 0) {
                    String genreName = genresList.get(Integer.valueOf(genreId));
                    stringBuilder.append(genreName).append(", ");
                }
            }

            String genres = stringBuilder.toString();
            if (genres.length() > 2)
                genres = genres.substring(0, genres.length() - 2);	// Hilangkan tanda koma & spasi di akhir String
            return genres;
        } else {
            return null;
        }
    }
}
