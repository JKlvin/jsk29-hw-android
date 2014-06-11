/**
 * CityWeather is used to contain all the weather data
 * that has been downloaded.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework314jsk29;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class CityWeather {
	public CurrentConditionsParser.CurrentCondition currentConditions;
	public ArrayList<ForecastParser.ForecastDay> dialyForecast;
	public Bitmap currentCondition;
	public Bitmap forecastDay1;
	public Bitmap forecastDay2;
	public Bitmap forecastDay3;
}
