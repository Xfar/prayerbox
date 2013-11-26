package ca.uwccf.prayerbox.Data;

import ca.uwccf.prayerbox.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class PrayerInternetInfo {
	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public void noInternetToast(Context context){
		Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
	}

}
