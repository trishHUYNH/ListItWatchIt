package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Movie object class for lists. Creates an instance of Movie for each value
 * returned from JSON results.
 */
public class Movie implements Serializable {

    private String mMovieTitle;
    private String mReleaseDate;
    private String mMovieID;

    public static final String MOVIE_TITLE = "Title";
    public static final String MOVIE_ID = "MovieID";

    /**
     * Creates a Movie object that holds a movie's title and release date
     * @param movieTitle The title of the movie
     * @param movieID The movie ID associated with TMDb
     */
    public Movie(String movieTitle, String movieID) {
        setMovieTitle(movieTitle);
        setMovieID(movieID);
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMovieID() {
        return mMovieID;
    }

    public void setMovieTitle(String movieTitle) {
        this.mMovieTitle = movieTitle;
    }

    public void setMovieID(String movieID) {
        this.mMovieID = movieID;
    }

    /**
     * Parses JSONObject that holds movie information to retreive the movie's title and release date
     * @param movieJSON
     * @param movieList
     * @return String of error. Else, returns null
     */
    public static String parseMovieJSON(String movieJSON, List<Movie> movieList) {
        String reason = null;
        if (movieJSON != null) {
            try {
                JSONArray arr = new JSONArray(movieJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Movie movie = new Movie(obj.getString(Movie.MOVIE_TITLE), obj.getString(Movie.MOVIE_ID));
                    movieList.add(movie);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }
        }
        return reason;
    }
}
