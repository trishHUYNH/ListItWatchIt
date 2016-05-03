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

import thuynh90.tacoma.uw.edu.listitwatchit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private RegisterInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    /**
     * Validates EditText fields to prevent empty fields, invalid emails,
     * and non-matching passwords.
     * Calls register from LoginActivity.
     * Sets listener to link back to login page
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
                String email = emailRegister.getText().toString().trim();
                String password = passwordRegister.getText().toString().trim();
                String passwordConfirm = passwordConfirmRegister.getText().toString().trim();

                if(email.isEmpty() || !email.contains("@")) {
                    emailRegister.setError("Enter valid email");
                    return;
                }
                if(password.isEmpty() || passwordConfirm.isEmpty()) {
                    passwordRegister.setError("Enter valid password");
                    return;
                }
                if(password.length() < 6) {
                    passwordRegister.setError("Password must be at least six characters");
                    return;
                }
                if((!password.equals(passwordConfirm))) {
                    passwordRegister.setError("Passwords must match");
                    return;
                }

                // Passes through all validation
                // Attempt registration
                ( (LoginActivity) getActivity()).register(email, password);
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

    public interface RegisterInteractionListener {
        void register(String email, String password);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterInteractionListener) {
            mListener = (RegisterInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RegisterInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
