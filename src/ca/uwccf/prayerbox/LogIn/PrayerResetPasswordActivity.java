package ca.uwccf.prayerbox.LogIn;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.Data.PrayerApplication;
import ca.uwccf.prayerbox.Data.PrayerParser;
import ca.uwccf.prayerbox.MainScreen.MainTabbedFragmentActivity;

public class PrayerResetPasswordActivity extends Activity {

	private EditText mEmailView;
	private String mEmail;
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		getActionBar().hide();
		mEmailView = (EditText) findViewById(R.id.forgot_password_email);
		findViewById(R.id.forgot_password_request_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						submitEmail();
					}
				});

		mEmailView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == EditorInfo.IME_ACTION_GO) {
							submitEmail();
							return true;
						}
						return false;
					}
				});
	}

	private void submitEmail() {
		mEmail = mEmailView.getText().toString();
		boolean error = false;
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
			StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.forgot_password_url),
			new Response.Listener<String>() {
		        @Override
		        public void onResponse(String result) {
					Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
					Intent intent = new Intent(getApplicationContext(),
							PrayerLoginActivity.class);
					startActivity(intent);
					overridePendingTransition(0, 0);
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
			        map.put("email", mEmail);
			        return map;
			    }
			};
			PrayerApplication.getInstance().addToRequestQueue(request);
		}
	}
}
