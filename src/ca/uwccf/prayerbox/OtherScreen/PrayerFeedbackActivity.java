package ca.uwccf.prayerbox.OtherScreen;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
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
			FeedbackTask feed = new FeedbackTask(getApplicationContext());
			feedbackView = (EditText) findViewById(R.id.feedback);
			String message = feedbackView.getText().toString();
			if(message.isEmpty()){
				feedbackView.setError(getString(R.string.error_field_required));
			}
			feed.execute(message);
	    	NavUtils.navigateUpFromSameTask(this);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public class FeedbackTask extends AsyncTask<String, Void, String> {
		
		Context mContext;
		
		public FeedbackTask(Context context){
			mContext = context;
		}
		
	    protected String doInBackground(String... feedback) {
	    	    try {
		    	    HttpPost post = new HttpPost("http://www.uwccf.ca/prayerbox/api/feedback.php");
	
		    	    List<BasicNameValuePair> results = new ArrayList<BasicNameValuePair>();
		    	    results.add(new BasicNameValuePair("comments", feedback[0]));
					CheckBox anon = (CheckBox) findViewById(R.id.anonCheck);
					if (anon.isChecked()) {
			    	    results.add(new BasicNameValuePair("name", "Anonymous"));
					} else {
						SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_SERVICE, MODE_PRIVATE);
						String user = prefs.getString("user", "Anonymous");
						results.add(new BasicNameValuePair("name",user));
					}

	    	        post.setEntity(new UrlEncodedFormEntity(results));
	    	        HttpResponse response = PrayerLoginActivity.client.execute(post);
					HttpEntity entity = response.getEntity();
					String result = EntityUtils.toString(entity);
	    	        return result;
	    	    } catch (Exception e) {
	    	        // Auto-generated catch block
	    	        Log.e("YOUR_TAG", "io exception", e);
	    	        return null;
	    	    }
	    }

	    protected void onProgressUpdate() {
	    }

	    protected void onPostExecute(String result) {
	    	Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
	    }
	}
	
}