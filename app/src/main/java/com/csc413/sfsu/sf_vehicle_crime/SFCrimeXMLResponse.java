package com.csc413.sfsu.sf_vehicle_crime;

import com.csc413.sfsu.sfpark_simplified.NetworkRequest;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** The SFCrimeXMLResponse class retrieves and stores select data from a query to the San Francisco Crimespotters database.
 * The class is intended to be used with the csc413_parking package and stores only the data that is pertinent in order to
 * avoid unnecessary bloating.
 * Currently, this class stores the following information from specifically vehicle theft reports:
 * - Date of occurrence
 * - Location (latitude and longitude) of occurrence
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
class SFCrimeXMLResponse {
    /** Stores dates of reported vehicle thefts */
    private List<String> crimeDates;
    /** Stores locations of reported vehicle thefts */
    private List<LatLng> crimeLocs;
    /** Status of a query; "SUCCESS" = database accessed and reports retrieved (note: success possible with zero reports returned).
     * "FAILURE" = error accessing database or retrieving reports */
    private String status;
    /** Denotes the time in seconds until a NetworkRequest times out; defaults to 20 */
    private int timeout;
    /** Denotes the number of reports returned from a query */
    private int numReports;

    /** Clears all data in the response object.
    */
    private void reset () {
        crimeDates = new ArrayList<String>();
        crimeLocs = new ArrayList<LatLng>();
        timeout = 20;
        numReports = 0;
        status = "";
    }

    /** Restructures the default date format of a report as returned from a query to the SFCrimespotters database.
     * The SFCrimespotters date format is as follows: [DAY OF WEEK], [FIRST 3 LETTERS OF MONTH] [DAY OF MONTH, NO LEADING ZERO], [YEAR];
     * the restructured format is as follows: [YEAR]-[NUMBER OF MONTH WITH LEADING ZERO]-[DAY OF MONTH WITH LEADING ZERO]
     *
     * @param   date    a String with a date in the SFCrimespotters format
     * @return  a String in with a date in the restructured format if reformatting successful, or an empty String otherwise
     */
    private String restructureDate(String date) {
        try {
            // Parse year
            String newDate = date.substring(date.length() - 4, date.length()) + "-";
            // Parse month
            newDate += String.format("%02d", Integer.parseInt(date.substring(date.length() - 8, date.length() - 6).trim())) + "-";
            // Parse day
            if (date.contains("Ja"))
                newDate += "01";
            else if (date.contains("Fe"))
                newDate += "02";
            else if (date.contains("Ma"))
                newDate += "03";
            else if (date.contains("Ap"))
                newDate += "04";
            else if (date.contains("Ma"))
                newDate += "05";
            else if (date.contains("Jun"))
                newDate += "06";
            else if (date.contains("Jul"))
                newDate += "07";
            else if (date.contains("Au"))
                newDate += "08";
            else if (date.contains("Se"))
                newDate += "09";
            else if (date.contains("Oc"))
                newDate += "10";
            else if (date.contains("No"))
                newDate += "11";
            else if (date.contains("De"))
                newDate += "12";

            return newDate;
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Constructor.
    */
    protected  SFCrimeXMLResponse () {
        reset();
    }

    /** Populates the SFCrimeXMLResponse object with data parsed from a successful San Francisco Crimespotters database query.
     *
     * @param   query    a String representing a San Francisco Crimespotters API query in the form of a URL
     * @return  true if the query was successful and no exceptions were thrown, false otherwise
     */
    protected boolean populate (String query) {
        try {
            reset();

            Document doc = new NetworkRequest().execute(query).get(timeout, TimeUnit.SECONDS);
            Element root = (Element)doc.getElementsByTagName("reports").item(0);
            NodeList reports = root.getElementsByTagName("report");

            numReports = reports.getLength();

            // Extract and store only the location and date information
            for (int i = 0; i < reports.getLength(); i++) {
                Element report = (Element)reports.item(i);

                // Structure data, throw Exception if error is encountered
                String date = restructureDate(report.getAttribute("date"));
                if (date.equals(""))
                    throw new Exception();
                LatLng loc = new LatLng(Double.parseDouble(report.getAttribute("lat")), Double.parseDouble(report.getAttribute("lon")));

                // Add successfully parsed data to lists
                crimeDates.add(date);
                crimeLocs.add(loc);
            }

        } catch (Exception e) {
            reset();
            status = "FAILURE: " + e.getClass().toString();
            return false;
        }
        status = "SUCCESS";
        return true;
    }

    /** Populates the SFCrimeXMLResponse object with data parsed from a successful San Francisco Crimespotters database query.
     *
     * @param   query    a SFCrimeQuery object representing a San Francisco Crimespotters API query
     * @return  true if the query was successful and no exceptions were thrown, false otherwise
     */
    protected boolean populate (SFCrimeQuery query) {
        return populate(query.toString());
    }

    /** Returns the date of the crime at the specified index.
     *
     * @param   index   index of crime
     * @return  the date of the crime in the form of a String
     * @throws  java.lang.IndexOutOfBoundsException if the index is < 0 or > the number of crimes - 1
    */
    protected String date (int index) {
        return crimeDates.get(index);
    }

    /** Returns the location of the crime at the specified index.
     *
     * @param   index   index of crime
     * @return  the location of the crime in the form of a LatLng object
     * @throws  java.lang.IndexOutOfBoundsException if the index is < 0 or > the number of crimes - 1
     */
    protected LatLng loc (int index) {
        return crimeLocs.get(index);
    }

    /** Returns the status of the latest database query.
     *
     * @return  a String containing the status
    */
    protected String status () {
        return status;
    }

    /** Returns the number of reports returned from the latest database query.
     *
     * @return  the number of reports
    */
    protected int numReports () {
        return numReports;
    }

    /** Returns the timeout value for a NetworkRequest.
     *
     * @return  the timeout value in seconds
    */
    protected int timeout () {
        return timeout;
    }

    /** Sets the time in seconds until a NetworkRequest times out.
     *
     * @param   secs    the timeout value in seconds
     */
    protected void setTimeout (int secs) {
        timeout = secs;
    }
}
