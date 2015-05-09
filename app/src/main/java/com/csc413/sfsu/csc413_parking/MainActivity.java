package com.csc413.sfsu.csc413_parking;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.location.Location;
import android.location.LocationManager;

import com.csc413.sfsu.sfpark_simplified.SFParkQuery;
import com.csc413.sfsu.sfpark_simplified.SFParkXMLResponse;
import com.csc413.sfsu.sf_vehicle_crime.*;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Luis Estrada + UI Team (Jonathan Raxa & Ishwari)
 * Class: CSC413
 */
public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
         {

    public GoogleMap theMap;
    private SFParkQuery query;
    private SFParkXMLResponse response;

    private LocationManager locMan;
    ArrayList<Marker> userMarkers = new ArrayList<>();

    private Marker userMarker; // user position
    private Marker userMarker2; // car position

//    private Marker userMarker3; // test

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private TextView mLocationView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    LatLng lastLatLng; // last known location

    // navigation drawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    int previousPosition; // keeps track of which item in navigation drawer is already selected





    /**
     * Where activity is initialized
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (servicesOK()) {

            if (initMap()) {
                Toast.makeText(this, "Ready to park!", Toast.LENGTH_SHORT).show();
                mLocationView = new TextView(this);
            } else {
                Toast.makeText(this, "Map Unavailable!", Toast.LENGTH_SHORT).show();
            }

        }

        setActionBar();
        setMap();
        updatePlaces();

        query = new SFParkQuery();
        query.setLatitude(37.792275);
        query.setLongitude(-122.397089);
        query.setRadius(0.5);
        query.setUnitOfMeasurement("MILE");
        response = new SFParkXMLResponse();

        SFParkLocationFactory locationFactory=new SFParkLocationFactory(this);
        LatLng origin=new LatLng(37.792279, -122.39709);
        double radius=.25;
        List<ParkingLocation> parkingList=new ArrayList<ParkingLocation>();
        parkingList=locationFactory.getParkingLocations(origin, radius);

        Toast.makeText(getBaseContext(), "PARKING LIST SIZE: " + parkingList.size(), Toast.LENGTH_SHORT).show();

        theMap.addMarker(new MarkerOptions()
                .position(parkingList.get(1).getOriginLocation())
                .title("Origin")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));



        for (int i = 0; i < parkingList.size(); i++) {

            // populate the parking information here
            theMap.addMarker(new MarkerOptions()
                    .position(parkingList.get(i).getCoords())
                    .title("Parking Spot")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .snippet("Parking location: " + i + "\n"
                            + parkingList.get(i).toString()));
        }

        /* navigation drawer stuff */
        String[] mNavigationTitles = getResources().getStringArray(R.array.nav_titles_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
                this,             // host Activity
                mDrawerLayout,    // DrawerLayout object
                R.string.drawer_open,    // "open drawer" description for accessibility
                R.string.drawer_close   // "close drawer" description for accessibility
        ) {

            // called when drawer has settled in a completely closed state
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle(mTitle);
            }

            // called when drawer has settled in a completely open state
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle(mDrawerTitle);

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
           selectItem(0);
        }
    }

    public void setMap() {
         /* map stuff */
        theMap.setMyLocationEnabled(true);
        theMap.setIndoorEnabled(false);
        theMap.setBuildingsEnabled(false); // optional for user in filter
        theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        theMap.getUiSettings().setZoomControlsEnabled(true);

    }

