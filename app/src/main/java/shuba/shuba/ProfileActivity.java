package shuba.shuba;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

import shuba.shuba.database.DbContract;
import shuba.shuba.database.MySqlHelper;
import shuba.shuba.model.Contract;
import shuba.shuba.model.User;

public class ProfileActivity extends AppCompatActivity implements Dialog.Callbacks {

    private static final String TAG = "ProfileActivity";
    private MySqlHelper db;


    private Dialog mLogoutDialog;
    private TextView mGroupName;
    private TextView mUsername;
    private TextView mEmail;
    private TextView mName;
    private ImageView mProfilePic;
    private DrawerLayout mDrawerLayout;
    private ImageButton mImageButton;
    private TextView mPoints, mLevel, mStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new MySqlHelper(this);



        /* widgets for this activity */

        mGroupName = (TextView) findViewById(R.id.profile_group_name);
        mUsername = (TextView) findViewById(R.id.profile_username);
        mProfilePic = (ImageView) findViewById(R.id.profile_picture);
        mEmail = (TextView) findViewById(R.id.profile_email);
        mName = (TextView) findViewById(R.id.profile_name);
        mImageButton = findViewById(R.id.image_button);
        mPoints = findViewById(R.id.profile_points);
        mLevel = findViewById(R.id.profile_level);
        mStatus = findViewById(R.id.profile_status);


        Intent intent = getIntent();
        if (intent != null) {
            setTextUnderlined(mUsername, intent.getStringExtra(Contract.ProfileActivity.USERNAME));
        }



        /* ------ some menu stuff -------------------------- */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // set my own toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up home as my menu button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                TextView navigationViewHeader = findViewById(R.id.nav_header_text);

                if (mUsername.getText() != null) {
                    navigationViewHeader.setText(mUsername.getText());
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                TextView navigationViewHeader = findViewById(R.id.nav_header_text);

                if (mUsername.getText() != null) {
                    navigationViewHeader.setText(mUsername.getText());
                }

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // do nothing
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // do nothing
            }
        });

        NavigationView navigationView = findViewById(R.id.navigation_view);
        // click listener for navigation items
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_logout:
                        mLogoutDialog = Utils.ShowLogOutDialog(ProfileActivity.this);
                        break;
                    case R.id.menu_profile:
                        break;
                    case R.id.menu_group:
                        goToGroupScreen(mUsername.getText().toString());
                        break;
                    case R.id.menu_tasks:
                        goToTaskScreen(mGroupName.getText().toString(), mUsername.getText().toString());
                        break;
                    case R.id.menu_map:
                        goToMapScreen(mGroupName.getText().toString(), mUsername.getText().toString());
                        break;

                }
                item.setChecked(true);

                // Toast.makeText(ProfileActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();

                // close the drawer when one item is selected
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mProfilePic.setImageBitmap(db.getPicture(mUsername.getText().toString()));
        mImageButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }});
        updateUIFromDb();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                mProfilePic.setImageBitmap(bitmap);
                db.uploadPicture(mUsername.getText().toString(), bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(Dialog dialog) {
        //  identify which dialog was clicked
        if (dialog == mLogoutDialog) {
            // clean up the local database (if another user will be logging in, we don't want the
            // previous user's info to be available for him)

            // log the user out from the UI
            Utils.LogOut(this);
        }
    }

    @Override
    public void onDialogNegativeClick(Dialog dialog) {
        //  do nothing
    }

    private void goToGroupScreen(String username) {
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra(Contract.ProfileActivity.USERNAME, username);
        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }

    private void goToTaskScreen(String groupname, String username) {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra(Contract.ProfileActivity.GROUPNAME, groupname);
        intent.putExtra(Contract.ProfileActivity.USERNAME, username);
        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }

    private void goToMapScreen(String groupname, String username) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(Contract.ProfileActivity.GROUPNAME, groupname);
        intent.putExtra(Contract.ProfileActivity.USERNAME, username);
        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }


    private void updateUIFromDb() {
        // Update data from db
        Cursor cursor = db.getUserData(mUsername.getText().toString());

        if (cursor.getCount() == 0){
            //TODO pop-up - USER DOES NOT EXIST
            return;
        }
        cursor.moveToFirst();
        mName.setText(cursor.getString(cursor.getColumnIndex(DbContract.User.NAME)));
        mEmail.setText(cursor.getString(cursor.getColumnIndex(DbContract.User.EMAIL)));
        mGroupName.setText(cursor.getString(cursor.getColumnIndex(DbContract.User.GROUP_NAME)));
        mLevel.setText(cursor.getString(cursor.getColumnIndex(DbContract.User.LEVEL)));
        //TODO extract image
    }



    private void setTextUnderlined(TextView textView, String text) {
        if (!TextUtils.isEmpty(text)) {
            SpannableString content = new SpannableString(text);
            content.setSpan(new UnderlineSpan(), 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            textView.setText(content);
        }
    }

}
