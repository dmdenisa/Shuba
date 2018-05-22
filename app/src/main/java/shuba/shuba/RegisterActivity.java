package shuba.shuba;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.ThreadLocalRandom;

import shuba.shuba.database.DbContract;
import shuba.shuba.database.MySqlHelper;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mName;
    private TextView mUsername;
    private TextView mEmail;
    private TextView mPassword;
    private TextView mConfirmPassword;
    private Button mSubmit;

    private static final String TAG = "RegisterActivity";

    private MySqlHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MySqlHelper(this);
        setContentView(R.layout.activity_register);

        mName = (TextView) findViewById(R.id.reg_name_text);
        mUsername = (TextView) findViewById(R.id.reg_username_text);
        mEmail = (TextView) findViewById(R.id.reg_email_text);
        mPassword = (TextView) findViewById(R.id.reg_password_text);
        mConfirmPassword = (TextView) findViewById(R.id.reg_confirm_text);
        mSubmit = (Button) findViewById(R.id.reg_create_btn);

        mSubmit.setOnClickListener(this);

    }

    private void goToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }

    @Override
    public void onClick(View v) {
        if (mName.getText().length() == 0 || mUsername.getText().length() == 0
                || mEmail.getText().length() == 0 || mPassword.getText().length() == 0) {
            Toast.makeText(this, R.string.empty_fields_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        /* TODO make min length 6 */
        if (mPassword.getText().length() < 1) {
            Toast.makeText(this, R.string.short_password_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPassword.getText().toString().equals(mConfirmPassword.getText().toString())){

            //TODO add filters for group select!
            Cursor cursor = db.getGroup(0);

            if (!cursor.moveToFirst()){
                /* Create new group! */
                Log.d(TAG, "!!!!!!!!&&&&&!!!!!!");
                int randomNum = ThreadLocalRandom.current().nextInt(0, 200 + 1);
                boolean added = db.addGroup(Integer.toString(randomNum), "new group", mUsername.getText().toString(), 5);

                if (added == false)
                    Log.d(TAG, "No group found!");

                cursor = db.getGroup(0);
            }

            Cursor checkExcistance = db.getUserData(mUsername.getText().toString());
            if (checkExcistance.moveToFirst()){
                Toast.makeText(this, R.string.existing_username_toast, Toast.LENGTH_SHORT).show();
                return;
            }

            cursor.moveToFirst();
            boolean insertOp = db.insertUser(
                    mName.getText().toString(),
                    mUsername.getText().toString(),
                    mPassword.getText().toString(),
                    mEmail.getText().toString(),
                    cursor.getString(cursor.getColumnIndex(DbContract.Group.NAME)));

            if (insertOp == false){
                Toast.makeText(this, R.string.cursor_toast, Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                db.updateGroupMembers(cursor.getString(cursor.getColumnIndex(DbContract.Group.NAME)));
                goToLoginScreen();
            }
        }

        else {
            /* Toast that says the passwords do not match*/
            Toast.makeText(this, R.string.incorrect_confirm_passowrd_toast, Toast.LENGTH_SHORT).show();
        }

    }
}
