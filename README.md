# CSC413_Parking

SFSU CSC413 Android mobile application that finds information on parking locations around the downtown area of San Francisco, CA.

See the extensive documentation
<a href="hrefhttps://docs.google.com/document/d/1Tja8cUpdISC0JKpar1c3Pj2pLt2VSHS2GqtcYhVEtUo/edit?usp=sharing">here</a>.

User Interface:
- Splash Page
- Map Activity

<hr>

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

<h4>Querying the database</h4>
SFParkXMLResponse objects are instantiated empty and then subsequently "populated" with data by calling the 
<code>populate()</code> method; this method may also be called any time the user wishes to update the response 
by overwriting the previous information with that of a new query. Once the method is called, the status variable will be updated to reflect the state of the most recent database query. The user may call the SFParkXMLResponse object's <code>status()</code> method to retrieve this value, which will be one of the following:
<ul>
	<li><b>SUCCESS:</b> the response was populated with data from a successful database query</li>
	<li><b>ERROR:</b> a connection was established with the database but no data could be retrieved</li>
	<li><b>FAILED:<i>[Exception class]:</i></b> an Exception was thrown before the response could populate</li>
</ul>
Note that a status of SUCCESS does not guarantee that any records were found, only that the database was accessed and that data was retrieved. An example would be queries with location parameters outside of the SFPark Availability Service's range. 

<h4>Handling data</h4>
With the exception of the root element (the <b>SFP_AVAILABILITY</b> element, to be specific) which is stored in a 
SFParkXMLResponse object, all data is stored in <b>SFParkElement</b> objects, which are of an abstract class that is
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
<b>RS</b> elements, and so on) and contains unique accessors for each of their child elements. It is highly recommended that the user familiarize him/herself with the SFPark Availability Service API XML response hierarchy <i>(section 3.1 XML Response, pg 11)</i>, as this will make it much easier to interpret the SFPark Simplified API hierarchy since it directly models the former.

Finally, location data is stored in SFParkLocation objects, which are intended to contain either one or two 
longitude/latitude coordinate pairs <i>(since any location extracted from the SFPark Availability database will contain 
either one or two pairs)</i> but may in fact contain none at all or (theoretically) an unlimited number.
The number of locations stored in a SFParkLocation object may be easily retrieved, as well as the longitude and 
latitude values, which are accessed "list" style (eg. <code>SFParkLocation.longitude(index)</code> or 
<code>SFParkLocation.latitude(index)</code>).

<h4>Default values</h4>
Every data member of an element is assigned a default value in cases where either no corresponding data was available from the query response or an error occurred while retrieving it from the element. Default values include:
<ul>
	<li><b>-1</b> for numerical data (no numerical data member will ever have a valid value below 0)</li>
	<li><b>empty String </b> for textual data</li>
	<li><b>null</b> for other Object data</li>
</ul>
It is good practice to always check data members against their default values to determine whether their data is valid. Some data members may also throw Exceptions if their values cannot be parsed, so the user should consult the Javadoc to get an idea of such potential cases.

## Getting started
The following steps describe how to get started with the SFPark Simplified API:

<ol>
<li><b>Create a query:</b>
 </br>Create a new SFParkQuery instance, which will by default have no parameters.</li>
 </br>
	
<li><b>Modify query parameters:</b>
</br>Append to or update the SFParkQuery instance any desired parameters calling mutator methods respective to each query parameter. Parameters may also be removed by calling "resetter" methods, also respective to each query parameter. Manipulable query parameters for which such methods exist are:
<ul>
	<li><b>REQUESTID:</b> Request ID</li>
	<li><b>LONGITUDE:</b> Longitude</li>
	<li><b>LATITUDE:</b> Latitude</li>
	<li><b>RADIUS:</b> Search Radius</li>
	<li><b>UOM:</b> Unit of Measurement</li>
	<li><b>TYPE:</b> Parking Type</li>
	<li><b>PRICING:</b> Pricing Information</li>
	<li><b>UDF1:</b> User Defined Field #1</li>
</ul>
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

<h4>Example usage:</h4>
<pre style="background-color:lightgray">
SFParkQuery query = new SFParkQuery(); <i>/* Create empty query */</i>

