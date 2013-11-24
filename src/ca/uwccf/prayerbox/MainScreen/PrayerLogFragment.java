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
					PrayerDeleteMultHandler data = new PrayerDeleteMultHandler(
							getActivity());
					data.execute(del_prayers);
					mode.finish();
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
			new PrayerLogData().execute("");
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
	private class PrayerDeleteMultHandler extends
	AsyncTask<ArrayList<String>, Void, String> {
		private String result;
		private Context mContext;
		private String mUser;

	public PrayerDeleteMultHandler(Context context) {
		mContext = context;
	}
	
	@Override
	protected String doInBackground(ArrayList<String>... arg0) {
		try {
			String url;
			url = "http://www.uwccf.ca/prayerbox/api/prayerlistdelmultproc.php";
			HttpPost httpMethod = new HttpPost(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			SharedPreferences prefs = mContext.getApplicationContext().getSharedPreferences("account", 0);
			String user = prefs.getString("user","");
			nameValuePairs.add(new BasicNameValuePair("user", user));
			for(String pid: arg0[0]){
				nameValuePairs.add(new BasicNameValuePair("prayer_ids[]", pid));	
			}
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
	@Override
	protected void onPostExecute(String result) {
		refresh();
	}
	
}

	private class PrayerLogData extends AsyncTask<String, Void, String> {
		private String result;

		// private ProgressDialog Dialog = new ProgressDialog(getActivity());

		@Override
		protected String doInBackground(String... params) {
			try {
				HttpPost httpMethod = new HttpPost(
						"http://www.uwccf.ca/prayerbox/api/prayerlistproxy.php");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("username",
						MainTabbedFragmentActivity.mUser));
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
		}

		@Override
		protected void onPostExecute(String result) {
			PrayerParser pray_parser = new PrayerParser(result);
			ArrayList<Prayer> prayer_list = pray_parser.parsePrayerList();
			PrayerAdapter prayerAdapter = new PrayerAdapter(getActivity(),
					prayer_list, true);
			setListAdapter(prayerAdapter);
			// Dialog.dismiss();
		}
	}

}
