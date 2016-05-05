package thuynh90.tacoma.uw.edu.listitwatchit.tabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import thuynh90.tacoma.uw.edu.listitwatchit.R;

/**A fragment for displaying the custom lists created by the user
 * A simple {@link Fragment} subclass.
 */
public class MyListsFragment extends Fragment {


    public MyListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_lists, container, false);
    }

}
