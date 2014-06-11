/**
 * ForecastParser is used to parse the daily forecast
 * conditions weather data in the format supplied by
 * wunderground.com
 * 
 * @author      Jeff Klavine
 */

package com.example.homework314jsk29;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

class High {
	public String tempF;
	public String tempC;
}

class Low {
	public String tempF;
	public String tempC;
}

public class ForecastParser {

	class ForecastDay {
		public String highTempF;
		public String highTempC;
		public String lowTempF;
		public String lowTempC;
		public String iconUri;
		public String conditions;
		public String day;
		public String icon; // makes easier for saving to internal storage
	};

	/*
	 * This method is used to parse the xml file containing
	 * the weather current condition.
	 * 
	 * @param InputStream  input stream of the download.
	 * 
	 * @return list of daily forecast weather data parsed from file
	 */
	public ArrayList<ForecastDay>  parseXml(InputStream inputStream) {

		XmlPullParserFactory pullParserFactory;
		ArrayList<ForecastDay> forecasts = null;
		ForecastDay forecastDay = null;
		High high = null;
		Low low = null;
		
		try {
			// Create the XmlPullParser factory and parser
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			// Get the xml file and set into the parser.
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);

			int eventType = parser.getEventType();

			// While not at the end of the document continue checking for required
			// items.
			while (eventType != XmlPullParser.END_DOCUMENT){
				String name = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					// Found a start tag.  Check if one looking for.  If simpleforecast
					// then have a new daily forecast.  If expected data then save for
					// later use.
					name = parser.getName();
					if (name.equals("simpleforecast")){
						forecasts = new ArrayList<ForecastDay>();
					} else if (name.equals("forecastday") && forecasts != null){
						forecastDay = new ForecastDay();
					} else if (forecastDay != null) {
						if (name.equals("weekday_short")){
							forecastDay.day = parser.nextText();
						} else if (name.equals("conditions")){
							forecastDay.conditions = parser.nextText();
						} else if (name.equals("icon")){
							forecastDay.icon = parser.nextText();
						} else if (name.equals("icon_url")) {
							forecastDay.iconUri = parser.nextText();
						} else if (name.equals("high") && high == null) {
							high = new High();
						} else if (high != null) {
							if(name.equals("fahrenheit")) {
								high.tempF = parser.nextText();
							} else if(name.equals("celsius")) {
								high.tempC = parser.nextText();
							}
						} else if (name.equals("low") && low == null) {
							low = new Low();
						} else if (low != null) {
							if(name.equals("fahrenheit")) {
								low.tempF = parser.nextText();
							} else if(name.equals("celsius")) {
								low.tempC = parser.nextText();
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					// If this is an end tag check if this means end of item.
					// If so then save the article.
					name = parser.getName();
					if (name.equalsIgnoreCase("forecastday") && forecastDay != null){
						forecasts.add(forecastDay);
						forecastDay = null;
					}
					if (name.equalsIgnoreCase("high") && high != null) {
						forecastDay.highTempF = high.tempF;
						forecastDay.highTempC = high.tempC;
						high = null;
					}
					if (name.equalsIgnoreCase("low") && low != null) {
						forecastDay.lowTempF = low.tempF;
						forecastDay.lowTempC = low.tempC;
						low = null;
					}
					break;
				case XmlPullParser.TEXT:
					// This is only used for checking how working.
					System.out.println("getting text");
				}
				eventType = parser.next();
			}

			System.out.println("finally done");

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return forecasts;
	}
}
