package thuynh90.tacoma.uw.edu.listitwatchit.tabs;

import android.content.Context;
import android.content.SharedPreferences;
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
 * A fragment representing the list of movies in a user's custom list.
 * <p/>
 * Activities containing this fragment MUST implement the {@link CustomMovieListFragmentInteractionListener}
 * interface.
 */
public class CustomMovieListFragment extends Fragment {

    private int mColumnCount = 1;
    private static final String VIEW_LIST_URL = "http://cssgate.insttech.washington.edu/~_450atm6/viewList.php?cmd=watched&email=";
    private RecyclerView mRecyclerView;
    private SharedPreferences mSharedPreferences;
    private CustomMovieListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomMovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // Retrieve movie list data
            downloadHelper();
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CustomMovieListFragmentInteractionListener) {
            mListener = (CustomMovieListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CustomMovieListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    /**
     * Refreshes "To Watch" list when user returns back to fragment after searching a movie.
     * Calls helper method to retrieve data.
     */
    public void onResume(){
        super.onResume();
        downloadHelper();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface CustomMovieListFragmentInteractionListener {
        void onCustomMovieListFragmentInteraction(Movie item);
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

            List<Movie> movieList = new ArrayList<Movie>();
            result = Movie.parseMovieJSON(result, movieList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }

            // Everything is good, show the list of movies.
            if (!movieList.isEmpty()) {
                mRecyclerView.setAdapter(new CustomMovieListRecyclerViewAdapter(movieList, mListener));
            }
        }
    }

    /**
     * Helper method that retrieves user email and creates an instance of
     * DownloadMoviesTask to retrieve movie list from database.
     */
    public void downloadHelper() {
        mSharedPreferences = this.getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        // Retrieves email from SharedPreferences, return 'error' if email not found
        String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");
        DownloadMoviesTask downloadMovies = new DownloadMoviesTask();
        downloadMovies.execute(VIEW_LIST_URL + email);
    }
}
