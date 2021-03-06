package ca.uwccf.prayerbox.OtherScreen;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.Data.PrayerApplication;
import ca.uwccf.prayerbox.Data.PrayerParser;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
import ca.uwccf.prayerbox.MainScreen.MainTabbedFragmentActivity;
import ca.uwccf.prayerbox.R.id;
import ca.uwccf.prayerbox.R.layout;
import ca.uwccf.prayerbox.R.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

public class PrayerDetailsActivity extends Activity {
	private String prayer_id;
	private boolean mIsAdd;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_SERVICE, MODE_PRIVATE);
		if(!prefs.contains("user")){
			Intent intent = new Intent(getApplicationContext(),
					PrayerLoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
		}
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
			if(PrayerLoginActivity.intInfo.isNetworkAvailable(getApplicationContext())){
				StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.prayer_log_add_url),
					new Response.Listener<String>() {
				        @Override
				        public void onResponse(String result) {
							MainTabbedFragmentActivity.refresh();
				        }
				    },
				    new Response.ErrorListener() {
				        @Override
				        public void onErrorResponse(VolleyError error) {
				        }
				    }){
				    @Override
				    protected Map<String, String> getParams() throws AuthFailureError {
						SharedPreferences prefs = getApplicationContext().getSharedPreferences("account", 0);
						String user = prefs.getString("user","");
				        Map<String, String> map = new HashMap<String, String>();
				        map.put("user", user);
				        map.put("prayer_id", prayer_id);
				        return map;
				    }
				};
				PrayerApplication.getInstance().addToRequestQueue(request);
				mIsAdd = false;
				invalidateOptionsMenu();
			}else{
				Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.details_delete:
			if(PrayerLoginActivity.intInfo.isNetworkAvailable(getApplicationContext())){
				StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.prayer_log_del_url),
					new Response.Listener<String>() {
				        @Override
				        public void onResponse(String result) {
							MainTabbedFragmentActivity.refresh();
				        }
				    },
				    new Response.ErrorListener() {
				        @Override
				        public void onErrorResponse(VolleyError error) {
				        }
				    }){
				    @Override
				    protected Map<String, String> getParams() throws AuthFailureError {
						SharedPreferences prefs = getApplicationContext().getSharedPreferences("account", 0);
						String user = prefs.getString("user","");
				        Map<String, String> map = new HashMap<String, String>();
				        map.put("user", user);
				        map.put("prayer_id", prayer_id);
				        return map;
				    }
				};
				PrayerApplication.getInstance().addToRequestQueue(request);
				mIsAdd = true;
				invalidateOptionsMenu();
			}else{
				Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
			}
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
