package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.CustomMovieListFragment.CustomMovieListFragmentInteractionListener;

/**
 * Activity that holds list of movies from when a user views movies from "My Lists" tab.
 * User can delete movies from list and view details of movie.
 */
public class MyListsHolderActivity extends AppCompatActivity implements CustomMovieListFragmentInteractionListener {

    private String listName = "";
    private String listID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lists_holder);

        Intent intent = getIntent();
        listName = intent.getStringExtra("listName");
        listID = intent.getStringExtra("listID");
        getSupportActionBar().setTitle(listName);

        Bundle listBundle = new Bundle();
        listBundle.putString("listName", listName);
        listBundle.putString("listID", listID);

        CustomMovieListFragment viewCustomList = new CustomMovieListFragment();
        viewCustomList.setArguments(listBundle);

        getSupportFragmentManager().beginTransaction().add(R.id.my_lists_container, viewCustomList).commit();
    }

    @Override
    public void onCustomMovieListFragmentInteraction(Movie item) {

    }
}
