package thuynh90.tacoma.uw.edu.listitwatchit;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import thuynh90.tacoma.uw.edu.listitwatchit.login.LoginActivity;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.ListNameDialogFragment;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.Movie;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.MyList;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.MyListsFragment;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.MyListsFragment.MyListsFragmentInteractionListener;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.MyListsHolderActivity;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.PagerAdapter;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.ToWatchFragment;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.ToWatchFragment.toWatchFragmentInteractionListener;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.WatchedFragment;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.WatchedFragment.WatchedListFragmentInteractionListener;
import thuynh90.tacoma.uw.edu.listitwatchit.viewDetails.ViewMovieDetailsActivity;

/**
 * Activity that is the home screen on the app. Houses the search function and has links to the other Activities
 */
public class MainActivity extends AppCompatActivity
        implements toWatchFragmentInteractionListener, WatchedListFragmentInteractionListener, MyListsFragmentInteractionListener {

    private SharedPreferences mSharedPreferences;
    private static final String ADD_LIST_URL = "http://cssgate.insttech.washington.edu/~_450atm6/addList.php?";
    private static final String DELETE_LIST_URL = "http://cssgate.insttech.washington.edu/~_450atm6/deleteList.php?";
    private static final String MOVE_TO_WATCHED_URL = "http://cssgate.insttech.washington.edu/~_450atm6/moveToWatched.php?";
    private final static String DELETE_MOVIE_URL = "http://cssgate.insttech.washington.edu/~_450atm6/deleteMovie.php?cmd=default&";

    private ViewPager viewPager;
    private PagerAdapter adapter;

    /**
     * If not logged in, only search functionality is available.
     * Provides link for user to log in.
     * Sets different layout depending on status of login.
     */
    @Override
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

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
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

    /**
     * Handles search intents.
     * Suggestion selection returns movie ID from TMDb
     * @param intent The new intent that was created
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Intent detailIntent = new Intent (this, ViewMovieDetailsActivity.class);
            detailIntent.putExtra("movieID", uri);
            detailIntent.putExtra("location", "fromSearch");
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
        assert tabLayout != null;
        final TabLayout.Tab toWatch = tabLayout.newTab().setText("To Watch").setTag("TO_WATCH");
        final TabLayout.Tab watched = tabLayout.newTab().setText("Watched").setTag("WATCHED");
        final TabLayout.Tab myLists = tabLayout.newTab().setText("My Lists").setTag("MY_LISTS");

        tabLayout.addTab(toWatch);
        tabLayout.addTab(watched);
        tabLayout.addTab(myLists);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final FloatingActionButton addListButton = (FloatingActionButton) findViewById(R.id.add_list_button);
        assert addListButton != null;
        addListButton.hide();

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment listNameDialog = new ListNameDialogFragment();
                listNameDialog.show(getFragmentManager(), "list title dialog");
            }
        });

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        assert viewPager != null;
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.equals(myLists)) {
                    addListButton.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.equals(myLists)) {
                    addListButton.hide();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.equals(myLists)) {
                    addListButton.show();
                }
            }
        });
    }

    /**
     * Fragment interaction for To Watch tab.
     * Passes movie ID as intent to ViewMovieDetailsActivity if task is to view details
     * Else makes an instance of UpdateListTask to move movie to "Watched"
     * @param item Movie item in list
     * @param task String that describes task that describes whether to view or move movie to "Watched"
     */
    @Override
    public void toWatchFragmentInteraction(Movie item, String task) {
        if(task.equals("viewDetails")) {
            Intent detailIntent = new Intent(this, ViewMovieDetailsActivity.class);
            detailIntent.putExtra("movieID", item.getMovieID());
            detailIntent.putExtra("location", "fromToWatch");
            detailIntent.putExtra("listName", "To Watch");
            startActivity(detailIntent);
        } else if(task.equals("watchedMovie")) {
            StringBuilder urlBuilder = new StringBuilder(MOVE_TO_WATCHED_URL);
            mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");

            try {
                urlBuilder.append("email=");
                urlBuilder.append(email.trim());

                urlBuilder.append("&movie_id=");
                urlBuilder.append(item.getMovieID().trim());
            }
            catch(Exception e) {
                Toast.makeText(getApplicationContext(), "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            System.out.println(urlBuilder.toString());
            UpdateListTask newListTask = new UpdateListTask();
            newListTask.execute(urlBuilder.toString());
        }

        // Refresh "To Watch" & "Watched" when moving movie to "Watched"
        ToWatchFragment toWatchList = (ToWatchFragment) adapter.getItem(viewPager.getCurrentItem());
        WatchedFragment watchedList = (WatchedFragment) adapter.getItem(1);
        if(toWatchList != null && watchedList != null) {
            toWatchList.downloadToWatch();
            watchedList.downloadWatched();
        }
    }

    /**
     * Fragment interaction for Watched tab.
     * Passes movie ID as intent to ViewMovieDetailsActivity
     * @param item Movie item in list
     * @param task String that describes task that describes whether to view or delete movie
     */
    @Override
    public void watchedFragmentInteraction(Movie item, String task) {
        if(task.equals("viewDetails")) {
            Intent detailIntent = new Intent(this, ViewMovieDetailsActivity.class);
            detailIntent.putExtra("movieID", item.getMovieID());
            detailIntent.putExtra("location", "fromWatched");
            detailIntent.putExtra("listName", "Watched");
            startActivity(detailIntent);
        } else if(task.equals("deleteMovie")) {
            StringBuilder urlBuilder = new StringBuilder(DELETE_MOVIE_URL);
            mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");

            try {
                urlBuilder.append("email=");
                urlBuilder.append(email.trim());

                urlBuilder.append("&list=");
                urlBuilder.append("Watched");

                urlBuilder.append("&movie_id=");
                urlBuilder.append(item.getMovieID().trim());
            }
            catch(Exception e) {
                Toast.makeText(getApplicationContext(), "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            System.out.println(urlBuilder.toString());
            UpdateListTask newListTask = new UpdateListTask();
            newListTask.execute(urlBuilder.toString());

            // Refresh "Watched" when moving movie is deleted
            WatchedFragment watchedList = (WatchedFragment) adapter.getItem(viewPager.getCurrentItem());
            if(watchedList != null) {
                watchedList.downloadWatched();
            }
        }
    }

    /**
     * Fragment interaction for My Lists tab.
     * @param eachList List item in My Lists
     * @param task String that describes task that describes whether to view or delete list
     */
    public void myListFragmentInteraction(MyList eachList, String task) {
        if(task.equals("deleteList")) {
            // Prevent user from deleting "To Watch" or "Watched"
            if(eachList.getListName().equals("To Watch") || eachList.getListName().equals("Watched")) {
                Toast.makeText(getApplicationContext(), "This list cannot be deleted.", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuilder urlBuilder = new StringBuilder(DELETE_LIST_URL);
            mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");

            try {
                urlBuilder.append("email=");
                urlBuilder.append(email.trim());

                urlBuilder.append("&list_id=");
                urlBuilder.append(eachList.getListID().trim());
            }
            catch(Exception e) {
                Toast.makeText(getApplicationContext(), "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            UpdateListTask newListTask = new UpdateListTask();
            newListTask.execute(urlBuilder.toString());

            // Refreshes "My Lists" tab after deleting
            MyListsFragment myLists = (MyListsFragment) adapter.getItem(viewPager.getCurrentItem());
            if(myLists != null) {
                myLists.downloadMyLists();
            }

        } else {
            Intent detailIntent = new Intent(this, MyListsHolderActivity.class);
            detailIntent.putExtra("listName", eachList.getListName());
            detailIntent.putExtra("listID", eachList.getListID());
            startActivity(detailIntent);
        }
    }


    /**
     * Builds the URL to access PHP file and create the user's list
     * @param listName List name entered by user from ListNameDialogFragment
     */
    public void createList(String listName){

        StringBuilder urlBuilder = new StringBuilder(ADD_LIST_URL);
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");

        try {
            urlBuilder.append("list_name=");
            urlBuilder.append(listName.trim());

            urlBuilder.append("&email=");
            urlBuilder.append(email.trim());
        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        UpdateListTask newListTask = new UpdateListTask();
        newListTask.execute(urlBuilder.toString());

        // Refreshes "My Lists" tab
        MyListsFragment myLists = (MyListsFragment) adapter.getItem(viewPager.getCurrentItem());
        if(myLists != null) {
            myLists.downloadMyLists();
        }
    }

    /**
     * AsyncTask class used to update list items
     */
    private class UpdateListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlInfo = params[0];
            BufferedReader bufferedReader;
            HttpURLConnection connection = null;
            String result;
            try {
                URL url = new URL(urlInfo);
                connection = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                result = bufferedReader.readLine();
                return result;
            } catch (Exception e) {
                return result = "Error Reason: " + e.getMessage();
            }
            finally {
                if (connection != null)
                    connection.disconnect();
            }
        }
        @Override
        protected void onPostExecute(String result)  {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), jsonObject.get("message").toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Data problem: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
