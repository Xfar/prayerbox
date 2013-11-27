package ca.uwccf.prayerbox.Data;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PrayerWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent arg0) {
		return (new PrayerRemoteViewsFactory(getApplicationContext(), arg0));
	}

}
