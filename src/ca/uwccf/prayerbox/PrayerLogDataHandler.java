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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

public class PrayerLogDataHandler extends
		AsyncTask<String, Void, String> {
	private String result;
	private Context mContext;
	private String mUser;
	private boolean mAddToLog;

	public PrayerLogDataHandler(Context context, Boolean addToLog) {
		mContext = context;
		mAddToLog = addToLog;
	}

	@Override
	protected String doInBackground(String... arg0) {
		try {
			String url;
			if(mAddToLog){
				url = "http://www.uwccf.ca/prayerbox/api/prayerlistaddproc.php";
			}else {
				url = "http://www.uwccf.ca/prayerbox/api/prayerlistdelproc.php";
			}
			HttpPost httpMethod = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			SharedPreferences prefs = mContext.getApplicationContext().getSharedPreferences("account", 0);
			String user = prefs.getString("user","");
			nameValuePairs.add(new BasicNameValuePair("user", user));
			nameValuePairs.add(new BasicNameValuePair("prayer_id", arg0[0]));
			httpMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = PrayerLoginActivity.client
					.execute(httpMethod);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
			return result;
		} catch (Exception e) {

		}
		return null;
	}

}
