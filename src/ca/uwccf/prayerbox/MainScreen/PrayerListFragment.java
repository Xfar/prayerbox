package ca.uwccf.prayerbox.MainScreen;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.Data.Prayer;
import ca.uwccf.prayerbox.Data.PrayerParser;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
import ca.uwccf.prayerbox.OtherScreen.PrayerDetailsActivity;
import ca.uwccf.prayerbox.R.layout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PrayerListFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new GetData().execute("");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_prayer_list,
				container, false);

		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Prayer item = (Prayer) getListAdapter().getItem(position);
		String request = item.request;
		String subject = item.subject;
		String author = item.author;
		String date = item.date;
		String prayer_id = item.prayer_id;

		Intent nextScreen = new Intent(getActivity(),
				PrayerDetailsActivity.class);

		// Sending data to another Activity
		nextScreen.putExtra("subject", subject);
		nextScreen.putExtra("request", request);
		nextScreen.putExtra("author", author);
		nextScreen.putExtra("date", date);
		nextScreen.putExtra("prayer_id", prayer_id);
		nextScreen.putExtra("isStarred", item.isStarred);

		startActivity(nextScreen);
	}

	private class GetData extends AsyncTask<String, Void, String> {
		private String result;
		// private ProgressDialog Dialog = new ProgressDialog(getActivity());
		@Override
		protected String doInBackground(String... params) {
			HttpPost httpMethod = new HttpPost(
					"http://www.uwccf.ca/prayerbox/api/prayerproxy.php");

			result = null;
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username",MainTabbedFragmentActivity.mUser));
				httpMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = PrayerLoginActivity.client
						.execute(httpMethod);
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
			// Dialog.setMessage("Loading Prayer Requests...");
			// Dialog.show();
			getActivity().setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(String result) {
			PrayerParser pray_parser = new PrayerParser(result);
			ArrayList<Prayer> prayer_list = pray_parser.parsePrayerList();
			PrayerAdapter prayerAdapter = new PrayerAdapter(getActivity(),
					prayer_list, false);
			setListAdapter(prayerAdapter);
			// Dialog.dismiss();
			getActivity().setProgressBarIndeterminateVisibility(false);
		}
	}

	public void refresh() {
		// TODO Auto-generated method stub
		new GetData().execute("");
	}

}
