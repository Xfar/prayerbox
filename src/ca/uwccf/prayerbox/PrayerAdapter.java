package ca.uwccf.prayerbox;

import java.util.ArrayList;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

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
						ArrayList<String> prayerList = new ArrayList<String>();
						prayerList.add(item.prayer_id);
						PrayerListDataHandler data = new PrayerListDataHandler(
								context);
						data.execute(prayerList);
						((PrayerListActivity) context).refreshPrayerLog();
					} else {
						// REMOVE item.prayer_id FROM LOG
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