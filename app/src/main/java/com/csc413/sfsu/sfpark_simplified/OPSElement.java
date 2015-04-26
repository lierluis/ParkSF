package com.csc413.sfsu.sfpark_simplified;

import org.w3c.dom.Element;

/** The OPSElement class deals with the storage and passing of data extracted from a SFPark operating schedule element.
 * Operating schedule elements are associated with the OPS tag name.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class OPSElement extends BranchElement {
    // PUBLIC METHODS //
    //
    /** Constructor.
     *
     * @param   elem    an org.w3c.dom.Element object with the tag name OPS, passed to the BranchElement class for data extraction
     */
    public OPSElement (Element elem) {
        super(elem);
    }

    /** Returns the start day for the schedule.
     * This data is associated with the FROM tag.
     *
     * @return  a String containing the start day for the schedule
     */
    public String from () {
        try {
            return ((DataElement)getValue("FROM")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the end day for the schedule.
     * This data is associated with the TO tag.
     *
     * @return  a String containing the end day for the schedule
     */
    public String to () {
        try {
            return ((DataElement)getValue("TO")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    /** Returns the begin time for the schedule.
     * This data is associated with the BEG tag.
     *
     * @return  a String containing the begin time for the schedule
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
     * @return  a String containing the end time for the schedule
     */
    public String end () {
        try {
            return ((DataElement)getValue("END")).getData();
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }
}
