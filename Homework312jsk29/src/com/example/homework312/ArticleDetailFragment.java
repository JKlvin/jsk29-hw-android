/**
 * ArticleDetailFragment extends Fragment and is used to display the
 * icon, title, date, and contents of an article.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework312;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ArticleDetailFragment extends Fragment {
	
	Context thisContext;

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
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		thisContext = container.getContext();
		return inflater.inflate(R.layout.fragment_articledetail, container, false);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Get the views to populate the details.
		TextView articleTitle = (TextView) getView().findViewById(R.id.article_titleTextView);
		TextView articleContents = (TextView) getView().findViewById(R.id.articleContentsTextView);
		TextView articleDate = (TextView) getView().findViewById(R.id.article_dateTextView);
		// Allow for scrolling the text as may not fit on small screens.
		articleContents.setMovementMethod(new ScrollingMovementMethod());
		ImageView articleImage = (ImageView) getView().findViewById(R.id.articleImage);

		Bundle args = this.getArguments();

		// Check that the Bundle isn't null.  If not null get the string 
		// and display the details.  If not details display default.
		if(args != null) {
			articleContents.setText(args.getString("ARTICLE_CONTENT"));
			articleTitle.setText(args.getString("ARTICLE_TITLE"));
			articleDate.setText(args.getString("ARTICLE_DATE"));
			// Need to figure out how to properly do once get real images.
			articleImage.setImageResource(getResources().getIdentifier(args.getString("ARTICLE_IMAGE"), "drawable", thisContext.getPackageName()));
			
			
		}
		else {
			articleContents.setText("Default detail.  No Bundle received.");
		}
	}
}
