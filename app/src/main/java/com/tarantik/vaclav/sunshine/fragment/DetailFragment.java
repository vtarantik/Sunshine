package com.tarantik.vaclav.sunshine.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tarantik.vaclav.sunshine.R;
import com.tarantik.vaclav.sunshine.data.WeatherContract;
import com.tarantik.vaclav.sunshine.helper.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER_ID = 0;

    private static final String[] DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private ShareActionProvider mShareActionProvider;
    private String mDetailUriText;

    private TextView textViewDay;
    private TextView textViewDate;
    private TextView textViewTempHigh;
    private TextView textViewTempLow;
    private ImageView imageViewIcon;
    private TextView textViewDescription;
    private TextView textViewHumidity;
    private TextView textViewWind;
    private TextView textViewPressure;

    private String forecast;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        textViewDay = (TextView) rootView.findViewById(R.id.textview_day);
        textViewDate = (TextView) rootView.findViewById(R.id.textview_date);
        textViewTempHigh = (TextView) rootView.findViewById(R.id.textview_temp_high);
        textViewTempLow = (TextView) rootView.findViewById(R.id.textview_temp_low);
        imageViewIcon = (ImageView) rootView.findViewById(R.id.imageview_weather_icon);
        textViewDescription = (TextView) rootView.findViewById(R.id.textview_description);
        textViewHumidity = (TextView) rootView.findViewById(R.id.textview_humidity);
        textViewWind = (TextView) rootView.findViewById(R.id.textview_wind);
        textViewPressure = (TextView) rootView.findViewById(R.id.textview_pressure);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecast + FORECAST_SHARE_HASHTAG);
        shareIntent.setType("text/plain");
        Log.d(TAG, "SHARING INFO: " + forecast);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent detailsIntent = getActivity().getIntent();
        mDetailUriText = detailsIntent.getStringExtra("weather_data");
        if (detailsIntent == null) {
            return null;
        }
        return new CursorLoader(getActivity(), detailsIntent.getData(), DETAIL_COLUMNS, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            Log.d(TAG,"No data, returning");
            return;
        }

        int weatherId = data.getInt(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        imageViewIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        long dateInMillis = data.getLong(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE));
        boolean isMetric = Utility.isMetric(getActivity());


        textViewDay.setText(Utility.getFriendlyDayString(getActivity(), dateInMillis));
        textViewDate.setText(Utility.getFormattedMonthDay(getActivity(), dateInMillis));

        textViewDescription.setText(data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)));

        textViewTempHigh.setText(Utility.formatTemperature(getActivity(), data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), isMetric));
        textViewTempLow.setText(Utility.formatTemperature(getActivity(), data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), isMetric));

        textViewWind.setText(Utility.getFormattedWind(getActivity(), data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)), data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES))));

        float humidity = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        textViewHumidity.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Read pressure from cursor and update view
        float pressure = data.getFloat(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        textViewPressure.setText(getActivity().getString(R.string.format_pressure, pressure));

        // We still need this for the share intent
        forecast = String.format("%s - %s - %s/%s", Utility.getFormattedMonthDay(getActivity(), data.getLong(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE))), data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)), data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), data.getDouble(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)));

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
