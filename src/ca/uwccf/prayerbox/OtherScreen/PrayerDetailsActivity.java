package ca.uwccf.prayerbox.OtherScreen;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.MainScreen.PrayerListActivity;
import ca.uwccf.prayerbox.MainScreen.PrayerLogDataHandler;
import ca.uwccf.prayerbox.R.id;
import ca.uwccf.prayerbox.R.layout;
import ca.uwccf.prayerbox.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class PrayerDetailsActivity extends Activity {
	private String prayer_id;
	private boolean mIsAdd;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prayer_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);

		Intent i = getIntent();
		// Receiving the Data
		String subject = i.getStringExtra("subject");
		String request = i.getStringExtra("request");
		String author = i.getStringExtra("author");
		String date = i.getStringExtra("date");
		prayer_id = i.getStringExtra("prayer_id");
		mIsAdd = !i.getBooleanExtra("isStarred", false);
		TextView txtRequest = (TextView) findViewById(R.id.prayer_request);
		TextView txtAuthor = (TextView) findViewById(R.id.prayer_author);
		TextView txtDate = (TextView) findViewById(R.id.prayer_date);

		// Displaying Received data
		setTitle(subject);
		txtRequest.setText(request);
		txtAuthor.setText(author);
		txtDate.setText(date);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.prayer_details, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
	  MenuItem pluspray = menu.findItem(R.id.details_pluspray);
	  MenuItem delete = menu.findItem(R.id.details_delete);
	  pluspray.setVisible(mIsAdd);
	  pluspray.setEnabled(mIsAdd);
	  delete.setEnabled(!mIsAdd);
	  delete.setVisible(!mIsAdd);
	  return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.details_pluspray:
			PrayerLogDataHandler data = new PrayerLogDataHandler(
					this.getApplicationContext(), true);
			data.execute(prayer_id);
			PrayerListActivity.refresh();
			mIsAdd = false;
			invalidateOptionsMenu();
			return true;
		case R.id.details_delete:
			PrayerLogDataHandler data_2 = new PrayerLogDataHandler(
					this.getApplicationContext(), false);
			data_2.execute(prayer_id);
			PrayerListActivity.refresh();
			mIsAdd = true;
			invalidateOptionsMenu();
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		return;
	}

}
