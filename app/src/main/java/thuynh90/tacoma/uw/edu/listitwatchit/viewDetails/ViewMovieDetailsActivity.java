package thuynh90.tacoma.uw.edu.listitwatchit.viewDetails;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import thuynh90.tacoma.uw.edu.listitwatchit.R;

/**
 * An Activity which shows the details of a movie (Title, Poster, Release Date, and Synopsis) on one screen
 */
public class ViewMovieDetailsActivity extends AppCompatActivity implements AddToListDialogFragment.OnAddToListDialogFragmentInteractionListener {

    private final static String ADD_MOVIE_URL = "http://cssgate.insttech.washington.edu/~_450atm6/addMovie.php?";
    private final static String DELETE_MOVIE_URL = "http://cssgate.insttech.washington.edu/~_450atm6/deleteMovie.php?";
    private static final String MOVE_TO_WATCHED_URL = "http://cssgate.insttech.washington.edu/~_450atm6/moveToWatched.php?";
    private SharedPreferences mSharedPreferences;

    private TextView mMovieTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mSynopsisTextView;
    private ImageView mPosterImageView;
    String id;
    String movieTitle = "";
    String releaseDate= "";
    String synopsis= "";
    Bitmap poster;
    String listName = "";


    /**
     * Runs AsyncTask to load movie details and sets the TextView and ImageView variables to the proper layout views from the XML file
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("movieID");
        listName = intent.getStringExtra("listName");
        String lastLocation = intent.getStringExtra("location");
        loadMovieDetailsTask task = new loadMovieDetailsTask();
        task.execute();

        if(lastLocation.equals("fromSearch") || lastLocation.equals("fromWatched")) {
            setContentView(R.layout.activity_view_movie_details);
            Button mAddMovieButton = (Button) findViewById(R.id.add_to_list_button);
            assert mAddMovieButton != null;
            mAddMovieButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Bundle bundle = new Bundle();
                    DialogFragment addToListDialog = new AddToListDialogFragment();
                    //addToListDialog.setArguments(bundle);
                    addToListDialog.show(getSupportFragmentManager(), "launch");
                }
            });
        } else if (lastLocation.equals("fromToWatch")){
            setContentView(R.layout.activity_view_movie_details_from_watched);
        }

        mMovieTitleTextView = (TextView) findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.release_date);
        mSynopsisTextView = (TextView) findViewById(R.id.synopsis);
        mPosterImageView = (ImageView) findViewById(R.id.poster);
    }

    @Override
    public void onAddToListDialogFragmentInteraction(Uri uri) {
    }

    /**
     * AsyncTask to retrieve movie details from TMDb
     */
    public class loadMovieDetailsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            query(id);
            return null;
        }

        /**
         * Runs onStart() so that the view will be updated
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onStart();
        }

        /**
         * Opens HTTP connection to query TMDb for details of a specified movie,
         * parses JSON response to get string values for movieTitle, releaseDate, and synopsis, and a url for the poster
         * @param movieID TMDb id for the movie that is being looked up
         */
        public void query(String movieID) {
            Log.d("Context", "Beginning of Query");
            BufferedReader reader;
            HttpURLConnection urlConnection = null;
            String urlString;
            String jsonString;

            try {
                urlString = "http://api.themoviedb.org/3/movie/" + movieID + "?api_key=6e2537d9c135091718d558d8d56a7cde";
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    StringBuilder stringBuilder = new StringBuilder();

                    reader = new BufferedReader(new InputStreamReader(in));
                    String line;

                    while((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append("\n");
                    }

                    jsonString = stringBuilder.toString();
                    reader.close();
                }
                finally {
                    urlConnection.disconnect();
                }


                JSONObject JSONmovie = new JSONObject(jsonString);

                movieTitle = JSONmovie.getString("title");
                releaseDate = JSONmovie.getString("release_date");
                synopsis = JSONmovie.getString("overview");

                String posterUrlString = "http://image.tmdb.org/t/p/w500" + JSONmovie.getString("poster_path");
                Log.d("URL", posterUrlString);

                //get poster
                URL posterURL = new URL(posterUrlString);
                poster = BitmapFactory.decodeStream(posterURL.openConnection().getInputStream());

            }
            catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }


    }

    /**
     * Sets TextViews to the proper values that have been set in the AsyncTask
     */
    public void updateView(){
        mMovieTitleTextView.setText(movieTitle);
        mReleaseDateTextView.setText(releaseDate);
        mSynopsisTextView.setText(synopsis);
        mPosterImageView.setImageBitmap(poster);
    }


    @Override
    public void onStart() {
        super.onStart();
        updateView();
    }


    /**
     * Builds a URL that will be sent to the database so that a movie can be added to a User's list
     * @return The URL that was built
     */
    private String buildAddMovieURL(String listName) {

        StringBuilder urlBuilder = new StringBuilder(ADD_MOVIE_URL);
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");

        try {
            urlBuilder.append("movie_id=");
            urlBuilder.append(id.replaceAll(" ", "+").trim());

            urlBuilder.append("&list_name=");
            urlBuilder.append(listName.replaceAll(" ", "+").trim());

            urlBuilder.append("&movie_title=");
            urlBuilder.append(movieTitle.replaceAll(" ", "+").replace("\'", "\\'").trim());

            urlBuilder.append("&release_date=");
            urlBuilder.append(releaseDate.trim());

            urlBuilder.append("&email=");
            urlBuilder.append(email.trim());

            urlBuilder.append("&synopsis=");
            urlBuilder.append(synopsis.replaceAll(" ", "+").replace("\'", "\\'").trim());

        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return urlBuilder.toString();
    }

    /**
     * Runs an AsyncTask helper method to send movie information to the database so that the movie
     * may be added to a user's list
     * @param listName Value returned when user selects list
     */
    public void addMovie(String listName) {
        String movieDetails = buildAddMovieURL(listName);
        System.out.println(movieDetails);

        UpdateListTask addMovie = new UpdateListTask();
        addMovie.execute(movieDetails);
    }

    /**
     * Runs an AsyncTask helper method to send movie information to the database so that the movie
     * selected movie can be deleted from "To Watch" and added to "Watched"
     * @param view
     */
    public void moveToWatched(View view) {
        StringBuilder urlBuilder = new StringBuilder(MOVE_TO_WATCHED_URL);
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");

        try {
            urlBuilder.append("email=");
            urlBuilder.append(email.trim());

            urlBuilder.append("&movie_id=");
            urlBuilder.append(id.replaceAll(" ", "+").trim());
        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        UpdateListTask newListTask = new UpdateListTask();
        newListTask.execute(urlBuilder.toString());
    }

    /**
     * Runs an AsyncTask helper method to send movie information to the database so that the movie
     * can be deleted from the selected list
     * @param view
     */
    public void deleteMovie(View view) {

        StringBuilder urlBuilder = new StringBuilder(DELETE_MOVIE_URL);
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");

        try {
            urlBuilder.append("email=");
            urlBuilder.append(email.trim());

            urlBuilder.append("&list=");
            urlBuilder.append(listName.replaceAll(" ", "+").trim());

            urlBuilder.append("&movie_id=");
            urlBuilder.append(id.trim());

        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "URL error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        System.out.println(urlBuilder.toString());

        UpdateListTask deleteMovie = new UpdateListTask();
        deleteMovie.execute(urlBuilder.toString());
    }

    private class UpdateListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String movieDetailUrl = params[0];
            BufferedReader bufferedReader;
            HttpURLConnection connection = null;
            String result;
            try {
                URL url = new URL(movieDetailUrl);
                connection = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                result = bufferedReader.readLine();
                Log.d("PHP response", result);

                return result;
            } catch (Exception e) {
                Log.d("PHP response", e.getMessage());
                return result = "Error. Reason: " + e.getMessage();
            }
            finally {
                if (connection != null)
                    connection.disconnect();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), jsonObject.get("message").toString() , Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), jsonObject.get("error").toString(), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Data problem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        }
    }
}
