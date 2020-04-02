package com.example.consumerapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.consumerapp.activity.MainActivity.ITEM_MOVIES;

public class Movie implements Parcelable {

    private int id;
    private String itemType, photo, title, description, year, language, genres;
    private float rating;

    public Movie(JSONObject movie, String itemType){
        try {
            this.id = movie.getInt("id");

            this.itemType = itemType;

            photo = "https://image.tmdb.org/t/p/w342" + movie.getString("poster_path");

            title = itemType.equals(ITEM_MOVIES) ? movie.getString("title") : movie.getString("name");

            rating = (float) movie.getDouble("vote_average")/2;

            language = movie.getString("original_language").toUpperCase();

            year = itemType.equals(ITEM_MOVIES)
                    ? movie.getString("release_date").substring(0, 4)
                    : movie.getString("first_air_date").substring(0, 4);

            description = movie.getString("overview");

            JSONArray listIdGenres = movie.getJSONArray("genre_ids");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < listIdGenres.length(); ++i)
                stringBuilder.append(listIdGenres.getInt(i)).append("_");
            genres = stringBuilder.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Movie() {

    }

    public Movie(int id, String itemType, String photo, String title, String genres, String description, String year, String language, float rating) {
        this.id = id;
        this.itemType = itemType;
        this.photo = photo;
        this.title = title;
        this.genres = genres;
        this.description = description;
        this.year = year;
        this.language = language;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getGenres() {
        return genres;
    }

    public String getItemType(){
        return itemType;
    }

    public String getPhoto() {
        return photo;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getYear() {
        return year;
    }

    public String getLanguage() {
        return language;
    }

    public float getRating() {
        return rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    private Movie(Parcel in) {
        id = in.readInt();
        itemType = in.readString();
        genres = in.readString();
        photo = in.readString();
        title = in.readString();
        description = in.readString();
        rating = in.readFloat();
        year = in.readString();
        language = in.readString();
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(itemType);
        parcel.writeString(genres);
        parcel.writeString(photo);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeFloat(rating);
        parcel.writeString(year);
        parcel.writeString(language);

    }
}
