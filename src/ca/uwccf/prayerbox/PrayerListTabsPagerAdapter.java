package ca.uwccf.prayerbox;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PrayerListTabsPagerAdapter extends FragmentPagerAdapter {

	public PrayerListFragment prayerListFragment;
	public PrayerLogFragment prayerLogFragment;

	public PrayerListTabsPagerAdapter(FragmentManager fm) {
		super(fm);
		
		prayerListFragment = new PrayerListFragment();
		prayerLogFragment = new PrayerLogFragment();
	}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return prayerListFragment;
		case 1:
			return prayerLogFragment;
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}