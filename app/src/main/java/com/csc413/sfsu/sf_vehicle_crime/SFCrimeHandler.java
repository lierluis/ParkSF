package com.csc413.sfsu.sf_vehicle_crime;

import com.google.android.gms.maps.model.LatLng;

/** The SFCrimeHandler class allows the user to pass queries to the San Francisco Crimespotters database and retrieve the
 * resulting data by abstracting the SFCrimeQuery and SFCrimeXMLResponse classes to provide a simplified interface.
*/
public class SFCrimeHandler {
    private SFCrimeXMLResponse response;
    private boolean responsePopulated;

    public SFCrimeHandler () {
        response = new SFCrimeXMLResponse();
        responsePopulated = false;
    }

    /** Generates a list of crime reports which may be subsequently accessed by the date(int) and location(int) methods.
     *
     * @param   origin      a LatLng object denoting the origin of the query;
     *                      query bounds default to all of San Francisco if set to null
     * @param   radius      the radius from the origin from which to retrieve reports, in miles;
     *                      query bounds default to all of San Francisco if set to less than or equal to 0,
     *                      or greater than or equal to bounds of San Francisco
     * @param   startYear   the year from which to start returning reports;
     *                      defaults to one year prior if set to less than 0 or greater than 9999
     * @param   count       the number of reports to return;
     *                      defaults to 20 if less than 1 or greater than 10000
     * @param   offset      the offset from the beginning of the report list from which to begin returning data;
     *                      defaults to 0 if set to less than 0 or greater than 9999
     * @return  true if reports generated successfully, false otherwise
    */
    public boolean generateReports (LatLng origin, double radius, int startYear, int count, int offset) {
        SFCrimeQuery query = new SFCrimeQuery();
        query.setBoundingBox(origin, radius);
        query.setStartYear(startYear);
        query.setCount(count);
        query.setOffset(offset);
        return (responsePopulated = response.populate(query));
    }

    /** Returns the date of the report at the specified index.
     *
     * @param   index   index of report
     * @return  the date of the report at the specified index in the form [YEAR]-[MONTH]-[DAY] with leading zeros if necessary
     * @throws  java.lang.IndexOutOfBoundsException if the index is less than 0 or greater than the number of reports generated
     * @throws  com.csc413.sfsu.sf_vehicle_crime.EmptyResponseException if the response has not been populated
    */
    public String date (int index) throws EmptyResponseException {
        if (!responsePopulated)
            throw new EmptyResponseException();
        return response.date(index);
    }

    /** Returns the location of the report at the specified index.
     *
     * @param   index   index of report
     * @return  the location of the report at the specified index in the form of a LatLng object
     * @throws  java.lang.IndexOutOfBoundsException if the index is less than 0 or greater than the number of reports generated
     * @throws  com.csc413.sfsu.sf_vehicle_crime.EmptyResponseException if the response has not been populated
     */
    public LatLng location (int index) throws EmptyResponseException {
        if (!responsePopulated)
            throw new EmptyResponseException();
        return response.loc(index);
    }

    /** Returns the number of reports returned from the latest query.
     *
     * @return  the number of reports
    */
    public int numReports () {
        return response.numReports();
    }

    /** Returns the status of the latest query.
     *
     * @return  the status of the lastest query
    */
    public String status () {
        return response.status();
    }

    /** Returns the timeout value for a query's NetworkRequest.
     *
     * @return  the timeout value in seconds
    */
    public int timeout () {
        return response.timeout();
    }

    /** Sets the time in seconds until a query's NetworkRequest times out.
     *
     * @param   secs    the timeout value in seconds
     */
    protected void setTimeout (int secs) {
        response.setTimeout(secs);
    }
}

