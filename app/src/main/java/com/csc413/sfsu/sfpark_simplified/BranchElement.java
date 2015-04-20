package com.csc413.sfsu.sfpark_simplified;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/** The BranchElement class extends the SFParkElement class and provides further functionality intended for non-leaf
 * elements (specifically elements which contain additional child elements).
 *
 * @author      Jeremy Erickson
 * @version     %I%, %G%
 */
public abstract class BranchElement extends SFParkElement {
    // PRIVATE DATA MEMBERS AND METHODS //
    //
    /** Holds a list of Tuples, each containing a "key" in the form of a tag name
     * and a data value in the form of a SFParkElement object */
    private ArrayList<Tuple<String, SFParkElement>> elements;

    /** Retrieves the index of a Tuple in the elements list based on the value of the key.
     *
     * @param   key     the tag name of an element to be used as a search key
     * @return          the index location of a Tuple in the elements list if found, or -1 otherwise
     */
    private int getIndexByKey (String key) {
        for (int i = 0; i < elements.size(); i++) {
            Tuple elem = elements.get(i);
            if (elem.first().equals(key))
                return i;
        }
        return -1;
    }

    // PROTECTED METHODS //
    //
    /** Constructor.
     *
     * @param   elem    an org.w3c.dom.Element object from which to extract child elements
     */
    protected BranchElement (Element elem) {
        // Initialize tag and type
        super(elem.getTagName(), "Branch");

        // Initialize and populate entries map
        elements = new ArrayList<Tuple<String, SFParkElement>>();
        NodeList childElements = elem.getChildNodes();
        for (int i = 0; i < childElements.getLength(); i++) {
            // Check whether a leaf or branch node
            Element e = (Element) childElements.item(i);
            if (e.hasChildNodes())
                elements.add(new Tuple<String, SFParkElement>(e.getTagName(), SFParkElement.newInstanceByTag(e)));
            else
                elements.add(new Tuple<String, SFParkElement>(e.getTagName(), SFParkElement.newInstanceByTag(e)));
        }
    }

    /** Returns the number of child elements of this element.
     *
     * @return  the number of child elements of this element
     */
    protected int numChildElements () {
        return elements.size();
    }


    /** Retrieves a key from a Tuple in the elements list at the specified index.
     * The value of the key is the tag name of the element data stored in this location.
     *
     * @param   index                       the index location of the Tuple containing the desired key value
     * @return                              the key String at index
     * @throws  IndexOutOfBoundsException   if an invalid index value is passed as a parameter
     */
    protected String getKey (int index) {
        return elements.get(index).first();
    }

    /** Retrieves a value from a Tuple in the elements list at the specified index.
     * The value is a SFParkElement object representing a child element of this class.
     *
     * @param   index                       the index location of the Tuple containing the desired value
     * @return                              the SFParkElement object at index
     * @throws  IndexOutOfBoundsException   if an invalid index value is passed as a parameter
     */
    protected SFParkElement getValue (int index) {
        return elements.get(index).last();
    }

    /** Retrieves a value from a Tuple in the elements list at the specified index.
     * The value is a SFParkElement object representing a child element of this class.
     *
     * @param   key                         the tag name of the Tuple containing the desired value
     * @return                              the SFParkElement object matched to the key
     * @throws  IndexOutOfBoundsException   if the index value returned by getIndexByKey is -1 (meaning the key was not found)
     *                                      and passed as an invalid index parameter to getValue(int index)
     */
    protected SFParkElement getValue (String key) {
        return getValue(getIndexByKey(key));
    }
}
