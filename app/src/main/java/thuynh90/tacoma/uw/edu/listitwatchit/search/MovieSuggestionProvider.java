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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by thuynh90 on 4/28/16.
 */
public class MovieSuggestionProvider extends ContentProvider{

    static private String API_KEY = "6e2537d9c135091718d558d8d56a7cde";
    static private String movieQueryURL = "http://api.themoviedb.org/3/search/movie?api_key=";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    /**
     * Submits query to TMDb, parses JSON result, returns cursor array of suggested results.
     */
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        BufferedReader reader = null;
        HttpURLConnection urlConnection = null;
        String urlString;
        String jsonString;
        String query = uri.getLastPathSegment().toLowerCase();
        String tmdbQuery = query.replaceAll(" ", "+");
        MatrixCursor cursor;
        int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

        try {
            urlString = movieQueryURL + API_KEY + "&query=" + tmdbQuery;
            System.out.println(urlString);
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read input stream into String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder stringBuilder = new StringBuilder();
            if(inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            if(stringBuilder.length() == 0) {
                return null;
            }

            jsonString = stringBuilder.toString();


            try {
                // Retrieves values from String[] movies to put into cursor table that fits user query
                cursor = new MatrixCursor(
                        new String[] {
                                BaseColumns._ID,
                                SearchManager.SUGGEST_COLUMN_TEXT_1,
                                SearchManager.SUGGEST_COLUMN_TEXT_2,
                                SearchManager.SUGGEST_COLUMN_INTENT_DATA
                        }
                );

                JSONObject JSONString = new JSONObject(jsonString);

                JSONArray moviesArray = JSONString.getJSONArray("results");

                for(int i = 0; i < moviesArray.length() && cursor.getCount() < limit; i++)
                {

                    JSONObject movie = moviesArray.getJSONObject(i);
                    String movieTitle = movie.getString("original_title");
                    String releaseDate = "Release Date: " + movie.getString("release_date");
                    String movieID = movie.getString("id");
                    if (movieTitle.toLowerCase().contains(query)) {
                        cursor.addRow(new Object[]{i, movieTitle, releaseDate, movieID});
                    }

                }

                if (cursor.getCount() == 0 && query.length() > 1) {
                    cursor.addRow(new Object[]{0, "No movies found matching your search", null, null});
                }

                return cursor;
            } catch (JSONException e) {
                return null;
            }

        } catch (Exception e) {
                e.printStackTrace();
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
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
