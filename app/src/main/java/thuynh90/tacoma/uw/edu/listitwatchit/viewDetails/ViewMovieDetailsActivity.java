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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
    private RatingBar mRatingBar;
    private TextView mMPAATextView;
    private TextView mSynopsisTextView;
    private ImageView mPosterImageView;
    private Button mAddMovieButton;
    String id;
    String movieTitle = "";
    String releaseDate= "";
    String mpaaRating= "";
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

        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);

        // Viewing from "To Watch" list
        if (lastLocation.equals("fromToWatch")){
            setContentView(R.layout.activity_view_movie_details_from_to_watch);
        } else {
            setContentView(R.layout.activity_view_movie_details);
            mAddMovieButton = (Button) findViewById(R.id.add_to_list_button);
            assert mAddMovieButton != null;
            mAddMovieButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //User not logged in. Can't add movie
                    if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
                        Toast.makeText(getApplicationContext(), "Please login to add a movie." , Toast.LENGTH_LONG).show();
                    } else {
                        DialogFragment addToListDialog = new AddToListDialogFragment();
                        addToListDialog.show(getSupportFragmentManager(), "launch");
                    }
                }
            });
        }

        mMovieTitleTextView = (TextView) findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.release_date);
        mRatingBar = (RatingBar) findViewById(R.id.rating_bar);
        mMPAATextView = (TextView) findViewById(R.id.mpaa);
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
            String detailsRequestURL = "http://api.themoviedb.org/3/movie/" + id + "?api_key=6e2537d9c135091718d558d8d56a7cde";
            String MPAARatingRequestURL = "http://api.themoviedb.org/3/movie/" + id + "/release_dates?api_key=6e2537d9c135091718d558d8d56a7cde";

            loadDetails(requestFromTMDb(detailsRequestURL));
            loadMPAARating(requestFromTMDb(MPAARatingRequestURL));
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
         * @param urlString String that represents the URL that will be queried
         * @return String response from the URL
         */
        public String requestFromTMDb(String urlString) {
            BufferedReader reader;
            HttpURLConnection urlConnection = null;
            String jsonString = null;

            
            try {
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
            }
            catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                return jsonString;
            }
        }

        public void loadMPAARating(String jsonString) {
            JSONObject JSONmovie = null;
            try {
                JSONmovie = new JSONObject(jsonString);
                JSONArray resultsArray = JSONmovie.getJSONArray("results");

                //Iterate through the array of releases to find the US release
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject currentObj = resultsArray.getJSONObject(i);
                    if (currentObj.getString("iso_3166_1").equals("US")) {
                        mpaaRating = currentObj.getJSONArray("release_dates").getJSONObject(0).getString("certification");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void loadDetails(String jsonString) {
            JSONObject JSONmovie = null;
            try {
                JSONmovie = new JSONObject(jsonString);
                movieTitle = JSONmovie.getString("title");
                releaseDate = JSONmovie.getString("release_date");
                synopsis = JSONmovie.getString("overview");

                String posterUrlString = "http://image.tmdb.org/t/p/original" + JSONmovie.getString("poster_path");

                //get poster
                URL posterURL = new URL(posterUrlString);
                poster = BitmapFactory.decodeStream(posterURL.openConnection().getInputStream());

                Double rating = JSONmovie.getDouble("vote_average");
                float starRating =  (float) (rating/2);
                mRatingBar.setRating(starRating);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets TextViews to the proper values that have been set in the AsyncTask
     */
    public void updateView(){
        mMovieTitleTextView.setText(movieTitle);
        mReleaseDateTextView.setText(releaseDate);
        mMPAATextView.setText(mpaaRating);
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
