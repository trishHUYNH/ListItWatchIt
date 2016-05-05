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

    public static final String MOVIE_TITLE = "Title";
    public static final String MOVIE_RELEASE_DATE = "ReleaseDate";

    public Movie(String movieTitle, String releaseDate) {
        setMovieTitle(movieTitle);
        setMovieReleaseDate(releaseDate);
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMovieReleaseDate() {
        return mReleaseDate;
    }

    public void setMovieTitle(String movieTitle) {
        this.mMovieTitle = movieTitle;
    }

    public void setMovieReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    /**
     *
     * @param movieJSON
     * @param courseList
     * @return String of error. Else, returns null
     */
    public static String parseMovieJSON(String movieJSON, List<Movie> courseList) {
        String reason = null;
        if (movieJSON != null) {
            try {
                JSONArray arr = new JSONArray(movieJSON);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Movie movie = new Movie(obj.getString(Movie.MOVIE_TITLE), obj.getString(Movie.MOVIE_RELEASE_DATE));
                    courseList.add(movie);
                }
            } catch (JSONException e) {
                reason =  "Unable to parse data, Reason: " + e.getMessage();
            }

        }
        return reason;
    }

}
