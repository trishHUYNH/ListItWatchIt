package thuynh90.tacoma.uw.edu.listitwatchit;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import thuynh90.tacoma.uw.edu.listitwatchit.login.LoginActivity;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.Movie;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.PagerAdapter;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.ToWatchFragment;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.WatchedFragment;
import thuynh90.tacoma.uw.edu.listitwatchit.viewDetails.ViewMovieDetailsActivity;

/**
 * Activity that is the home screen on the app. Houses the search function and has links to the other Activities
 */
public class MainActivity extends AppCompatActivity implements ToWatchFragment.OnListFragmentInteractionListener, WatchedFragment.OnListFragmentInteractionListener {

    private SharedPreferences mSharedPreferences;

    @Override
    /**
     * If not logged in, only search functionality is available.
     * Provides link for user to log in.
     * Sets different layout depending on status of login.
     */
    protected void onCreate(Bundle savedInstanceState) {

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);

        // Not logged in. Only search is available.
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_login_false);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        } else {
            // Logged in. Tabs are displayed.
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            createTabs();
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, MainActivity.class)));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.logout) {
            mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            mSharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false).apply();
            mSharedPreferences.edit().putString(getString(R.string.USERNAME), "").apply();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * Handles search intents.
     * Suggestion selection returns movie ID from TMDb
     * @param intent The new intent that was created
     */
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Intent detailIntent = new Intent (this, ViewMovieDetailsActivity.class);
            detailIntent.putExtra("movieID", uri);
            startActivity(detailIntent);
        }
    }

    /**
     * Called when user clicks on login from main
     * @param view The current view
     */
    public void directToLogin(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Helper method to create tabs and display on to layout.
     * Called from onCreate. Only called for users that are logged in.
     */
    public void createTabs() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        final TabLayout.Tab toWatch = tabLayout.newTab().setText("To Watch");
        final TabLayout.Tab watched = tabLayout.newTab().setText("Watched");
        final TabLayout.Tab myLists = tabLayout.newTab().setText("My Lists");

        tabLayout.addTab(toWatch);
        tabLayout.addTab(watched);
        tabLayout.addTab(myLists);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    /**
     * Fragment interaction for To Watch tab
     */
    public void toWatchFragmentInteraction(Movie item) {

    }

    @Override
    /**
     * Fragment interaction for Watched tab
     */
    public void watchedFragmentInteraction(Movie item) {

    }
}
