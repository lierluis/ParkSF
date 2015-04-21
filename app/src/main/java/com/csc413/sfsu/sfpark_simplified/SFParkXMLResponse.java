package com.csc413.sfsu.sfpark_simplified;

import android.os.AsyncTask;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/** The SFParkXMLResponse class is a helper class that allows the user to conveniently interface with a
 * response returned from a SFPark Availability database query and easily retrieve associated data.
 * This class initializes a data tree based on the hierarchy detailed in the SFPark Availability REST Service API documentation.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class SFParkXMLResponse {
    // PRIVATE DATA MEMBERS AND METHODS //
    //
    /** Holds the query to passed to the SFPark Availability database */
    private String query;

    /** The status of the response.
     * Three possible values exist for this data member:
     *  - SUCCESS: database accessed and query returned successfully (official SFPark API value)
     *  - ERROR: database accessed but encountered an error with the query (official SFPark API value)
     *  - FAILURE: no data could be extracted at all (i.e. SFP_AVAILABILITY tag returns null) (custom sfparkdb API value)
     */
    private String status;
    /** The request ID of the response */
    private int requestID;
    /** The user-defined field identifier */
    private int udf1;
    /** The number of records the query returned */
    private int numRecords;
    /** The error code of a query if one exists */
    private int errorCode;
    /** The message of the response (usually states the number of records returned) */
    private String message;
    /** The time stamp of when the availability data response was updated for the query */
    private String availabilityUpdatedTimeStamp;
    /** The time stamp of when the query was received by the database*/
    private String availabilityRequestTimeStamp;
    /** Holds all availability (AVL) elements; an AVL element represents one record */
    private ArrayList<AVLElement> avlList;

    /** Resets all data members to their starting values.
     * Numerical data is set to -1; Strings and Lists are reinitialized as empty.
    */
    private void reset () {
        status = "";
        requestID = -1;
        udf1 = -1;
        numRecords = -1;
        errorCode = -1;
        message = "";
        availabilityUpdatedTimeStamp = "";
        availabilityRequestTimeStamp = "";
        avlList = new ArrayList<AVLElement>();
    }

    // PUBLIC METHODS //
    //
    /** Constructor.
     * This is the sole constructor which instantiates an empty SFParkXMLResponse object.
     * <b>Note:</b>The method {@code createResponse(String url)} or {@code createResponse(SFParkQuery query)} must be called
     * in order to populate the object with xml data.
     */
    public SFParkXMLResponse () {}

    /** Populates the SFParkXMLResponse object with data parsed from a successful SFPark Availability REST Service API query.
     *
     * @param   url a String representing a SFPark Availability REST Service API query in the form of a URL
     * @return  true if the query was successful and no exceptions were thrown, false otherwise
     */
    public boolean populate (String url) {
        try {
            // Reset all data members to their default values
            reset();

            Document document = new NetworkRequest().execute(url).get(10, TimeUnit.SECONDS);

            // Extract root element which contains all data
            Element root;
            try {
                root = (Element) document.getElementsByTagName("SFP_AVAILABILITY").item(0);
            } catch (NullPointerException e) {
                status = "FAILURE"; // FAILURE status indicates that the root element - and therefore all data within - could not be extracted
                return false;
            }

            // Extract non-AVL elements and copy their data into the appropriate data member.
            // If a tag name does not match any available element, String variables are set to empty
            // and int variables are set to -1.
            try {
                status = ((Element) root.getElementsByTagName("STATUS").item(0)).getTextContent();
                if (status.equals("ERROR")) // Return on error status
                    return false;
            } catch (NullPointerException e) {
                status = "";
            }

            try {
                requestID = Integer.parseInt(((Element)root.getElementsByTagName("REQUESTID").item(0)).getTextContent());
            } catch (NullPointerException e) {
                requestID = -1;
            }

            try {
                udf1 = Integer.parseInt(((Element)root.getElementsByTagName("UDF1").item(0)).getTextContent());
            } catch (NullPointerException e) {
                udf1 = -1;
            }

            try {
                numRecords = Integer.parseInt(((Element)root.getElementsByTagName("NUM_RECORDS").item(0)).getTextContent());
            } catch (NullPointerException e) {
                numRecords = -1;
            }

            try {
                errorCode = Integer.parseInt(((Element)root.getElementsByTagName("ERROR_CODE").item(0)).getTextContent());
            } catch (NullPointerException e) {
                errorCode = -1;
            }

            try {
                message = ((Element)root.getElementsByTagName("MESSAGE").item(0)).getTextContent();
            } catch (NullPointerException e) {
                message = "";
            }

            try {
                availabilityUpdatedTimeStamp = ((Element)root.getElementsByTagName("AVAILABILITY_UPDATED_TIMESTAMP").item(0)).getTextContent();
            } catch (NullPointerException e) {
                availabilityUpdatedTimeStamp = "";
            }

            try {
                availabilityRequestTimeStamp = ((Element)root.getElementsByTagName("AVAILABILITY_REQUEST_TIMESTAMP").item(0)).getTextContent();
            } catch (NullPointerException e) {
                availabilityRequestTimeStamp = "";
            }

            // Populate availability list
            NodeList avlNodes = root.getElementsByTagName("AVL");
            for (int i = 0; i < avlNodes.getLength(); i++) {
                avlList.add(new AVLElement((Element) avlNodes.item(i)));
            }
            // Return true upon success
            return true;
        } catch (Exception e) {
            reset();
            status = "FAILED: " + e.getClass();
            return false;
        }
    }

    /** Populates the SFParkXMLResponse object with data parsed from a successful SFPark Availability REST Service API query.
     *
     * @param   query   an SFParkQuery object containing the SFPark Availability REST Service API query
     * @return  true if the query was successful, false otherwise
     */
    public boolean populate (SFParkQuery query) {
        return populate(query.toString());
    }

    /** Returns a SFPark availability element at the specified index.
     * This method throws an IndexOutOfBoundsException if an index is invalid.
     *
     * @param   index                       index of AVLElement to be returned
     * @return                              the AVLElement stored at the index
     * @throws  IndexOutOfBoundsException   if an invalid index is passed as a parameter
     */
    public AVLElement avl (int index) {
        return avlList.get(index);
    }

    /** Returns the availability request time stamp of the response.
     *
     * @return  the availability request time stamp of the response if it exists, or an empty String otherwise
     */
    public String availabilityRequestTimeStamp () {
        return availabilityRequestTimeStamp;
    }

    /** Returns the availability updated time stamp of the response.
     *
     * @return  the availability updated time stamp of the response if it exists, or an empty String otherwise
     */
    public String availabilityUpdatedTimeStamp () {
        return availabilityUpdatedTimeStamp;
    }

    /** Returns the message of the response.
     *
     * @return  the message of the response if it exists, or an empty String otherwise
     */
    public String message () {
        return message;
    }

    /** Returns the status of the response.
     * Possible status values are:
     *  SUCCESS: database accessed and query returned successfully (official SFPark API value)
     *  ERROR: database accessed but encountered an error with the query (official SFPark API value)
     *  FAILURE: no data could be extracted at all (i.e. SFP_AVAILABILITY tag returns null) (custom sfparkdb API value)
     *
     * @return  a String containing the status if it exists, or an empty String otherwise
     */
    public String status () {
        return status;
    }

    /** Returns the request ID of the response.
     *
     * @return  the request ID of the response if it exists, or -1 otherwise
     */
    public int requestID () {
        return requestID;
    }

    /** Returns the user-defined field ID of the response.
     *
     * @return  the user-defined field ID of the response if it exists, or -1 otherwise
     */
    public int udf1 () {
        return udf1;
    }

    /** Returns the number of records of the response.
     *
     * @return  the number of records of the response if it exists, or -1 otherwise
     */
    public int numRecords () {
        return numRecords;
    }

    /** Returns the error code of the response.
     *
     * @return  the error code of the response if it exists, or -1 otherwise
     */
    public int errorCode () {
        return errorCode;
    }
}