public void setActionBar(){
         /* action bar stuff */
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
    //getSupportActionBar().setTitle("Parking");
    getSupportActionBar().setSubtitle("CSC413");
    //getSupportActionBar().setDisplayUseLogoEnabled(true); // show logo
    //getSupportActionBar().setDisplayHomeAsUpEnabled(true); // if child page

}
    /**
     * The click listener for ListView in the navigation drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           selectItem(position);
        }
    }

    /**
     * Select item in navigation drawer
     *
     * @param position of item in navigation drawer
     */
    private void selectItem(int position) {
        // update selected item, then close the drawer
        mDrawerList.setItemChecked(position, true);
        switch (position) {

            case 0:
                if (position != previousPosition) {
                    Intent intent = new Intent(MainActivity.this, Settings.class);
                    MainActivity.this.startActivity(intent); // starting settings activity
                }
                break;
            case 1:
                break;
            default:
        }
        previousPosition = position; // keeps track of position so item can't be selected twice
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Prints message if connected to location services
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected to location services", Toast.LENGTH_SHORT).show();
    }

    /**
     * Implementing the location listener
     *
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        // Log.i(TAG, "GoogleApiClient connection has been suspend");
        Toast.makeText(this, "Connected to location services", Toast.LENGTH_SHORT).show();
        LocationRequest request = LocationRequest.create();

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(60000); //every 5 seconds
        request.setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //  Log.i(TAG, "GoogleApiClient connection has failed");
    }

    /**
     * Prints location coordinates
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        // mLocationView.setText("Location received: " + location.toString());
        String msg = "Location: " + location.getLatitude() + "," + location.getLongitude();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks if connection to Google Play Services is successful
     *
     * @return true if connection is successful, false if not
     */
    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Initialize the map object
     *
     * @return true if map is not null
     */
    private boolean initMap() {
        if (theMap == null) {
            SupportMapFragment mapFrag =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            theMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            //theMap = mapFrag.getMap();
        }
        if (theMap != null) {
            theMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                    TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);

                    // gets latitude and longitude
                    LatLng ll = marker.getPosition();

                    tvLocality.setText(marker.getTitle());
                   tvLat.setText("Latitude: " + ll.latitude);
                   tvLng.setText("Longitude: " + ll.longitude);
                    tvSnippet.setText(marker.getSnippet());

                    return v;
                }
            });
        }
        return (theMap != null);
    }



    /**
     * Updates location, including marker position
     */
    private void updatePlaces() {
        //update location
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();

        //LatLng lastLatLng = new LatLng(lat, lng);
        lastLatLng = new LatLng(lat, lng);

        if (userMarker != null) userMarker.remove();

        userMarker = theMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("User Location")
                .draggable(true));

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(lastLatLng, 14);
        theMap.moveCamera(update);
        theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
    }

    /**
     * Initialize the contents of the Activity's standard options menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater mif = getMenuInflater();
        mif.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * Called when user clicks on icon in action bar
     *
     * @param item - item selected on action bar
     * @return - true if option selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // if navigation drawer button clicked on
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        // checks which action bar icon is clicked on
        switch (item.getItemId()) {

            // search
            case R.id.search_icon:
                return true;

            /*
             * checkbox logic:     if item is already checked, un-check item.
             *                     else if item is not checked, check item.
             * radio button logic: if item is already selected, disallow re-selecting (do nothing)
             *                     else if item is not selected, select item
             */

            // filter (checkbox)
            case R.id.filter_1: // parking likelihood rating
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), R.string.filter_1, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.filter_2: // parking restriction
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), R.string.filter_2, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.filter_3: // parking safety
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), R.string.filter_3, Toast.LENGTH_SHORT).show();
                    parkingSafteyStart();
                    //crime.endCrimeSettings();
                }
                return true;
            case R.id.filter_4: // parking structure
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), R.string.filter_4, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.filter_5: // traffic
                if (item.isChecked()) {
                    item.setChecked(false);
                    theMap.setTrafficEnabled(false);
                } else {
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), R.string.filter_5, Toast.LENGTH_SHORT).show();
                    theMap.setTrafficEnabled(true);
                }
                return true;
            case R.id.filter_6: // buildings
                if (item.isChecked()) {
                    item.setChecked(false);
                    theMap.setBuildingsEnabled(false);
                } else {
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), R.string.filter_6, Toast.LENGTH_SHORT).show();
                    theMap.setBuildingsEnabled(true);
                }
                return true;


            /*
             * parked icon (radio button logic)
             *
             * 1. prints whether user is now parked or no longer parked
             * 2. places undraggable car icon at current location to indicate where user is parked
             */
            case R.id.parked_icon:
                if (item.isChecked()) {
                    item.setChecked(false);
                    Toast.makeText(getBaseContext(), "No longer parked", Toast.LENGTH_SHORT).show();
                    userMarker2.remove();
                } else {
                    //updatePlaces();
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), "Parked", Toast.LENGTH_SHORT).show();

                    userMarker2 = theMap.addMarker(new MarkerOptions()
                            .position(lastLatLng)
                            .title("Parking Location")
                            .snippet("You are parked here")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_parked)));
                }
                return true;

            // favorite (checkbox)
            case R.id.favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                    Toast.makeText(getBaseContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                }
                return true;

            // layers (radio buttons) - map views
            case R.id.layersMenu_1:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    Toast.makeText(getBaseContext(), "Normal View", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.layersMenu_2:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    theMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    Toast.makeText(getBaseContext(), "Satellite View", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.layersMenu_3:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    theMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    Toast.makeText(getBaseContext(), "Terrain View", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.layersMenu_4:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    Toast.makeText(getBaseContext(), "Hybrid View", Toast.LENGTH_SHORT).show();
                }
                return true;

            // settings (new activity)
         //   case R.id.settings:
           //     Intent intent = new Intent(MainActivity.this, Settings.class);
             //   MainActivity.this.startActivity(intent); // starting settings activity
               // return true;

            default:
                return super.onOptionsItemSelected(item);
        } // end switch
    } // end onOptionsItemSelected



 SFCrimeHandler crimeHandler = new SFCrimeHandler(); /* Initialize empty handler */
 boolean success = crimeHandler.generateReports(null, -1, -1, -1, -1); /* Generate reports with all default values */
 Button crimeButt;
