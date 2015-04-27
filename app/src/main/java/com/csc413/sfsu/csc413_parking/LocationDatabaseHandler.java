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
 *  LocationDataBaseHandler objects create, instantiate, and interface with databases of locations:
 *  -Each row represents the data from a ParkingLocation object.
 *  -The data contained in each row is directly paralleled to the data returned by the
 *  SFParkSimplified API.
 *  -Not all fields for each location will contain data.
 *  -LocationDataBaseHandler objects will automatically remove the least searched locations when
 *  the size limit is reached.
 *
 * @Important Currently the SFParkLocationFactory should be the only point of entry into the
 * LocationDatabaseHandler class. This is due to the fact that if the timesSearched field of
 * ParkingLocation objects are set manually and added to the database manually, the database will
 * not properly remove the least searched locations.
 *  
 */
public class LocationDatabaseHandler extends SQLiteOpenHelper {
    /**The minimum number of searches performed on a location within the database*/
    private int minTimesSearched;
    /**The last ParkingLocation that was found with the least number of searches.
     * Used for quick deletion when DB becomes full. Note that there may be many values in the DB
     * with the minTimesSearched value.*/
    private ParkingLocation leastSearchedLocation;
    /** The version of the database. If changed, the update method is called.*/
    private static final int DATABASE_VERSION = 1;
    /** The maximum number of rows to be maintained by the database*/
    private static final int maxRows=LocationDatabaseContract.MAX_ROWS;
    /**The name of the locations table.*/
    private static final String tableName= LocationDatabaseContract.LocationEntry.TABLE_LOCATIONS;
    /**The name of the latitude column key, to be stored as an SQLite DOUBLE value.*/
    private static final String keyLat= LocationDatabaseContract.LocationEntry.KEY_LATITUDE;
    /**The name of the longitude column key, to be stored as an SQLite DOUBLE value.*/
    private static final String keyLong= LocationDatabaseContract.LocationEntry.KEY_LONGITUDE;
    /**The name of the latitude column key for the origin of the SFPark query resulting in each
     * location. Stored as a DOUBLE value. */
    private static final String keyOriginLat=LocationDatabaseContract.LocationEntry.KEY_ORIGIN_LATITIUDE;
    /**The name of the longitude column key for the origin of the SFPark query resulting in each
     * location. Stored as a DOUBLE value. */
    private static final String keyOriginLong=LocationDatabaseContract.LocationEntry.KEY_ORIGIN_LONGITUDE;
    /**The name of the radius column key corresponding to the radius from the origin of the search
     * area from the SFPark database. Stored as a DOUBLE value.*/
    private static final String keyRadius=LocationDatabaseContract.LocationEntry.KEY_RADIUS;
    /**The name of the hasStreetParking column key, to be stored as an SQLite INTEGER value, 1 or 0
     * for true or false.*/
    private static final String keyHasStreetParking=LocationDatabaseContract.LocationEntry.KEY_HAS_STREET_PARKING;
    /**The name of the name column key, corresponding to the name of each location. Stored as
     * a STRING value. */
    private static final String keyName=LocationDatabaseContract.LocationEntry.KEY_NAME;
    /**The name of the desc column key, corresponding to the description of each location.
     * Stored as a STRING value.*/
    private static final String keyDesc=LocationDatabaseContract.LocationEntry.KEY_DESC;
    /**The name of the ospid column key, corresponding to the off street parking ID of each
     * location. Stored as an INTEGER value.*/
    private static final String keyOSPID=LocationDatabaseContract.LocationEntry.KEY_OSPID;
    /**The name of the bfid column key, corresponding to the on street parking ID of each location.
     * Stored as an INTEGER value.*/
    private static final String keyBFID=LocationDatabaseContract.LocationEntry.KEY_BFID;
    /**The name of the isFavorite column key, to be stored as an SQLite INTEGER value, 1 or 0 for
     * true or false.*/
    private static final String keyIsFavorite= LocationDatabaseContract.LocationEntry.KEY_IS_FAVORITE;
    /**The name of the timesSearched column key, to be stored as an SQLite INTEGER value.*/
    private static final String keyTimesSearched= LocationDatabaseContract.LocationEntry.KEY_TIMES_SEARCHED;
    /**The name of the ID key for each entry, to be stored automatically as an INTEGER PRIMARY KEY*/
    private static final String locationID= LocationDatabaseContract.LocationEntry.LOCATION_NAME_ENTRY_ID;


    /**
     * Constructor
     * @param context An object specific to Android containing information about the application
     *                environment.
     */
    public LocationDatabaseHandler(Context context){
        super(context, this.tableName, null, DATABASE_VERSION);
    }

