package com.tarantik.vaclav.sunshine.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tarantik.vaclav.sunshine.R;
import com.tarantik.vaclav.sunshine.fragment.ForecastFragment;
import com.tarantik.vaclav.sunshine.helper.Utility;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.drawable.common_google_signin_btn_icon_dark_disabled);

        //Forecast
        String forecast = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        TextView forecastTextView = (TextView)view.findViewById(R.id.list_item_forecast_textview);
        forecastTextView.setText(forecast);

        //Date
        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        TextView dateTextView = (TextView)view.findViewById(R.id.list_item_date_textview);
        dateTextView.setText(Utility.getFriendlyDayString(context,dateInMillis));

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        //High temp
        float highTemp = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView highTempTextView = (TextView)view.findViewById(R.id.list_item_high_textview);
        highTempTextView.setText(Utility.formatTemperature(highTemp,isMetric));

        //Low temp
        float lowTemp = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowTempTextView = (TextView)view.findViewById(R.id.list_item_low_textview);
        lowTempTextView.setText(Utility.formatTemperature(lowTemp,isMetric));
    }
}
