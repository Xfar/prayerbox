package ca.uwccf.prayerbox;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class PrayerListFragment extends ListFragment {

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
						R.menu.contextual_prayer_list, menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.contextual_action_pluspray:
					// TODO: Replace with code for adding selected items to log
					// here
					SparseBooleanArray selected = ((PrayerAdapter) getListAdapter())
							.getSelectedIds();
					String strSelected = new String("+Pray selected on: ");
					for (int i = (selected.size() - 1); i >= 0; i--) {
						if (selected.valueAt(i)) {
							Prayer selectedItem = (Prayer) getListAdapter()
									.getItem(selected.keyAt(i));
							// addToPrayerLog(selectedItem);

							strSelected = strSelected + selectedItem.subject
									+ ", ";
						}
					}
					Toast.makeText(getActivity(), strSelected,
							Toast.LENGTH_SHORT).show();
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
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

		Intent nextScreen = new Intent(getActivity(),
				PrayerDetailsActivity.class);

		// Sending data to another Activity
		nextScreen.putExtra("subject", subject);
		nextScreen.putExtra("request", request);
		nextScreen.putExtra("author", author);
		nextScreen.putExtra("date", date);

		startActivity(nextScreen);
	}

	private class GetData extends AsyncTask<String, Void, String> {
		private String result;
		private ProgressDialog Dialog = new ProgressDialog(getActivity());

		@Override
		protected String doInBackground(String... params) {
			HttpPost httpMethod = new HttpPost(
					"http://www.uwccf.ca/prayerbox/api/prayerproxy.php");

			result = null;
			try {
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
			Dialog.setMessage("Loading Prayer Requests...");
			Dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			PrayerParser pray_parser = new PrayerParser(result);
			ArrayList<Prayer> prayer_list = pray_parser.parsePrayerList();
			PrayerAdapter prayerAdapter = new PrayerAdapter(getActivity(),
					prayer_list);
			setListAdapter(prayerAdapter);
			Dialog.dismiss();
		}
	}

}
