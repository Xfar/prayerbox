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
import ca.uwccf.prayerbox.Data.PrayerApplication;
import ca.uwccf.prayerbox.Data.PrayerParser;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
import ca.uwccf.prayerbox.OtherScreen.PrayerDetailsActivity;
import ca.uwccf.prayerbox.R.id;
import ca.uwccf.prayerbox.R.layout;
import ca.uwccf.prayerbox.R.menu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class PrayerLogFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				((ListView) parent).setItemChecked(position,
						((ListView) parent).isItemChecked(position));
				return false;
			}
		});

		getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {

			private int nr = 0;

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				getActivity().getMenuInflater().inflate(
						R.menu.contextual_prayer_log, menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.contextual_action_delete:
					// TODO: Replace toast with code for deleting selected items
					// here
					SparseBooleanArray selected = ((PrayerAdapter) getListAdapter())
							.getSelectedIds();
					ArrayList<String> del_prayers = new ArrayList<String>();
					for (int i = (selected.size() - 1); i >= 0; i--) {
						if (selected.valueAt(i)) {
							Prayer selectedItem = (Prayer) getListAdapter()
									.getItem(selected.keyAt(i));
							del_prayers.add(selectedItem.prayer_id);
						}
					}
					if(PrayerLoginActivity.intInfo.isNetworkAvailable(getActivity().getApplicationContext())){
						final ArrayList<String> plist = del_prayers;
						SharedPreferences prefs = getActivity().getSharedPreferences("account", 0);
						final String user = prefs.getString("user","");
						StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.prayer_log_del_mult_url),
								new Response.Listener<String>() {
							        @Override
							        public void onResponse(String result) {
							        	MainTabbedFragmentActivity.refresh();
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
									map.put("user", user);
									String prayerids = "";
									for(String pid: plist){
										prayerids += pid + ",";
									}
									prayerids.substring(0, prayerids.length()-1);
									map.put("prayer_ids",prayerids);
							        return map;
							    }
							};
						PrayerApplication.getInstance().addToRequestQueue(request);
						mode.finish();
					} else {
						PrayerLoginActivity.intInfo.noInternetToast(getActivity().getApplicationContext());
					}
					break;

				}
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				nr = 0;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				((PrayerAdapter) getListAdapter()).toggleSelection(position);

				if (checked) {
					nr++;
				} else {
					nr--;
				}
				if (nr > 1)
					mode.setTitle(nr + " items selected");
				else
					mode.setTitle("1 item selected");
			}
		});
	}

	public void refresh() {
		if (PrayerLoginActivity.intInfo.isNetworkAvailable(getActivity()
				.getApplicationContext())) {
			getActivity().setProgressBarIndeterminateVisibility(true);
			StringRequest request = new StringRequest(Request.Method.POST, getString(R.string.prayer_log_url),
				new Response.Listener<String>() {
			        @Override
			        public void onResponse(String result) {
						PrayerParser pray_parser = new PrayerParser(result);
						ArrayList<Prayer> prayer_list = pray_parser.parsePrayerList();
						PrayerAdapter prayerAdapter = new PrayerAdapter(getActivity(),
								prayer_list, true);
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
			PrayerApplication.getInstance().addToRequestQueue(request);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		refresh();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_prayer_log,
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
		nextScreen.putExtra("isStarred", true);
		nextScreen.putExtra("prayer_id", prayer_id);

		startActivity(nextScreen);
	}

}
