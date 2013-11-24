package ca.uwccf.prayerbox.MainScreen;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import ca.uwccf.prayerbox.R;
import ca.uwccf.prayerbox.Data.PrayerInternetInfo;
import ca.uwccf.prayerbox.LogIn.PrayerLoginActivity;
import ca.uwccf.prayerbox.OtherScreen.PrayerAddEditActivity;
import ca.uwccf.prayerbox.OtherScreen.PrayerFeedbackActivity;

public class MainTabbedFragmentActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public static PrayerInternetInfo intInfo;

	public MenuItem menuItemRefresh;

	private ViewPager viewPager;
	static private PrayerListTabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	static public String mUser;
	// Tab titles
	private String[] tabs = { "Prayer List", "Prayer Log" };
	public boolean newItemsStarred = false;

	public void refreshPrayerLog() {
		((PrayerLogFragment) mAdapter.getItem(1)).refresh();
	}

	static public void refresh() {
		((PrayerListFragment) mAdapter.getItem(0)).refresh();
		((PrayerLogFragment) mAdapter.getItem(1)).refresh();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_prayer_list);
		// Initialization
		SharedPreferences prefs = this.getSharedPreferences(ACCOUNT_SERVICE,
				MODE_PRIVATE);
		mUser = prefs.getString("user", "");
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		intInfo = new PrayerInternetInfo();

		mAdapter = new PrayerListTabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// On changing fragments, make respective tab selected
				actionBar.setSelectedNavigationItem(position);

				if (newItemsStarred) {
					refreshPrayerLog();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		// On tab selected, show respective fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prayer_list, menu);
		ActionBar bar = this.getActionBar();
		bar.setTitle(R.string.app_name);
		menuItemRefresh = menu.findItem(R.id.action_refresh);
		if (intInfo.isNetworkAvailable(getApplicationContext())) {
			menuItemRefresh.setVisible(false);
		} else {
			menuItemRefresh.setVisible(true);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.log_out:
			SharedPreferences sharedPref = this.getSharedPreferences(
					ACCOUNT_SERVICE, MODE_PRIVATE);
			SharedPreferences.Editor manage = sharedPref.edit();
			manage.clear();
			manage.putBoolean("validated", true);
			manage.commit();
			Intent intent = new Intent(getApplicationContext(),
					PrayerLoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			finish();
			return true;
		case R.id.action_refresh:
			if (intInfo.isNetworkAvailable(getApplicationContext())) {
				menuItemRefresh.setVisible(false);
				refresh();
			} else {
				menuItemRefresh.setVisible(true);
				Toast.makeText(getApplicationContext(), R.string.no_internet,
						Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.action_addedit:
			Intent intnt = new Intent(getApplicationContext(),
					PrayerAddEditActivity.class);
			startActivity(intnt);
			return true;
		case R.id.action_feedback:
			intnt = new Intent(getApplicationContext(),
					PrayerFeedbackActivity.class);
			startActivity(intnt);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}