query.setLongitude(-122.98880); <i>/* Add parameters */</i>
query.setLatitude(37.8979);
query.setRadius(0.5);
query.setUnitOfMeasurement("MILE");
query.setPricingInformation("YES");

SFParkXMLResponse response = new SFParkXMLResponse(); <i>/* Create empty response */</i>
boolean success = response.populate(query); <i>/* Populate the response with the query */</i>

String status = response.status(); <i>/* Status will have a valid value regardless of success */</i>

<i>/* It is a good practice to only access data from a successful query */</i>
if (success) {
    String message = response.message();
    int numRecords = response.numRecords();
    <i>/* Availability elements (records) may be accessed indexically */</i>
    for (int i = 0; i < numRecords; i++) { 
        <i>/* Print each record name, for example */</i>
    	System.out.println("Record #" + (i+1) + " name: " + response.avl(i).name());
    }
    
    <i>/* Etc... */</i>
}

query.resetPricingInformation(); <i>/* Reset parameter to default value */</i>
query.setRadius(0.75); <i>/* Widen the search radius */</i>

success = response.populate(query); <i>/* Repopulate the response with new query */</i>

<i>/* Access data if successfully populated... */</i>
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

</br>
<hr>
# SF Vehicle Crime API
## Description
The SF Vehicle Crime API provides access to the San Francisco Crimespotters database by sending queries 
and extracting the data from responses in a simple and easy-accessible format.
## Author
Jeremy Erickson
## Package name
com.csc413.sfsu.sf_vehicle_crime
## Included classes
<ul>
	<li>EmptyResponseException</li>
	<li>SFCrimeHandler</li>
	<li>SFCrimeQuery</li>
	<li>SFCrimeXMLResponse</li>
</ul>
## See also
<a href="http://sanfrancisco.crimespotting.org/api">San Francisco Crimespotters API</a>
## Overview
The SF Vehicle Crime API operates on similar principles to the SFPark Simplified API, in that queries are sent to a database and the results are retrieved and extracted into a convenient format. 
Unlike the SFPark Simplified API, however, the SF Vehicle Crime API combines queries and responses into a single handler class called <code>SFCrimeHandler</code>. While this API does contain separate query and response classes, these classes are not public, and all access of the database by the user must go through the <code>SFCrimeHandler</code>'s interface.

Another notable difference is the limited scope of the API; while the SFPark Simplified API grants full access to all data fields in the SFPark Availability API, the SF Vehicle Crime API returns only data that is pertinent to vehicular crimes and excludes all other crime types (narcotics or arson, to name a few). In addition to limiting the type of crimes returned, the API also stores only two data fields associated with vehicular crimes: a crime's <b>date</b> and <b>location</b>; the date is a String returned to the user in a <i>YEAR-MONTH-DAY</i> format, with leading zeros where appropriate (eg. "01" instead of "1", etc.), and the location is returned in the form of a <code>com.google.android.gms.maps.model.LatLng</code> object.

<h4>Querying the database</h4>
Modifiable query parameters for the SF Vehicle Crime API are as follows:
<ul>
	<li>The location origin</li>
	<li>The radius from the origin, in miles (no other unit of measurement is currently supported)</li>
	<li>The number of reports to return</li>
	<li>The offset in the list of query results from which to start returning reports (eg. an offset of 5 would return reports beginning with the sixth item on the list)</li>
	<li>The earliest year from which to return reports</li>
</ul>
The current defaults for the above parameters if none are specified are:
<ul>
	<li>Location origin & radius = <b>all of San Francisco</b> (either both or neither must be defaulted)</li>
	<li>Number of reports returned = <b>20</b></li>
	<li>Offset = <b>0</b></li>
	<li>Starting year = <b>1 year before the current</b></li>
</ul>

