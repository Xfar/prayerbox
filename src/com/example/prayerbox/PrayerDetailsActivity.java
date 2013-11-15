package com.example.prayerbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

public class PrayerDetailsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pray_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);

		Intent i = getIntent();
		// Receiving the Data
		String subject = i.getStringExtra("subject");
		String request = i.getStringExtra("request");

		TextView txtRequest = (TextView) findViewById(R.id.prayer_request);

		// Displaying Received data
		setTitle(subject);
		txtRequest.setText(request);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			overridePendingTransition(0, R.anim.right_slide_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(0, R.anim.right_slide_out);
		return;
	}

}
