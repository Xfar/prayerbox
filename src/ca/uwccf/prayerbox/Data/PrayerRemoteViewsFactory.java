package ca.uwccf.prayerbox.Data;

import java.util.ArrayList;

import ca.uwccf.prayerbox.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class PrayerRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	private static ArrayList<Prayer> mPrayerItems = new ArrayList<Prayer>();
	private int mAppWidgetId;
	private Context mContext;
	
	public PrayerRemoteViewsFactory(Context context, Intent intent){
		mContext = context;
       mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
	}
	@Override
	public int getCount() {
		return mPrayerItems.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public RemoteViews getViewAt(int position) {
	    RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_prayer_list_item);
	    Prayer pitem = mPrayerItems.get(position);
	    rv.setTextViewText(R.id.label, pitem.subject);
	    rv.setTextViewText(R.id.request, pitem.request);
	    rv.setTextViewText(R.id.author, pitem.author);
	    rv.setTextViewText(R.id.date, pitem.date);
        Bundle extras = new Bundle();
        extras.putString(PrayerWidgetProvider.SUBJECT, pitem.subject);
        extras.putString(PrayerWidgetProvider.REQUEST, pitem.request);
        extras.putString(PrayerWidgetProvider.DATE, pitem.date);
        extras.putString(PrayerWidgetProvider.PRAYER_ID, pitem.prayer_id);
        extras.putString(PrayerWidgetProvider.AUTHOR, pitem.author);
        extras.putBoolean(PrayerWidgetProvider.ISStarred, pitem.isStarred);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
	    return rv;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public void onCreate() {
        populatePrayerItems();
	}
	
	public void populatePrayerItems(){
		StringRequest request = new StringRequest(Request.Method.POST, "http://prayer.uwccf.ca/api/prayerproxy.php",
				new Response.Listener<String>() {
			        @Override
			        public void onResponse(String result) {
						PrayerParser pray_parser = new PrayerParser(result);
						mPrayerItems = pray_parser.parsePrayerList();
						AppWidgetManager widgetManager = AppWidgetManager.getInstance(mContext);
						widgetManager.notifyAppWidgetViewDataChanged(widgetManager.getAppWidgetIds(
		                        new ComponentName(mContext, PrayerWidgetProvider.class)), R.id.widget_plist);
			        }
			    },
			    new Response.ErrorListener() {
			        @Override
			        public void onErrorResponse(VolleyError error) {
			        }
			    }){
			};
			PrayerApplication.getInstance().addToRequestQueue(request);
			
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

}
