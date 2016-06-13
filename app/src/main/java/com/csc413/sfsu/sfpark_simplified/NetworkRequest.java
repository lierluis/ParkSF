package com.csc413.sfsu.sfpark_simplified;

import android.os.AsyncTask;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;

/** The NetworkRequest class allows the user to make network requests asynchronously.
 * Networking operations in Android are not permitted on the main thread, so this class is
 * necessary for their execution.
*/
public class NetworkRequest extends AsyncTask<String, Void, Document> {
    /** Creates and returns an org.w3c.dom.Document containing data parsed from a network location.
     *
     * @param   url     a String containing a URL from which to parse data
     * @return          an org.w3c.dom.Document containing data parsed from the location at url
    */
    protected Document doInBackground (String... url) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url[0]);
        } catch (Exception e) {
            return null;
        }
    }
}
