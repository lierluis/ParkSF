package com.csc413.sfsu.sfpark_simplified;

import org.w3c.dom.Element;

/** The AVLElement class deals with the storage and passing of data extracted from a SFPark availability element.
 * Availability elements are associated with the AVL tag name.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class AVLElement extends BranchElement {
    // PUBLIC METHODS //
    //
    /** Constructor.
     *
     * @param   elem    an org.w3c.dom.Element object with the tag name AVL, passed to the BranchElement class for data extraction
     */
    public AVLElement (Element elem) {
        super(elem);
    }

    /** Returns the type of parking at the location, either on street or off street.
     * This data is associated with the TYPE tag.
     *
     * @return  a String representing the type of parking (on street or off street) if available, or an empty String otherwise
     */
    public String type () {
        try {
            return ((DataElement)getValue("TYPE")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the name of the parking location (structure name or street with from and to addresses).
     * This data is associated with the NAME tag.
     *
     * @return  a String representing the name of the parking location if available, or an empty String otherwise
     */
    public String name () {
        try {
            return ((DataElement)getValue("NAME")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the description (usually the address) for the parking location.
     * This data is associated with the DESC tag.
     *
     * @return  a String representing the description/address for the parking location if available, or an empty String otherwise
     */
    public String desc () {
        try {
            return ((DataElement)getValue("DESC")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the nearby intersection to the parking location.
     * This data is associated with the INTER tag.
     *
     * @return  a String representing the nearby intersection if available, or an empty String otherwise
     */
    public String inter () {
        try {
            return ((DataElement)getValue("INTER")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the contact telephone number of the parking location.
     * This data is associated with the TEL tag.
     *
     * @return  a String representing the contact telephone number of the parking structure if it exists, or an empty String otherwise
     */
    public String tel () {
        try {
            return ((DataElement)getValue("TEL")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the off street parking ID of the parking location.
     * This data is associated with the OSPID tag.
     *
     * @return  an int representing the off street parking ID if available, or -1 otherwise
     */
    public int ospid () {
        try {
            return Integer.parseInt(((DataElement)getValue("OSPID")).getData());
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }

    /** Returns the on street block face ID of the parking location.
     * This data is associated with the BFID tag.
     *
     * @return  an int representing the on street block face ID if available, or -1 otherwise
     */
    public int bfid () {
        try {
            return Integer.parseInt(((DataElement)getValue("BFID")).getData());
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }

    /** Returns the number of spaces currently occupied at the parking location.
     * This data is associated with the OCC tag.
     * <b>Note (per the SFPark Availability Service API Reference):</b> Effective Dec 30, 2013 for on street
     * parking locations, this will be sent as 0 if all spaces are currently Restricted, e.g., Tow-away or not sent at all.
     * See the SFPark website for more info: www.sfpark.org
     *
     * @return  an int representing the number of spaces occupied if available, or -1 otherwise
     */
    public int occ () {
        try {
            return Integer.parseInt(((DataElement)getValue("OCC")).getData());
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }

    /** Returns the number of spaces currently operational at the parking location.
     * This data is associated with the OPER tag.
     * <b>Note (per the SFPark Availability Service API Reference):</b> Effective Dec 30, 2013 for on street
     * parking locations, this will be sent as 0 if all spaces are currently Restricted, e.g., Tow-away or not sent at all.
     * See the SFPark website for more info: www.sfpark.org
     *
     * @return  an int representing the number of spaces operational if available, or -1 otherwise
     */
    public int oper () {
        try {
            return Integer.parseInt(((DataElement)getValue("OPER")).getData());
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }

    /** Returns the number of location points for the parking location.
     * There is usually one point for off street and two points for on street parking.
     * This data is associated with the PTS tag.
     *
     * @return  an int representing the number of location points if available, or -1 otherwise
     */
    public int pts () {
        try {
            return Integer.parseInt(((DataElement)getValue("PTS")).getData());
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }

    /** Returns the longitudinal and latitudinal coordinates of the parking location.
     * There is usually one pair of coordinates for off street and two pairs for on street parking.
     * This data is associated with the LOC tag.
     *
     * @return  a SFParkLocation object containing one or more pairs of coordinates if they exist,
     *          an empty SFParkLocation object if no coordinates exist,
     *          or null otherwise
     */
    public SFParkLocation loc () {
        try {
            return new SFParkLocation(((DataElement)getValue("LOC")).getData());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /** Returns the operating hours of the parking location.
     * There may be 1 or more operating schedules associated with this location.
     * This data is associated with the OPHRS tag.
     *
     * @return  an OPHRSElement containing operating schedules if available, or null otherwise
     */
    public OPHRSElement ophrs () {
        try {
            return (OPHRSElement)getValue("OPHRS");
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /** Returns the parking rates for the parking location.
     * This data is associated with the RATES tag.
     *
     * @return  a RSElement containing rate schedules if available, or null otherwise
     */
    public RATESElement rates () {
        try {
            return (RATESElement)getValue("RATES");
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}