Querying and accessing data is achieved with three simple methods from the <code>SFCrimeHandler</code> class:
<ol>
	<li>
		<code>generateReports(LatLng origin, double radius, int startYear, int count, int offset)</code>: queries, retrieves, and stores crime data.
		<ul>
			<li><code>origin</code>: a LatLng object denoting the origin of the query; defaults if set to <code>null</code></li>
			<li><code>radius</code>: the radius from the origin from which to retrieve reports, in miles; defaults if set to <= 0 or >= bounds of San Francisco</li>
			<li><code>startYear</code>: the year from which to start returning reports; defaults if set to < 0 or > 9999</li>
			<li><code>count</code>: the number of reports to return; defaults if < 1 or > 10000</li>
			<li><code>offset</code>: the offset from the beginning of the report list from which to begin returning data; defaults if set to < 0 or > 9999</li>
		</ul>
	</li>
	<li><code>date(int index)</code>: returns the date for the crime at the given <code>index</code></li>
	<li><code>location(int index)</code>: returns the location for the crime at the given <code>index</code></li>
</ol>
A successful call to <code>generateReports</code> will return a boolean value of <code>true</code> and set the handler's <code>status</code> variable to a value of <i>SUCCESS</i>; such a status indicates that the query was valid and the database was accessed, even if no results were returned. A status of <i>FAILURE</i> indicates that an error was encountered while attempting to access the database.
The status of the latest query may be retrieved by calling the handler class's <code>status()</code> method.
Additionally, the number of reports returned on a successful query may be retrieved with the handler class's <code>numReports()</code> method; this will allow the user to iterate through all reports indexically. 

One additional modifiable variable is the <code>timeout</code> variable; by calling the handler class's <code>setTimeout(int seconds)</code> method, the user may specify the number of seconds the query will attempt to access the database before timing out, after which the query will return failed status.

Lastly, the <code>EmptyResponseException</code> class, which extends <code>java.lang.Exception</code>, is thrown by the handler class in the event that the user attempts to access either the date or location of a report from a response that has either been initialized but not yet populated or has returned a failed status on its latest query.

<h4>Example usage:</h4>
<pre style="background-color:lightgray">
SFCrimeHandler crimeHandler = new SFCrimeHandler(); <i>/* Initialize empty handler */</i>
boolean success = crimeHandler.generateReports(null, -1, -1, -1, -1); <i>/* Generate reports with all default values */</i>

<i>/* It is good practice to access data only from a successful query */</i>
if (success) {
    <i>/* Iterate through reports returned */</i>
    for (int i = 0; i < crimeHandler.numReports(); i++) {
        <i>/* Print the location and date of each, for instance */</i>
        System.out.println("Report #: " + (i+1));
        System.out.println("Date: " + crimeHandler.date(i));
        System.out.println("Location: " + crimeHandler.location(i));
    }
}

LatLng origin = new LatLng(37.728271, -122.433385); <i>/* Create a new LatLng object to pass to the handler */</i>
double radius = 0.5; <i>/* Radius from the origin in miles */</i>
int startYear = 2011; <i>/* Retrieve reports as far back as 2011 */</i>
int count = 1000; <i>/* Grab the first 1000 reports */</i>
int offset = 200; <i>/* Start after the 200th report */</i>

crimeHandler.setTimeout(30); <i>/* Increase the number of seconds before timeout from 20 to 30 */</i>

<i>/* Generate a new report list with the new parameters */</i>
success = crimeHandler.generateReports(origin, radius, startYear, count, offset);

<i>/* Retrieve report data on a successful query */</i>
if (success) {
    <i>/* The number of reports returned will be at most "count", but may be fewer given narrowed parameter values */</i>
    System.out.println("Number of reports: " + crimeHandler.numReports());
}

crimeHandler = new SFCrimeHandler(); <i>/* Reset the handler */</i>

<i>/* Etc... */</i>
</pre>
<h4>

<h4>Radius data is approximate!</h4>
One final noteworthy item is the way in which the SF Vehicle Crime API handles the calculations for the search radius. The San Francisco Crimespotters API does not support a circular radius query parameter; instead, query bounds are in the form of a square <i>"bounding box"</i> as defined by a north-to-south latitude range and an east-to-west longitude range. The SF Vehicle Crime API uses an <i>approximate mapping</i> of latitude and longitude degrees to miles in the San Francisco area to calculate this bounding box based on a radius value, given in miles. As such, the query bounds are likely to vary to some (hopefully small) degree from the exact radius value passed by the user.

## Known issues
[No currently known issues]
