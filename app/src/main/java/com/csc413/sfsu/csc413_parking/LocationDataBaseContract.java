package com.csc413.sfsu.csc413_parking;

import android.provider.BaseColumns;

/**
 *  The LocationDataBaseContract class is a wrapper class for SQLite constants used by LocationDataBase objects.
 *
 *  It is not intended for any objects of type LocationDataBaseContract to be created, as all data members
 *  of the LocationDataBaseContract class will be static across all instances of the class
 *  in order to maintain consistent naming conventions.
 *
 *  @author Devin Clary
 */
public class LocationDataBaseContract {

    // Constructor is empty if someone creates an instance.
    public LocationDataBaseContract() {}

    // Static column names
    public static abstract class LocationEntry implements BaseColumns {
        public static final String TABLE_LOCATIONS = "locations";
        public static final String KEY_LATITUDE="lat";
        public static final String KEY_LONGITUDE="long";
        public static final String KEY_IS_FAVORITE="isFavorite";
        public static final String KEY_TIMES_SEARCHED="timesSearched";
    }

}
