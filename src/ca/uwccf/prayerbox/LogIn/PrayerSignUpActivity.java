package ca.uwccf.prayerbox.LogIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.Data.PrayerParser;
import ca.uwccf.prayerbox.MainScreen.MainTabbedFragmentActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PrayerSignUpActivity extends Activity {
	private String mDisplayName;
	private String mEmail;
	private String mPassword;
	private EditText mEmailView;
	private EditText mDisplayNameView;
	private EditText mPasswordView;
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prayer_signup);
		getActionBar().hide();

		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.requestFocus();
		mPasswordView = (EditText) findViewById(R.id.password);
		mDisplayNameView = (EditText) findViewById(R.id.displayname);
		mEmailView.requestFocus();
		mDisplayNameView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == EditorInfo.IME_ACTION_GO) {
							attemptSignUp();
							return true;
						}
						return false;
					}
				});

		findViewById(R.id.sign_up_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSignUp();
					}
				});
	}

	public void attemptSignUp() {
		mDisplayNameView.setError(null);
		mEmailView.setError(null);
		mPasswordView.setError(null);

		mDisplayName = mDisplayNameView.getText().toString();
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		boolean error = false;
		if (mDisplayName.isEmpty()) {
			mDisplayNameView.setError(getString(R.string.error_field_required));
			error = true;
		}
		if (mPassword.isEmpty()) {
			mPasswordView.setError(getString(R.string.error_field_required));
			error = true;
		}
		if (mEmail.isEmpty()) {
			mEmailView.setError(getString(R.string.error_field_required));
			error = true;
		} else if (!mEmail.matches(EMAIL_PATTERN)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			error = true;
		}
		if(!PrayerLoginActivity.intInfo.isNetworkAvailable(getApplicationContext())){
			PrayerLoginActivity.intInfo.noInternetToast(getApplicationContext());
			return;
		}
		if(!error){
			UserSignUpTask userSignUp = new UserSignUpTask(getApplicationContext());
			userSignUp.execute();
		}
	}

	public class UserSignUpTask extends AsyncTask<Void, Void, String> {
		private Context mContext;
		private String result;

		public UserSignUpTask(Context context) {
			mContext = context;
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpPost httpMethod = new HttpPost(
						"http://www.uwccf.ca/prayerbox/api/signupproxy.php");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("displayname",
						mDisplayName));
				nameValuePairs.add(new BasicNameValuePair("email", mEmail));
				nameValuePairs
						.add(new BasicNameValuePair("password", mPassword));
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
		protected void onPostExecute(final String result) {
			PrayerParser pray_parser = new PrayerParser(result);
			HashMap<String, String> accountInfo = pray_parser.parseLogin();
			if (!accountInfo.get("error").isEmpty()) {
				Toast.makeText(getApplicationContext(),
						accountInfo.get("error"), Toast.LENGTH_LONG).show();
			} else {
				SharedPreferences sharedPref = mContext.getSharedPreferences(
						ACCOUNT_SERVICE, MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("user", accountInfo.get("user"));
				editor.putString("user", accountInfo.get("email"));
				editor.commit();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
				Intent intent = new Intent(getApplicationContext(),
						MainTabbedFragmentActivity.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();
			}
		}
	}
}
