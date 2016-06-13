package com.csc413.sfsu.sfpark_simplified;

import org.w3c.dom.Element;

/** The RSElement class deals with the storage and passing of data extracted from a SFPark rate schedule element.
 * Rate schedule elements are associated with the RS tag name.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class RSElement extends BranchElement {
    // PUBLIC METHODS //
    //
    /** Constructor.
     *
     * @param   elem    an org.w3c.dom.Element object with the tag name RS, passed to the BranchElement class for data extraction
     */
    public RSElement (Element elem) {
        super(elem);
    }

    /** Returns the begin time for the schedule.
     * This data is associated with the BEG tag.
     *
     * @return  a String containing the begin time for the schedule if available, or an empty String otherwise
     */
    public String beg () {
        try {
            return ((DataElement)getValue("BEG")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the end time for the schedule.
     * This data is associated with the END tag.
     *
     * @return  a String containing the end time for the schedule if available, or an empty String otherwise
     */
    public String end () {
        try {
            return ((DataElement)getValue("END")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the applicable rate for the schedule.
     * This data is associated with the RATE tag.
     *
     * @return  a String containing the applicable rate for the schedule if available, or an empty String otherwise
     */
    public String rate () {
        try {
            return ((DataElement)getValue("RATE")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the descriptive rate for the schedule.
     * This data is associated with the DESC tag.
     *
     * @return  a String containing the descriptive rate for the schedule if available, or an empty String otherwise
     */
    public String desc () {
        try {
            return ((DataElement)getValue("DESC")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the rate qualifier for the schedule (eg Per Hr).
     * This data is associated with the RQ tag.
     *
     * @return  a String containing the rate qualifier for the schedule if available, or an empty String otherwise
     */
    public String rq () {
        try {
            return ((DataElement)getValue("RQ")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns any rate restriction for the schedule.
     * This data is associated with the RR tag.
     *
     * @return  a String containing a rate restriction for the schedule if available, or an empty String otherwise
     */
    public String rr () {
        try {
            return ((DataElement)getValue("RR")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }
}
