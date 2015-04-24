package com.csc413.sfsu.csc413_parking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 *  LocationDataBaseHandler objects create, instantiate, and interface with databases of locations.
 * @author Devin Clary
 */
public class LocationDatabaseHandler extends SQLiteOpenHelper {

    /** The version of the database. If changed, it will force the database to call an update method */
    private static final int DATABASE_VERSION = 1;
    /**The name of the locations table.*/
    private static final String tableName= LocationDatabaseContract.LocationEntry.TABLE_LOCATIONS;
    /**The name of the latitude column key, to be stored as an SQLite DOUBLE value.*/
    private static final String keyLat= LocationDatabaseContract.LocationEntry.KEY_LATITUDE;
    /**The name of the longitude column key, to be stored as an SQLite DOUBLE value.*/
    private static final String keyLong= LocationDatabaseContract.LocationEntry.KEY_LONGITUDE;
    /**The name of the latitude column key for the origin of the SFPark query resulting in each location. Stored as a DOUBLE value. */
    private static final String keyOriginLat=LocationDatabaseContract.LocationEntry.KEY_ORIGIN_LATITIUDE;
    /**The name of the longitude column key for the origin of the SFPark query resulting in each location. Stored as a DOUBLE value. */
    private static final String keyOriginLong=LocationDatabaseContract.LocationEntry.KEY_ORIGIN_LONGITUDE;
    /**The name of the radius column key corresponding to the radius from the origin of the search area from the SFPark database. Stored as a DOUBLE value.*/
    private static final String keyRadius=LocationDatabaseContract.LocationEntry.KEY_RADIUS;
    /**The name of the hasStreetParking column key, to be stored as an SQLite INTEGER value, 1 or 0 for true or false.*/
    private static final String keyHasStreetParking=LocationDatabaseContract.LocationEntry.KEY_HAS_STREET_PARKING;
    /**The name of the name column key, corresponding to the name of each location. Stored as a STRING value. */
    private static final String keyName=LocationDatabaseContract.LocationEntry.KEY_NAME;
    /**The name of the desc column key, corresponding to the description of each location. Stored as a STRING value.*/
    private static final String keyDesc=LocationDatabaseContract.LocationEntry.KEY_DESC;
    /**The name of the ospid column key, corresponding to the off street parking ID of each location. Stored as an INTEGER value.*/
    private static final String keyOSPID=LocationDatabaseContract.LocationEntry.KEY_OSPID;
    /**The name of the bfid column key, corresponding to the on street parking ID of each location. Stored as an INTEGER value.*/
    private static final String keyBFID=LocationDatabaseContract.LocationEntry.KEY_BFID;
    /**The name of the isFavorite column key, to be stored as an SQLite INTEGER value, 1 or 0 for true or false.*/
    private static final String keyIsFavorite= LocationDatabaseContract.LocationEntry.KEY_IS_FAVORITE;
    /**The name of the timesSearched column key, to be stored as an SQLite INTEGER value.*/
    private static final String keyTimesSearched= LocationDatabaseContract.LocationEntry.KEY_TIMES_SEARCHED;
    /**The name of the ID key for each entry, to be stored automatically as an INTEGER PRIMARY KEY value*/
    private static final String locationID= LocationDatabaseContract.LocationEntry.LOCATION_NAME_ENTRY_ID;


    /**
     * Constructor
     * @param context An object specific to Android containing information about the application environment.
     */
    public LocationDatabaseHandler(Context context){
        super(context, LocationDatabaseContract.LocationEntry.TABLE_LOCATIONS, null, DATABASE_VERSION);
    }

    /**
     * Upon first initialization of the database, onCreate() is called.
     * All rows and row titles are instantiated within this method using the static names declared in LocationDatabaseContract class
     * @param db The SQLiteDatabase object is automatically passed after it is instantiated.
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + this.tableName + "("+this.locationID+" INTEGER PRIMARY KEY, " + this.keyLat + " DOUBLE,"
            + this.keyLong + " DOUBLE, " + this.keyOriginLat + " DOUBLE, "+ this.keyOriginLong + " DOUBLE, "+this.keyRadius+" DOUBLE, "+this.keyHasStreetParking+" INTEGER, "
                +this.keyName+" STRING, "+this.keyDesc+" STRING, "+this.keyOSPID+"INTEGER, "+this.keyBFID+" INTEGER, "+keyIsFavorite+" INTEGER, "+this.keyTimesSearched+" INTEGER"+")";
        db.execSQL(CREATE_LOCATIONS_TABLE);

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
        db.execSQL("DROP TABLE IF EXISTS " + LocationDatabaseContract.LocationEntry.TABLE_LOCATIONS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Adds the a Latitude-Longitude pair to the corresponding columns of the database in the form of two doubles.
     * @param location a LatLng object to be parsed into two doubles for latitude and longitude.
     */
    public void addLocation(LatLng location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(this.keyLat, location.latitude);
        values.put(this.keyLong, location.longitude);
        db.insert(this.tableName, null, values);
        db.close();
    }

    /**
     * An accessor method for retrieving all latitude/longitude pairs from the location database
     * @return An array list of LatLng objects.
     */
    public List<LatLng> getAllLocations() {
        List<LatLng> locationList = new ArrayList<LatLng>();
        String selectQuery = "SELECT  * FROM " + this.tableName;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LatLng location=new LatLng(Double.parseDouble(cursor.getString(1)),Double.parseDouble(cursor.getString(2)));
                locationList.add(location);
            } while(cursor.moveToNext());
        }

        return locationList;
    }

    /**
     * Retrieves the number of entries in the SQLite LocationDatabase
     * @return An integer count representing the number of rows (locations) in the database
     */

    public int getLocationsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + this.tableName;
        Cursor cursor = db.rawQuery(countQuery, null);

        int count= cursor.getCount();
        cursor.close();
        return count;
    }


}
