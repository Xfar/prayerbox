package ca.uwccf.prayerbox.OtherScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
import ca.uwccf.prayerbox.MainScreen.MainTabbedFragmentActivity;
import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PrayerFeedbackActivity extends Activity {
	private EditText feedbackView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_feedback);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.prayer_feedback, menu);
		getActionBar().setTitle(ca.uwccf.prayerbox.R.string.send_feedback);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.submit:
			if(PrayerLoginActivity.intInfo.isNetworkAvailable(getApplicationContext())){
				feedbackView = (EditText) findViewById(R.id.feedback);
				final String message = feedbackView.getText().toString();
				if(message.isEmpty()){
					feedbackView.setError(getString(R.string.error_field_required));
					return true;
				}
				StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.prayer_feedback_url),
					new Response.Listener<String>() {
				        @Override
				        public void onResponse(String result) {
					    	Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
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
						CheckBox anon = (CheckBox) findViewById(R.id.anonCheck);
						if (anon.isChecked()) {
				    	    map.put("name", "Anonymous");
						} else {
							SharedPreferences prefs = getSharedPreferences(ACCOUNT_SERVICE, MODE_PRIVATE);
							String user = prefs.getString("user", "Anonymous");
							map.put("name",user);
						}
				        map.put("comments", message);
				        return map;
				    }
				};
				PrayerLoginActivity.queue.add(request);
		    	NavUtils.navigateUpFromSameTask(this);
			}else {
				Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
				return true;
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}