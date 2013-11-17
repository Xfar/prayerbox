package com.uwccf.prayerbox;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.uwccf.prayerbox.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class PrayerListActivity extends ListActivity {
	public ArrayList<String> your_array_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_page);

		new GetData().execute("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prayer_list, menu);
		ActionBar bar = this.getActionBar();
		bar.setTitle(R.string.prayer_list);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.log_out:
	            SharedPreferences sharedPref = this.getSharedPreferences(ACCOUNT_SERVICE, MODE_PRIVATE);
	            SharedPreferences.Editor manage = sharedPref.edit();
	            manage.clear();
	            manage.commit();
	            Intent intent = new Intent(getApplicationContext(), PrayerLoginActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	            startActivity(intent);
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Prayer item = (Prayer) getListAdapter().getItem(position);
		String request = item.request;
		String subject = item.subject;

		Intent nextScreen = new Intent(getApplicationContext(),
				PrayerDetailsActivity.class);

		// Sending data to another Activity
		nextScreen.putExtra("subject", subject);
		nextScreen.putExtra("request", request);

		startActivity(nextScreen);
		overridePendingTransition(R.anim.right_slide_in, R.anim.no_anim);
	}

	private class GetData extends AsyncTask<String, Void, String> {
		private String result;
		private ProgressDialog Dialog = new ProgressDialog(
				PrayerListActivity.this);

		@Override
		protected String doInBackground(String... params) {
			HttpPost httpMethod = new HttpPost(
					"http://www.uwccf.ca/prayerbox/api/prayerproxy.php");

			DefaultHttpClient client = new DefaultHttpClient();
			result = null;
			try {
				HttpResponse response = client.execute(httpMethod);

				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			Dialog.setMessage("Loading Prayer Requests...");
			Dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			PrayerParser pray_parser = new PrayerParser(result);
			ArrayList<Prayer> prayer_list = pray_parser.parsePrayerList();
			PrayerAdapter prayerAdapter = new PrayerAdapter(
					PrayerListActivity.this, prayer_list);
			setListAdapter(prayerAdapter);
			Dialog.dismiss();
		}
	}
}