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
import android.os.AsyncTask;
import android.widget.Toast;

public class PrayerListDataHandler extends AsyncTask<ArrayList<String>, Void, String> {
	private String result;
	private Context mContext;
	
	public PrayerListDataHandler(Context context){
		mContext = context;
	}
	@Override
	protected String doInBackground(ArrayList<String>... arg0) {
		try{
			HttpPost httpMethod = new HttpPost(
					"http://www.uwccf.ca/prayerbox/api/prayerlistproc.php");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
			ArrayList<String> plist = arg0[0];
			for (String prayer_id : plist) {
				if(!prayer_id.isEmpty()){
					nameValuePairs.add(new BasicNameValuePair("prayerids[]", prayer_id));
				}
			}
			httpMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = PrayerLoginActivity.client
					.execute(httpMethod);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity);
			return result;
		}
		catch(Exception e){
		
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(final String success) {
		Toast.makeText(mContext, success, Toast.LENGTH_LONG).show();
	}

}
