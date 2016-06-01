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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import thuynh90.tacoma.uw.edu.listitwatchit.MainActivity;
import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.CustomMovieListFragment;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.MyList;
import thuynh90.tacoma.uw.edu.listitwatchit.tabs.MyListsFragment;

/**
 * Dialog fragment that prompts user to choose a list to add their selected
 * movie to.
 */
public class AddToListDialogFragment extends DialogFragment {

    List<MyList> movieLists;
    ArrayAdapter<String> adapter;
    String jSONResult;
    private OnAddToListDialogFragmentInteractionListener mListener;
    private static final String VIEW_LIST_URL = "http://cssgate.insttech.washington.edu/~_450atm6/viewList.php?cmd=mylists&email=";

    public AddToListDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        downloadList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the dialog title
        builder.setTitle(R.string.select_list);

        // Set adapter that to dialog that contains movie lists
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (getActivity() instanceof ViewMovieDetailsActivity) {
                    ((ViewMovieDetailsActivity) getActivity()).addMovie(adapter.getItem(which));
                }
                else if (getActivity() instanceof MainActivity) {

                }

            }
        });
        return builder.create();
    }

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
        void onAddToListDialogFragmentInteraction(Uri uri);
    }

    /**
     * Parses JSON results from MyList class. Adds list names to ArrayAdapter for
     * dialog
     */
    public void inputList() {
        // Something wrong with the network or the URL.
        if (jSONResult.startsWith("Unable to")) {
            Toast.makeText(getActivity().getApplicationContext(), jSONResult, Toast.LENGTH_LONG).show();
            return;
        }

        movieLists = new ArrayList<>();
        jSONResult = MyList.parseListJSON(jSONResult, movieLists);
        // Something wrong with the JSON returned.
        if (jSONResult != null) {
            Toast.makeText(getActivity().getApplicationContext(), jSONResult, Toast.LENGTH_LONG).show();
            return;
        }

        // For every list the user has, add it to the adapter
        if (!movieLists.isEmpty()) {
            for (MyList list : movieLists) {
                adapter.add(list.getListName());
            }
        }
    }

    /**
     * Opens URL connection and calls inputList() to parse JSON results to view lists specific
     * to users. Called from onCreateDialog.
     */

    private void downloadList() {
        class DownloadMyListsTask extends AsyncTask<String, Void, String> {

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
                    return "Unable to view lists. Reason: " + e.getMessage();
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }

            protected void onPostExecute(String result) {
                jSONResult = result;
                inputList();
            }
        }

        SharedPreferences mSharedPreferences = this.getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        // Retrieves email from SharedPreferences, return 'error' if email not found
        String email = mSharedPreferences.getString(getString(R.string.USERNAME), "error");
        DownloadMyListsTask downloadMovies = new DownloadMyListsTask();
        downloadMovies.execute(VIEW_LIST_URL + email);
    }
}
