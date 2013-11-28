package ca.uwccf.prayerbox.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
import ca.uwccf.prayerbox.MainScreen.MainTabbedFragmentActivity;
import ca.uwccf.prayerbox.MainScreen.PrayerAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class PrayerRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	private static ArrayList<Prayer> mPrayerItems = new ArrayList<Prayer>();
	private int mAppWidgetId;
	//private static RequestQueue queue;
	private Context mContext;
	
	public PrayerRemoteViewsFactory(Context context, Intent intent){
		mContext = context;
       mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mPrayerItems.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
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
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
	    return rv;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
public void onCreate() {
	    Thread thread = new Thread() {
	        public void run() {
	            populatePrayerItems();
	        }
	    };
	    thread.start();
	}
	public void populatePrayerItems(){
		new GetData().execute("");
			
	}

	@Override
	public void onDataSetChanged() {
	    Thread thread = new Thread() {
	        public void run() {
	            populatePrayerItems();
	        }
	    };
	    thread.start();
	    try {
	        thread.join();
	    } catch (InterruptedException e) {
	    }
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
	
	  private class GetData extends AsyncTask<String, Void, String> {
		     private String result;
		 
		     @Override
		     protected String doInBackground(String... params) {
		       DefaultHttpClient client = new DefaultHttpClient();
		       HttpPost httpMethod = new HttpPost(
		           "http://www.uwccf.ca/prayerbox/api/prayerproxy.php");
		 
		       result = null;
		       try {
		         HttpResponse response = client.execute(httpMethod);
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
		       mPrayerItems = pray_parser.parsePrayerList();
		     }
		   }

}
