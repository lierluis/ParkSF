package com.csc413.sfsu.sfpark_simplified;

import org.w3c.dom.Element;

/** The SFParkElement class provides a fundamental implementation  and generic representation
 * of all extending "element" classes used with the SFPark Simplified API.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public abstract class SFParkElement {
    // PRIVATE DATA MEMBERS //
    //
    /** Tag name of the SFParkElement object */
    private final String tag;
    /** Denotes whether a BranchElement or DataElement, both of which extend this class */
    private final String category;

    // PROTECTED METHODS //
    //
    /** Initializes and returns an appropriate instance of one of several child classes of SFParkElement.
     * The exact class is chosen in accordance with the tag name of an org.w3c.dom.Element object:
     *  - AVL   --  AVLElement
     *  - OPHRS --  OPHRSElement
     *  - OPS   --  OPSElement
     *  - RATES --  RATESElement
     *  - RS    --  RSElement
     *
     * @param   e   an org.w3c.dom.Element object from which to parse a tag name;
     *              the Element is then passed as a parameter to the constructor of the appropriate child class.
     * @return      an instance of a child class of SFParkElement as a SFParkElement object;
     *              this should be cast as the appropriate child class after return.
     */
    protected static SFParkElement newInstanceByTag (Element e) {
        switch (e.getTagName()) {
            case "AVL": // Availability element
                return new AVLElement(e);
            case "OPHRS": // Operating hours element
                return new OPHRSElement(e);
            case "OPS": // Operating schedule element
                return new OPSElement(e);
            case "RATES": // Rates element
                return new RATESElement(e);
            case "RS": // Rate schedule element
                return new RSElement(e);
            default: // Data element otherwise
                return new DataElement(e);
        }
    }

    /** Constructor.
     *
     * @param   tag         the tag name of the SFParkElement
     * @param   category    "Branch" for BranchElements or "Data" for DataElements
     */
    protected SFParkElement (String tag, String category) {
        this.tag = tag;
        this.category = category;
    }

    /** Returns the tag for this element.
     *
     * @return  the tag for this element
     */
    protected String getTag () {
        return tag;
    }

    /** Returns the category of this element ("Branch" or "Data")
     *
     * @return  the category of this element
     */
    protected String getCategory () {
        return category;
    }
}