package com.csc413.sfsu.sfpark_simplified;

/** The Tuple class represents a simple pair of non-mapped objects.
 * Objects may either be of the same or separate classes.
 *
 * @author  Jeremy Erickson
 */
public class Tuple <X, Y> {
    /** Represents the first item in the pair. */
    private X first;
    /** Represents the last item in the pair. */
    private Y last;

    /** Constructor
     *
     * @param   first   an object of class X as the first item in the pair
     * @param   last    an object of class Y as the second item in the pair
     */
    public Tuple (X first, Y last) {
        this.first = first;
        this.last = last;
    }

    // ACCESSORS

    /** Returns the first item in the pair.
     *
     * @return  first
     */
    public X first () {
        return first;
    }
    /** Returns the last item in the pair.
     *
     * @return  last
     */
    public Y last () {
        return last;
    }

    // MUTATORS

    /** Sets the value for the first item in the pair.
     *
     * @param   newFirst    an object of class X
     */
    public void setFirst (X newFirst) {
        first = newFirst;
    }
    /** Sets the value for the last item in the pair.
     *
     * @param   newLast an object of class Y
     */
    public void setLast (Y newLast) {
        last = newLast;
    }

    @Override
    public String toString () {
        return "(" + first.toString() + "," + last.toString() + ")";
    }
}