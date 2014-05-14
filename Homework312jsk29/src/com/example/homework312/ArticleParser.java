/**
 * ArticleParser is used to parse the xml file containing the articles.
 * Tried to use XPath but, kept getting errors.  So fell back to XMLPullParser.
 * May try changing in the future as XPath seems more flexible.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework312;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.Html;
import android.text.Spanned;

public class ArticleParser {

	//  Class containing the article data.
	class Article
	{
		public String title; 
		public String content;
		public String date;
		public String imageUri;
	};

	/*
	 * This method is used to parse the xml file containing
	 * the articles.
	 * 
	 * @param InputStream  input stream of the download.
	 * 
	 * @return ArrayList<Article> list of articles parsed from file
	 */
	public ArrayList<Article> parseXml(InputStream inputStream)
	{
		XmlPullParserFactory pullParserFactory;
		ArrayList<Article> articles = null;
		String defaultImage = null;

		try {
			// Create the XmlPullParser factory and parser
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			// Get the xml file and set into the parser.
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);

			int eventType = parser.getEventType();
			Article currentItem = null;
			
			// kluge to take care of html to string issue.
			int decimal = Integer.parseInt("fffc", 16);
		      //convert the decimal to character
		     char tempChar = (char)decimal;

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
					} else if (name.equals("title") && currentItem == null) {
						// check which feed so can set default image if one
						// isn't found. 
						String temp = parser.nextText();
						// Hmmm, intended to access the default image.  However,
						// this would probably mean having to do differently.
						// Should probably save images to internal storage so
						// they can be accessed the same as cached images.
                        if(temp.toLowerCase().contains("google")) {
                        	defaultImage = new String("google");
                        } else if (temp.toLowerCase().contains("yahoo")) {
                        	defaultImage = new String("yahoo");
                        }
					} else if (currentItem != null){
						if (name.equals("title")){
							currentItem.title = parser.nextText();
						} else if (name.equals("description")){
							// TODO: This should be where we pull the image from the description 
							// and put that into the content image.  Since we are already in
							// an async task we can do here.  If trying to make reusable would
							// need to put into another async task.
							Spanned result = Html.fromHtml(parser.nextText(), null, null);
							currentItem.content = result.toString(); 
							// Major hack to get rid of "obj" chars.  Need to do different
							// if have time.
							currentItem.content = currentItem.content.replace(tempChar,'\0');
						} else if (name.equals("pubDate")){
							currentItem.date = parser.nextText();
						} else if (name.equals("media:content")) {
							// Comment out for now since haven't downloaded images.
							//currentItem.imageUri = parser.getAttributeValue(null, "url");
							//currentItem.imageUri = parser.nextText();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					// If this is an end tag check if this means end of item.
					// If so then save the article.
					name = parser.getName();
					if (name.equalsIgnoreCase("item") && currentItem != null){
						// If didn't find an image than use the default
						if(currentItem.imageUri == null) {
							currentItem.imageUri = defaultImage;
						}
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
