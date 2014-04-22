/**
 * MainActivity is the main activity for homework 311.  It 
 * will allow the user input articles from a xml file,
 * display the article titles in a list and display the 
 * selected article details.  Articles will be saved in
 * a database and when the use shakes the device the 
 * articles will be reloaded.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework311;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.homework311.Articles.Article;
import com.example.homework311.ShakeEventManager.OnShakeListener;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private SensorManager sensorManager;
	private Sensor accelerometer;
	private ShakeEventManager shakeManager;
	private SimpleCursorAdapter cursorAdapter;
	private ListView articleListView;
	private ArticleDetailFragment articleDetailFragment;
	//private ArticleListFragment articleListFragment;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the cursor that we will use to populate the database
		// and display data.
		CursorLoader cl = new CursorLoader(this, Articles.CONTENT_URI, Articles.Article.PROJECTION, null, null, null);
		Cursor cursor = cl.loadInBackground();

		if (cursor == null) {
			return;
		}

		//        showArticleList();
		// Setup our mapping from the cursor result to the display field
		String[] from = { Articles.Article.TITLE };
		int[] to = { R.id.article_title }; 

		// Create a simple cursor adapter.  I need to change sometime 
		// since this is deprecated.
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.article_list, cursor, from, to);

		// Associate the simple cursor adapter to the list view
		// articleListView = (ListView) this.findViewById(R.id.articlesListView); 
		articleListView = (ListView) this.findViewById(R.id.articlesListView);
		articleListView.setAdapter(cursorAdapter);
		articleListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showArticleDetails(id);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		// Need a listener to sensors for shake and reload of xml/database/etc.
		// ShakeManager initialization
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		shakeManager = new ShakeEventManager();
		shakeManager.setOnShakeListener(new OnShakeListener() {

			@Override
			public void onShake(int count) {
				handleShakeEvent(count); 
			}
		});

		// Now that everthing is put together register for the listener.
		sensorManager.registerListener(shakeManager, accelerometer, SensorManager.SENSOR_DELAY_UI);
	}


	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();

		// Make certain to unregister the sensor listener.
		sensorManager.unregisterListener(shakeManager);

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * This method is called when the shaking the device causes a shake
	 * even to occur.  
	 * 
	 * @param int task count of the shakes
	 */
	private void handleShakeEvent(int count) {

		// So need to parse the xml 
		ArticleParser myparser = new ArticleParser();
		ArrayList<ArticleParser.Article> articles = myparser.parseXml(this);

		// Now that we have the articles we can parse need to loop
		// through and put them into the database.  Not sure if we
		// should be updating or just adding on.  Right now it will 
		// just add on.
		if(articles != null) {
			Iterator iterator = articles.iterator();
			ContentResolver cr = this.getContentResolver();
			ContentValues cv;
			cr.delete(Articles.CONTENT_URI, null, null);
			Fragment fragment = getFragmentManager().findFragmentByTag("ArticleDetailFragment");
			if(fragment != null) {
				getFragmentManager().popBackStackImmediate();
				getFragmentManager()
				.beginTransaction()
				.remove(fragment)
				.commit();  }
			while (iterator.hasNext()) {
				ArticleParser.Article article = (ArticleParser.Article)iterator.next();
				//System.out.println(article);
				cv = new ContentValues();
				cv.put(Articles.Article.TITLE, article.title);
				cv.put(Articles.Article.CONTENT, article.content);
				cr.insert(Articles.CONTENT_URI, cv);
			} 
			Toast.makeText(this, "List updated", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * This method is used to display the article details.
	 * 
	 * @param long id of the database item selected to display
	 */
	public void showArticleDetails(long id) {

		// Save position for later if decide to delete the active task.
		//activeTask = position;

		// Need to get the details.  
		Uri uri = Uri.parse(Articles.CONTENT_URI + "/" + id);

		// Have to get the data for the article.  The easy way is to
		// just use the passed in id to get the cursor.
		Cursor cursor = getContentResolver().query(uri,  Articles.Article.PROJECTION, null, null,
				null);

		if(cursor != null) {

			// Need to get the data from the cursor and put into bundle.

			// Create the bundle that will get passed to the details
			// fragment.
			Bundle args = new Bundle();
			System.out.print(DatabaseUtils.dumpCursorToString(cursor));
			cursor.moveToFirst();
			args.putString("ARTICLE_TITLE", cursor.getString(cursor.getColumnIndex(Article.TITLE)));
			args.putString("ARTICLE_CONTENT", cursor.getString(cursor.getColumnIndex(Article.CONTENT)));

			cursor.close();
			// Check if the details fragment already exists.  If not create 
			// and set the arguments.
			articleDetailFragment = (ArticleDetailFragment) this.getFragmentManager().findFragmentByTag("ArticleDetailFragment");
			if(articleDetailFragment == null) {
				articleDetailFragment = new ArticleDetailFragment();

				// Now add to the fragment
				articleDetailFragment.setArguments(args);
			}

			// Now need to display the details.  Check to see if the details are 
			// displayed on the same layout as the list.
			if(!articleDetailFragment.isAdded()) {
				getFragmentManager()
				.beginTransaction()
				.add(R.id.mainPlaceholder, articleDetailFragment, "ArticleDetailFragment")
				.addToBackStack(null)
				.commit();            
			}
			else {
				// Fragment exists so assuming this is a different selection than
				// the previous task details a new one is created and replaces
				// the current details in the fragment manager.  First pop the 
				// stack so don't continue to push the fragments on the stack.
				articleDetailFragment = new ArticleDetailFragment();
				articleDetailFragment.setArguments(args);
				getFragmentManager().popBackStackImmediate();
				getFragmentManager()
				.beginTransaction()
				.replace(R.id.mainPlaceholder, articleDetailFragment, "ArticleDetailFragment")
				.addToBackStack(null)
				.commit();
			}
		}
		else {
			System.out.println("cursor is null");
		}
	}

	/*
	 * showTaskList method displays the fragment containing the articles
	 */
/*	private void showArticleList() {
			articleListFragment = (ArticleListFragment) getFragmentManager().findFragmentByTag("ArticleListFragment");
		if(articleListFragment == null) {
			articleListFragment = new ArticleListFragment();
		}

		if(articleListFragment.isAdded()) {
			getFragmentManager()
			.beginTransaction()
			.show(articleListFragment)
			.commit();
		} else {
			getFragmentManager()
			.beginTransaction()
			.add(R.id.articleListPlaceholder, articleListFragment, "ArticleListFragment")
			.commit();
		}  
	} */
}
