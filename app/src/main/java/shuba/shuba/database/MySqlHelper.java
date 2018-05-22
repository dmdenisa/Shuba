package shuba.shuba.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;

import shuba.shuba.model.Task;
import shuba.shuba.model.User;

/**
 * Created by andreea on 13/05/2018.
 *
 * This represents the link between the application and the database support. It implements
 * the following functionality:
 *   1. creating the SQLite tables
 *   2. handling database upgrades
 */
public class MySqlHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "shuba.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "MySQLHelper";

    public MySqlHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    Task[] tasks = {new Task("Know each other!",
            "Get to know your new friends. After you checked all the other members, mark this task as done!",
            false, 5),
            new Task("Check the map!",
                    "Mark this task as done after you checked out your location!",
                    false, 5),
            new Task("Upload picture!",
                    "Upload a picture as your profile picture! Mark this task done after you changed the picture",
                    false, 5
            ),
            new Task("Meet up!",
                    "Meet with the other members of the group! One of you mark the task as done. (Do not forget to have your location on)",
                    false, 5),
            new Task("Visit the Old Town of Bucharest!",
                    "You have one week to visit the Old City Centre of Bucharest. " +
                            "Mark this task done when you are located in the Old Town (make sure your location is on)." +
                            "You could go there as a group or individually. ",
                    false, 5)
    };
    static int indexTask = 0;

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the profile table (this is where all of the users data will be persisted)
        db.execSQL("CREATE TABLE " + DbContract.User.TABLE + "("
                + DbContract.User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DbContract.User.NAME + " TEXT, "
                + DbContract.User.USERNAME + " TEXT NOT NULL UNIQUE ON CONFLICT ABORT, "
                + DbContract.User.PASSWORD + " TEXT, "
                + DbContract.User.EMAIL + " TEXT, "
                + DbContract.User.GROUP_NAME + " TEXT, "
                + DbContract.User.LEVEL + " INTEGER, "
                + DbContract.User.PICTURE + " BLOB, "
                + DbContract.User.LOGIN + " BOOLEAN "
                + ")");

        // Create the repository table (this is where all of the group data will be persisted)
        db.execSQL("CREATE TABLE " + DbContract.Group.TABLE + "("
                + DbContract.Group._ID + " INTEGER PRIMARY KEY, "
                + DbContract.Group.NAME + " TEXT NOT NULL UNIQUE ON CONFLICT ABORT, "
                + DbContract.Group.DESCRIPTION + " TEXT, "
                + DbContract.Group.MANAGER + " INTEGER, "
                + DbContract.Group.MAXLEVEL + " INTEGER, "
                + DbContract.Group.NOMEMBERS + " INTEGER "
                + ")");

        db.execSQL("CREATE TABLE " + DbContract.Task.TABLE + "("
                + DbContract.Task._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + DbContract.Task.NAME + " TEXT, "
                + DbContract.Task.DESCRIPTION + " TEXT, "
                + DbContract.Task.STATE + " BOOLEAN, "
                + DbContract.Task.NUMBER + " INTEGER, "
                + DbContract.Task.GROUP + " TEXT "
                + ")");

        // Notice how the GitHub IDs have a UNIQUE clause which leads to aborting on conflict. This
        // is used to prevent accidentally inserting the same entity twice and throwing an exception
        // in case this were to happen.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // We are currently not supporting database upgrades, so just clean it up and re-create it
        db.execSQL("DROP TABLE " + DbContract.User.TABLE);
        onCreate(db);
    }

    public boolean insertUser(String name, String username, String password, String email, String groupName){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.User.NAME, name);
        values.put(DbContract.User.USERNAME, username);
        values.put(DbContract.User.PASSWORD, password);
        values.put(DbContract.User.EMAIL, email);
        values.put(DbContract.User.GROUP_NAME, groupName);
        values.put(DbContract.User.LEVEL, 0);

        long ret = db.insert(DbContract.User.TABLE, null, values);

        if (ret < 0) {
            Log.d(TAG, "Add group failed!");
            return false;
        }
        else
            return true;
    }

    public Cursor getUserData(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + DbContract.User.TABLE
                + " where "+DbContract.User.USERNAME + "=?", new String[]{username});
    }

    public boolean uploadPicture(String username, Bitmap img){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + DbContract.User.TABLE
                + " where " + DbContract.User.USERNAME + "=?", new String[]{username});

        if (res.moveToFirst()){
            ContentValues values = new ContentValues();

            values.put(DbContract.User.NAME, res.getString(res.getColumnIndex(DbContract.User.NAME)));
            values.put(DbContract.User.USERNAME, username);
            values.put(DbContract.User.PASSWORD,res.getString(res.getColumnIndex(DbContract.User.PASSWORD)));
            values.put(DbContract.User.EMAIL, res.getString(res.getColumnIndex(DbContract.User.EMAIL)));
            values.put(DbContract.User.GROUP_NAME, res.getString(res.getColumnIndex(DbContract.User.GROUP_NAME)));
            values.put(DbContract.User.LEVEL, res.getString(res.getColumnIndex(DbContract.User.LEVEL)));
            values.put(DbContract.User.PICTURE, getBitmapAsByteArray(img));

            long ret = db.update(DbContract.User.TABLE, values, DbContract.User.USERNAME + "=?", new String[]{username});

            if (ret < 0)
                return false;
            else
                return true;

        } else
            return false;
    }

    public Bitmap getPicture(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + DbContract.User.TABLE
                + " where " + DbContract.User.USERNAME + "=?", new String[]{username});

        if (res.moveToFirst()){
            byte[] imgByte = res.getBlob(res.getColumnIndex(DbContract.User.PICTURE));
            res.close();
            return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (res != null && !res.isClosed()) {
            res.close();
        }

        return null ;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + DbContract.User.TABLE, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(DbContract.User.USERNAME)));
            res.moveToNext();
        }
        return array_list;
    }

    public boolean addGroup(String name, String description, String manager, Integer maxlevel){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Group.NAME, name);
        values.put(DbContract.Group.DESCRIPTION, description);
        values.put(DbContract.Group.MANAGER, manager);
        values.put(DbContract.Group.MAXLEVEL, maxlevel);
        values.put(DbContract.Group.NOMEMBERS, 0);

        long ret = db.insert(DbContract.Group.TABLE, null, values);

        for (int i = 0 ; i < 3; i++) {
            values = new ContentValues();

            values.put(DbContract.Task.NAME, tasks[indexTask].getName());
            values.put(DbContract.Task.DESCRIPTION, tasks[indexTask].getDescription());
            values.put(DbContract.Task.STATE, tasks[indexTask].getState());
            values.put(DbContract.Task.NUMBER, tasks[indexTask].getNumber());
            values.put(DbContract.Task.GROUP, name);

            db.insert(DbContract.Task.TABLE, null, values);
            indexTask = (indexTask + 1) % tasks.length;
        }


        if (ret < 0)
            return false;
        else
            return true;
    }

    public boolean updateGroupMembers(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " +
                DbContract.Group.TABLE + " where "+ DbContract.Group.NAME+"=?", new String[]{name} );

        res.moveToFirst();

        int noMembers = res.getInt(res.getColumnIndex(DbContract.Group.NOMEMBERS));
        int maxLevel = res.getInt(res.getColumnIndex(DbContract.Group.MAXLEVEL));

        /* TODO check document to implement all possible cases*/
        if (maxLevel >= 5){
            if (noMembers >= 5)
                return false;
        }

        ContentValues values = new ContentValues();

        values.put(DbContract.Group.NAME, res.getString(res.getColumnIndex(DbContract.Group.NAME)));
        values.put(DbContract.Group.DESCRIPTION, res.getString(res.getColumnIndex(DbContract.Group.DESCRIPTION)));
        values.put(DbContract.Group.MANAGER, res.getString(res.getColumnIndex(DbContract.Group.MANAGER)));
        values.put(DbContract.Group.MAXLEVEL, maxLevel);
        values.put(DbContract.Group.NOMEMBERS, noMembers + 1);

        long ret = db.update(DbContract.Group.TABLE, values, DbContract.Group.NAME + "=?", new String[]{name});

        if (ret < 0)
            return false;
        else
            return true;

    }

    public Cursor getGroup(int level){
        SQLiteDatabase db = this.getReadableDatabase();

        int maxNumberOfMembers = getMaxNumberOfMembers(level);
        Cursor res = db.rawQuery("select * from " + DbContract.Group.TABLE + " where "
                        + DbContract.Group.NOMEMBERS+ "<" + maxNumberOfMembers + " and maxlevel>?",
                new String[]{Integer.toString(level)});

        if (!res.moveToFirst()) {
            Log.d(TAG, "[2]No group found!");
        }
        return res;

    }

    public ArrayList<User> getAllUsersInGroup(String username) {
        ArrayList<User> array_list = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + DbContract.User.TABLE + " where "
                + DbContract.User.USERNAME + "=?", new String[]{username} );
        res.moveToFirst();
        String groupname = res.getString(res.getColumnIndex(DbContract.User.GROUP_NAME));

        res = db.rawQuery("select * from " + DbContract.User.TABLE + " where "
                        + DbContract.User.GROUP_NAME + "=? and " + DbContract.User.USERNAME + "!=?",
                new String[]{groupname, username});
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String name = res.getString(res.getColumnIndex(DbContract.User.NAME));
            String userName = res.getString(res.getColumnIndex(DbContract.User.USERNAME));
            String email = res.getString(res.getColumnIndex(DbContract.User.EMAIL));
            String groupName = res.getString(res.getColumnIndex(DbContract.User.GROUP_NAME));
            Integer level = res.getInt(res.getColumnIndex(DbContract.User.LEVEL));

            User user = new User(name, userName, groupName, email);
            array_list.add(user);
            res.moveToNext();
        }
        return array_list;
    }

    private int getMaxNumberOfMembers(int level) {
        if (level < 5)
            return 5;
        if (level < 10)
            return 10;
        if (level < 15)
            return 20;
        return 50;
    }

    public boolean addTask(String name, String description, Boolean state, Integer no){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.Task.NAME, name);
        values.put(DbContract.Task.DESCRIPTION, description);
        values.put(DbContract.Task.STATE, state);
        values.put(DbContract.Task.NUMBER, no);

        long ret = db.insert(DbContract.Task.TABLE, null, values);

        if (ret < 0)
            return false;
        else
            return true;
    }

    public boolean updateTask(String taskName, String groupName){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + DbContract.Task.TABLE + " where " +
                DbContract.Task.NAME + "=?", new String[]{taskName});

        if (res.moveToFirst()){
            if (res.getInt(res.getColumnIndex(DbContract.Task.NUMBER)) <= 0)
                return false;
            else{
                ContentValues val = new ContentValues();

                val.put(DbContract.Task.NAME, res.getString(res.getColumnIndex(DbContract.Task.NAME)));
                val.put(DbContract.Task.DESCRIPTION, res.getString(res.getColumnIndex(DbContract.Task.DESCRIPTION)));
                val.put(DbContract.Task.GROUP, res.getString(res.getColumnIndex(DbContract.Task.GROUP)));

                int value = res.getInt(res.getColumnIndex(DbContract.Task.NUMBER));
                value--;
                val.put(DbContract.Task.NUMBER, value);

                if (value <=0)
                    val.put(DbContract.Task.STATE, true);
                else
                    val.put(DbContract.Task.STATE, false);

                long ret = db.update(DbContract.Task.TABLE, val, DbContract.Task.NAME + "=? and " + DbContract.Task.GROUP + "=?", new String[]{taskName, groupName});

                if (ret < 0)
                    return false;
                else
                    return true;
            }
        }
        else
            return false;
    }

    public String getGroupUser(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("SELECT * FROM " + DbContract.User.TABLE
                + " where " + DbContract.User.USERNAME + "=?", new String[]{username});

        if(res.moveToFirst())
            return res.getString(res.getColumnIndex(DbContract.User.GROUP_NAME));
        else
            return "";

    }

    public ArrayList<Task> getAllTasksInGroup(String groupName) {
        ArrayList<Task> array_list = new ArrayList<Task>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + DbContract.Task.TABLE + " where " +
                DbContract.Task.NAME + "=?", new String[]{groupName});

        res.moveToFirst();

        while(res.isAfterLast() == false){
            String name = res.getString(res.getColumnIndex(DbContract.Task.NAME));
            String descr = res.getString(res.getColumnIndex(DbContract.Task.DESCRIPTION));
            int stateInt = res.getInt(res.getColumnIndex(DbContract.Task.STATE));
            boolean state;
            if (stateInt == 0)
                state = false;
            else
                state = true;
            int number = res.getInt(res.getColumnIndex(DbContract.Task.NUMBER));

            Task task = new Task(name, descr, groupName, state, number);
            array_list.add(task);
            res.moveToNext();
        }
        return array_list;
    }

    public int noUsers(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + DbContract.User.TABLE, null);

        return res.getCount();
    }
}