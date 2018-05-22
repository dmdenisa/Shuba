package shuba.shuba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import shuba.shuba.database.MySqlHelper;
import shuba.shuba.model.Contract;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private MySqlHelper db;

    /* widgets used by this activity */
    private TextView mUsername;
    private TextView mPassword;
    private Button mLoginBtn;
    private Button mRegisterBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new MySqlHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // hide action bar
        }

        //  TODO sth is wrong here check if user already logged in
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getString(Contract.Preferences.AUTH_HASH, null) != null) {
            //  go directly to profile screen
            goToProfileScreen(preferences.getString(Contract.Preferences.USERNAME, null));
        }

        /* bind widgets with xml */
        mLoginBtn = (Button) findViewById(R.id.login_log_btn);
        mRegisterBtn = (Button) findViewById(R.id.login_register_btn);

        mUsername = (TextView) findViewById(R.id.login_user_text);
        mPassword = (TextView) findViewById(R.id.login_password_text);

        mLoginBtn.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);

        /* hide keyboard when not necessary */
        findViewById(R.id.container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                closeKeyboard(v);
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() called with: v = [" + v + "]");
        switch (v.getId()) {
            case R.id.login_log_btn:
                closeKeyboard(v);
                //  when the user tapped the button, retrieve the username and password and perform the login
                performLogin(mUsername.getText().toString(), mPassword.getText().toString());
                break;
            case R.id.login_register_btn:
                performRegister();
                break;
        }
    }


    private void goToProfileScreen(String username) {
         Intent intent = new Intent(this, ProfileActivity.class);
        //  now we can send some extra information to the Profile screen
         intent.putExtra(Contract.ProfileActivity.USERNAME, username);

        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }

    private void goToRegisterScreen() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }

    private void closeKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void performRegister() {
        goToRegisterScreen();
    }

    private void performLogin(String username, String password) {

        Cursor cursor = db.getUserData(username);
        cursor.moveToFirst();

        if (username.length() == 0 || password.length() == 0) {
            Toast.makeText(this, R.string.empty_fields_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor.getCount() != 0) {
            if (cursor.getString(3).equals(password))
                goToProfileScreen(username);
            else {
                Toast.makeText(this, R.string.error_incorrect_password, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, R.string.inexistent_user_toast, Toast.LENGTH_SHORT).show();
        }


    }

}
