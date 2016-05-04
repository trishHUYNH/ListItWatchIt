package thuynh90.tacoma.uw.edu.listitwatchit.viewDetails;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class ViewMovieDetailsActivity extends AppCompatActivity {

    private final static String MOVIE_ADD_URL = "http://cssgate.insttech.washington.edu/~_450atm6/addMovie.php?";

    static private String API_KEY = "6e2537d9c135091718d558d8d56a7cde";

    private TextView mMovieTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mSynopsisTextView;
    private ImageView mPosterImageView;
    String id;
    String movieTitle = "";
    String releaseDate= "";
    String synopsis= "";
    Bitmap poster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("movieID");
        loadMovieDetailsTask task = new loadMovieDetailsTask();
        task.execute();
        setContentView(R.layout.activity_view_movie_details);

        mMovieTitleTextView = (TextView) findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.release_date);
        mSynopsisTextView = (TextView) findViewById(R.id.synopsis);
        mPosterImageView = (ImageView) findViewById(R.id.poster);

        Button addMovieButton = (Button) findViewById(R.id.add_to_list_button);
//        addMovieButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Adding " + movieTitle + " to your To Watch list.", Toast.LENGTH_LONG)
//                        .show();
//
//                String url = buildAddMovieURL();
//                addMovieTask addTask = new addMovieTask();
//                addTask.execute(url);
//            }
//        });



    }


    public class loadMovieDetailsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            query(id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateView();
            onStart();
        }

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



    private String buildAddMovieURL() {

        StringBuilder urlBuilder = new StringBuilder(MOVIE_ADD_URL);
        // Delete this
        String listId = "370";
        try {
            urlBuilder.append("movie_id=");
            urlBuilder.append(id.replaceAll(" ", "+").trim());

            urlBuilder.append("&movie_title=");
            urlBuilder.append(movieTitle.replaceAll(" ", "+").trim());

            urlBuilder.append("&release_date=");
            urlBuilder.append(releaseDate.trim());

            urlBuilder.append("&list_id=");
            urlBuilder.append(listId.trim()); //Hard coding in list #1

            urlBuilder.append("&synopsis=");
            urlBuilder.append(synopsis.replaceAll(" ", "+").trim());

        }
        catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Something wrong with the url" + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return urlBuilder.toString();
    }

    public void addMovie(View view) {
        String movieDetails = buildAddMovieURL();
        System.out.println(movieDetails);
        class addMovieTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String movieDetailUrl = params[0];
                BufferedReader bufferedReader;
                HttpURLConnection connection = null;
                String result;
                try {
                    // Test. ToBeDeleted.
                    System.out.println("MOVIE DETAIL URL: " + movieDetailUrl);
                    URL url = new URL(movieDetailUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    result = bufferedReader.readLine();
                    Log.d("PHP response", result);

                    return result;
                } catch (Exception e) {
                    Log.d("PHP response", e.getMessage());
                    return result = "Unable to Add. Reason: " + e.getMessage();
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
                        Toast.makeText(getApplicationContext(), "Movie successfully added", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "Movie not added: " + jsonObject.get("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Data problem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                }
            }
        }
        addMovieTask addMovie = new addMovieTask();
        addMovie.execute(movieDetails);
    }




}
