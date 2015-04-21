# SFPark Simplified API
## Description
The SFPark Simplified API provides access to the SFPark Availability database by sending queries 
and extracting the data from responses in a simple and easy-accessible format.
## Author
Jeremy Erickson
## Package name
com.csc413.sfsu.sfpark_simplified
## Included classes
<ul>
<li>AVLElement</li>
<li>BranchElement</li>
<li>DataElement</li>
<li>NetworkRequest</li>
<li>OPHRSElement</li>
<li>OPSElement</li>
<li>RATESElement</li>
<li>RSElement</li>
<li>SFParkElement</li>
<li>SFParkLocation</li>
<li>SFParkQuery</li>
<li>SFParkXMLResponse</li>
<li>Tuple</li>
</ul>
## See also
<a href="http://www.sfpark.org">SFPark Availability Service API</a>
## Overview
The basic idea behind the SFPark Simplified API is to create and store queries in <b>SFParkQuery</b> objects and pass 
them to a <b>SFParkXMLResponse</b> object which establishes a connection with the SFPark Availability database, passes the query, retrieves the data from the response, and extracts it into a convenient format.

SFParkXMLResponse objects are instantiated empty and then subsequently "populated" with data by calling the 
<code>populate()</code> method; this method may also be called any time the user wishes to update the response 
by overwriting the previous information with that of a new query. Regardless of the degree of success the method has 
in populating the response, at minimum the status variable will be updated to reflect the state of the most recent 
database query. The user may call the SFParkXMLResponse object's <code>status()</code> method to retrieve this value.

With the exception of the root element (the <b>SFP_AVAILABILITY</b> element, to be specific) which is stored in a 
SFParkXMLResponse object, all data is stored in <b>SFParkElement</b> objects, which are abstract classes that are 
extended by the <b>BranchElement</b> class (an abstract class which stores non-leaf elements, i.e. elements with one 
or more child elements) and the <b>DataElement</b> class (a public class which stores leaf elements).
DataElements contain textual data in the form of Strings, but may return their data to the user in a different 
format depending on the accessor called. Such formats may include:
<ul>
  <li>String objects (for messages, status, etc.)</li>
	<li>Numeric data types (for IDs, error codes, etc.)</li>
	<li><b>SFParkLocation</b> objects (for holding longitude and latitude locations; see below for further information on this class)</li>
</ul>
Any extending class of BranchElement contains and returns data in the form of SFParkElements, which may include BranchElements, DataElements, or both. There are currently five classes that extend BranchElement:
<ul>
	<li><b>AVLElement:</b> 
	</br>availability element, which is the top level container for all data at a given location. Availability elements are synonymous with "<b>records</b>" when referred to by the SFPark Availability Service API</li>
	<li><b>OPHRSElement:</b> 
	</br>operating hours element, which contains operating schedules</li>
	<li><b>OPSElement:</b> 
	</br>operating schedule element, which contains parking structure operating schedules</li>
	<li><b>RATESElement:</b> 
	</br>rates element, which contains rate schedules</li>
	<li><b>RSElement:</b> 
	</br>rate schedule element, which contains parking structure rate schedules</li>
</ul>
Each of these classes corresponds to an element with the same tag name as the class name prefix (for instance the 
AVLElement class corresponds to <b>AVL</b> elements in the SFPark Availability Service API, RSElements correspond to 
<b>RS</b> elements, and so on) and contains unique accessors for each of their child elements. It is highly recommended that the user familiarize him/herself with the SFPark Availability Service API XML response hierarchy <i>(section 3.1 XML Response, pg 11)</i>, as this will make it much easier to interpret the SFPark Simplicity API hierarchy since it directly models the former.

Finally, location data is stored in SFParkLocation objects, which are intended to contain either one or two 
longitude/latitude coordinate pairs <i>(since any location extracted from the SFPark Availability database will contain 
either one or two pairs)</i> but may in fact contain none at all or (theoretically) an unlimited number.
The number of locations stored in a SFParkLocation object may be easily retrieved, as well as the longitude and 
latitude values, which are accessed "list" style (eg. <code>SFParkLocation.longitude(index)</code> or 
<code>SFParkLocation.latitude(index)</code>).

## Getting started
The following steps describe how to get started with the SFPark Simplified API:

<ol>
<li><b>Create a query:</b>
 </br>Create a new SFParkQuery instance, which will by default have no parameters.</li>
 </br>
	
<li><b>Modify query parameters:</b>
</br>Append to, update, or delete from the SFParkQuery instance any desired parameters using the <code>addParameter()</code>, <code>updateParameter()</code>, <code>addOrUpdateParameter()</code>, or <code>removeParameter()</code> methods. Consult the official SFPark Availability Service documentation for a list of valid parameters, as invalid parameters will return an error status.</li>
</br>
	
