/**
 * ArticleSQLiteOpenHelper extends SQLiteOpenHelper and to add, delete and
 * retrieve articles from the database.  
 * 
 * @author      Jeff Klavine
 */

package com.example.homework311;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ArticlesSQLiteOpenHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "books";
    private final static int DB_VERSION = 1;

    private final static String TABLE_NAME = Articles.Article.TABLE_NAME;
    private final static String TABLE_ROW_ID = Articles.Article.ID;
    private final static String TABLE_ROW_TITLE = Articles.Article.TITLE;
    private final static String TABLE_ROW_CONTENT = Articles.Article.CONTENT;
    private final static String TABLE_ROW_ICON = Articles.Article.ICON;
    private final static String TABLE_ROW_DATE = Articles.Article.DATE;

    /*
     * Constructor
     * 
     * @param Context context of the activity
     */
    public ArticlesSQLiteOpenHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    /*
     * (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableQueryString = 
                        "CREATE TABLE " + 
                        TABLE_NAME + " (" + 
                        TABLE_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
                        TABLE_ROW_TITLE + " TEXT, " + 
                        TABLE_ROW_CONTENT + " TEXT, " + 
                        TABLE_ROW_ICON + " TEXT, " +
                        TABLE_ROW_DATE + " TEXT" + ");";
        
        db.execSQL(createTableQueryString);
    }

    /*
     * (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
