package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.CustomMovieListFragment.CustomMovieListFragmentInteractionListener;
import thuynh90.tacoma.uw.edu.listitwatchit.viewDetails.ViewMovieDetailsActivity;

/**
 * Activity that holds list of movies from when a user views movies from "My Lists" tab.
 * User can delete movies from list and view details of movie.
 */
public class MyListsHolderActivity extends AppCompatActivity implements CustomMovieListFragmentInteractionListener {

    private String listName = "";
    private String listID = "";
    private final static String DELETE_MOVIE_URL = "http://cssgate.insttech.washington.edu/~_450atm6/deleteMovie.php?cmd=custom&";

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

    /**
     * Fragment interaction for details of a custom list.
     * Passes movie ID as intent to ViewMovieDetailsActivity
     * @param item Movie item in list
     * @param task String that describes task that describes whether to view or delete movie
     */
    @Override
    public void onCustomMovieListFragmentInteraction(Movie item, String task) {
        if(task.equals("viewDetails")) {
            Intent detailIntent = new Intent(this, ViewMovieDetailsActivity.class);
            detailIntent.putExtra("movieID", item.getMovieID());
            detailIntent.putExtra("location", "");
            detailIntent.putExtra("listName", listName);
            startActivity(detailIntent);
        } else if(task.equals("deleteMovie")) {
            StringBuilder urlBuilder = new StringBuilder(DELETE_MOVIE_URL);

            try {
                urlBuilder.append("list_id=");
                urlBuilder.append(listID.trim());

                urlBuilder.append("&movie_id=");
                urlBuilder.append(item.getMovieID().trim());
            }
            catch(Exception e) {
                Toast.makeText(getApplicationContext(), "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            UpdateListTask newListTask = new UpdateListTask();
            newListTask.execute(urlBuilder.toString());

            //Refresh "My Lists"
            Bundle listBundle = new Bundle();
            listBundle.putString("listName", listName);
            listBundle.putString("listID", listID);

            CustomMovieListFragment viewCustomList = new CustomMovieListFragment();
            viewCustomList.setArguments(listBundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.my_lists_container, viewCustomList).commit();
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
