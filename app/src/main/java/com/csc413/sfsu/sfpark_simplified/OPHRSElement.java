package com.csc413.sfsu.sfpark_simplified;

import org.w3c.dom.Element;

/** The OPHRSElement class deals with the storage and passing of data extracted from a SFPark operating hours element.
 * Data is in the form of OPSElement objects.
 * Operating hours elements are associated with the OPHRS tag name.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class OPHRSElement extends BranchElement {
    // PUBLIC METHODS //
    //
    /** Constructor.
     *
     * @param   elem    an org.w3c.dom.Element object with the tag name OPHRS, passed to the BranchElement class for data extraction
     */
    public OPHRSElement (Element elem) {
        super(elem);
    }

    /** Returns an operating schedule for the parking location.
     * This data is associated with the OPS tag.
     *
     * @param   index                       the index location of the desired operating schedule
     * @return                              an OPSElement containing operating schedules if available, or null otherwise
     * @throws  IndexOutOfBoundsException   if an invalid index value is passed as a parameter
     */
    public OPSElement ops (int index) {
        return (OPSElement)getValue(index);
    }
}