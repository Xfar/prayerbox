package ca.uwccf.prayerbox.OtherScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.Data.PrayerParser;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
import ca.uwccf.prayerbox.MainScreen.MainTabbedFragmentActivity;
import ca.uwccf.prayerbox.R.id;
import ca.uwccf.prayerbox.R.layout;
import ca.uwccf.prayerbox.R.menu;
import ca.uwccf.prayerbox.R.string;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PrayerAddEditActivity extends Activity {

	private String mSubject;
	private EditText mSubjectView;
	private String mPrayer;
	private String mUser;
	private EditText mPrayerView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prayer_addedit);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setTitle("New Prayer Request");

		mPrayerView = (EditText) findViewById(R.id.prayer);
		mSubjectView = (EditText) findViewById(R.id.subject);
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_SERVICE, MODE_PRIVATE);
		mUser = prefs.getString("user", "");
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mSubjectView, InputMethodManager.SHOW_IMPLICIT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.prayer_addedit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.submit:
			return submitRequest();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean submitRequest() {
		mSubject = mSubjectView.getText().toString();
		mPrayer = mPrayerView.getText().toString();
		View focusView = null;
		if (TextUtils.isEmpty(mSubject)) {
			mSubjectView.setError(getString(R.string.error_field_required));
			focusView = mSubjectView;
		} else if (TextUtils.isEmpty(mPrayer)) {
			mPrayerView.setError(getString(R.string.error_field_required));
			focusView = mPrayerView;
		} else {
			if(PrayerLoginActivity.intInfo.isNetworkAvailable(getApplicationContext())){
				StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.forgot_password_url),
					new Response.Listener<String>() {
				        @Override
				        public void onResponse(String result) {
							Intent intnt = new Intent(getApplicationContext(), MainTabbedFragmentActivity.class);
							startActivity(intnt);
							finish();
				        }
				    },
				    new Response.ErrorListener() {
				        @Override
				        public void onErrorResponse(VolleyError error) {
				        }
				    }){
					    @Override
					    protected Map<String, String> getParams() throws AuthFailureError {

					        Map<String, String> map = new HashMap<String, String>();
							map.put("subjectInput",mSubject);
							map.put("user",mUser);
							map.put("requestInput",mPrayer);
							map.put("type", "request");
							CheckBox anon = (CheckBox) findViewById(R.id.anonCheck);
							if (anon.isChecked()) {
								map.put("anon", "1");
							}
					        return map;
					    }
					};
				PrayerLoginActivity.queue.add(request);
			}else{
				Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
			}
			return true;
		}
		focusView.requestFocus();
		return false;
	}

}