<li><b>Create a container to hold the data from the response</b>
</br>Create a new SFParkXMLResponse instance, which will by default be unpopulated.</li>
</br>
	
<li><b>Pass the query to the database and retrieve the response:</b>
</br>Populate the SFParkXMLResponse instance by passing the SFParkQuery created above to the <code>populate()</code> method. It is a good idea to check the value returned by the <code>status()</code> method to determine whether the SFParkXMLResponse object was successfully populated. Attempts to access data from this object on a status other than SUCCESS could result in an Exception being thrown (typically an IndexOutOfRangeException or NullPointerException).</li>
</br>
	
<li><b>Access the data</b>
</br>Once populated successfully, the user may now access the data with the appropriate accessor methods; the naming convention for the accessors is, with a few small exceptions, the exact lowercase equivalent of the SFPark Availability Service API element tag names, with underscore separators being replaced by capital letter separators. So to access an element with the tag name DESC, for instance, you would call the <code>desc()</code> method, or <code>rr()</code> for a RR element; to access an element with the tag name AVAILABILITY_REQUEST_TIMESTAMP, the user would call <code>availabilityRequestTimestamp()</code>, and so forth. The accessor naming convention is the same for both leaf and non-leaf elements, so to access an AVL element, the <code>avl(int index)</code> accessor can be called. To access a child element of a non-root branch element such as an AVL, a simple dot-sytax chain is all that is needed; for instance, <code>avl(index).ophrs(index).end()</code> accesses the data from an END element of an OPS element, which is contained in the OPHRS element of an AVL element. Again, the user is encouraged to consult the official documentation <i>(section 3.1 XML Response, pg 11)</i> for the breakdown of the different SFPark elements and their hierarchy.</li>
</ol>

<b>Example usage:</b>
</br>
<pre style="background-color:lightgray">
SFParkQuery query = new SFParkQuery(); /* Create empty query */

query.addParameter("long", "-122.98880"); /* Add parameters */
query.addParameter("lat", "37.8979");
query.addParameter("radius", "0.5");
query.addParameter("uom", "mile");
query.addParameter("response", "xml");


SFParkXMLResponse response = new SFParkXMLResponse(); /* Create empty response */
boolean success = response.populate(query); /* Populate the response with the query */

/* Access response only on successful population to avoid NullPointerException */
if (success) { 
    /* Access and retrieve data from response */
    String status = response.status();
    String message = response.message();
    int numRecords = response.numRecords();
    
    /* Availability elements (records) may be accessed indexically */
    for (int i = 0; i < numRecords; i++) { 
    	 /* Print each record name, for example */
    	System.out.println("Record #" + (i+1) + " name: " + response.avl(i).name());
    }
    
    /* Etc... */
}


query.removeParamter("response"); /* Default response is XML, no need for parameter */
query.updateParameter("radius", "0.75"); /* Widen the search radius */

success = response.populate(query); /* Repopulate the response with new query */

/* Access data if successfully populated... */
</pre>



## Element hierarchy
The following is a general tree structure showing the SFPark Availability Service element hierarchy;
the names in parentheses are the SFPark Simplified classes that hold the corresponding elements:
<ol>
  <li>SFP_AVAILABILITY (<code>SFParkXMLResponse</code>)</li>
  <ol>
		<li>STATUS</li>
		<li>REQUEST_ID</li>
		<li>UDF1</li>
		<li>NUM_RECORDS</li>
		<li>ERROR_CODE</li>
		<li>MESSAGE</li>
	  <li>AVAILABILITY_UPDATED_TIMESTAMP</li>
		<li>AVAILABILITY_REQUEST_TIMESTAMP</li>
		<li>AVL (<code>AVLElement</code>)</li>
		<ol>
			<li>TYPE</li>
			<li>NAME</li>
			<li>DESC</li>
			<li>INTER</li>
			<li>TEL</li>
			<li>OSPID</li>
			<li>BFID</li>
			<li>OCC</li>
			<li>OPER</li>
			<li>PTS</li>
			<li>LOC</li>
			<li>OPHRS (<code>OPHRSElement</code>)</li>
			<ol>
				<li>OPS (<code>OPSElement</code>)</li>
				<ol>
					<li>FROM</li>
					<li>TO</li>
					<li>BEG</li>
					<li>END</li>
				</ol>
				<li>RATES (<code>RATESElement</code>)</li>
				<ol>
					<li>RS (<code>RSElement</code>)</li>
					<ol>
						<li>BEG</li>
					  <li>END</li>
						<li>RATE</li>
						<li>DESC</li>
						<li>RQ</li>
						<li>RR</li>
					</ol>
				</ol>
			</ol>
		</ol>
	</ol>
</ol>

## Known issues
[No currently known issues]
