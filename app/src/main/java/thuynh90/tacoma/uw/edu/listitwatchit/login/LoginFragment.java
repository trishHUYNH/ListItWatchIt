package thuynh90.tacoma.uw.edu.listitwatchit.login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import thuynh90.tacoma.uw.edu.listitwatchit.R;

/**
 * Login fragment.
 * Validates user inputs before calling login method.
 * Calls RegistrationFragment when users select link.
 */
public class LoginFragment extends Fragment {

    private LoginInteractionListener mListener;

    private TextView info;
    private LoginButton fbloginButton;
    private CallbackManager callbackManager;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText emailLogin = (EditText) view.findViewById(R.id.login_email);
        final EditText passwordLogin = (EditText) view.findViewById(R.id.login_password);
        Button buttonLogin = (Button) view.findViewById(R.id.login_button);
        // Listener validates all input before passing values to login()
        buttonLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailLogin.getText().toString().trim();
                String password = passwordLogin.getText().toString().trim();

                if(email.isEmpty() || !email.contains("@")) {
                    emailLogin.setError("Enter valid email");
                    return;
                }
                if(password.isEmpty()) {
                    passwordLogin.setError("Enter valid password");
                    return;
                }
                if(password.length() < 6) {
                    passwordLogin.setError("Password must be at least six characters");
                    return;
                }

                // Passes through all validation
                // Attempt login
                ( (LoginActivity) getActivity()).login(email, password);

            }
        });
        // Listener to re-direct user to register fragment
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

        //facebook
        callbackManager = CallbackManager.Factory.create();
        info = (TextView)view.findViewById(R.id.info);
        fbloginButton = (LoginButton)view.findViewById(R.id.fb_login_button);

        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("userId", loginResult.getAccessToken().getUserId());
                ( (LoginActivity) getActivity()).socialMediaLogin(loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        });
        return view;
    }

    /**
     * Listener interface for when Login is selected
     */
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