    /**
     * Upon first initialization of the database, onCreate() is called.
     * All rows and row titles are instantiated within this method using the static names declared
     * in LocationDatabaseContract class
     * @param db The SQLiteDatabase object is automatically passed after it is instantiated.
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + this.tableName + "("+this.locationID
                +" INTEGER PRIMARY KEY, "+ this.keyLat + " DOUBLE,"+ this.keyLong + " DOUBLE, "
                +this.keyOriginLat + " DOUBLE, "+ this.keyOriginLong+ " DOUBLE, "+this.keyRadius
                +" DOUBLE, "+this.keyHasStreetParking+" INTEGER, "+this.keyName+" STRING, "
                +this.keyDesc+" STRING, "+this.keyOSPID+" INTEGER, "+this.keyBFID+" INTEGER, "
                +keyIsFavorite+" INTEGER, "+this.keyTimesSearched+" INTEGER"+")";
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
        db.execSQL("DROP TABLE IF EXISTS "+this.tableName);

        // Create tables again
        onCreate(db);
    }

    /**
     * Adds a ParkingLocation object's data fields into a row of the LocationDatabase.
     * Note: if location already exists in database, the timesSearched field will be incremented.
     * @param loc a ParkingLocation object to be parsed into discrete data to store in database.
     */
    public void addLocation(ParkingLocation loc){
        if(loc.hasOnStreetParking()){ //Check if OSPID exists in db.
            if(this.getLocationFromOSPID(loc.getOspid())!=null){ //Duplicate. Update timesSearched
                loc.setTimesSearched(loc.getTimesSearched()+1);
                this.updateLocation(loc);
                return;
            }
        }
        else if(this.getLocationFromBFID(loc.getBfid())!=null){ //Check if BFID exists in db.
            loc.setTimesSearched(loc.getTimesSearched()+1);//Duplicate. Update timesSearched
            this.updateLocation(loc);
            return;
        }
        else { //New entry, add to DB.
            if(this.getLocationsCount()==this.maxRows){ //Delete the least searched location.
                this.deleteLocation(this.leastSearchedLocation);
                this.leastSearchedLocation=loc;
            }
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(this.keyLat, loc.getCoords().latitude);
            values.put(this.keyLong, loc.getCoords().longitude);
            values.put(this.keyOriginLat, loc.getOriginLocation().latitude);
            values.put(this.keyOriginLong, loc.getOriginLocation().longitude);
            values.put(this.keyRadius, loc.getRadiusFromOrigin());

            //convert boolean to 1 or 0 to store in Database
            values.put(this.keyHasStreetParking, ((loc.hasOnStreetParking()) ? 1 : 0));
            values.put(this.keyName, loc.getName());
            values.put(this.keyDesc, loc.getDesc());
            values.put(this.keyOSPID, loc.getOspid());
            values.put(this.keyBFID, loc.getBfid());
            values.put(this.keyIsFavorite, ((loc.isFavorite()) ? 1 : 0));
            values.put(this.keyTimesSearched, loc.getTimesSearched());

            db.insert(this.tableName, null, values);
            db.close();
            if (loc.getTimesSearched()<this.minTimesSearched){ //Maintain min number of searches.
                minTimesSearched=loc.getTimesSearched();
            }
        }
    }

