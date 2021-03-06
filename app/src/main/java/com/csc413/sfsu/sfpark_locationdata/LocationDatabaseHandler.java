package com.csc413.sfsu.sfpark_locationdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

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
 * Currently the SFParkLocationFactory should be the only point of entry into the
 * LocationDatabaseHandler class. This is due to the fact that if the timesSearched field of
 * ParkingLocation objects are set manually and added to the database manually, the database will
 * not properly remove the least searched locations.
 *
 */
public class LocationDatabaseHandler extends SQLiteOpenHelper {

    /** The number of searches on the minimally searched location*/
    private int minTimesSearched;
    /** The number of favorite fields in the database*/
    private int numFav;
    /** The number of parkedHere fields in the database */
    private int numParkedHere;
    /**The parkedHere location to delete if max number of ParkedHere is reached*/
    protected ParkingLocation parkedHereToDelete;
    /**The last ParkingLocation that was found with the least number of searches.
     * Used for quick deletion when DB becomes full. Note that there may be many values in the DB
     * with the minTimesSearched value.*/    /**
     * A debugging method, not for use in a production environment for security reasons.
     * Prints the values of all parkedHere locations in the database to the console.
     */
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
    /**The name of the parkedHere column key, to be stored as an SQLite INTEGER value*/
    private static final String keyParkedHere= LocationDatabaseContract.LocationEntry.KEY_PARKED_HERE;
    /**The name of the isUserDefined clumn key, to be stored as an SQLite INTEGER value 1 or 0*/
    private static final String keyIsUserDefined= LocationDatabaseContract.LocationEntry.KEY_IS_USER_DEFINED;
    /** The name of the theftProbability key for each entry, to be stored as an SQLite INTEGER value.*/
    private static final String keyTheftProb=LocationDatabaseContract.LocationEntry.KEY_THEFT_PROBABILITY;
    /**The name of the ID key for each entry, to be stored automatically as an INTEGER PRIMARY KEY*/
    private static final String locationID= LocationDatabaseContract.LocationEntry.LOCATION_NAME_ENTRY_ID;



