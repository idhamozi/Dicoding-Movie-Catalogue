package com.example.moviecataloguesubmission3.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.moviecataloguesubmission3.BuildConfig;
import com.example.moviecataloguesubmission3.R;
import com.example.moviecataloguesubmission3.model.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String API_KEY = BuildConfig.TMDB_API_KEY;


    public static final String DAILY_REMINDER = "daily_reminder";
    public static final String RELEASE_REMINDER = "release_reminder";

    public static final int ID_DAILY_REMINDER = 101;
    public static int ID_RELEASE_REMINDER = 201;
    public static int ID_RELEASE_REMINDER_DIFF = 0;

    private static final String EXTRA_TYPE = "extra_type";

    public void setRepeatingAlarm(Context context, String type, String time) {
        if (isTimeInvalid(time))
            return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(EXTRA_TYPE, type);

        String[] timeArray = time.split(":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);

        int requestCode = type.equals(DAILY_REMINDER) ? ID_DAILY_REMINDER : ID_RELEASE_REMINDER;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);

        if (alarmManager != null)

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        String localeType = type.equals(DAILY_REMINDER) ?
                context.getString(R.string.reminder) :
                context.getString(R.string.release_reminder);

        Toast.makeText(context, String.format(context.getString(R.string.alarm_turned_on), localeType), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(EXTRA_TYPE);
        if (type != null) {
            if (type.equalsIgnoreCase(DAILY_REMINDER))
                AlarmNotification.showDailyReminderNotification(context);
            else
                RepoNotif(context);
        }
    }

    public void cancelAlarm(Context context, String type) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        int requestCode = type.equals(DAILY_REMINDER) ? ID_DAILY_REMINDER : (ID_RELEASE_REMINDER - ID_RELEASE_REMINDER_DIFF);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        pendingIntent.cancel();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);

        String localeType = type.equals(DAILY_REMINDER) ?
                context.getString(R.string.reminder) :
                context.getString(R.string.release_reminder);

        Toast.makeText(context, String.format(context.getString(R.string.alarm_turned_off), localeType), Toast.LENGTH_SHORT).show();
    }

    private boolean isTimeInvalid(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            dateFormat.setLenient(false);
            dateFormat.parse(time);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

    public void RepoNotif(final Context appContext) {
        Log.d(this.getClass().getSimpleName(), "ReleaseTodayRepository: EXEC");

        final ArrayList<Movie> listMovie = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY + "&primary_release_date.gte=" + getCurrentDate() + "&primary_release_date.lte=" + getCurrentDate();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("results");

                    for (int i = 0; i < list.length(); ++i) {
                        JSONObject itemData = list.getJSONObject(i);
                        Movie movie = new Movie(itemData, "movie");
                        listMovie.add(movie);
                    }

                    AlarmNotification.onReceiveReleaseToday(appContext, listMovie);
                } catch (JSONException e) {
                    Log.d("JSONException", Objects.requireNonNull(e.getMessage()));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("onFailure", Objects.requireNonNull(error.getMessage()));
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
