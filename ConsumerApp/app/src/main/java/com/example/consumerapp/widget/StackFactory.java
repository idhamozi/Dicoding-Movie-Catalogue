package com.example.consumerapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.example.consumerapp.R;
import com.example.consumerapp.database.FavoritesDbContract;
import com.example.consumerapp.mapping.Mapping;
import com.example.consumerapp.model.Movie;
import com.example.consumerapp.model.Widget;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class StackFactory implements RemoteViewsService.RemoteViewsFactory {

    private final ArrayList<Widget> widgetItems = new ArrayList<>();
    private final Context context;

    StackFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        widgetItems.clear();

        final long identityToken = Binder.clearCallingIdentity();
        Cursor favoritesCursor = context.getContentResolver().query(FavoritesDbContract.CONTENT_URI, null, null, null, null);
        Binder.restoreCallingIdentity(identityToken);

        ArrayList<Movie> itemsList = Mapping.mapItemsCursorToArrayList(favoritesCursor);

        for (Movie movie : itemsList) {
            String name = movie.getTitle();
            Bitmap image = null;
            try {
                image = Glide.with(context)
                        .asBitmap()
                        .load(movie.getPhoto())
                        .submit()
                        .get();
            } catch (ExecutionException | InterruptedException e) {
                Log.d(this.getClass().getSimpleName(), "onDataSetChanged: " + e.getMessage());
            }

            if (image == null)
                image = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_movie);

            widgetItems.add(new Widget(name, image));
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return widgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setImageViewBitmap(R.id.img_widget_poster, widgetItems.get(i).getImgPoster());
        remoteViews.setTextViewText(R.id.tv_name, widgetItems.get(i).getName());

        Bundle extras = new Bundle();
        extras.putString(FavoritesWidget.EXTRA_NAME, widgetItems.get(i).getName());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        remoteViews.setOnClickFillInIntent(R.id.img_widget_poster, fillInIntent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

}
