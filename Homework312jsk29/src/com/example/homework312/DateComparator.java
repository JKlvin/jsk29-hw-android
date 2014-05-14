/**
 * DateComparator is used to order the articles in an ArrayList by date.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework312;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.example.homework312.ArticleParser.Article;

public class DateComparator implements Comparator<Article>{

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Article lhs, Article rhs) {
		
		// Set up a SimpleDateFormat with the same format contained in an RSS feed.
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
		try {
			Date lhsDate = formatter.parse(lhs.date);
			Date rhsDate = formatter.parse(rhs.date);
			// Now that we have to two dates compare them.  A "larger" number 
			// of seconds means the date is later so return indication that 
			// this date should be higher in the array.
			if(lhsDate.after(rhsDate)) {
				return -1;
			}
			else {
				return 1;
			}
		}
		catch(Exception e) {
			// TODO add logging.
			return -1;
		}
	}
}