//             EditText radius = (EditText) findViewById(R.id.radius);
//             EditText reports = (EditText) findViewById(R.id.reports);
//             EditText offset = (EditText) findViewById(R.id.offset);
//             EditText year = (EditText) findViewById(R.id.earliestYear);


/* Takes user to crime data settings for configurations */
public void  parkingSafteyStart(){

    setContentView(R.layout.crime);

}


     public void btnDone(View v) throws EmptyResponseException {

             EditText radius = (EditText) findViewById(R.id.radius);
             EditText reports = (EditText) findViewById(R.id.reports);
             EditText offset = (EditText) findViewById(R.id.offset);
             EditText year = (EditText) findViewById(R.id.earliestYear);

         int radius0 = Integer.parseInt(String.valueOf(radius.getText()));
         int reports0 = Integer.parseInt(String.valueOf(reports.getText()));
         int offset0 = Integer.parseInt(String.valueOf(offset.getText()));
         int year0 = Integer.parseInt(String.valueOf(year.getText()));

         setBack();
         parkingSaftey(radius0, reports0, offset0, year0);



             }
/*
* onClick function 'DONE' - takes in the user input
* and transfers to API handler
* */
public void setBack(){
   // setContentView(R.layout.activity_main);
    super.onBackPressed();
}
    public void parkingSaftey(int radius, int reports, int offset,int year) throws EmptyResponseException {



        if (success) {
    /* Iterate through reports returned */

            for (int i = 0; i < reports; i++) {
        /* Print the location and date of each, for instance */
                System.out.println("Report #: " + (i+1));
                System.out.println("Date: " + crimeHandler.date(i));
                System.out.println("Location: " + crimeHandler.location(i));



            theMap.addMarker(new MarkerOptions()
                    .position(crimeHandler.location(i))
                    .title("Report #: " + (i+1))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .snippet("Date: " + crimeHandler.date(i)));

            }
        }

        LatLng origin = new LatLng(37.728271, -122.433385); /* Create a new LatLng object to pass to the handler */

         int theRadius = radius;
         int theYear = year;
         int theReports = reports;
         int theOffset = offset;

        crimeHandler.setTimeout(30); /* Increase the number of seconds before timeout from 20 to 30 */

/* Generate a new report list with the new parameters */
        success = crimeHandler.generateReports(origin, theRadius, theYear, theReports, theOffset);

/* Retrieve report data on a successful query */
        if (success) {
    /* The number of reports returned will be at most "count", but may be fewer given narrowed parameter values */
            System.out.println("Number of reports: " + crimeHandler.numReports());
        }

        crimeHandler = new SFCrimeHandler(); /* Reset the handler */


    }



} // end MainActivity