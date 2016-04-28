package thuynh90.tacoma.uw.edu.listitwatchit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List It Watch It");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search Movies...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            // TODO: Submitted search
            public boolean onQueryTextSubmit(String query) {
                //Test search value.ToBeDeleted
                Toast.makeText(MainActivity.this, "Search value: " + query, Toast.LENGTH_SHORT).show();
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                return false;
            }
            @Override
            // TODO: Suggested results
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

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

        // TODO: Implement Logout
        if (id == R.id.logout) {

        }



        return super.onOptionsItemSelected(item);
    }
}
