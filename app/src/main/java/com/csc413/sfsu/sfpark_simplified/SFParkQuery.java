package com.csc413.sfsu.sfpark_simplified;

import java.util.LinkedList;
import java.net.URL;
import java.net.MalformedURLException;

/** The SFParkQuery class allows a simplified way of constructing and modifying a query to the SFPark Availability database.
 * The query is in the form of a URL, but the class abstracts this and simply allows the user to insert or delete parameters
 * after initialization. Any instantiated object of this class may be returned as either a String representation of the query
 * or a java.net.URL object.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class SFParkQuery {
    // PRIVATE DATA MEMBERS AND METHODS //
    //
    /** The endpoint URL for the SFPark Availability database */
    private static final String ENDPOINT = "http://api.sfpark.org/sfpark/rest/availabilityservice";
    /** Holds the query value */
    private String query;
    /** Stores the parameters to be passed in the query */
    private LinkedList<Tuple<String, String>> params;

    /** Updates the value of the query to commit any changes made to the parameters since the last update.
     */
    private void updateQuery () {
        query = ENDPOINT + "?";
        for (int i = 0; i < params.size(); i++) {
            query += (createParam(params.get(i)) + (i < params.size()-1? "&" : ""));
        }
    }

    /** Creates a properly-formatted parameter to append to the query.
     *
     * @param   param   a Tuple object containing two Strings:
     *                      - a parameter tag name as the first entry
     *                      - a parameter value as the second entry
     * @return  a String containing a properly-formatted paramter to append to the query
     */
    private String createParam (Tuple<String, String> param) {
        return param.first() + "=" + param.last();
    }

    /** Indicates whether a parameter exists in the query.
     * The parameter list is searched using a key in the form of a parameter name,
     * which is matched against parameter names in the parameter list.
     *
     * @param   key a String representing a parameter name as a search criteria
     * @return  true if a match to the key is found, false otherwise
     */
    private boolean contains (String key) {
        for (Tuple t : params)
            if (t.first().equals(key))
                return true;
        return false;
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

    /** Constructor.
     * Initializes a "bare bones" query object to accept user-defined parameters.
     * Parameters must be appended for the query to return any useful data from the SFPark Availability database.
     */
    public SFParkQuery () {
        params = new LinkedList<Tuple<String, String>>();
        updateQuery();
    }

    /** Appends a new parameter to the query.
     * Since a query cannot contain more than instance of any parameter, only
     * unique parameters - determined by the parameter name - may be added;
     * duplicate parameters will be ignored.
     * To update an existing parameter's value, call either the updateParameter or addOrUpdateParameter method.
     *
     * @param   arg     the name of the parameter to add
     * @param   val     the value of the parameter to add
     * @return          true if the parameter was added successfully, false otherwise
     */
    private boolean addParameter (String arg, String val) {
        if (!contains(arg)) {
            params.add(new Tuple<String, String>(arg, val));
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
     * @param   arg     the name of the parameter to update
     * @param   val     the value of the parameter to update
     * @return          true if the parameter was updated successfully, false otherwise
     */
    private boolean updateParameter (String arg, String val) {
        if (contains(arg)) {
            for (int i = 0; i < params.size(); i++) {
                if (params.get(i).first().equals(arg)) {
                    params.set(i, new Tuple<String, String>(arg, val));
                    updateQuery();
                    return true;
                }
            }
        }
        return false;
    }

    /** Updates a parameter if it exists in the query, or adds it as a new parameter otherwise.
     * To add a non-existing parameter only, call the addParameter method.
     * To update an existing parameter only, call the updateParameter method.
     *
     * @param   arg     the name of the parameter to update or add
     * @param   val     the value of the parameter to update or add
     * @return          'u' if the parameter was updated
     *                  'a' if the parameter was added
     */
    private char addOrUpdateParameter (String arg, String val) {
        if (contains(arg)) {
            updateParameter(arg, val);
            return 'u';
        }
        else {
            addParameter(arg, val);
            return 'a';
        }
    }


    /** Removes a parameter from the query, if it exists.
     *
     * @param   arg     the name of the parameter to remove
     * @return  true if the parameter existed and was successfully removed, false otherwise
     */
    private boolean removeParameter (String arg) {
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).first().equals(arg)) {
                params.remove(i);
                updateQuery();
                return true;
            }
        }
        return false;
    }

    // PUBLIC METHODS //
    //
    // ACCESSORS //
    //
    /** Returns the value of the Request Identifier (REQUESTID) parameter, if it exists.
     *
     * @return  a String containing the Request Identifier value if it exists, or an empty String otherwise
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setRequestID(String)
    */
    public String getRequestID () {
        return getValue("REQUESTID");
    }

    /** Returns the value of the Longitude (LONG) parameter, if it exists.
     *
     * @return  a Double containing the Longitude of the query if it exists, or null otherwise
     */
    public Double getLongitude () {
        try {
            return Double.parseDouble(getValue("LONG"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Returns the value of the Latitude (LAT) parameter, if it exists.
     *
     * @return  a Double containing the Latitude of the query if it exists, or null otherwise
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setLatitude(Double)
     */
    public Double getLatitude () {
        try {
            return Double.parseDouble(getValue("LAT"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Returns the value of the Search Radius (RADIUS) parameter, if it exists.
     *
     * @return  a Double containing the Search Radius of the query if it exists, or null otherwise
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setRadius(Double)
     */
    public Double getRadius () {
        try {
            return Double.parseDouble(getValue("RADIUS"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Returns the value of the Unit of Measurement (UOM) parameter for the Search Radius, if it exists.
     *
     * @return  a String containing the Unit of Measurement value if it exists, or an empty String otherwise
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setUnitOfMeasurement(String)
     */
    public String getUnitOfMeasurement () {
        return getValue("UOM");
    }

    /** Returns the value of the Parking Type (TYPE) parameter, if it exists.
     *
     * @return  a String containing the Parking Type value if it exists, or an empty String otherwise
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setParkingType(String)
     */
    public String getParkingType () {
        return getValue("TYPE");
    }

    /** Returns the value of the Pricing Information (PRICING) parameter, if it exists.
     *
     * @return  a String containing the Rate Information value if it exists, or an empty String otherwise
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setPricingInformation(String)
     */
    public String getPricingInformation () {
        return getValue("PRICING");
    }

    /** Returns the value of the User Defined Field #1 (UDF1) parameter, if it exists.
     *
     * @return  a String containing the User Defined Field #1 value if it exists, or an empty String otherwise
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setUserDefinedField1(String)
     */
    public String getUserDefinedField1 () {
        return getValue("UDF1");
    }

    // MUTATORS //
    //
    /** Sets the value for the Request ID (REQUESTID) parameter.
     *
     * Taken from the SFPark Availability Service API Reference:
     * "This optional request parameter allows correlating a response to a particular request or may be used for
     * tracking purposes. If passed, this identifier is returned as is in the response generated for the particular
     * request. It has no other purpose in determining the outcome of the request."
     *
     * Default value if none specified: no default value provided (optional value)
     * Allowed values:  any alphanumeric String of no more than 100 characters
     *
     * NOTE: some special characters are not supported and may cause issues with the query;
     * it is recommended to restrict the value to alphanumeric values.
     *
     * Also note that any String longer than 100 characters is not allowed; any such value passed to this method
     * will be rejected.
     *
     * @param   requestID   a String containing the value for the Request ID parameter
     * @return  true if the parameter value was set successfully, false otherwise
    */
    public boolean setRequestID (String requestID) {
        if (requestID.length() > 100)
            return false;
        addOrUpdateParameter("REQUESTID", requestID);
        return true;
    }

    /** Sets the value for the Longitude (LONG) parameter.
     *
     * Taken from the SFPark Availability Service API Reference:
     * "This request parameter is used in conjunction with the specified Latitude parameter and represents the
     * geographical point from which the search results will be centered."
     *
     * - Default value if both LAT and LONG are skipped: -122.4200
     * - Allowed values: nothing whose absolute value is > 180
     *
     * Note that the query must contain both LAT and LONG or neither; calling this method when LAT does not exist
     * will initialize LAT to its default value (37.7819).
     *
     * @param   lng   a Double containing the new value for the Longitude
     * @return  true if the parameter value was set successfully, false otherwise
    */
    public boolean setLongitude (Double lng) {
        if (lng > 180 || lng < -180)
            return false;
        addOrUpdateParameter("LONG", lng.toString());
        if (!contains("LAT"))
            addParameter("LAT", "37.7819");
        return true;
    }

    /** Sets the value for the Latitude (LAT) parameter.
     *
     * Taken from the SFPark Availability Service API Reference:
     * "This request parameter is used in conjunction with the specified Longitude parameter and represents the
     * geographical point from which the search results will be centered."
     *
     * - Default value if both LAT and LONG are skipped: 37.7819
     * - Allowed values: nothing whose absolute value is > 90
     *
     * Note that the query must contain both LAT and LONG or neither; calling this method when LONG does not exist
     * will initialize LONG to its default value (-122.4200).
     *
     * @param   lat   a Double containing the new value for the Latitude
     */
    public boolean setLatitude (Double lat) {
        if (lat > 90 || lat < -90)
            return false;
        addOrUpdateParameter("LAT", lat.toString());
        if (!contains("LONG"))
            addParameter("LONG", "-122.4200");
        return true;
    }

    /** Sets the value for the Search Radius (RADIUS) parameter.
     *
     * Taken from the SFPark Availability Service API Reference:
     *
     * "This request parameter is used in conjunction with the specified UOM parameter and represents the
     * search radius the result will return from the requested location point. If UOM is not passed, then the
     * service will use the default value for UOM.
     * Note: If no UOM is specified but RADIUS is specified, then UOM is still defaulted to mile. So be aware of
     * these default values and their behavior and hence it is recommended to specify both RADIUS and UOM
     * or leave them out to use the SFPark default, currently 0.25 mile radius."
     *
     * - Default value if none specified: 0.25
     *
     * @param    radius   a Double containing the new value for the Search Radius
    */
    public void setRadius (Double radius) {
        addOrUpdateParameter("RADIUS", radius.toString());
    }

    /** Sets the value for the Unit of Measurement (UOM) parameter.
     *
     * Taken from the SFPark Availability Service API Reference:
     *
     * "This request parameter is used in conjunction with the specified RADIUS parameter and represents the
     * unit of measurement for the radius parameter. The result will return available data points based on the
     * requested radius in this unit of measurement from the requested location point. If RADIUS is not passed,
     * then the service will use the default value for RADIUS."
     *
     * - Default value if none specified: "MILE"
     * - Allow values: MILE, KM, FOOT, METER, M, YARD
     *
     * @param   uom     a String containing the new value for the Unit of Measurement parameter
    */
    public void setUnitOfMeasurement (String uom) {
        addOrUpdateParameter("UOM", uom);
    }

    /** Sets the value for the Parking Type (TYPE) parameter.
     *
     * Taken from the SFPark Availability Service API Reference:
     *
     * "This request parameter is used to specify the data returned be limited to the requested parking type.
     * There are currently following two parking types supported, on-street (on) and off-street (off). Use the
     * parameter to allow restricting data to following parking types; on-street (on), off-street (off) or all (returns
     * both on and off-street or all results)"
     *
     * - Default value if none specified: "ALL"
     * - Allowed values: ON, OFF, ALL
     *
     * @param   type    a String containing the new value for the Parking Type parameter
    */
    public void setParkingType (String type) {
        addOrUpdateParameter("TYPE", type);
    }

    /** Sets the value for the Pricing Information (PRICING) parameter.
     *
     * Taken from the SFPark Availability Service API Reference:
     *
     * "This request parameter is used to specify whether the data returned should include the pricing
     * information for the parking locations included in the response. The rate information does not change
     * frequently but involves extra information to be added to the response. Hence, requests to retrieve pricing
     * data should be made specifically by setting the pricing option. The rate information returned by the
     * service is discussed in an earlier section."
     *
     * - Default value if none specified: NO
     * - Allowed values: YES, NO
     *
     * @param   pricing     a String containing the new value for the Pricing Information parameter
    */
    public void setPricingInformation (String pricing) {
        addOrUpdateParameter("PRICING", pricing);
    }

    /** Sets the value for the User Defined Field #1 (UDF1) parameter.
     *
     * Taken from the SFPark Availability Service API Reference:
     *
     * "This optional request parameter allows user to pass in a text string to be used as a field that they may
     * define for their internal use, e.g., specifying an organization name or may be used for tracking purposes.
     * If passed, this identifier is returned as is in the response generated for the particular request. It has no
     * other purpose in determining the outcome of the request."
     *
     * Default value if none specified: no default value provided (optional value)
     * Allowed values:  any alphanumeric String of no more than 100 characters
     *
     * NOTE: some special characters are not supported and may cause issues with the query;
     * it is recommended to restrict the value to alphanumeric values.
     *
     * Also note that any String longer than 100 characters is not allowed; any such value passed to this method
     * will be rejected.
     *
     * @param   udf1   a String containing the value for the User Defined Field #1 parameter
     * @return  true if the parameter value was set successfully, false otherwise
    */
    public boolean setUserDefinedField1 (String udf1) {
        if (udf1.length() > 100)
            return false;
        addOrUpdateParameter("UDF1", udf1);
        return true;
    }

    // RESETTERS (Reset a parameter to default values) //
    //
    /** Resets the Request ID (REQUESTID) parameter to its default value.
     *
     * Default value: no default value (removes parameter)
     *
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setRequestID(String)
    */
    public void resetRequestID () {
        removeParameter("REQUESTID");
    }

    /** Resets the location parameters (Longitude (LONG) and Latitude (LAT)) to their respective default values.
     * Both Longitude and Latitude values must concurrently be either specified or at their default values
     *
     * Default value (Longitude):   -122.4200
     * Default value (Latitude):    37.7819
     *
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setLongitude(Double)
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setLatitude(Double)
    */
    public void resetLocation () {
        removeParameter("LONG");
        removeParameter("LAT");
    }

    /** Resets the Radius (RADIUS) parameter to its default value.
     *
     * Default value: 0.25
     *
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setRadius(Double)
    */
    public void resetRadius () {
        removeParameter("RADIUS");
    }

    /** Resets the Unit of Measurement (UOM) parameter to its default value.
     *
     * Default value: "MILE"
     *
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setUnitOfMeasurement(String)
    */
    public void resetUnitOfMeasurement () {
        removeParameter("UOM");
    }

    /** Resets the Parking Type (TYPE) parameter to its default value.
     *
     * Default value: "ALL"
     *
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setParkingType(String)
    */
    public void resetParkingType () {
        removeParameter("TYPE");
    }

    /** Resets the Pricing Information (PRICING) parameter to its default value.
     *
     * Default value: "NO"
     *
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setPricingInformation(String)
    */
    public void resetPricingInformation () {
        removeParameter("PRICING");
    }

    /** Resets the User Defined Field #1 (UDF1) parameter to its default value.
     *
     * Default value: no default value (removes parameter)
     *
     * @see     com.csc413.sfsu.sfpark_simplified.SFParkQuery#setUserDefinedField1(String)
     */
    public void resetUserDefinedField1 () {
        removeParameter("UDF1");
    }

    // SUPPLEMENTARY METHODS //
    //
    /** Returns the query in the form of a java.net.URL object.
     *
     * @return  a java.net.URL object containing the query in its present state, or null if an error occurred
     */
    public URL toURL () {
        try {
            return new URL(query);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public String toString () {
        return query;
    }
}