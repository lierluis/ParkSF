package com.csc413.sfsu.sfpark_simplified;

import com.google.android.gms.maps.model.LatLng;

/** The SFParkLocation class stores and retrieves one or more latitude / longitude coordinate pairs that form a geolocation.
 * While the SFPark Availability REST Service API currently contains anywhere from 0 to 2 locations, this class is capable of
 * handling any number of coordinates.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class SFParkLocation {
    // PRIVATE DATA MEMBERS //
    //
    /** Stores all latitudinal values */
    private double [] lat;
    /** Stores all longitudinal values */
    private double [] lng;
    /** Represents the number of longitude/latitude pairs that form a location point */
    private int numPoints;

    // PUBLIC METHODS //
    //
    /** Constructor.
     * Initializes a newly-created SFParkLocation object that contains pairs of latitude / longitude coordinates.
     * There is no theoretical limit to the number of coordinates, and the object may contain no coordinates at all;
     * an error in parsing the numerical value from the String parameter results in such an empty object.
     *
     * @param   loc a String containing an even amount of double values delimited by commas;
     *              every two values represent a longitude / latitude coordinate pair that form a location,
     *              with longitude values as the first item in each pair.
     */
    public SFParkLocation (String loc) {
        try {
            // Split String into tokens
            // Throw a NumberFormatException if the number of coordinates are odd (must have both a longitude and latitude for every point)
            String [] coords = loc.split(",");
            if (coords.length%2 != 0)
                throw new NumberFormatException();

            numPoints = coords.length/2;

            lat = new double[numPoints];
            lng = new double[numPoints];

            for (int i = 0, lngIndex = 0, latIndex = 0; i < coords.length; i++) {
                if (i%2 == 0) // Coordinates at even indices are longitudinal
                    lng[lngIndex++] = Double.parseDouble(coords[i]);
                else // Coordinates at odd indices are latitudinal
                    lat[latIndex++] = Double.parseDouble(coords[i]);
            }

        } catch (NumberFormatException e) {
            lat = new double[0];
            lng = new double[0];
            numPoints = 0;
        }
    }

    /** Returns the number of longitude/latitude pairs.
     *
     * @return  the number of longitude/latitude pairs
     */
    public int numPoints () {
        return numPoints;
    }

    /** Returns the latitude for a particular point.
     *
     * @param   index                       index of the desired point
     * @return                              a latitude value in the form of a double
     * @throws  IndexOutOfBoundsException   if an invalid index is passed as a parameter
     */
    public Double latitude (int index) {
        return lat[index];
    }

    /** Returns the longitude for a particular point.
     *
     * @param   index   index of the desired point
     * @return  a longitude value in the form of a double
     * @throws  IndexOutOfBoundsException   if the index is invalid
     */
    public Double longitude (int index) {
        return lng[index];
    }

    /** Returns a latitude / longitude coordinate pair in the form of a LatLng object.
     *
     * @param   index   index of the desired location
     * @return  a location comprising a latitude longitude pair
     * @throws  IndexOutOfBoundsException   if the index is invalid
     * @see     com.google.android.gms.maps.model.LatLng
     */
    public LatLng getLatLngLocation (int index) {
        return new LatLng(lat[index], lng[index]);
    }

    @Override
    public String toString () {
        String allLocs = "";
        for (int i = 0; i < numPoints; i++) {
            allLocs += "(" + lat[i] + "," + lng[i] + ")";
            if (i < numPoints-1)
                allLocs += ",";
        }
        return allLocs;
    }
}
