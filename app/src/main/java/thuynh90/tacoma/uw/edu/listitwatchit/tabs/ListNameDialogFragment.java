package thuynh90.tacoma.uw.edu.listitwatchit.tabs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import thuynh90.tacoma.uw.edu.listitwatchit.MainActivity;
import thuynh90.tacoma.uw.edu.listitwatchit.R;

/**
 * Dialog to display when user creates a custom list.
 * Allows user to enter list name in text field to create a new list.
 */
public class ListNameDialogFragment extends DialogFragment {

    private SharedPreferences mSharedPreferences;

    public ListNameDialogFragment() {
        // Required empty public constructor
    }

    /**
     * User enters list name and selects positive button to create list.
     * Empty text field and positive button closes list. Prevents an empty
     * list from being created.
     * Negative button closes dialog.
     * @param savedInstanceState Previous fragment activity
     * @return
     */
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
                // Prevent users from creating empty list names
                if(listNameField.getText().toString().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid list name.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create new list
                    String listName = listNameField.getText().toString().replaceAll(" ", "+").replaceAll("\'", "\\'").trim();
                    ((MainActivity) getActivity()).createList(listName);
                }
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
