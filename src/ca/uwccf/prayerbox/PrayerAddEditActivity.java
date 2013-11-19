package ca.uwccf.prayerbox;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

public class PrayerAddEditActivity extends Activity {

	private String mSubject;
	private EditText mSubjectView;
	private String mPrayer;
	private EditText mPrayerView;
	private AddEditTask mAddEditTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prayer_addedit);
		setTitle("New prayer request");

		mPrayerView = (EditText) findViewById(R.id.prayer);
		mSubjectView = (EditText) findViewById(R.id.subject);
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
			mAddEditTask = new AddEditTask(getApplicationContext());
			mAddEditTask.execute((Void) null);
			return true;
		}
		focusView.requestFocus();
		return false;
	}

	public class AddEditTask extends AsyncTask<Void, Void, String> {
		private String result;
		private Context mContext;

		public AddEditTask(Context context) {
			mContext = context;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpPost httpMethod = new HttpPost(
						"http://www.uwccf.ca/prayerbox/api/process.php");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("subjectInput",
						mSubject));
				nameValuePairs.add(new BasicNameValuePair("requestInput",
						mPrayer));
				nameValuePairs.add(new BasicNameValuePair("type", "request"));
				CheckBox anon = (CheckBox) findViewById(R.id.anonCheck);
				if (anon.isChecked()) {
					nameValuePairs.add(new BasicNameValuePair("anon", "1"));
				}
				httpMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = PrayerLoginActivity.client
						.execute(httpMethod);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(final String success) {
			Intent intnt = new Intent(mContext, PrayerListActivity.class);
			startActivity(intnt);
			finish();
		}

		@Override
		protected void onCancelled() {
		}
	}

}
