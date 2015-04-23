package com.csc413.sfsu.csc413_parking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by devin on 4/22/15.
 */
public class LocationDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor
     * @param context An object specific to Android containing information about the application environment.
     */
    public LocationDatabaseHandler(Context context){
        super(context, LocationDataBaseContract.LocationEntry.TABLE_LOCATIONS, null, DATABASE_VERSION);
    }

    /**
     * Upon first initialization of the database, onCreate() is called.
     * All rows and row titles are instantiated within this method using the static names declared in LocationDataBaseContract class
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + LocationDataBaseContract.LocationEntry.TABLE_LOCATIONS + "("
                + LocationDataBaseContract.LocationEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," + LocationDataBaseContract.LocationEntry.KEY_LATITUDE + " DOUBLE,"
                + LocationDataBaseContract.LocationEntry.KEY_LONGITUDE + " DOUBLE" +LocationDataBaseContract.LocationEntry.KEY_IS_FAVORITE+"INTEGER"+LocationDataBaseContract.LocationEntry.KEY_TIMES_SEARCHED+"INTEGER"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    /**
     * Called if the database is updated.
     * @param db The database to upgrade
     * @param oldVersion The integer version number of the previous database version.
     * @param newVersion The integer version number of the new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + LocationDataBaseContract.LocationEntry.TABLE_LOCATIONS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Adds the a Latitude-Longitude pair to the database in the form of two doubles.
     * @param location
     */
    public void addLocation(LatLng location){

    }


}
