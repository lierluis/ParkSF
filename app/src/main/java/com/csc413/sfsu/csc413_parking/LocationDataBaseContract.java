package com.csc413.sfsu.csc413_parking;

import android.provider.BaseColumns;

/**
 *  The LocationDatabaseContract class is a wrapper class for SQLite constants used by
 *  LocationDataBase objects.
 *
 *  It is not intended for any objects of type LocationDatabaseContract to be created, as all
 *  data members of the LocationDatabaseContract class will be static across all instances
 *  of the class in order to maintain consistent naming conventions.
 *
 *  @author Devin Clary
 */
public class LocationDatabaseContract {

    /**The max number of rows to hold in the database.*/
    public static final int MAX_ROWS=1000;
    // Constructor is empty if someone creates an instance.
    public LocationDatabaseContract() {}

    // Static column names
    public static abstract class LocationEntry implements BaseColumns {
        public static final String TABLE_LOCATIONS = "locations";
        public static final String LOCATION_NAME_ENTRY_ID = "locationID";
        public static final String KEY_LATITUDE="lat";
        public static final String KEY_LONGITUDE="long";
        public static final String KEY_ORIGIN_LATITIUDE="originLat";
        public static final String KEY_ORIGIN_LONGITUDE="originLong";
        public static final String KEY_RADIUS="radius";
        public static final String KEY_HAS_STREET_PARKING="hasStreetParking";
        public static final String KEY_NAME="name";
        public static final String KEY_DESC="desc";
        public static final String KEY_OSPID="ospid";
        public static final String KEY_BFID="bfid";
        public static final String KEY_IS_FAVORITE="isFavorite";
        public static final String KEY_TIMES_SEARCHED="timesSearched";
    }

}
