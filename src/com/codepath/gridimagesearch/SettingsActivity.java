package com.codepath.gridimagesearch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.gridimagesearch.models.Settings;

public class SettingsActivity extends Activity {
	
	private Settings settings;
	private Spinner spImageSize;
	private Spinner spColorFilter;
	private Spinner spImageType;
	private EditText etSite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		settings = getIntent().getParcelableExtra("settings");
		setUp();
	}

	private void setUp() {
		spImageSize = (Spinner) findViewById(R.id.spSize);
		selectSpinnerItemByString(spImageSize, settings.getSize());
		spColorFilter = (Spinner) findViewById(R.id.spColor);
		selectSpinnerItemByString(spColorFilter, settings.getColor());
		spImageType = (Spinner) findViewById(R.id.spType);
		selectSpinnerItemByString(spImageType, settings.getType());
		etSite = (EditText) findViewById(R.id.etSite);
		etSite.setText(settings.getSearch());
	}
	
	public static void selectSpinnerItemByString(Spinner spnr, String value)
	{
	    for (int position = 0; position < spnr.getCount(); position++)	{
	        if(spnr.getItemAtPosition(position).equals(value))	{
	            spnr.setSelection(position);
	            return;
	        }
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
		} else if (id == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		i.putExtra("settings", settings);
		setResult(RESULT_OK, i);
		finish();
	}
	
	public void onClickSave(View v) {
		settings.setSize(spImageSize.getSelectedItem().toString());
		settings.setColor(spColorFilter.getSelectedItem().toString());
		settings.setType(spImageType.getSelectedItem().toString());
		settings.setSearch(etSite.getText().toString());
		Intent i = new Intent();
		i.putExtra("settings", settings);
		setResult(RESULT_OK, i);
		finish();
	}
}
