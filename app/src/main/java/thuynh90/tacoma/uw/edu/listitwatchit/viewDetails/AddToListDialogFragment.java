package thuynh90.tacoma.uw.edu.listitwatchit.viewDetails;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.MyList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddToListDialogFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AddToListDialogFragment extends DialogFragment {

    List<MyList> movieLists;
    public static CharSequence[] movieTitles = {"To Watch", "Watched", "Don't click me, please. :("};
    private static final String VIEW_LIST_URL = "http://cssgate.insttech.washington.edu/~_450atm6/viewList.php?cmd=mylists&email=";
    private OnAddToListDialogFragmentInteractionListener mListener;

    public AddToListDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.select_list);
        builder.setItems(movieTitles, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((ViewMovieDetailsActivity) getActivity()).addMovie(movieTitles[which].toString());
            }
        });


        return builder.create();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAddToListDialogFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddToListDialogFragmentInteractionListener) {
            mListener = (OnAddToListDialogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAddToListDialogFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnAddToListDialogFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAddToListDialogFragmentInteraction(Uri uri);
    }

    /**
     * Helper method that retrieves user email and creates an instance of
     * DownloadMoviesTask to retrieve movie list from database.
     */
    public void downloadHelper()  {
        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        // Retrieves email from SharedPreferences, return 'error' if email not found
        String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");
        DownloadMyListsTask downloadMovies = new DownloadMyListsTask();
        downloadMovies.execute(VIEW_LIST_URL + email);
    }

    /**
     * Opens URL connection and parses JSON result to view lists specific
     * to users. Called from onCreate.
     */
    public class DownloadMyListsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String myListsValues = params[0];
            BufferedReader bufferedReader;
            HttpURLConnection connection = null;
            String result;
            try {
                URL url = new URL(myListsValues);
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

            movieLists = new ArrayList<MyList>();
            result = MyList.parseListJSON(result, movieLists);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                return;
            }

            if (!movieLists.isEmpty()) {
                int i = 0;
                AddToListDialogFragment.movieTitles = new String[movieLists.size()];
                for(MyList list : movieLists) {
                    System.out.println("List name: " + list.getListName());
                    System.out.println("List ID: " + list.getListID());
                    AddToListDialogFragment.movieTitles[i] = list.getListName();
                    i++;
                }
            }
        }
    }
}
