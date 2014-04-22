/**
 * Articles extends BaseColumns and provides the database column data.
 * 
 * @author      Jeff Klavine
 */
package com.example.homework311;

import com.example.homework311.ArticlesContentProvider;
import android.net.Uri;
import android.provider.BaseColumns;

public class Articles implements BaseColumns {
	public static final Uri CONTENT_URI = Uri.parse("content://" + ArticlesContentProvider.AUTHORITY + "/articles");
    public static final String DIR_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.example.homework311.articles";
    public static final String ITEM_CONTENT_TYPE = "vnd.android.cursor.item/vnd.com.example.homework311.article";

    public static final class Article {
        
        public static final String TABLE_NAME = "article";

        public static final String ID = BaseColumns._ID;
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String ICON = "icon";
        public static final String DATE = "date";

        public static final String[] PROJECTION = new String[] {
        /* 0 */ Articles.Article.ID,
        /* 1 */ Articles.Article.TITLE,
        /* 2 */ Articles.Article.CONTENT,
        /* 3 */ Articles.Article.ICON,
        /* 4 */ Articles.Article.DATE};

    }
}
