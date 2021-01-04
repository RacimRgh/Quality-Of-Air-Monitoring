package com.example.QualityOfAirMonitoring.accounts_creation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Users";
    // User table name
    private static final String TABLE_USER = "user";
    // User Table Columns names
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    // Weather table name
    private static final String TABLE_WEATHER = "weather";
    // User Table Columns names
    //private static final String COLUMN_WEATHER_ID_USER = "user_id";
    private static final String COLUMN_WEATHER_DATE = "crtdate";
    private static final String COLUMN_WEATHER_LAT = "latitude";
    private static final String COLUMN_WEATHER_LON = "longitude";
    private static final String COLUMN_WEATHER_HMD = "humidity" ;
    private static final String COLUMN_WEATHER_TMP = "temperature";

    // Create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";

    // Drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // Create table sql query
    private String CREATE_WEATHER_TABLE = "CREATE TABLE " + TABLE_WEATHER + "("
            + COLUMN_WEATHER_DATE + " TEXT," + COLUMN_WEATHER_LAT + " TEXT," +
            COLUMN_WEATHER_LON + " TEXT," + COLUMN_WEATHER_HMD + " TEXT,"
            + COLUMN_WEATHER_TMP + " TEXT" +")";

    private String DROP_WEATHER_TABLE = "DROP TABLE IF EXISTS " + TABLE_WEATHER;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop Users table if exists
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_WEATHER_TABLE);
        // Create tables again
        onCreate(db);
    }

    // Method to add a user to the DB
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    // Method to update user records
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void addWeather(Weather w) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(COLUMN_WEATHER_ID_USER, w.getId());
        values.put(COLUMN_WEATHER_DATE, (w.getDate()));
        values.put(COLUMN_WEATHER_LAT, Double.toString(w.getLat()));
        values.put(COLUMN_WEATHER_LON, Double.toString(w.getLon()));
        values.put(COLUMN_WEATHER_HMD, Float.toString(w.getHmd()));
        values.put(COLUMN_WEATHER_TMP, Float.toString(w.getTmp()));
        // Inserting Row
        db.insert(TABLE_WEATHER, null, values);
        db.close();
    }

    // Get all rows
    public List<Weather> getAllRows() {
        List<Weather> l = new ArrayList<>();

        // Select all query
        String selectQuery = "SELECT * FROM " + TABLE_WEATHER + " ORDER BY " + "crtdate DESC LIMIT 5";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all the rows and addi the to the list
        if (cursor.moveToFirst()) {
            do {
                Weather w = new Weather(
                (cursor.getDouble(1)),
                (cursor.getDouble(2)),
                (cursor.getString(0)),
                (cursor.getFloat(3)),
                (cursor.getFloat(4))
                );
             /*   String i;
                i= (cursor.getString(0));
                    Date d = null;
                    try {
                        d = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss").parse(String.valueOf(i));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
*/
                // Add row to list
                l.add(w);

            } while (cursor.moveToNext());


        }

        cursor.close();
        db.close();

        // Return the list
        return l;
    }



    // Method to delete a user
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }


    /***********************************************
     * Method to check whether user exists or not  *
     * We check by email only                      *
     **********************************************/
    public boolean checkUser(String email) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};
        // query user table with condition
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }
    /***********************************************
     * Method to check whether user exists or not  *
     * We check by email and password              *
     **********************************************/
    public boolean checkUser(String email, String password) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        // query user table with conditions
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }
}
