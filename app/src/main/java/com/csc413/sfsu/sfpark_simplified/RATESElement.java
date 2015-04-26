package com.csc413.sfsu.sfpark_simplified;

import org.w3c.dom.Element;

/** The RATESElement class deals with the storage and passing of data extracted from a SFPark rates element.
 * Rates elements are associated with the RATE tag name.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class RATESElement extends BranchElement {
    // PUBLIC METHODS //
    //
    /** Constructor.
     *
     * @param   elem    an org.w3c.dom.Element object with the tag name RATES, passed to the BranchElement class for data extraction
     */
    public RATESElement (Element elem) {
        super(elem);
    }

    /** Returns a rate schedule for the parking location.
     * This data is associated with the RS tag.
     *
     * @param   index                       the index location of the desired rate schedule
     * @return                              a RSElement containing rate schedules if available
     * @throws  IndexOutOfBoundsException   if an invalid index value is passed as a parameter
     */
    public RSElement rs (int index) {
        return (RSElement)getValue(index);
    }
}