    /**
     * An accessor method for retrieving all ParkingLocation objects from the location database.
     * @return An array list of ParkingLocation objects.
     */
    public List<ParkingLocation> getAllLocations() {
        List<ParkingLocation> locationList = new ArrayList<ParkingLocation>();
        String selectQuery = "SELECT  * FROM " + this.tableName;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LatLng coords=new LatLng(cursor.getDouble(1),cursor.getDouble(2));
                LatLng origin=new LatLng(cursor.getDouble(3),cursor.getDouble(4));
                Double radius=cursor.getDouble(5);
                boolean hasStreetParking=((cursor.getInt(6)==1) ? true : false);
                String name=cursor.getString(7);
                String desc=cursor.getString(8);
                int ospid=cursor.getInt(9);
                int bfid=cursor.getInt(10);
                boolean isFavorite=(cursor.getInt(11)==1 ? true : false);
                int timesSearched=(cursor.getInt(12));

                ParkingLocation location=new ParkingLocation(origin, radius, hasStreetParking, name,
                        desc, ospid, bfid, coords, isFavorite, timesSearched);
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

    /**
     * Retrieves a ParkingLocation object with the specified OSPID field from the database.
     * @param ospid the ospid field to search for
     * @return A ParkingLocation object composed of the SQLite row with the specified OSPID.
     */
    public ParkingLocation getLocationFromOSPID(int ospid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(this.tableName, new String[]{this.locationID, this.keyLat, this.keyLong,
                this.keyOriginLat, this.keyOriginLong, this.keyRadius, this.keyHasStreetParking,
                this.keyName, this.keyDesc, this.keyOSPID, this.keyBFID, this.keyIsFavorite,
                this.keyTimesSearched}, this.keyOSPID+"=?",new String[]{String.valueOf(ospid)},
                null,null,null,null);

        if(cursor!=null){
            cursor.moveToFirst();
            LatLng coords=new LatLng(cursor.getDouble(1),cursor.getDouble(2));
            LatLng origin=new LatLng(cursor.getDouble(3),cursor.getDouble(4));
            Double radius=cursor.getDouble(5);
            boolean hasStreetParking=((cursor.getInt(6)==1) ? true : false);
            String name=cursor.getString(7);
            String desc=cursor.getString(8);
            int bfid=cursor.getInt(10);
            boolean isFavorite=(cursor.getInt(11)==1 ? true : false);
            int timesSearched=(cursor.getInt(12));

            ParkingLocation location=new ParkingLocation(origin, radius, hasStreetParking,
                    name, desc, ospid, bfid, coords, isFavorite, timesSearched);
            return location;
        }

        else {return null;}

    }

    /**
     * Retrieves a ParkingLocation object with the specified BFID from the database.
     * @param bfid The BFID to search for.
     * @return A ParkingLocation object composed of the SQLite row with the specified BFID
     */
    public ParkingLocation getLocationFromBFID(int bfid){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(this.tableName, new String[]{this.locationID, this.keyLat,
                        this.keyLong,this.keyOriginLat, this.keyOriginLong, this.keyRadius,
                        this.keyHasStreetParking,this.keyName, this.keyDesc, this.keyOSPID,
                        this.keyBFID, this.keyIsFavorite,this.keyTimesSearched},
                        this.keyBFID+"=?",new String[]{String.valueOf(bfid)},null,null,null,null);

        if(cursor!=null){
            cursor.moveToFirst();
            LatLng coords=new LatLng(cursor.getDouble(1),cursor.getDouble(2));
            LatLng origin=new LatLng(cursor.getDouble(3),cursor.getDouble(4));
            Double radius=cursor.getDouble(5);
            boolean hasStreetParking=((cursor.getInt(6)==1) ? true : false);
            String name=cursor.getString(7);
            String desc=cursor.getString(8);
            int ospid=cursor.getInt(9);
            boolean isFavorite=(cursor.getInt(11)==1 ? true : false);
            int timesSearched=(cursor.getInt(12));

            ParkingLocation location=new ParkingLocation(origin, radius, hasStreetParking, name,
                    desc, ospid, bfid, coords, isFavorite, timesSearched);
            return location;
        }

        else {return null;}
    }

    /**
     * Updates the SQLite database entry with data contained in the ParkingLocation parameter.
     * Note that if this method returns 0, the caller should call the addLocation() method.
     * @param location The location to update in the database.
     * @return The number of rows that were affected by the update. If 0, the caller should call
     * addLocation()
     */
    public int updateLocation(ParkingLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(this.keyLat, location.getCoords().latitude);
        values.put(this.keyLong, location.getCoords().longitude);
        values.put(this.keyOriginLat, location.getOriginLocation().latitude);
        values.put(this.keyOriginLong, location.getOriginLocation().longitude);
        values.put(this.keyRadius, location.getRadiusFromOrigin());
        values.put(this.keyHasStreetParking, (location.hasOnStreetParking())? 1 : 0);
        values.put(this.keyName, location.getName());
        values.put(this.keyDesc, location.getDesc());
        values.put(this.keyOSPID, location.getOspid());
        values.put(this.keyBFID, location.getBfid());
        values.put(this.keyIsFavorite, location.isFavorite()? 1 : 0);
        values.put(this.keyTimesSearched, location.getTimesSearched());

        return db.update(this.tableName, values, location.hasOnStreetParking()? this.keyBFID: this.keyOSPID,
                new String[] { String.valueOf(
                        location.hasOnStreetParking()? location.getBfid(): location.getOspid()) });
    }

    /**
     * Deletes the specified location from the SQLite database.
     * Note that the database uses the BFID or OSPID data fields to find the location to delete
     * @param location The location to delete.
     */
    public void deleteLocation(ParkingLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(this.tableName,
                location.hasOnStreetParking()? this.keyBFID : this.keyOSPID + " = ?",
                new String[] { String.valueOf(
                        location.hasOnStreetParking()? location.getBfid() : location.getOspid()) });
        db.close();
    }


}