    /**
     * Constructor
     * @param context An object specific to Android containing information about the application
     *                environment.
     */
    public LocationDatabaseHandler(Context context){
        super(context, LocationDatabaseContract.LocationEntry.TABLE_LOCATIONS, null, DATABASE_VERSION);
        this.updateMinTimesSearched();
        this.updateFavCount();
        this.updateparkedHereCount();
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
                +keyIsFavorite+" INTEGER, "+this.keyTimesSearched+" INTEGER, "+this.keyParkedHere
                +" INTEGER, "+this.keyIsUserDefined+" INTEGER, "+this.keyTheftProb+" DOUBLE"+")";
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
     * If the capacity of the database is reached, the minimally searched location will be deleted.
     * @param loc a ParkingLocation object to be parsed into discrete data to store in database.
     */
    public void addLocation(ParkingLocation loc){
        //Make sure minimum is set properly.
        this.updateMinTimesSearched();
        this.updateparkedHereCount();
        this.updateFavCount();

        //Check if BFID exists in db.
        if(loc.hasOnStreetParking()&&this.getLocationFromBFID(loc.getBfid())!=null
                &&loc.getBfid()!=-1){
            //Duplicate on street location. Update timesSearched, do not add location to database.
            this.incrementTimesSearched(loc);
            this.updateMinTimesSearched();
            return;

        }
        //Check if OSPID exists in db.
        else if(!loc.hasOnStreetParking()&&this.getLocationFromOSPID(loc.getOspid())!=null
                &&loc.getOspid()!=-1){
            //Duplicate off street location. Update timesSearched, do not add location to database.
            this.incrementTimesSearched(loc);
            this.updateMinTimesSearched();
            return;
        }
        else { //New entry, add to DB.
            if(this.getLocationsCount()>=this.maxRows){ //Delete the least searched location.
                this.deleteLocation(this.leastSearchedLocation);
                this.updateMinTimesSearched();
            }
            if(loc.isFavorite()||loc.getParkedHere()){ //handle maximums in separate method
                this.addFavOrParkedHere(loc);
                return;
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
            values.put(this.keyTimesSearched, 1); //New entries should always have 1 timesSearched
            values.put(this.keyParkedHere, ((loc.getParkedHere() ? 1: 0)));
            values.put(this.keyIsUserDefined, ((loc.isUserDefined()? 1: 0)));
            values.put(this.keyTheftProb, loc.getTheftProbability());

            db.insert(this.tableName, null, values);
            db.close();
            this.updateMinTimesSearched();
        }
    }

    public void addFavOrParkedHere(ParkingLocation loc){
        if(loc.isFavorite()&&this.numFav>=10){ //do not allow more than 10 favorites
            return;
        }
        else{
            if(this.numParkedHere>=20){ //delete a parked here location (mark parkedHere as false)
                this.parkedHereToDelete.setParkedHere(false);
                this.updateLocation(this.parkedHereToDelete);
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
            values.put(this.keyTimesSearched, 1); //New entries should always have 1 timesSearched
            values.put(this.keyParkedHere, ((loc.getParkedHere() ? 1: 0)));
            values.put(this.keyIsUserDefined, ((loc.isUserDefined()? 1: 0)));
            values.put(this.keyTheftProb, loc.getTheftProbability());

            db.insert(this.tableName, null, values);
            db.close();
            this.updateMinTimesSearched();
            this.updateparkedHereCount();
            this.updateFavCount();
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

        if (cursor.moveToFirst()&&cursor!=null) {
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
                boolean parkedHere=(cursor.getInt(13)==1 ? true : false);
                boolean isUserDefined=(cursor.getInt(14)==1 ? true : false);
                double theftProb=cursor.getDouble(15);

                ParkingLocation location=new ParkingLocation(origin, radius, hasStreetParking, name,
                        desc, ospid, bfid, coords, isFavorite, timesSearched, parkedHere,
                        isUserDefined, theftProb);
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
     * Increments the timesSearched field of the given location in the database.
     * Note that the timesSearched field of the location parameter may not be up to date with the
     * database. Therefore, the timesSearched field in the database will be used.
     * @param loc The ParkingLocation to increment the timesSearched field on.
     */
    public void incrementTimesSearched(ParkingLocation loc){
        if(loc.hasOnStreetParking()&&this.getLocationFromBFID(loc.getBfid())!=null
                &&loc.getBfid()!=-1){ //Check if BFID location exists in database.
            loc=this.getLocationFromBFID(loc.getBfid()); //Use value of locationCount stored in DB.
            loc.setTimesSearched(loc.getTimesSearched()+1);
            this.updateLocation(loc);

        }
        else if(!loc.hasOnStreetParking()&&this.getLocationFromOSPID(loc.getOspid())!=null
                &&loc.getOspid()!=-1){ //Check if OSPID location exists in database.
            loc=this.getLocationFromOSPID(loc.getOspid());//Use value of locationCount stored in DB.
            loc.setTimesSearched(loc.getTimesSearched()+1);
            this.updateLocation(loc);
        }
    }

    /**
     * Retrieves a ParkingLocation object with the specified OSPID field from the database.
     * @param ospid the ospid field to search for.
     * @return A ParkingLocation object composed of the SQLite row with the specified OSPID.
     */
    public ParkingLocation getLocationFromOSPID(int ospid){
        SQLiteDatabase db=this.getReadableDatabase();

        String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyOSPID+" = '"+ospid+"'";
        Cursor cursor=db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            LatLng coords=new LatLng(cursor.getDouble(1),cursor.getDouble(2));
            LatLng origin=new LatLng(cursor.getDouble(3),cursor.getDouble(4));
            Double radius=cursor.getDouble(5);
            boolean hasStreetParking=((cursor.getInt(6)==1) ? true : false);
            String name=cursor.getString(7);
            String desc=cursor.getString(8);
            int bfid=cursor.getInt(10);
            boolean isFavorite=(cursor.getInt(11)==1 ? true : false);
            int timesSearched=(cursor.getInt(12));
            boolean parkedHere=(cursor.getInt(13)==1 ? true : false);
            boolean isUserDefined=(cursor.getInt(14)==1 ? true : false);
            double theftProb=cursor.getDouble(15);

            ParkingLocation location=new ParkingLocation(origin, radius, hasStreetParking,
                    name, desc, ospid, bfid, coords, isFavorite, timesSearched, parkedHere,
                    isUserDefined, theftProb);
            cursor.close();

            return location;
        }


        else {
            cursor.close();
            return null;
        }

    }

    /**
     * Retrieves a ParkingLocation object with the specified BFID from the database.
     * @param bfid The BFID to search for.
     * @return A ParkingLocation object composed of the SQLite row with the specified BFID
     */
    public ParkingLocation getLocationFromBFID(int bfid){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyBFID+" = "+bfid;
        Cursor cursor=db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            LatLng coords=new LatLng(cursor.getDouble(1),cursor.getDouble(2));
            LatLng origin=new LatLng(cursor.getDouble(3),cursor.getDouble(4));
            Double radius=cursor.getDouble(5);
            boolean hasStreetParking=((cursor.getInt(6)==1) ? true : false);
            String name=cursor.getString(7);
            String desc=cursor.getString(8);
            int ospid=cursor.getInt(9);
            boolean isFavorite=(cursor.getInt(11)==1 ? true : false);
            int timesSearched=(cursor.getInt(12));
            boolean parkedHere=(cursor.getInt(13)==1 ? true : false);
            boolean isUserDefined=(cursor.getInt(14)==1 ? true : false);
            double theftProb=cursor.getDouble(15);

            ParkingLocation location=new ParkingLocation(origin, radius, hasStreetParking, name,
                    desc, ospid, bfid, coords, isFavorite, timesSearched, parkedHere,
                    isUserDefined, theftProb);
            cursor.close();

            return location;
        }


        cursor.close();
        return null;

    }

    /**
     * Retrieves a list of all locations with the isFavorite value set to 1 (true).
     * @return An array list of ParkingLocations that are favorites in the database.
     */
    public List<ParkingLocation> getFavorites(){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyIsFavorite+" = 1";
        Cursor cursor=db.rawQuery(query, null);
        List<ParkingLocation> favorites=new ArrayList<ParkingLocation>();

        if(cursor.moveToFirst()){
            do {
                LatLng coords = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
                LatLng origin = new LatLng(cursor.getDouble(3), cursor.getDouble(4));
                Double radius = cursor.getDouble(5);
                boolean hasStreetParking = ((cursor.getInt(6) == 1) ? true : false);
                String name = cursor.getString(7);
                String desc = cursor.getString(8);
                int ospid = cursor.getInt(9);
                int bfid=cursor.getInt(10);
                boolean isFavorite = (cursor.getInt(11) == 1 ? true : false);
                int timesSearched = (cursor.getInt(12));
                boolean parkedHere = (cursor.getInt(13) == 1 ? true : false);
                boolean isUserDefined=(cursor.getInt(14)==1 ? true : false);
                double theftProb=cursor.getDouble(15);

                ParkingLocation location = new ParkingLocation(origin, radius, hasStreetParking, name,
                        desc, ospid, bfid, coords, isFavorite, timesSearched, parkedHere,
                        isUserDefined, theftProb);
                favorites.add(location);

            }while(cursor.moveToNext());
            cursor.close();
            return favorites;
        }


        cursor.close();
        return favorites;

    }

    /**
     * Retrieves a list of all locations with the parkedHere value set to 1 (true).
     * @return An array list of ParkingLocations that have been parked at by the user.
     */
    public List<ParkingLocation> getParkedLocations(){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyParkedHere+" = 1";
        Cursor cursor=db.rawQuery(query, null);
        List<ParkingLocation> parkedList=new ArrayList<ParkingLocation>();

        if(cursor.moveToFirst()){
            do {
                LatLng coords = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
                LatLng origin = new LatLng(cursor.getDouble(3), cursor.getDouble(4));
                Double radius = cursor.getDouble(5);
                boolean hasStreetParking = ((cursor.getInt(6) == 1) ? true : false);
                String name = cursor.getString(7);
                String desc = cursor.getString(8);
                int ospid = cursor.getInt(9);
                int bfid=cursor.getInt(10);
                boolean isFavorite = (cursor.getInt(11) == 1 ? true : false);
                int timesSearched = (cursor.getInt(12));
                boolean parkedHere = (cursor.getInt(13) == 1 ? true : false);
                boolean isUserDefined=(cursor.getInt(14)==1 ? true : false);
                double theftProb=cursor.getDouble(15);

                ParkingLocation location = new ParkingLocation(origin, radius, hasStreetParking, name,
                        desc, ospid, bfid, coords, isFavorite, timesSearched, parkedHere,
                        isUserDefined, theftProb);
                parkedList.add(location);

            }while(cursor.moveToNext());
            cursor.close();
            return parkedList;
        }


        cursor.close();
        return parkedList;

    }

    /**
     * Retrieves a list of user defined parking locations (with minimal data)
     * @return An array list of ParkingLocations that have been defined by the user.
     */
    public List<ParkingLocation> getUserDefinedLocations(){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyIsUserDefined+" = 1";
        Cursor cursor=db.rawQuery(query, null);
        List<ParkingLocation> udlList=new ArrayList<ParkingLocation>();

        if(cursor.moveToFirst()){
            do {
                LatLng coords = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
                LatLng origin = new LatLng(cursor.getDouble(3), cursor.getDouble(4));
                Double radius = cursor.getDouble(5);
                boolean hasStreetParking = ((cursor.getInt(6) == 1) ? true : false);
                String name = cursor.getString(7);
                String desc = cursor.getString(8);
                int ospid = cursor.getInt(9);
                int bfid=cursor.getInt(10);
                boolean isFavorite = (cursor.getInt(11) == 1 ? true : false);
                int timesSearched = (cursor.getInt(12));
                boolean parkedHere = (cursor.getInt(13) == 1 ? true : false);
                boolean isUserDefined=(cursor.getInt(14)==1 ? true : false);
                double theftProb=cursor.getDouble(15);

                ParkingLocation location = new ParkingLocation(origin, radius, hasStreetParking, name,
                        desc, ospid, bfid, coords, isFavorite, timesSearched, parkedHere,
                        isUserDefined, theftProb);
                udlList.add(location);

            }while(cursor.moveToNext());
            cursor.close();
            return udlList;
        }


        cursor.close();
        return udlList;

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
        values.put(this.keyBFID, location.getBfid());
        values.put(this.keyOSPID, location.getOspid());
        values.put(this.keyIsFavorite, location.isFavorite()? 1 : 0);
        values.put(this.keyTimesSearched, location.getTimesSearched());
        values.put(this.keyParkedHere, location.getParkedHere()? 1: 0);
        values.put(this.keyIsUserDefined, ((location.isUserDefined()? 1: 0)));
        values.put(this.keyTheftProb, location.getTheftProbability());


        int rowsAffected=db.update(this.tableName, values, (location.hasOnStreetParking()?
                this.keyBFID: this.keyOSPID)+"=?",new String[] {
                (String.valueOf(location.hasOnStreetParking()?
                        location.getBfid(): location.getOspid())) });
        this.updateMinTimesSearched();
        this.updateFavCount();
        this.updateparkedHereCount();
        return rowsAffected;
    }

    public void updateFavCount(){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyIsFavorite+" = 1";
        Cursor cursor=db.rawQuery(query, null);
        this.numFav=cursor.getCount();
        cursor.close();
    }

    public void updateparkedHereCount(){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyParkedHere+" = 1";
        Cursor cursor=db.rawQuery(query, null);
        this.numParkedHere=cursor.getCount();
        if(cursor.moveToFirst()){
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
            boolean parkedHere=(cursor.getInt(13)==1 ? true : false);
            boolean isUserDefined=(cursor.getInt(14)==1 ? true : false);
            double theftProb=cursor.getDouble(15);

            this.parkedHereToDelete=new ParkingLocation(origin, radius, hasStreetParking, name,
                    desc, ospid, bfid, coords, isFavorite, timesSearched, parkedHere,
                    isUserDefined, theftProb);
        }
        cursor.close();
    }

    /**
     * Deletes the specified location from the SQLite database, and all locations with the same
     * origin.
     * Note that the database uses the BFID or OSPID data fields to find the location to delete
     * @param location The location to delete.
     */
    public void deleteLocation(ParkingLocation location) {
        List<Integer> offStreetToDelete=new ArrayList<Integer>();
        List<Integer> onStreetToDelete=new ArrayList<Integer>();

        if(location!=null) {
            SQLiteDatabase dbR=this.getReadableDatabase();
            String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyOriginLat+" > "
                    +(location.getOriginLocation().latitude-.0000000000001)+" AND "+this.keyOriginLong+" > "
                    +(location.getOriginLocation().longitude-.0000000000001)+" AND "+this.keyOriginLat+" < "
                    +(location.getOriginLocation().latitude+.0000000000001)+" AND "+this.keyOriginLong+" < "
                    +(location.getOriginLocation().longitude+.0000000000001);
            Cursor cursor=dbR.rawQuery(query, null);
            if(cursor.moveToFirst()){
                do{
                    //only delete if the locations are not favorites or parked here.
                    if((cursor.getInt(11) == 0) && (cursor.getInt(13) == 0)) {
                        if (cursor.getInt(6) == 1) {
                            onStreetToDelete.add(cursor.getInt(10));
                        } else {
                            offStreetToDelete.add(cursor.getInt(9));
                        }
                    }
                }while(cursor.moveToNext());
            }
            dbR.close();


            SQLiteDatabase dbW = this.getWritableDatabase();
            //delete location passed
            String firstDeleteColumnName=location.hasOnStreetParking() ? this.keyBFID+" = ?" : this.keyOSPID
                    + " = ?";
            String id= location.hasOnStreetParking() ? Integer.toString(location.getBfid())
                    : Integer.toString(location.getOspid()) ;
            dbW.delete(this.tableName, firstDeleteColumnName,new String[]{String.valueOf(id)});

            //delete all locations with the same origin.
            for(int i=0; i<offStreetToDelete.size(); i++){
                dbW.delete(this.tableName, this.keyOSPID+" =? ",
                        new String[]{String.valueOf(offStreetToDelete.get(i))});
            }

            for(int i=0; i<onStreetToDelete.size(); i++){
                dbW.delete(this.tableName, this.keyBFID+" =? ",
                        new String[]{String.valueOf(onStreetToDelete.get(i))});
            }



            dbW.close();
        }
    }

    /**
     * Finds the minimally searched entry in the database.
     * Updates the minTimesSearched field with the value of the least number of searches.
     * Updates the leastSearchedLocation to reference the least searched location.
     * This method will NOT consider locations that have favorite or parkedHere set to true.
     */
    public void updateMinTimesSearched(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from locations where timesSearched = " +
                "(select MIN(timesSearched) from locations)", null);

        this.minTimesSearched=0;
        this.leastSearchedLocation=null;
        if(cursor.moveToFirst()&&cursor!=null) {//Store the least searched location.
            do { //find location that is not a favorite or parkedHere in the minimally searched list
                if ((cursor.getInt(11) == 0) && (cursor.getInt(13) == 0)) {
                    this.leastSearchedLocation = ((cursor.getInt(6) == 1) ?
                            this.getLocationFromBFID(cursor.getInt(10))
                            : this.getLocationFromOSPID(cursor.getInt(9)));

                    this.minTimesSearched = cursor.getInt(12);
                }
            }while(cursor.moveToNext());
        }
    }

    /**
     * An optimised method for returning UserDefined locations within a radius of an origin.
     * Instead of constructing parking location objects from every SQLite field, only the requested
     * fields are constructed for return to the caller of the method.
     * @param origin The origin of search.
     * @param radius
     * @return
     */

    public List<ParkingLocation> getUserDefinedWithinRadius(LatLng origin, double radius){

        List<ParkingLocation> udlList=new ArrayList<ParkingLocation>();

        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+this.tableName+" WHERE "+this.keyIsUserDefined+" = 1";
        Cursor cursor=db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                if(this.isWithinRadius(new LatLng(cursor.getDouble(1), cursor.getDouble(2)), origin,
                        radius)) {

                    LatLng coords = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
                    LatLng nOrigin = new LatLng(cursor.getDouble(3), cursor.getDouble(4));
                    Double nRadius = cursor.getDouble(5);
                    boolean hasStreetParking = ((cursor.getInt(6) == 1) ? true : false);
                    String name = cursor.getString(7);
                    String desc = cursor.getString(8);
                    int ospid = cursor.getInt(9);
                    int bfid = cursor.getInt(10);
                    boolean isFavorite = (cursor.getInt(11) == 1 ? true : false);
                    int timesSearched = (cursor.getInt(12));
                    boolean parkedHere = (cursor.getInt(13) == 1 ? true : false);
                    boolean isUserDefined = (cursor.getInt(14) == 1 ? true : false);
                    double theftProb=cursor.getDouble(15);

                    ParkingLocation location = new ParkingLocation(nOrigin, nRadius,
                            hasStreetParking, name, desc, ospid, bfid, coords, isFavorite,
                            timesSearched, parkedHere, isUserDefined, theftProb);
                    udlList.add(location);
                }

            }while(cursor.moveToNext());
            cursor.close();

        }

        return udlList;
    }

    public List<ParkingLocation> getLocsFromOrigin(List<LatLng> latlngs){
        SQLiteDatabase db=this.getReadableDatabase();
        List<ParkingLocation> locs=new ArrayList<ParkingLocation>();
        for(int i=0; i<latlngs.size(); i++) {
            String query = "SELECT * FROM " + this.tableName + " WHERE " + this.keyOriginLat + " > "
                    + (latlngs.get(i).latitude - .0000000000001) + " AND " + this.keyOriginLong + " > "
                    + (latlngs.get(i).longitude - .0000000000001) + " AND " + this.keyOriginLat + " < "
                    + (latlngs.get(i).latitude + .0000000000001) + " AND " + this.keyOriginLong + " < "
                    + (latlngs.get(i).longitude + .0000000000001);
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    LatLng coords = new LatLng(cursor.getDouble(1), cursor.getDouble(2));
                    LatLng nOrigin = new LatLng(cursor.getDouble(3), cursor.getDouble(4));
                    Double radius = cursor.getDouble(5);
                    boolean hasStreetParking = ((cursor.getInt(6) == 1) ? true : false);
                    String name = cursor.getString(7);
                    String desc = cursor.getString(8);
                    int ospid = cursor.getInt(9);
                    int bfid = cursor.getInt(10);
                    boolean isFavorite = (cursor.getInt(11) == 1 ? true : false);
                    int timesSearched = ((cursor.getInt(12)) + 1);
                    boolean parkedHere = (cursor.getInt(13) == 1 ? true : false);
                    boolean isUserDefined = (cursor.getInt(14) == 1 ? true : false);
                    double theftProb=cursor.getDouble(15);

                    ParkingLocation location = new ParkingLocation(nOrigin, radius,
                            hasStreetParking, name, desc, ospid, bfid, coords, isFavorite,
                            timesSearched, parkedHere, isUserDefined, theftProb);
                    locs.add(location);
                    this.incrementTimesSearched(location);
                } while (cursor.moveToNext());
            }
        }
        return locs;
    }

    public List<LatLng> isWithinToleranceOfOrigins(LatLng test){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT DISTINCT "+this.keyOriginLat+", "+this.keyOriginLong
                +" FROM "+this.tableName;
        Cursor cursor=db.rawQuery(query, null);
        List<LatLng>latlngs=new ArrayList<LatLng>();

        if(cursor.moveToFirst()) {
            do {
                if (this.isWithinRadius(new LatLng(cursor.getDouble(0), cursor.getDouble(1)), test,
                        .15)) {
                    latlngs.add(new LatLng(cursor.getDouble(0), cursor.getDouble(1)));
                }

            } while (cursor.moveToNext());
        }

        return latlngs;

    }


    /**
     * Tests if the destination location is within the given radius of the source location.
     * @param dest The destination location to test.
     * @param source The source location to test.
     * @param radius The radius to check if the destination is within.
     * @return True if the destination is within radius of source. False otherwise.
     */
    private boolean isWithinRadius(LatLng dest, LatLng source, double radius){
        Location src = new Location("");
        src.setLatitude(source.latitude);
        src.setLongitude(source.longitude);

        Location dst=new Location("");
        dst.setLatitude(dest.latitude);
        dst.setLongitude(dest.longitude);

        //Convert miles to meters and compare
        return ((src.distanceTo(dst)<=radius*1609.34)? true : false);
    }

    protected int getNumParkedHere(){
        this.updateparkedHereCount();
        return this.numParkedHere;
    }

    protected int getNumFav(){
        this.updateFavCount();
        return this.numFav;
    }

    protected ParkingLocation getParkedHereToDelete(){
        return this.parkedHereToDelete;
    }



}
