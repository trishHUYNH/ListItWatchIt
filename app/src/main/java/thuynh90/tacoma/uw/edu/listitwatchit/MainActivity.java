package thuynh90.tacoma.uw.edu.listitwatchit;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import thuynh90.tacoma.uw.edu.listitwatchit.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    /**
     * If not logged in, only search functionality is available.
     * Provides link for user to log in.
     * Sets different layout depending on status of login.
     */
    protected void onCreate(Bundle savedInstanceState) {

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);

        // Logged in. Users can view their lists
        if (mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), true)) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

        } else {
            // Not logged in. Only search function is available
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_login_false);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
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

        // TODO: Make decisions on Settings
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.logout) {
            mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
            mSharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), false).apply();

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
     */
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // Test toast. ToBeDeleted
            Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_LONG).show();

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            // Test toast. ToBeDeleted.
            Toast.makeText(this, "Movie ID: "+ uri, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called when user clicks on login from main
     * @param view
     * @TODO: Add current activity to stack
     */
    public void directToLogin(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
