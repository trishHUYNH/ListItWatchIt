package thuynh90.tacoma.uw.edu.listitwatchit.viewDetails;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import thuynh90.tacoma.uw.edu.listitwatchit.R;

public class ViewMovieDetailsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_view_movie_details);

        mMovieTitleTextView = (TextView) findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.release_date);
        mSynopsisTextView = (TextView) findViewById(R.id.synopsis);
        mPosterImageView = (ImageView) findViewById(R.id.poster);

        Intent intent = getIntent();
        id = intent.getStringExtra("movieID");
        loadMovieDetailsTask task = new loadMovieDetailsTask();
        task.execute();

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
}
