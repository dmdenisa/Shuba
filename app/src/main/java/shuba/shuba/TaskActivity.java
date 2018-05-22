package shuba.shuba;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import shuba.shuba.database.DbContract;
import shuba.shuba.database.MySqlHelper;
import shuba.shuba.model.Contract;
import shuba.shuba.model.Task;
import shuba.shuba.model.User;

public class TaskActivity extends AppCompatActivity implements Dialog.Callbacks {


    private String username;
    private Dialog mLogoutDialog;
    private Adapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private String groupname;
    private MySqlHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        db = new MySqlHelper(this);

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra(Contract.ProfileActivity.USERNAME);
            groupname = intent.getStringExtra(Contract.ProfileActivity.GROUPNAME);
        }

        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));

        //  We also need an adapter to bind our views to the model
        mAdapter = new Adapter(new Adapter.Callback() {
            @Override
            public void show(Task task) {
                // TODO we should do sth when click on item
            }
        });

        rv.setAdapter(mAdapter);

        updateMembersFromDb();


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

                if (username != null) {
                    navigationViewHeader.setText(username);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                TextView navigationViewHeader = findViewById(R.id.nav_header_text);

                if (username != null) {
                    navigationViewHeader.setText(username);
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
                        mLogoutDialog = Utils.ShowLogOutDialog(TaskActivity.this);
                        break;
                    case R.id.menu_group:
                        goToGroupScreen(username);
                    case R.id.menu_profile:
                        goToProfileScreen();
                        break;
                    case R.id.menu_tasks:
                        break;
                    case R.id.menu_map:
                        goToMapScreen(username);
                        break;




                }
                item.setChecked(true);

                Toast.makeText(TaskActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();

                // close the drawer when one item is selected
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

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

    private void goToProfileScreen() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(Contract.ProfileActivity.USERNAME, username);
        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }

    private void goToGroupScreen(String username) {
        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra(Contract.ProfileActivity.USERNAME, username);
        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }

    private void goToMapScreen(String username) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(Contract.ProfileActivity.USERNAME, username);
        intent.putExtra(Contract.ProfileActivity.GROUPNAME, groupname);
        startActivity(intent);
        //  we no longer need the Login screen
        finish();
    }

    private static class Adapter extends RecyclerView.Adapter {
        List<Task> mData = new ArrayList<>();

        Callback mCallback;

        public Adapter(Callback callback) {
            mCallback = callback;
        }

        public interface Callback {
            void show(Task data);
        }

        public void setData(List<Task> data) {
            mData = data;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //  a new view needs to be inflated here
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_task, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            //  The repository must be bound to the view here
            ((ViewHolder) holder).bind(mData.get(position), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.show(mData.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView taskName;
            TextView taskDescription;
            /**
             * Create the ViewHolder that holds the references to our views
             * @param itemView The parent view, or root view from the inflated XML
             */
            ViewHolder(View itemView) {
                super(itemView);

                //  Cache all the views we will need when binding the model
                cv = (CardView)itemView.findViewById(R.id.card_view);
                taskName = (TextView) itemView.findViewById(R.id.task_name);
                taskDescription = (TextView) itemView.findViewById(R.id.task_description);

            }

            void bind(Task data, View.OnClickListener onClickListener) {
                //  The views are cached, just set the data
                taskName.setText(data.getName());
                if (data.getDescription() != null)
                    taskDescription.setText(data.getDescription());
                itemView.setOnClickListener(onClickListener);
                // TODO add image
            }
        }
    }

    void updateMembersFromDb() {
        List<Task> someData = db.getAllTasksInGroup(groupname); /*TODO = db.getAllUsersInGroup(username);*/
        mAdapter.setData(someData);
        mAdapter.notifyDataSetChanged();

    }

}
