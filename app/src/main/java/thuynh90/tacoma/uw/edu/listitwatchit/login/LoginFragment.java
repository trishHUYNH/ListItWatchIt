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
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import thuynh90.tacoma.uw.edu.listitwatchit.R;

/**
 * Login fragment.
 * Validates user inputs before calling login method.
 * Calls RegistrationFragment when users select link.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private LoginInteractionListener mListener;
    private CallbackManager callbackManager;
    private static final int RC_GOOGLE_SIGN_IN = 9001;
    boolean loggedInToFacebook = false;
    String facebookID;
    GoogleApiClient mGoogleApiClient;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Validates EditText fields to prevent empty fields, invalid emails,
     * and non-matching passwords.
     * Calls login from LoginActivity.
     * Sets listener to link back to registration page
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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

        // Facebook login

        LoginButton fbLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
        fbLoginButton.setFragment(this);

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("1234", "onSuccess");
                loggedInToFacebook = true;
                facebookID = loginResult.getAccessToken().getUserId();
                Log.d("userId", facebookID);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        });

        // Google login

        SignInButton googleLoginButton = (SignInButton) view.findViewById(R.id.google_login_button);
        googleLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
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

    /**
     * Displays error message on connection failure
     * @param connectionResult Result of connection
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this.getActivity(), 0).show();
            return;
        }
    }

    /**
     * Listener interface for when Login is selected
     */
    public interface LoginInteractionListener {
        void login(String email, String password);
    }

    /**
     * Called from Google or Facebook login to pass Hash ID to LoginActivity
     * @param requestCode Specifies which sign in to process
     * @param resultCode
     * @param data User ID values returned from API
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Google sign In
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct != null) {
                ((LoginActivity) getActivity()).socialMediaLogin(acct.getId());
                // Clears default account so user can choose a different account when logging in
                mGoogleApiClient.clearDefaultAccountAndReconnect();
            }
        } // Facebook sign in
        else if (loggedInToFacebook) {
            ( (LoginActivity) getActivity()).socialMediaLogin(facebookID);
        }
    }
}