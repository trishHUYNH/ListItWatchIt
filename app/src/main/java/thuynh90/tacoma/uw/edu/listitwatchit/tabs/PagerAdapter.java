package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Page adapter for tabs.
 * Creates new instance of a fragment based on position of tab selected.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumberOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.mNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ToWatchFragment toWatchTab = new ToWatchFragment();
                return toWatchTab;
            case 1:
                WatchedFragment watchedTab = new WatchedFragment();
                return watchedTab;
            case 2:
                MyListsFragment myListsTab = new MyListsFragment();
                return myListsTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}