package thuynh90.tacoma.uw.edu.listitwatchit.search;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by thuynh90 on 4/28/16.
 */
public class MovieSuggestionProvider extends ContentProvider{

    private String[] movies;
    static private String API_KEY = "6e2537d9c135091718d558d8d56a7cde";
    static private String movieQueryURL = "http://api.themoviedb.org/3/search/movie?api_key=";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    /**
     * TODO: Add year into Cursor to display in search results
     * TODO: Display "No Results Found"
     * TODO: Replace spaces in queries with "+"
     */
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        HttpURLConnection urlConnection;
        String urlString;
        String jsonString;
        BufferedReader reader;
        String query = uri.getLastPathSegment().toLowerCase();
        int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

        try {
            urlString = movieQueryURL + API_KEY + "&query=" + query;
            System.out.println(urlString);
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read input stream into String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();
            if(inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = reader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            if(stringBuffer.length() == 0) {
                return null;
            }

            jsonString = stringBuffer.toString();

            //Call helper method to return String array of JSON paths
            try {
                movies = getJSONPaths(jsonString);
            } catch (JSONException e) {
                return null;
            }

        } catch (Exception e) {
                e.printStackTrace();
        }


        // Retrieves values from String[] movies to put into cursor that fits user query
        MatrixCursor cursor = new MatrixCursor(
                new String[] {
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                }
        );
        if (movies != null) {
            int length = movies.length;
            for (int i = 0; i < length && cursor.getCount() < limit; i++) {
                String movie = movies[i];
                if (movie.toLowerCase().contains(query)){
                    cursor.addRow(new Object[]{ i, movie, i });
                }
            }
        }
        return cursor;
    }

    /**
     *
     * @param jsonString
     * @return Array of movie titles
     * @throws JSONException
     *
     * TODO: Add movie year to array
     */
    private String[] getJSONPaths(String jsonString) throws JSONException {

        JSONObject JSONString = new JSONObject(jsonString);

        JSONArray moviesArray = JSONString.getJSONArray("results");
        String[] result = new String[moviesArray.length()];

        for(int i = 0; i <moviesArray.length();i++)
        {
            JSONObject movie = moviesArray.getJSONObject(i);
            String movieTitle = movie.getString("original_title");
            result[i] = movieTitle;
        }

        return result;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
