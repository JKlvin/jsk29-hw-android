/**
 * MainActivity is the main activity for homework 314.  It 
 * will allow the user to download the current weather 
 * conditions and three day forecast for a selected city.
 * 
 * @author      Jeff Klavine
 */

package com.example.homework314jsk29;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {
	private String state;
	private String city;
	private ProgressDialog pd;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override 
	public void onResume() {
		super.onResume();
		
		// Get the shared preferences to get the last city/state
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		city = sharedPreferences.getString("CITY", "Seattle");
		state = sharedPreferences.getString("STATE", "WA");
		
		// Make certain to hide the views since there won't 
		// be anything in them until download finishes.
		hideViews();
		
		getWeather(city + "," + state);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		
		// Save the alarm set and alarm time values.  That way
		// whenever the activity is destroyed the values are 
		// saved.
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putString("CITY", city);
		editor.putString("STATE", state);
		
		editor.commit();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		final Dialog city_input_dialog = new Dialog(MainActivity.this);

		switch(item.getItemId()) {
		case R.id.action_city_input:
		{
			// get prompts.xml view
			LayoutInflater layoutInflater = LayoutInflater.from(this);

			View cityInputView = layoutInflater.inflate(R.layout.city_input, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			// set prompts.xml to be the layout file of the alertdialog builder
			alertDialogBuilder.setView(cityInputView);

			final EditText input = (EditText) cityInputView.findViewById(R.id.cityInput);

			// setup a dialog window
			alertDialogBuilder
			.setCancelable(true)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// get user input and set it to result
					// TODO this is where we would launch the task to input weather
					String city = input.getText().toString();
					getWeather(city);
				}
			})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

			// create an alert dialog
			AlertDialog alertD = alertDialogBuilder.create();

			alertD.show();
		}
		break;
		default:
			// Seems like there should be some kind 
			// of setting or error tagged here.  Not
			// sure how android is usually expected
			// to deal with errors so do nothing for
			// now.
			break;
		}
		
		int id = item.getItemId();
		if (id == R.id.action_city_input) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * This method is used to start the asyn task to download the
	 * weather from the internet.
	 */
	private void getWeather(String city) {
		new getWeatherAsyncTask().execute(city, null);
	}
	
	/*
	 * This method is an async task used to download weather reports.  It will 
	 * also parse both the current conditions and forecast for at least the
	 * next three days.
	 * 
	 */
	private class getWeatherAsyncTask extends AsyncTask<String, Void, CityWeather> {
		
		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(MainActivity.this);
			pd.setTitle("Getting weather....");
			pd.setMessage("Please wait.");
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected CityWeather doInBackground(String... city) {

			String baseWundergroundCurrentUri = "http://api.wunderground.com/api/e5490878a7027e5d/conditions/q/"; 
			String baseWundergroundForecastUri = "http://api.wunderground.com/api/e5490878a7027e5d/forecast10day/q/";
			String baseZipSearchUri = "http://maps.googleapis.com/maps/api/geocode/xml?address=";
			String endBaseZipSearchUri = "&sensor=true";
			String splited[] = new String[2];
			CityWeather cityWeather = null;

			AndroidHttpClient client = AndroidHttpClient.newInstance("Homework314jsk29");

			HttpResponse response = null;
			HttpGet request = null;

			// Build up the full URIs.  To do this will need to get the city/state from input.  However,
			// the input could be a zip.  Check if a zip first and if so then query the internet for the 
			// matching city/state.
			String cityState = null;
			if(Character.isDigit(city[0].charAt(0))) {

				String uri = baseZipSearchUri + city[0] + endBaseZipSearchUri;
				request = new HttpGet(uri);
				try {
					response = client.execute(request);
				}
				catch (IOException e) {
					e.printStackTrace();
				}

				// If response to zip request parse the results.  It there is
				// a failure then set cityState to null so know not to perform
				// any more work.
				if(response != null) {
					ZipToCityParser zcp = new ZipToCityParser();
					try {
						String time = getCurrentTime();
						cityState = zcp.parseXml(response.getEntity().getContent());
					}
					catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
						// Set cityState to null so can skip getting any other data.
						cityState = null;
					}
				}
			}
			else {
				cityState = city[0];
			}

			// Only perform work if were able to get an actual city/state combo.
			if(cityState != null) {
				// Now split the city state apart so can build the http request.
				splited = cityState.split(","); 
				// Wunderground needs underscores in a city rather than spaces so replace all
				// spaces with underscores.
				splited[0] = splited[0].replace(" ", "_");
				splited[1] = splited[1].replace(" " , "");

				// Now take the city/state and finish building the URIs.
				String currentUri = baseWundergroundCurrentUri + splited[1] + "/" + splited[0] + ".xml";
				String forecastUri = baseWundergroundForecastUri  + splited[1] + "/" + splited[0] + ".xml";

				cityWeather = new CityWeather();

				request = new HttpGet(currentUri);
				try {
					response = client.execute(request);
				}
				catch (IOException e) {
					e.printStackTrace();
				}

				if(response != null) {
					CurrentConditionsParser ccp = new CurrentConditionsParser();
					try {
						String time = getCurrentTime();
						cityWeather.currentConditions = ccp.parseXml(response.getEntity().getContent());
						cityWeather.currentConditions.time = time;
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
						cityWeather.currentConditions = null;
					}
				}

				// If the input city was not valid then currentConditions will be null.
				// Don't do any more work and set cityWeather to null;
				if(cityWeather.currentConditions != null) {
					request = new HttpGet(forecastUri);
					try {
						response = client.execute(request);
					}
					catch (IOException e) {
						e.printStackTrace();
					}

					if(response != null) {
						ForecastParser fp = new ForecastParser();
						try {
							cityWeather.dialyForecast = fp.parseXml(response.getEntity().getContent());
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}

					// Now we can get the graphics icon for the current weather conditions.
					// First check if there the currentConditions is null.  This can happen
					// if the city used in the query doesn't exist.
					request = new HttpGet(cityWeather.currentConditions.iconUri);
					try {
						response = client.execute(request);
					}
					catch (IOException e) {
						e.printStackTrace();
					}

					if(response != null) {
						try {
							cityWeather.currentCondition = BitmapFactory.decodeStream(response.getEntity().getContent());
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}

					//  Need to get the icons for the three days of forecast.
					// Now we can get the graphics icon for the current weather conditions.
					// For some reason the downloading of the icons from the web is hanging.
					// Once figure out why then reenable.
			/*		for(int j = 0; j < 3; j++) {
						String st = cityWeather.dialyForecast.get(j).iconUri;
						request = new HttpGet(cityWeather.dialyForecast.get(j).iconUri);
						try {
							response = client.execute(request);
						}
						catch (IOException e) {
							e.printStackTrace();
						}

						// This is a bit ugly.  Need to make better if have time.
						if(response != null) {
							try {
								if(j == 0) {
									cityWeather.forecastDay1 = BitmapFactory.decodeStream(response.getEntity().getContent());
								} else if (j == 1) {
									cityWeather.forecastDay2 = BitmapFactory.decodeStream(response.getEntity().getContent());
								} else {
									cityWeather.forecastDay3 = BitmapFactory.decodeStream(response.getEntity().getContent());
								}
							}
							catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
					} */
				}
				else {
					cityWeather = null;
				}
			}

			client.close();

			// Before returning the articles sort by date/time.

			return cityWeather;     
		} 

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(CityWeather result){
			// Now that we have finished getting all the feeds save to the database.
			if (pd!=null) {
				pd.dismiss();
			}

			// TODO need to check results as an incorrect entry will cause failure.

			if(result != null) {
				showWeather(result);
				city = result.currentConditions.city;
				state = result.currentConditions.state;
			}
			else {
				Toast.makeText(getApplicationContext(), "Invalid city entry",
						Toast.LENGTH_LONG).show();
			}
		}		
	}
	
	/*
	 * This method is used to put all the weather data into the views.
	 * 
	 * @param weather contains the weather both current and forecast.
	 */
	private void showWeather(CityWeather weather) {
		ImageView imageView = (ImageView) findViewById(R.id.conditionImage);
		imageView.setImageBitmap(weather.currentCondition);
		TextView textView = (TextView) findViewById(R.id.dateTextView);
		textView.setText(weather.currentConditions.time);
		textView = (TextView) findViewById(R.id.tempTextView);
		textView.setText(weather.currentConditions.temp);
		textView = (TextView) findViewById(R.id.conditionTextView);
		textView.setText(weather.currentConditions.weather);
		textView = (TextView) findViewById(R.id.locationText);
		textView.setText(weather.currentConditions.city + ", " + weather.currentConditions.state);
		
		LinearLayout day1_layout = (LinearLayout)findViewById(R.id.day1_layout);
		textView = (TextView) day1_layout.findViewById(R.id.day_textView);
		textView.setText(weather.dialyForecast.get(0).day);
		textView = (TextView) day1_layout.findViewById(R.id.condition_textView);
		textView.setText(weather.dialyForecast.get(0).conditions);
		
		// Now have to get the temps
		String high = (weather.dialyForecast.get(0).highTempF + "F (" + weather.dialyForecast.get(0).highTempC + "C)");
		String low = (weather.dialyForecast.get(0).lowTempF + "F (" + weather.dialyForecast.get(0).lowTempC + "C)");
		textView = (TextView) day1_layout.findViewById(R.id.high_textView);
		textView.setText(high);
		textView = (TextView) day1_layout.findViewById(R.id.low_textView);
		textView.setText(low);
		
		LinearLayout day2_layout = (LinearLayout)findViewById(R.id.day2_layout);
		textView = (TextView) day2_layout.findViewById(R.id.day_textView);
		textView.setText(weather.dialyForecast.get(1).day);
		textView = (TextView) day2_layout.findViewById(R.id.condition_textView);
		textView.setText(weather.dialyForecast.get(1).conditions);
		
		// Now have to get the temps
		high = (weather.dialyForecast.get(1).highTempF + "F (" + weather.dialyForecast.get(1).highTempC + "C)");
		low = (weather.dialyForecast.get(1).lowTempF + "F (" + weather.dialyForecast.get(1).lowTempC + "C)");
		textView = (TextView) day2_layout.findViewById(R.id.high_textView);
		textView.setText(high);
		textView = (TextView) day2_layout.findViewById(R.id.low_textView);
		textView.setText(low);
		
		LinearLayout day3_layout = (LinearLayout)findViewById(R.id.day3_layout);
		textView = (TextView) day3_layout.findViewById(R.id.day_textView);
		textView.setText(weather.dialyForecast.get(2).day);
		textView = (TextView) day3_layout.findViewById(R.id.condition_textView);
		textView.setText(weather.dialyForecast.get(2).conditions);
		
		// Now have to get the temps
		high = (weather.dialyForecast.get(2).highTempF + "F (" + weather.dialyForecast.get(2).highTempC + "C)");
		low = (weather.dialyForecast.get(2).lowTempF + "F (" + weather.dialyForecast.get(2).lowTempC + "C)");
		textView = (TextView) day3_layout.findViewById(R.id.high_textView);
		textView.setText(high);
		textView = (TextView) day3_layout.findViewById(R.id.low_textView);
		textView.setText(low);
		
		showViews();
		
	}
	
	/*
	 * This method returns for current time.  Possibly should have gotten the 
	 * time from the download but, didn't like the format at the time.
	 * 
	 * @return This returns a string containing the current date/time.
	 */
	private String getCurrentTime() {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		
		// Now set up the date/time string
		String hour;
		if(today.hour < 10) {
			hour = "0" + Integer.toString(today.hour);
		}
		else {
			hour = Integer.toString(today.hour);
		}
		String minute;
		if(today.minute < 10) {
			minute = "0" + Integer.toString(today.minute);
		}
		else {
			minute = Integer.toString(today.minute);
		}
		
		String currentTime = Integer.toString(today.month) + "/" + Integer.toString(today.monthDay) + "/" + Integer.toString(today.year) + " " + hour + ":" + minute;
		return currentTime;
	}
	
	/*
	 * This method is used to hide the views from the user.  Really just used
	 * to make the UI look a bit prettier when waiting for the weather data
	 * to load.
	 */
	private void hideViews() {
		ImageView imageView = (ImageView) findViewById(R.id.conditionImage);
		imageView.setVisibility(View.INVISIBLE);
		TextView textView = (TextView) findViewById(R.id.dateTextView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) findViewById(R.id.tempTextView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) findViewById(R.id.conditionTextView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) findViewById(R.id.locationText);
		textView.setVisibility(View.INVISIBLE);
		
		LinearLayout day1_layout = (LinearLayout)findViewById(R.id.day1_layout);
		textView = (TextView) day1_layout.findViewById(R.id.day_textView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) day1_layout.findViewById(R.id.condition_textView);
		textView.setVisibility(View.INVISIBLE);
		
		textView = (TextView) day1_layout.findViewById(R.id.high_textView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) day1_layout.findViewById(R.id.low_textView);
		textView.setVisibility(View.INVISIBLE);
		
		LinearLayout day2_layout = (LinearLayout)findViewById(R.id.day2_layout);
		textView = (TextView) day2_layout.findViewById(R.id.day_textView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) day2_layout.findViewById(R.id.condition_textView);
		textView.setVisibility(View.INVISIBLE);
		
		textView = (TextView) day2_layout.findViewById(R.id.high_textView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) day2_layout.findViewById(R.id.low_textView);
		textView.setVisibility(View.INVISIBLE);
		
		LinearLayout day3_layout = (LinearLayout)findViewById(R.id.day3_layout);
		textView = (TextView) day3_layout.findViewById(R.id.day_textView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) day3_layout.findViewById(R.id.condition_textView);
		textView.setVisibility(View.INVISIBLE);
		
		textView = (TextView) day3_layout.findViewById(R.id.high_textView);
		textView.setVisibility(View.INVISIBLE);
		textView = (TextView) day3_layout.findViewById(R.id.low_textView);
		textView.setVisibility(View.INVISIBLE);	
	}
	
	/*
	 * This method is used to show the views from the user.
	 */
	private void showViews() {
		ImageView imageView = (ImageView) findViewById(R.id.conditionImage);    //retrieve the ImageView widget from the layout
		imageView.setVisibility(View.VISIBLE);
		TextView textView = (TextView) findViewById(R.id.dateTextView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) findViewById(R.id.tempTextView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) findViewById(R.id.conditionTextView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) findViewById(R.id.locationText);
		textView.setVisibility(View.VISIBLE);
		
		LinearLayout day1_layout = (LinearLayout)findViewById(R.id.day1_layout);
		textView = (TextView) day1_layout.findViewById(R.id.day_textView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) day1_layout.findViewById(R.id.condition_textView);
		textView.setVisibility(View.VISIBLE);
		
		textView = (TextView) day1_layout.findViewById(R.id.high_textView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) day1_layout.findViewById(R.id.low_textView);
		textView.setVisibility(View.VISIBLE);
		
		LinearLayout day2_layout = (LinearLayout)findViewById(R.id.day2_layout);
		textView = (TextView) day2_layout.findViewById(R.id.day_textView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) day2_layout.findViewById(R.id.condition_textView);
		textView.setVisibility(View.VISIBLE);
		
		textView = (TextView) day2_layout.findViewById(R.id.high_textView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) day2_layout.findViewById(R.id.low_textView);
		textView.setVisibility(View.VISIBLE);
		
		LinearLayout day3_layout = (LinearLayout)findViewById(R.id.day3_layout);
		textView = (TextView) day3_layout.findViewById(R.id.day_textView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) day3_layout.findViewById(R.id.condition_textView);
		textView.setVisibility(View.VISIBLE);
		
		textView = (TextView) day3_layout.findViewById(R.id.high_textView);
		textView.setVisibility(View.VISIBLE);
		textView = (TextView) day3_layout.findViewById(R.id.low_textView);
		textView.setVisibility(View.VISIBLE);	
	}
}
