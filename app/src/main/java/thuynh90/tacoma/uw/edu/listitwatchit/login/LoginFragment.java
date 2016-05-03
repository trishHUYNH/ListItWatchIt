package thuynh90.tacoma.uw.edu.listitwatchit.login;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import thuynh90.tacoma.uw.edu.listitwatchit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private LoginInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    /**
     * TODO: Login authentication
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText emailLogin = (EditText) view.findViewById(R.id.login_email);
        final EditText passwordLogin = (EditText) view.findViewById(R.id.login_password);
        Button buttonLogin = (Button) view.findViewById(R.id.login_button);
        buttonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Test. ToBeDeleted
                Toast.makeText(getActivity(), "Login button clicked", Toast.LENGTH_SHORT).show();

            }
        });
        TextView registerLink = (TextView) view.findViewById(R.id.register_link);
        registerLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment registerFragment = new RegisterFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.login_container, registerFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        return view;
    }

    public interface LoginInteractionListener {
        void login(String email, String password);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginInteractionListener) {
            mListener = (LoginInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
