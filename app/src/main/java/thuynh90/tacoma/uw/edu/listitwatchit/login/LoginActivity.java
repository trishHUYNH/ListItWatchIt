package thuynh90.tacoma.uw.edu.listitwatchit.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import thuynh90.tacoma.uw.edu.listitwatchit.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getSupportFragmentManager().beginTransaction().add(R.id.login_container, new LoginFragment() ).commit();


    }
}
