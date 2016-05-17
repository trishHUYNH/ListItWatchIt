package thuynh90.tacoma.uw.edu.listitwatchit.tabs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import thuynh90.tacoma.uw.edu.listitwatchit.MainActivity;
import thuynh90.tacoma.uw.edu.listitwatchit.R;


public class ListNameDialogFragment extends DialogFragment {



    private SharedPreferences mSharedPreferences;

    public ListNameDialogFragment() {
        // Required empty public constructor
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_list_name_dialog, null);


        final EditText listNameField = (EditText) view.findViewById(R.id.edit_list_text);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        builder.setMessage(R.string.enter_list_name);
        builder.setPositiveButton(R.string.create_list, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Create new list
                String listName = listNameField.getText().toString().replaceAll(" ", "+").replaceAll("\'", "\\'").trim();
                ((MainActivity) getActivity()).createList(listName);

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }


}
