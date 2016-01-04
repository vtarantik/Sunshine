package com.tarantik.vaclav.sunshine.helper;

/**
 * Created by DZCVT40 on 8.12.2015.
 */
public class MathHelper {
    public static double celsiusToFarnheit(double celsiusDegrees){
        return celsiusDegrees*9/5+32;
    }

    public static double farnheitToCelsius(double farnheitDegrees){
        return ((farnheitDegrees-32)*5)/9;
    }
}
