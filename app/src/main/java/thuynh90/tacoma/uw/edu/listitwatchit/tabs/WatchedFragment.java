package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import thuynh90.tacoma.uw.edu.listitwatchit.R;

/**
 * A fragment representing a list of Movie items for the user's "Watched" list.
 * MainActivity implements watchedFragmentInteractionListener.
 */
public class WatchedFragment extends Fragment {

    // Change this for Watched list
    private static final String VIEW_LIST_URL = "http://cssgate.insttech.washington.edu/~_450atm6/viewList.php?cmd=watched&email=";
    private WatchedListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private List<Movie> movieList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment.
     */
    public WatchedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Concatenates URL with user's email from SharedPreferences.
     * Calls DownloadMoviesTask to retrieve movie from database.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            int mColumnCount = 1;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }

        // Retrieve movie list data
        downloadWatched();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WatchedListFragmentInteractionListener) {
            mListener = (WatchedListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ToWatchFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Refreshes "To Watch" list when user returns back to fragment after searching a movie.
     * Calls helper method to retrieve data.
     */
    @Override
    public void onResume(){
        super.onResume();
        downloadWatched();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface WatchedListFragmentInteractionListener {
        void watchedFragmentInteraction(Movie item, String task);
    }

    /**
     * Helper method that retrieves user email and creates an instance of
     * DownloadMoviesTask to retrieve movie list from database.
     */
    public void downloadWatched() {
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        // Retrieves email from SharedPreferences, return 'error' if email not found
        String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");

        //Check for network connection.
        //If network exists, load list from web database.
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadMoviesTask downloadMovies = new DownloadMoviesTask();
            downloadMovies.execute(VIEW_LIST_URL + email);
        }
    }

    /**
     * Opens URL connection and parses JSON result to view lists specific
     * to users. Called from onCreate.
     */
    private class DownloadMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String movieValues = params[0];
            BufferedReader bufferedReader;
            HttpURLConnection connection = null;
            String result;
            try {
                URL url = new URL(movieValues);
                connection = (HttpURLConnection) url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                result = bufferedReader.readLine();

                return result;
            } catch (Exception e) {
                return result = "Unable to view lists. Reason: " + e.getMessage();
            }
            finally {
                if (connection != null)
                    connection.disconnect();
            }
        }

        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }

            movieList = new ArrayList<Movie>();
            result = Movie.parseMovieJSON(result, movieList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }
            // Set adapter regardless of list size
            mRecyclerView.setAdapter(new WatchedRecyclerViewAdapter(movieList, mListener));
        }
    }

    public List<Movie> getMovieList () {
        return movieList;
    }

    public void shareList (){
        StringBuilder emailBody = new StringBuilder();

        for (int i = 0; i < movieList.size(); i++) {
            Movie movie = movieList.get(i);
            emailBody.append(movie.getMovieTitle());
            emailBody.append("\n");
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setData(Uri.parse("mailto:"));
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "My movie list on List It Watch It");
        i.putExtra(Intent.EXTRA_TEXT   , emailBody.toString());
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity().getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
