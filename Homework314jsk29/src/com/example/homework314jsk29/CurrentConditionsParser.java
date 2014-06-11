/**
 * CurrentConditionsParser is used to parse the current
 * conditions weather data in the format supplied by
 * wunderground.com
 * 
 * @author      Jeff Klavine
 */

package com.example.homework314jsk29;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


public class CurrentConditionsParser {
	
//  Class containing the article data.
	class CurrentCondition
	{
		public String temp;
		public String iconUri;
		public String weather;
		public String icon; // makes easier for saving to internal storage
		public String time;
		public String state;
		public String city;
	};

	/*
	 * This method is used to parse the xml file containing
	 * the weather current condition.
	 * 
	 * @param InputStream  input stream of the download.
	 * 
	 * @return CurrentCondition list of weather data parsed from file
	 */
	public CurrentCondition parseXml(InputStream inputStream) {
		XmlPullParserFactory pullParserFactory;
		CurrentCondition condition = null;
		
		class Location {
			String state;
			String city;
		}

		try {
			// Create the XmlPullParser factory and parser
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			// Get the xml file and set into the parser.
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);

			int eventType = parser.getEventType();
			Location location = null;

			// While not at the end of the document continue checking for required
			// items.
			while (eventType != XmlPullParser.END_DOCUMENT){
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
					// TODO don't think anything to do here.
					break;
				case XmlPullParser.START_TAG:
					// Found a start tag.  Check if one looking for.  If current_observation
					// then have a the desired data.  If the tags are expected data then
					// save for later use.
					name = parser.getName();
					if (name.equals("current_observation")){
						condition = new CurrentCondition();
					} else if (condition != null){
						if (name.equals("weather")){
							condition.weather = parser.nextText();
						} else if (name.equals("temperature_string")){
							condition.temp = parser.nextText();
						} else if (name.equals("icon")){
							condition.icon = parser.nextText();
						} else if (name.equals("icon_url")) {
							condition.iconUri = parser.nextText();
						} else if (name.equals("display_location") && location == null) {
								location = new Location();
						} else if (name.equals("city") && location != null) {
							location.city = parser.nextText();
						} else if (name.equals("state") && location != null) {
							location.state = parser.nextText();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					name = parser.getName();
					if (name.equalsIgnoreCase("display_location") && location != null){
						condition.city = location.city;
						condition.state = location.state;
						location = null;
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
		return condition;
	}
}
