package thuynh90.tacoma.uw.edu.listitwatchit.login;


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
public class RegisterFragment extends Fragment {


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    /**
     * TODO: Create accounts to MYSQL
     * TODO: Automatically create "To Watch" & "Watch" lists for every new account
     * TODO: Direct user back to login after registration
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        final EditText emailRegister = (EditText) view.findViewById(R.id.register_email);
        final EditText passwordRegister = (EditText) view.findViewById(R.id.register_password);
        final EditText passwordConfirmRegister = (EditText) view.findViewById(R.id.register_password_confirm);
        final Button buttonRegister = (Button) view.findViewById(R.id.register_button);
        buttonRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Test. ToBeDeleted.
                Toast.makeText(getActivity(), "Create account button clicked", Toast.LENGTH_SHORT).show();
            }
        });
        final TextView loginLink = (TextView) view.findViewById(R.id.login_link);
        loginLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment loginFragment = new LoginFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.login_container, loginFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });
        return view;
    }

}
