package com.csc413.sfsu.sfpark_simplified;

import org.w3c.dom.Element;

/** The DataElement class is a wrapper for textual data that is intended to work with the SFPark Simplified API.
 * This class works only with leaf elements (that is elements with no child elements); branch (or parent) elements contain
 * no textual data and will cause a ClassCastException to be thrown if passed to the constructor.
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public class DataElement extends SFParkElement {
    // PRIVATE DATA MEMBERS //
    //
    /** Textual data of the DataElement */
    private final String data;

    // PUBLIC METHODS //
    //
    /** Constructor.
     *
     * @param   elem                            an org.w3c.dom.Element that contains textual data to be parsed
     * @throws  ClassCastException              if a non-leaf element is passed to the constructor
     * @throws  org.w3c.dom.DOMException        if an error occurs while getting text content from elem
     * @see     org.w3c.dom.Node#getTextContent
     */
    public DataElement (Element elem) {
        super(elem.getTagName(), "Data");
        data = elem.getTextContent();
    }

    /** Returns the data held by the DataElement.
     *
     * @return  the DataElement's data in the form of a String
     */
    public String getData () {
        return data;
    }
}
