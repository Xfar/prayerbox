package ca.uwccf.prayerbox.Data;

import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.OtherScreen.PrayerDetailsActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PrayerWidgetProvider extends AppWidgetProvider {
	RemoteViews view;
	
    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    public static final String SUBJECT = "ca.uwccf.prayerbox.subject";
    public static final String REQUEST = "ca.uwccf.prayerbox.request";
    public static final String DATE = "ca.uwccf.prayerbox.date";
    public static final String AUTHOR = "ca.uwccf.prayerbox.author";
    public static final String PRAYER_ID = "ca.uwccf.prayerbox.prayer_id";


    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is TOAST_ACTION. If it is, the app widget 
    // displays a Toast message for the current item.
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
           
            String subject = intent.getStringExtra(SUBJECT);
            String request = intent.getStringExtra(REQUEST);
            String date = intent.getStringExtra(DATE);
            String author = intent.getStringExtra(AUTHOR);
            String prayer_id = intent.getStringExtra(PRAYER_ID);
            
    		Intent nextScreen = new Intent(context,
    				PrayerDetailsActivity.class);

    		// Sending data to another Activity
    		nextScreen.putExtra("subject", subject);
    		nextScreen.putExtra("request", request);
    		nextScreen.putExtra("author", author);
    		nextScreen.putExtra("date", date);
    		nextScreen.putExtra("prayer_id", prayer_id);
    		nextScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		context.startActivity(nextScreen);
    		mgr.notifyAppWidgetViewDataChanged(appWidgetId, R.layout.widget_prayer_list);
        }
        super.onReceive(context, intent);
    }
  
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
	        
            Intent toastIntent = new Intent(context, PrayerWidgetProvider.class);
            // Set the action for the intent.
            // When the user touches a particular view, it will have the effect of
            // broadcasting TOAST_ACTION.
            toastIntent.setAction(PrayerWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_plist, toastPendingIntent);
	        appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
	    }
	    super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
