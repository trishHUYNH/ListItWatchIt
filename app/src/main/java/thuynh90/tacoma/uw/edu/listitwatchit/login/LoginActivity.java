package thuynh90.tacoma.uw.edu.listitwatchit.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import thuynh90.tacoma.uw.edu.listitwatchit.MainActivity;
import thuynh90.tacoma.uw.edu.listitwatchit.R;
import thuynh90.tacoma.uw.edu.listitwatchit.login.LoginFragment.LoginInteractionListener;
import thuynh90.tacoma.uw.edu.listitwatchit.login.RegisterFragment.RegisterInteractionListener;

/**
 * Hosts fragments for registration and login.
 * Processes JSON results from queries for registration and login.
 * Returns back to LoginFragment or MainActivity depending on method called.
 */
public class LoginActivity extends AppCompatActivity implements RegisterInteractionListener, LoginInteractionListener {

    private static final String REGISTER_URL = "http://cssgate.insttech.washington.edu/~_450atm6/registerUser.php?";
    private static final String LOGIN_URL = "http://cssgate.insttech.washington.edu/~_450atm6/login.php?";
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getSupportFragmentManager().beginTransaction().add(R.id.login_container, new LoginFragment() ).commit();
    }

    @Override
    /**
     * Called from RegisterFragment.
     * Processes inputs passed into one String for query.
     * Parses JSON result looking for 'success' if registration went through,
     * then redirects user to login page.
     * Creates instance of inner class RegisterTask.
     */
    public void register(String email, String password) {
        String userInformation = "email=" + email + "&password=" + password;
        class RegisterTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String registration = params[0];
                BufferedReader bufferedReader;
                HttpURLConnection connection = null;
                String result;
                try {
                    URL url = new URL(REGISTER_URL + registration);
                    connection = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    result = bufferedReader.readLine();

                    return result;
                } catch (Exception e) {
                    return result = "Unable to register. Reason: " + e.getMessage();
                }
                finally {
                    if (connection != null)
                        connection.disconnect();
                }

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = (String) jsonObject.get("result");
                    if (status.equals("success")) {
                        Toast.makeText(getApplicationContext(), "List It Watch It account created! Please login.", Toast.LENGTH_LONG).show();
                        returnToLogin();

                    } else {
                        Toast.makeText(getApplicationContext(), "Account creation failed: " + jsonObject.get("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Data problem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

        }
        RegisterTask newRegistration = new RegisterTask();
        newRegistration.execute(userInformation);
    }

    @Override
    /**
     * Called from LoginFragment.
     * Processes inputs passed into one String for query.
     * Parses JSON result looking for 'success' if login went through,
     * then redirects user to MainActivity.
     * Creates instance of inner class LoginTask.
     */
    public void login(String email, String password) {
        String userInformation = "email=" + email + "&password=" + password;
        class LoginTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String loginValues = params[0];
                BufferedReader bufferedReader;
                HttpURLConnection connection = null;
                String result;
                try {
                    URL url = new URL(LOGIN_URL + loginValues);
                    connection = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    result = bufferedReader.readLine();

                    return result;
                } catch (Exception e) {
                    return result = "Unable to login. Reason: " + e.getMessage();
                }
                finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = (String) jsonObject.get("result");
                    if (status.equals("success")) {
                        Toast.makeText(getApplicationContext(), "Login successful. Hello again!", Toast.LENGTH_LONG).show();
                        directToMain();

                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed: " + jsonObject.get("error"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Data problem: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println(e.getMessage());
                }
            }

        }
        LoginTask newLogin = new LoginTask();
        newLogin.execute(userInformation);

    }

    /**
     * Helper method to return user to login fragment after successful registration.
     */
    public void returnToLogin() {

        getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new LoginFragment()).commit();
    }

    /**
     * Helper method to return user to main after successful login.
     * Edits SharedPreferences to be true that user is logged in.
     */
    public void directToMain() {
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS), Context.MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(getString(R.string.LOGGEDIN), true).apply();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}