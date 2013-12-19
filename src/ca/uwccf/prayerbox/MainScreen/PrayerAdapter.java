package ca.uwccf.prayerbox.MainScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.Data.Prayer;
import ca.uwccf.prayerbox.Data.PrayerApplication;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
import ca.uwccf.prayerbox.R.id;
import ca.uwccf.prayerbox.R.layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class PrayerAdapter extends ArrayAdapter<Prayer> {

	private ArrayList<Prayer> items;
	private Context context;
	private boolean prayerLogItem = false;

	private SparseBooleanArray selectedItemIds;

	public PrayerAdapter(Context context, ArrayList<Prayer> items,
			boolean prayerLogItem) {
		super(context, R.layout.list_item_prayer, items);

		selectedItemIds = new SparseBooleanArray();

		this.context = context;
		this.items = items;
		this.prayerLogItem = prayerLogItem;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_item_prayer, null);
		}

		final Prayer item = items.get(position);
		if (item != null) {
			// My layout has only one TextView
			TextView itemView = (TextView) view.findViewById(R.id.label);
			if (itemView != null) {
				// do whatever you want with your string and long
				itemView.setText(item.subject);
			}
			TextView authorView = (TextView) view.findViewById(R.id.author);
			if (authorView != null) {
				authorView.setText(item.author);
			}
			TextView date = (TextView) view.findViewById(R.id.date);
			if (date != null) {
				date.setText(item.date);
			}
			TextView request = (TextView) view.findViewById(R.id.request);
			if (request != null) {
				request.setText(item.request);
			}
			final CheckBox star = (CheckBox) view.findViewById(R.id.star);
			if (prayerLogItem) {
				star.setVisibility(View.GONE);
			} else {
				star.setChecked(item.isStarred);
			}

			star.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (star.isChecked()) {
						if(!PrayerLoginActivity.intInfo.isNetworkAvailable(getContext().getApplicationContext())){
							star.setChecked(false);
							Toast.makeText(getContext().getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
						}
						StringRequest request = new StringRequest(Request.Method.POST, getContext().getString(R.string.prayer_log_add_url),
							new Response.Listener<String>() {
						        @Override
						        public void onResponse(String result) {
									((MainTabbedFragmentActivity) context).refreshPrayerLog();
						        }
						    },
						    new Response.ErrorListener() {
						        @Override
						        public void onErrorResponse(VolleyError error) {
						        }
						    }){
						    @Override
						    protected Map<String, String> getParams() throws AuthFailureError {
								SharedPreferences prefs = getContext().getApplicationContext().getSharedPreferences("account", 0);
								String user = prefs.getString("user","");
						        Map<String, String> map = new HashMap<String, String>();
						        map.put("user", user);
						        map.put("prayer_id", item.prayer_id);
						        return map;
						    }
						};
						PrayerApplication.getInstance().addToRequestQueue(request);
						item.isStarred = true;
					} else {
						if(!PrayerLoginActivity.intInfo.isNetworkAvailable(getContext().getApplicationContext())){
							star.setChecked(true);
							Toast.makeText(getContext().getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
						}
						StringRequest request = new StringRequest(Request.Method.POST, getContext().getString(R.string.prayer_log_del_url),
							new Response.Listener<String>() {
						        @Override
						        public void onResponse(String result) {
									((MainTabbedFragmentActivity) context).refreshPrayerLog();
						        }
						    },
						    new Response.ErrorListener() {
						        @Override
						        public void onErrorResponse(VolleyError error) {
						        }
						    }){
						    @Override
						    protected Map<String, String> getParams() throws AuthFailureError {
								SharedPreferences prefs = getContext().getApplicationContext().getSharedPreferences("account", 0);
								String user = prefs.getString("user","");
						        Map<String, String> map = new HashMap<String, String>();
						        map.put("user", user);
						        map.put("prayer_id", item.prayer_id);
						        return map;
						    }
						};
						PrayerApplication.getInstance().addToRequestQueue(request);
						item.isStarred = false;
					}
				}

			});

		}

		return view;
	}

	public void toggleSelection(int position) {
		selectView(position, !selectedItemIds.get(position));
	}

	public void removeSelection() {
		selectedItemIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public void selectView(int position, boolean value) {
		if (value)
			selectedItemIds.put(position, value);
		else
			selectedItemIds.delete(position);

		notifyDataSetChanged();
	}

	public int getSelectedCount() {
		return selectedItemIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return selectedItemIds;
	}

}