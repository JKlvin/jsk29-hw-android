/**
 * ArticleParser is used to parse the xml file containing the articles.
 * Tried to use XPath but, kept getting errors.  So fell back to XMLPullParser.
 * May try changing in the future as XPath seems more flexible.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework311;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class ArticleParser {

	//  Class containing the article data.
	class Article
	{
		public String title; 
		public String content;   
	};

	/*
	 * This method is used to parse the xml file containing
	 * the articles.
	 * 
	 * @param Context context of the activity
	 * 
	 * @return ArrayList<Article> list of articles parsed from file
	 */
	public ArrayList<Article> parseXml(Context context)
	{
		XmlPullParserFactory pullParserFactory;
		ArrayList<Article> articles = null;

		try {
			// Create the XmlPullParser factory and parser
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			// Get the xml file and set into the parser.
			InputStream in_s = context.getApplicationContext().getAssets().open("hrd311_data.xml");
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);


			int eventType = parser.getEventType();
			Article currentItem = null;

			// While not at the end of the document continue checking for required
			// items.
			while (eventType != XmlPullParser.END_DOCUMENT){
				String name = null;
				switch (eventType){
				case XmlPullParser.START_DOCUMENT:
					articles = new ArrayList<Article>();
					break;
				case XmlPullParser.START_TAG:
					// Found a start tag.  Check if one looking for.  It item
					// then have a new article.  If a title or content then
					// save the data.
					name = parser.getName();
					if (name.equals("item")){
						currentItem = new Article();
					} else if (currentItem != null){
						if (name.equals("title")){
							currentItem.title = parser.nextText();
						} else if (name.equals("content")){
							currentItem.content = parser.nextText(); 
						}
					}
					break;
				case XmlPullParser.END_TAG:
					// If this is an end tag check if this means end of item.
					// If so then save the article.
					name = parser.getName();
					if (name.equalsIgnoreCase("item") && currentItem != null){
						articles.add(currentItem);
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
		return articles;
	}
}
