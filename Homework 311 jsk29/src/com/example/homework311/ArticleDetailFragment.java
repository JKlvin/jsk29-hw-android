/**
 * ArticleDetailFragment extends Fragment and is used to display the
 * icon, title and contents of an article.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework311;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ArticleDetailFragment extends Fragment {

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
		// Allow for scrolling the text as may not fit on small screens.
		articleContents.setMovementMethod(new ScrollingMovementMethod());
		ImageView articleImage = (ImageView) getView().findViewById(R.id.articleImage);

		Bundle args = this.getArguments();

		// Check that the Bundle isn't null.  If not null get the string 
		// and display the details.  If not details display default.
		if(args != null) {
			articleContents.setText(args.getString("ARTICLE_CONTENT"));
			articleTitle.setText(args.getString("ARTICLE_TITLE"));
			// there isn't an image right now so for now the android icon 
			// is being used directly in the xml layout.
		}
		else {
			articleContents.setText("Default detail.  No Bundle received.");
		}
	}
}
