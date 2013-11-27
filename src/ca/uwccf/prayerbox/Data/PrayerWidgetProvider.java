package ca.uwccf.prayerbox.Data;

import ca.uwccf.prayerbox.R;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class PrayerWidgetProvider extends AppWidgetProvider {
	RemoteViews view;
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds){
	    // update each of the app widgets with the remote adapter
		
	    for (int i = 0; i < appWidgetIds.length; i++) {
	    	   //which layout to show on widget
	    	   RemoteViews remoteViews = new RemoteViews(
	    	         context.getPackageName(),R.layout.widget_prayer_list);
	    	 
	    	  //RemoteViews Service needed to provide adapter for ListView
	    	  Intent svcIntent = new Intent(context, PrayerWidgetService.class);
	    	  //passing app widget id to that RemoteViews Service
	    	  svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
	    	  //setting a unique Uri to the intent
	    	  //don't know its purpose to me right now
	    	  svcIntent.setData(Uri.parse(
	    	                    svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
	        // Set up the RemoteViews object to use a RemoteViews adapter. 
	        // This adapter connects
	        // to a RemoteViewsService  through the specified intent.
	        // This is how you populate the data.
	        remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.widget_plist, svcIntent);
	        remoteViews.setEmptyView(R.id.widget_plist, R.id.empty_view);

	        //
	        // Do additional processing specific to this app widget...
	        //
	        
	        appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);   
	    }
	    super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
