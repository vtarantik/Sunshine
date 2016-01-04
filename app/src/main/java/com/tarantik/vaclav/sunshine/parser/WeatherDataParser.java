package com.tarantik.vaclav.sunshine.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DZCVT40 on 7.12.2015.
 */
public class WeatherDataParser {
    /**
     * Given a string of the form returned by the api call:
     * http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7
     * retrieve the maximum temperature for the day indicated by dayIndex
     * (Note: 0-indexed, so 0 would refer to the first day).
     */
    public static double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex)
            throws JSONException {
        // TODO: add parsing code here
        JSONObject jsonObject = new JSONObject(weatherJsonStr);
        JSONArray forecastArray = jsonObject.getJSONArray("list");
        JSONObject givenDay = (JSONObject) forecastArray.get(dayIndex);
        JSONObject temp = givenDay.getJSONObject("temp");

        return temp.getDouble("max");
    }
}
