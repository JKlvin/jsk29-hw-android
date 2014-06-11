/**
 * ZipToCityParser is used to parse the city data
 * from the api supplied by maps.googleapis.com
 * 
 * @author      Jeff Klavine
 */

package com.example.homework314jsk29;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ZipToCityParser {



	/*
	 * This method is used to parse the xml file containing
	 * the articles.
	 * 
	 * @param InputStream  input stream of the download.
	 * 
	 * @return string containing the city & state
	 */
	public String parseXml(InputStream inputStream) {
		XmlPullParserFactory pullParserFactory;
		String location = null;
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
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
					// TODO don't think anything to do here.
					break;
				case XmlPullParser.START_TAG:
					// Found a start tag.  Check if one looking for.  If
					// formatted_address then have a valid city.  Save that
					// data for later use.
					name = parser.getName();
					if (name.equals("formatted_address")){
						location = parser.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					// TODO don't think anything here.
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
		
		// Now if not null need to get only the required
		// portions of the location.
		String[] splited;
		splited = location.split(",");
		String city = splited[0];
		String[] stateSplited = splited[1].split(" ");
		String onlyCityState = splited[0] + "," + stateSplited[1];
		
		return onlyCityState;
	}
}
