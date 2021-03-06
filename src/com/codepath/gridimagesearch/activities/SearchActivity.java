package com.codepath.gridimagesearch.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.gridimagesearch.R;
import com.codepath.gridimagesearch.SettingsActivity;
import com.codepath.gridimagesearch.adapters.ImageResultsAdapter;
import com.codepath.gridimagesearch.listeners.EndlessScrollListener;
import com.codepath.gridimagesearch.models.ImageResult;
import com.codepath.gridimagesearch.models.Settings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {
	private static int RSZ = 8;
	private final int INTENT_CODE = 1000;
	private int currentPage = 0;
	private EditText etQuery;
	private GridView gvResults;
	private Settings settings;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		settings = new Settings();
	}
	
	private void setupViews() {
		etQuery = (EditText)findViewById(R.id.etQuery);
		gvResults = (GridView)findViewById(R.id.gvResults);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Launch image display activity
				// Create intent
				Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
				// Get the image result to display
				ImageResult result = imageResults.get(position);
				// Pass image result to intent
				i.putExtra("result", result); // Need to be serializable or parcelable
				// Launch new activity
				startActivity(i);
			}
		});
		
		// Creates data source
		imageResults = new ArrayList<ImageResult>();
		// Attaches the data source to an adapter
		aImageResults = new ImageResultsAdapter(this, imageResults);
		// Link the adapter to the adapter view
		gvResults.setAdapter(aImageResults);

		// Set a scroll listner. 
		System.out.println("setOnScrollListner");
		gvResults.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Pass in the offset for the next query (almost increase by 8 to get new items to add)
				currentPage += RSZ;
				executeQuery(currentPage);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent i = new Intent();
			i.setClass(this, SettingsActivity.class);
			i.putExtra("settings", settings);
			startActivityForResult(i, INTENT_CODE);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("DEBUG", "onActivityResult: " + data);
		if (INTENT_CODE == requestCode) {
			settings = data.getParcelableExtra("settings");
		}
	}
	
	// When user click on the Search button
	public void onImageSearch(View v) {
		// Clear the grid view on new search
		imageResults.clear();  
		currentPage = 0;
		executeQuery(currentPage);
	}
	
	// Method to fetch more images for Search button and scrolling
	private void executeQuery(int currentPage) {
		System.out.println("executeQuery");
		query = etQuery.getText().toString();
		if (query.length() <= 0) {
			return;
		}
		AsyncHttpClient client = new AsyncHttpClient();//		
		String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?rsz="+ RSZ + "&v=1.0&start=" + currentPage + "&q=" + Uri.encode(query) + settings.generateFilterQuery(); 
		System.out.println(searchUrl);
		client.get(searchUrl, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				Log.d("DEBUG", response.toString());
				JSONArray imageResultsJson = null;
				try {
					imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
					// When you make changes to the adapter, underlying data is modified
					aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				System.out.println(errorResponse);
			}
		});
	}
}
