package ca.uwccf.prayerbox.MainScreen;

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
import android.widget.Toast;

public class PrayerListFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (PrayerLoginActivity.intInfo.isNetworkAvailable(getActivity()
				.getApplicationContext())) {
			refresh();
		} else {
			PrayerLoginActivity.intInfo.noInternetToast(getActivity().getApplicationContext());
		}
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

	public void refresh() {
		// TODO Auto-generated method stub
		getActivity().setProgressBarIndeterminateVisibility(true);
		StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.prayer_url),
			new Response.Listener<String>() {
		        @Override
		        public void onResponse(String result) {
					PrayerParser pray_parser = new PrayerParser(result);
					ArrayList<Prayer> prayer_list = pray_parser.parsePrayerList();
					PrayerAdapter prayerAdapter = new PrayerAdapter(getActivity(),
							prayer_list, false);
					setListAdapter(prayerAdapter);
					getActivity().setProgressBarIndeterminateVisibility(false);
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
		        map.put("username", MainTabbedFragmentActivity.mUser);
		        return map;
		    }
		};
		PrayerLoginActivity.queue.add(request);
	}

}
