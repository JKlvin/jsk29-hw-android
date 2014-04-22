/**
 * ArticleListFragment extends ListFragment and is supposed to be
 * used to display the list of articles.  However, I couldn't get to 
 * work correctly.  I've left this in to hopefully figure out what I'm
 * doing wrong.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework311;

import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class ArticleListFragment extends ListFragment{

	private Context context;

	/*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}	

	/*
	 * (non-Javadoc)
	 * @see android.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_articlelist, container, false);

		context = getActivity();

		ContentResolver cr = context.getContentResolver();
		// Cursor cursor = cr.query(Articles.CONTENT_URI, Articles.Article.PROJECTION, null, null, null);

		CursorLoader cl = new CursorLoader(context, Articles.CONTENT_URI, Articles.Article.PROJECTION, null, null, null);
		Cursor cursor = cl.loadInBackground();

		if(cursor != null) {
			// Setup our mapping from the cursor result to the display field
			//String[] from = { Articles.Article.ICON, Articles.Article.TITLE };
			// int[] to = { android.R.id.icon1, android.R.id.text1 };    
			String[] from = { Articles.Article.TITLE };
			int[] to = { R.id.article_title }; 

			// Create a simple cursor adapter.  
			SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.fragment_articlelist, cursor, from, to);

			// Associate the simple cursor adapter to the list view
			ListView listView = (ListView) view.findViewById(R.id.article_list);
			listView.setAdapter(cursorAdapter);
			
		}
		
		return view;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.ListFragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		//super.onViewCreated(view, savedInstanceState);

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {

		// Call the main activity to display the task details.
		((MainActivity) getActivity()).showArticleDetails(id);

		// view.setSelected(true);

		super.onListItemClick(listView, view, position, id);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	/*
	 * showTasks is used to show all task titles currently stored.  
	 */
	/*private void showTasks() {
		// Get the tasks from the db
		tasks = taskDbHelper.getTasks();

		// This should be done more elegantly but, wasn't getting to 
		// work properly so do the brute force method.

		// Now loop thru the tasks adding only the task titles to 
		// populate the listview.
		if(tasks.size() > 0) {
			ArrayList<String> taskTitles= new ArrayList<String>();
			for (int i = 0; i < tasks.size(); i++) {
				taskTitles.add(tasks.get(i).getTaskTitle());
			}
			// Now add to the adaptor
			taskList.clear();
			taskList.addAll(taskTitles);
			taskArrayAdapter.notifyDataSetChanged();
		}
		else {
			taskList.clear();
			taskArrayAdapter.notifyDataSetChanged();
		}
	} */
}