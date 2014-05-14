/**
 * ArticlesContentProvider extends ContentProvier and is the 
 * content provider for the articles database. It allows an
 * application access to the articles contained in the database.
 * The app can either get all the articles or an individual article
 * based on id.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework312;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ArticlesContentProvider extends ContentProvider {

    public static final String TAG = ArticlesContentProvider.class.getSimpleName();

    public static final String AUTHORITY = "com.example.homework312.ArticlesContentProvider";

    private static final int ARTICLE = 1;
    private static final int ARTICLE_ID = 2;
    private static final int ARTICLE_CONTENT = 3;

    ArticlesSQLiteOpenHelper mSQLHelper;

    private static final UriMatcher mURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mURIMatcher.addURI(AUTHORITY, "articles", ARTICLE);
        mURIMatcher.addURI(AUTHORITY, "articles/#", ARTICLE_ID);
        mURIMatcher.addURI(AUTHORITY, "articles/content/#", ARTICLE_CONTENT);
    }

    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        int count;
        switch (mURIMatcher.match(uri)) {
        case ARTICLE:
            count = db.delete(Articles.Article.TABLE_NAME, selection, selectionArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType(Uri uri) {

        switch (mURIMatcher.match(uri)) {
        case ARTICLE:
            return Articles.DIR_CONTENT_TYPE;
        case ARTICLE_ID:
        		return Articles.ITEM_CONTENT_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     */
    @Override
    public Uri insert(Uri uri, ContentValues cv) {

        if (mURIMatcher.match(uri) != ARTICLE) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mSQLHelper.getWritableDatabase();
        long rowID = db.insert(Articles.Article.TABLE_NAME, null, cv);
        if (rowID > 0) {

            Uri noteUri = ContentUris.withAppendedId(Articles.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        else {
            Log.e(TAG, "insert() Error inserting article");
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {

        mSQLHelper = new ArticlesSQLiteOpenHelper(getContext());

        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String id;
        
        switch (mURIMatcher.match(uri)) {
        case ARTICLE:
            qb.setTables(Articles.Article.TABLE_NAME);
            break;
        case ARTICLE_ID:
        		qb.setTables(Articles.Article.TABLE_NAME);
        		selection = "_ID = ?";
        		id = uri.getLastPathSegment();
        		selectionArgs = new String[] {id};
        		break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mSQLHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mSQLHelper.getWritableDatabase();

        int count;
        switch (mURIMatcher.match(uri)) {
        case ARTICLE:
            count = db.update(Articles.Article.TABLE_NAME, values, selection, selectionArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;

    }
}
