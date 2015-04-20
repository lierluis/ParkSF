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

    // PUBLIC METHODS //
    //
    /** Constructor.
     * Initializes a "bare bones" query object to accept user-defined parameters.
     * Parameters must be appended for the query to return any useful data from the SFPark Availability database.
     */
    public SFParkQuery () {
        params = new LinkedList<Tuple<String, String>>();
        updateQuery();
    }

    /** Appends a parameter to the query.
     * Since a query cannot contain more than instance of any parameter, only
     * unique parameters - determined by the parameter name - may be added;
     * duplicate parameters will be ignored.
     * To update an existing parameter's value, call the updateParameter method.
     *
     * @param   arg     the name of the parameter to add
     * @param   val     the value of the parameter to add
     * @return          true if the parameter was added successfully, false otherwise
     */
    public boolean addParameter (String arg, String val) {
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
    public boolean updateParameter (String arg, String val) {
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
     *
     * @param   arg     the name of the parameter to update or add
     * @param   val     the value of the parameter to update or add
     * @return          'u' if the parameter was updated
     *                  'a' if the parameter was added
     */
    public char addOrUpdateParameter (String arg, String val) {
        if (contains(arg)) {
            updateParameter(arg, val);
            return 'u';
        }
        else {
            addParameter(arg, val);
            return 'a';
        }
    }


    /** Removes a parameter from the query.
     * A parameter that does not exist in the query will be ignored.
     *
     * @param   arg     the name of the parameter to remove
     * @return  true if the parameter existed and was successfully removed, false otherwise
     */
    public boolean removeParameter (String arg) {
        for (int i = 0; i < params.size(); i++) {
            if (params.get(i).first().equals(arg)) {
                params.remove(i);
                updateQuery();
                return true;
            }
        }
        return false;
    }

    /** Returns the query in the form of a java.net.URL object.
     *
     * @return  a java.net.URL object containing the query in its present state
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