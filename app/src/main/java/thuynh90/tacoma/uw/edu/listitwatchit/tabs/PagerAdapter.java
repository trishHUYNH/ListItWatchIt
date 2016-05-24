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
    Fragment toWatchTab;
    Fragment watchedTab;
    Fragment myListsTab;

    public PagerAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.mNumberOfTabs = numberOfTabs;
    }

    /**
     * Creates the appropriate list fragment depending on which tab is selected
     * @param position The number tab selected
     * @return A toWatch, watched, or myLists fragment
     */
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (toWatchTab == null) {
                    toWatchTab = new ToWatchFragment();
                }
                return toWatchTab;
            case 1:
                if (watchedTab == null) {
                    watchedTab = new WatchedFragment();
                }
                return watchedTab;
            case 2:
                if (myListsTab == null) {
                    myListsTab = new MyListsFragment();
                }
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