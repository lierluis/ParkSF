package com.csc413.sfsu.sf_vehicle_crime;

import com.csc413.sfsu.sfpark_simplified.Tuple;
import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.LinkedList;

/** The SFCrimeQuery class allows a simplified way of constructing and modifying a query to the San Francisco Crimespotters database.
 * The query is in the form of a URL, but the class abstracts this and simply allows the user to insert or delete parameters
 * after initialization. Any instantiated object of this class may be returned as a String representation of the query.
 *
 * NOTES: This class attempts to encapsulate the greater part of San Francisco. However, due to differences in the way that
 * the SF Park Availability API and San Francisco Crimespotters API handle search radii, the SFCrimeQuery class attempts to
 * emulate the functionality of the former based on calculations that are merely approximations. Below are the values by which the
 * search radii are for this class are calculated:
 *
 *  1 degree latitude = roughly 68.94 miles
 *  1 degree longitude = roughly 54.49 miles
 *  1 mile = 1/68.94 degrees latitude = 1/54.49 degrees longitude
 *  This is assuming a spherical as opposed to an ellipsoid earth
 *
 *  SF Approximate Boundaries:
 *      East: -122.5125409 longitude
 *      West: -122.3570298 longitude
 *      North: 37.8108924 latitude
 *      South: 37.7081192 latitude
 *  Approximate east-west distance = 0.1555111 degrees longitude = 8.4737998 miles
 *  Approximate north-south distance = 0.1027732 degrees latitude = 7.0851844 miles
 *
 *  Also note that this class provides only a limited functionality potential of the San Francisco Crimespotters API in order
 *  to exclude any extraneous data unneeded by the csc413_parking package. Specifically, the query is limited to:
 *
 *  - Vehicle theft crimes
 *  - A maximum bounding box to include San Francisco's borders
 *  - Specifying start dates by year only, with month and day hard coded to January 1st
 *  - Ending dates as the default value of "present"
 *  - Return format as XML only
 *  - Specifying the report count (i.e. number of reports to return)
 *  - Specifying the report offset
 *
 *  Additional functionality may be added in the future if needed.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
class SFCrimeQuery {
    /** Endpoint for San Francisco Crimespotting API */
    private static final String ENDPOINT = "http://sanfrancisco.crimespotting.org/crime-data";
    /** Query parameter to return reports in XML format */
    private static final String FORMAT_XML = "format=xml";
    /** Query parameter to return only reports involving vehicle theft */
    private static final String TYPE_VEHICLE_THEFT = "type=vehicle_theft";

    /** Query parameter to return reports from January 1 of one year prior */
    private static final String START_DATE = (Calendar.getInstance().get(Calendar.YEAR) - 1) + "-01-01";
    /** Approximate westernmost boundary of San Francisco */
    private static final double BOUNDARY_WEST = -122.5125409;
    /** Approximate southernmost boundary of San Francisco */
    private static final double BOUNDARY_SOUTH = 37.7081192;
    /** Approximate easternmost boundary of San Francisco */
    private static final double BOUNDARY_EAST = -122.3570298;
    /** Approximate northernmost boundary of San Francisco */
    private static final double BOUNDARY_NORTH = 37.8108924;
    /** Query parameter to set the bounding box to include all of San Francisco */
    private static final String SF_BOUNDARIES = BOUNDARY_WEST + "," + BOUNDARY_SOUTH + "," + BOUNDARY_EAST + "," + BOUNDARY_NORTH;

    /** Default query passed to the SFCrimespotters database */
    private static final String BASE_QUERY = ENDPOINT + "?" + FORMAT_XML + "&" + TYPE_VEHICLE_THEFT + "&";

    /** Approximate number of miles per degree latitude in San Francisco */
    private static final double MILES_PER_LAT_DEGREE = 68.94;
    /** Approximate number of miles per degree longitude in San Francisco */
    private static final double MILES_PER_LONG_DEGREE = 54.49;
    /** Approximate number of degree latitude per mile in San Francisco */
    private static final double LAT_DEGREES_PER_MILE = 1 / MILES_PER_LAT_DEGREE;
    /** Approximate number of degree longitude per mile in San Francisco */
    private static final double LONG_DEGREES_PER_MILE = 1 / MILES_PER_LONG_DEGREE;

    /** Holds the query value */
    private String query;
    /** Stores the parameters to be passed in the query */
    private LinkedList<Tuple<String, String>> params;

    /** Updates the value of the query to commit any changes made to the parameters since the last update.
     */
    private void updateQuery() {
        query = BASE_QUERY;
        for (int i = 0; i < params.size(); i++) {
            query += params.get(i).first() + "=" + params.get(i).last();
            if (i < params.size() - 1)
                query += "&";
        }
    }

    /** Indicates whether a parameter exists in the query.
     * The parameter list is searched using a key in the form of a parameter name,
     * which is matched against parameter names in the parameter list.
     *
     * @param   key a String representing a parameter name as a search criteria
     * @return  true if a match to the key is found, false otherwise
     */
    private boolean contains(String key) {
        return getIndexByKey(key) > -1;
    }

    /** Returns the value of an existing parameter.
     *
     * @param   param   a String containing the parameter name
     * @return  a String containing the value of the parameter if found, or an empty String otherwise
     */
    private String getValue (String param) {
        if (contains(param)) {
            for (Tuple t : params) {
                if (t.first().equals((String)param))
                    return (String)t.last();
            }
        }
        return "";
    }

    /** Appends a new parameter to the query.
     * Since a query cannot contain more than instance of any parameter, only
     * unique parameters - determined by the parameter name - may be added; duplicate parameters will be ignored.
     * To update an existing parameter's value, call either the updateParameter or addOrUpdateParameter method.
     *
     * @param   key     the name of the parameter to add
     * @param   value   the value of the parameter to add
     * @return          true if the parameter was added successfully, false otherwise
     */
    private boolean addParameter(String key, String value) {
        if (!contains(key)) {
            params.add(new Tuple<String, String>(key, value));
            updateQuery();
            return true;
        }
        return false;
    }

    /** Updates the value of an existing parameter in the query.
     * Non-existing parameters will be ignored.
     * To update a parameter if it exists or add it as new if it doesn't exist,
     * call the addOrUpdateParameter method.
     *
     * @param   key     the name of the parameter to update
     * @param   value   the value of the parameter to update
     * @return          true if the parameter was updated successfully, false otherwise
     */
    private boolean updateParameter(String key, String value) {
        int index;
        if ((index = getIndexByKey(key)) > -1) {
            params.get(index).setLast(value);
            updateQuery();
            return true;
        }
        return false;
    }

    /** Updates a parameter if it exists in the query, or adds it as a new parameter otherwise.
     * To add a non-existing parameter only, call the addParameter method.
     * To update an existing parameter only, call the updateParameter method.
     *
     * @param   key     the name of the parameter to update or add
     * @param   value   the value of the parameter to update or add
     * @return          'u' if the parameter was updated
     *                  'a' if the parameter was added
     */
    private boolean addOrUpdateParameter(String key, String value) {
        if (contains(key))
            return updateParameter(key, value);
        else
            return addParameter(key, value);
    }

    /** Removes a parameter from the query, if it exists.
     *
     * @param   key     the name of the parameter to remove
     * @return  true if the parameter existed and was successfully removed, false otherwise
     */
    private boolean removeParameter(String key) {
        int index;
        if ((index = getIndexByKey(key)) > -1) {
            params.remove(index);
            updateQuery();
            return true;
        }
        return false;
    }

    /** Returns the index in the parameter list of the item whose name matches the key.
     *
     * @param   key     the name of the parameter to search against
     * @return  the index of the item if found, or -1 otherwise
     */
    private int getIndexByKey(String key) {
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).first().equals(key))
                return i;
        }
        return -1;
    }

    /**
     * Constructor.
     */
    protected SFCrimeQuery() {
        query = BASE_QUERY;
        params = new LinkedList<Tuple<String, String>>();
        addParameter("dstart", START_DATE);
        addParameter("bbox", SF_BOUNDARIES);
    }

    /**
     * Sets the value for the number of reports to return.
     * Records returned start from the most recent report plus the offset.
     *
     * - Default value if none specified: 20
     * - Allowed values: any value between 1 and 10000 inclusive
     *
     * @param count a new value for the report count
     * @return true if the new value was set successfully, false otherwise
     */
    public boolean setCount(int count) {
        if (count > 0 && count <= 10000) {
            addOrUpdateParameter("count", count + "");
            return true;
        }
        return false;
    }

    /**
     * Sets the value for the starting point of the reports to be returned.
     * By default, a query returns the [count] most recent reports; the offset denotes how far down the complete
     * list of reports from which to begin creating the list of returned items.
     *
     * - Default value if none specified: 0
     * - Allowed values: any value between 0 and 9999 inclusive
     *
     * @param offset a new value for the record offset
     * @return true if the new value was set successfully, false otherwise
     */
    public boolean setOffset(int offset) {
        if (offset > 0 && offset < 10000) {
            addOrUpdateParameter("offset", offset + "");
            return true;
        }
        return false;
    }

    /**
     * Sets the value for the year from which to begin parsing reports.
     * Any year before the earliest available will return act as the earliest available;
     * any year after the current will automatically return no reports.
     *
     * - Default value if none specified: [current year]
     * - Allowed values: any value between 0 and 9999 inclusive
     *
     * @param year a new value for the starting
     * @return true if the new value was set successfully, false otherwise
     */
    public boolean setStartYear(int year) {
        if (year > 0 && year < 10000) {
            addOrUpdateParameter("dstart", String.format("%04d", year) + "-01-01");
            return true;
        }
        return false;
    }

    /** Sets the bounding box for the area from which to return reports.
     * Any bound outside of San Francisco are kept within its boundaries.
     *
     * @param   origin  a LatLng object denoting the center of the bounding box
     * @param   radius  the radius in miles from the origin
     * @return  true if bounding box set successfully, false otherwise
     */
    public boolean setBoundingBox (LatLng origin, double radius) {
        if (radius > 0 && origin != null) {
            double east = origin.longitude + radius*LAT_DEGREES_PER_MILE;
            if (east > BOUNDARY_EAST)
                east = BOUNDARY_EAST;
            double west = origin.longitude - radius*LAT_DEGREES_PER_MILE;
            if (west < BOUNDARY_WEST)
                west = BOUNDARY_WEST;
            double north = origin.latitude + radius*LONG_DEGREES_PER_MILE;
            if (north > BOUNDARY_NORTH)
                north = BOUNDARY_NORTH;
            double south = origin.latitude - radius*LONG_DEGREES_PER_MILE;
            if (south < BOUNDARY_SOUTH)
                south = BOUNDARY_SOUTH;

            addOrUpdateParameter("bbox", west + "," + south + "," + east + "," + north);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return query;
    }
}
