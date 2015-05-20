package com.csc413.sfsu.csc413_parking;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.location.Location;
import android.location.LocationManager;

import com.csc413.sfsu.sfpark_locationdata.ParkingLocation;
import com.csc413.sfsu.sfpark_locationdata.SFParkLocationFactory;
import com.csc413.sfsu.sfpark_simplified.SFParkLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Luis Estrada + UI Team (Jonathan Raxa & Ishwari)
 * Class: CSC413
 */
public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap theMap;
    private SFParkLocationFactory locationFactory;
    private List<ParkingLocation> parkingList = new ArrayList<ParkingLocation>();
    private List<ParkingLocation> udlList;

    private static LocationManager locMan;
    private static Marker userMarker;
    private static Marker parkedLocation;

    //List<ParkingLocation> udlListFavorites;

    private static ArrayList<Marker> userMarkers = new ArrayList<>();
    private static ArrayList<Marker> favoriteMarkers = new ArrayList<>();
    private static ArrayList<Marker> UDLMarkers = new ArrayList<>();

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private static GoogleApiClient mGoogleApiClient;
    private static LocationRequest mLocationRequest;
    private static LatLng lastLatLng; // last known location
    private static LatLng origin;

    // navigation drawer
    private static DrawerLayout mDrawerLayout;
    private static ListView mDrawerList;
    private static android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private static int previousPosition; // keeps track of which item in navigation drawer is already selected

    private static boolean markerSelected;
    private static boolean parked;

    private static boolean isSFParkLocation;
    private static boolean isUDLMarker;

    private static boolean favorited;
    private static int parkingSpot;
    LatLng loc;

    //ParkingLocation userDefined;

    //    private static double lat; // current location latitude
//    private static double lng; // current location longitude
    private static LatLng newll; // lat,lng where marker is dragged to

    /**
     * Where activity is initialized
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create map
        theMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        setMap(); // set map properties like buttons, layers
        updatePlaces(); // set userMarker location

        theMap.setOnMarkerClickListener(this); // enable marker listener
        markerSelected = false;

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        getSupportActionBar().setSubtitle("CSC413");

        setNavigationDrawer();
        setInfoWindows();

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (savedInstanceState == null) {
            selectItem(0);
        }

        //this.deleteDatabase("locations"); // deletes database already existing

        // coordinates: N, E are +, S, W are -
        origin = new LatLng(37.7833, -122.4167);
        locationFactory = new SFParkLocationFactory(this);
        udlList = locationFactory.getUserDefinedLocations();

    }

    /**
     * Makes a Toast message from a String argument
     * @param s
     */
    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets current location & sets marker
     */
    private void updatePlaces() {
        locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        final double lat = lastLoc.getLatitude();
        final double lng = lastLoc.getLongitude();

        //LatLng lastLatLng = new LatLng(lat, lng);
        lastLatLng = new LatLng(lat, lng);

        // if userMarker already exists, remove it to replace it with one at current location
        if (userMarker != null) userMarker.remove();

        // userMarker put at current location
        userMarker = theMap.addMarker(new MarkerOptions()
                .position(lastLatLng)
                .title("User Location")
                .snippet("Location: lat/lng: (" + lat + "," + lng + ")")
                .draggable(true));

        // move camera to marker
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(lastLatLng, 11);
        theMap.moveCamera(update);
        theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
        theMap.setOnMarkerDragListener(
                new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        Geocoder gc = new Geocoder(MainActivity.this);
                        List<Address> list = null;
                        newll = marker.getPosition(); // lat,lng where marker is dragged to

                        try {
                            list = gc.getFromLocation(newll.latitude, newll.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        origin = new LatLng(newll.latitude, newll.longitude); // origin of parkingList = where marker is dragged to
                        double radius = .15;

                        parkingList = locationFactory.getParkingLocations(origin, radius);

                        toast("PARKING LIST SIZE: " + parkingList.size()); // # of parking locations around origin

                        if (parkingList.size() != 0) {

                            theMap.clear();

                            // clear the marker references before we add more, because the parkingList index resets to 0 when creating new markers
                            if (!userMarkers.isEmpty()) {
                                userMarkers.clear();
                            }

                            // set new userMarker at location where marker was dragged
                            userMarker = theMap.addMarker(new MarkerOptions()
                                    .position(marker.getPosition())
                                    .title("User Location")
                                    .snippet("Location: lat/lng: (" + lat + "," + lng + ")")
                                    .draggable(true));

                            if (parked) {
                                parkedLocation = theMap.addMarker(new MarkerOptions()
                                        .position(marker.getPosition())
                                        .title("Parked location")
                                        .snippet("Location: lat/lng: (" + lat + "," + lng + ")"));
                            }

                            // move camera to marker
//                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(newll, 14);
//                            theMap.moveCamera(update);
//                            theMap.animateCamera(CameraUpdateFactory.newLatLng(newll), 3000, null);

                        }

                        if (parkingList.size() != 0) {

                            for (int i = 0; i < parkingList.size(); i++) {

                                // add markers to map whose indexes align with each parking location
                                userMarkers.add(
                                        theMap.addMarker(new MarkerOptions()
                                                        .position(parkingList.get(i).getCoords())
                                                        .title(parkingList.get(i).getName())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.parking))
                                                //.snippet("Parking location: " + i + "\n" + parkingList.get(i).toString())
                                        ));

//                                // change marker & snippet if theft probability is high
                                if (parkingList.get(i).getTheftProbability() > 1) {
                                    userMarkers.get(i).setSnippet("Parking location: " + i + "\n" + parkingList.get(i).toString()
                                            + "\nWARNING: You may likely be robbed");
                                    userMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.crossbones));
                                } else {
                                    userMarkers.get(i).setSnippet("Parking location: " + i + "\n" + parkingList.get(i).toString());
                                }

                                // change marker if favorite
                                if (parkingList.get(i).isFavorite()) {
                                    userMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.star));
                                }

                                // change marker if user defined location
                                if (parkingList.get(i).isUserDefined()) {
                                    userMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                                }

                                // change marker if previously parked location
                                if (parkingList.get(i).getParkedHere()) {
                                    userMarkers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.parked));
                                }
                            } // end for

                        } // end if

                        // when dragging marker, set new title & snippet
                        Address add = list.get(0);
                        marker.setTitle(add.getLocality());
                        marker.setSnippet("Location: lat/lng: (" + String.valueOf(newll.latitude) +
                                "," + String.valueOf(newll.longitude) + ")"); // location = where marker is dragged to
                        marker.showInfoWindow();

                    } // end onMarkerDragEnd
                }

        ); // end setOnMarkerDragListener

//        theMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//            }
//        });

    } // end updatePlaces

    /**
     * Sets the map feature options
     */
    public void setMap() {
        theMap.setMyLocationEnabled(true);
        theMap.setIndoorEnabled(false);
        theMap.setBuildingsEnabled(false); // optional for user in filter
        theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        theMap.getUiSettings().setZoomControlsEnabled(true);
    }

    /**
     * Changes the user's current location on a location change
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        // mLocationView.setText("Location received: " + location.toString());
        String msg = "Location: " + location.getLatitude() + "," + location.getLongitude();
        toast(msg);
    }

    /**
     * goes to a specific location given the arguments
     * @param lat
     * @param lng
     * @param zoom
     */
    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        theMap.moveCamera(update);
    }


    ////////////////////////////////////////// MARKER //////////////////////////////////////////////

    /**
     * Listens for a click on a marker
     * Default behavior of clicking a marker is for the camera to move to the map and an info window to appear
     *
     * @param marker - the marker clicked on
     * @return true if listener consumes event (i.e. default behavior should not occur)
     * false if otherwise (i.e. default behavior should occur)
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        markerSelected = true;

        if (userMarkers.contains(marker)) {
            isSFParkLocation = true;
            parkingSpot = userMarkers.indexOf(marker); // saves which marker pertains to which parking location
        } else {
            isSFParkLocation = false;
        }

//        if (UDLMarkers.contains(marker)) {
//            isUDLMarker = true;
//            parkingSpot = UDLMarkers.indexOf(marker);
//            //toast("UDL");
//        } else {
//            isUDLMarker = false;
//            //toast("Not UDL");
//        }

        return false; // we want to keep the default behavior of the camera shift and info window
    }


    /**
     * A custom info window that can expand or contract depending on
     * how much information is put in
     *
     */
    private void setInfoWindows() {
        theMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }


            /* Formats the contents of each marker's snippet */
            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.info_window, null);
                TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);  // title
                //TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);          // latitude
                //TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);          // longitude
                TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);    // snippet

                // gets latitude and longitude
                LatLng ll = marker.getPosition();

                // sets up info window
                tvLocality.setText(marker.getTitle());
                //tvLat.setText("Latitude: " + ll.latitude);
                //tvLng.setText("Longitude: " + ll.longitude);
                tvSnippet.setText(marker.getSnippet());

                return v;

            } // end getInfoContents
        }); // end setInfoWindowAdapter
    } // end setInfoWindows
    ////////////////////////////////////////// END MARKER /////////////////////////////////////////


    ////////////////////////////////////////// ACTION BAR /////////////////////////////////////////

    /**
     * Initialize the contents of the Activity's standard options menu
     *
     * @param menu - action bar
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
            //case R.id.search_icon:
            //    return true;

            /*
             * checkbox logic:     if item is already checked, un-check item.
             *                     else if item is not checked, check item.
             * radio button logic: if item is already selected, disallow re-selecting (do nothing)
             *                     else if item is not selected, select item
             */
            // filter (checkbox)
            case R.id.filter_1: // Traffic
                if (item.isChecked()) {
                    item.setChecked(false);
                    theMap.setTrafficEnabled(false);
                } else {
                    item.setChecked(true);
                    Toast.makeText(this, R.string.filter_1, Toast.LENGTH_SHORT).show();
                    theMap.setTrafficEnabled(true);
                }
                return true;
            case R.id.filter_2: // Buildings
                if (item.isChecked()) {
                    item.setChecked(false);
                    theMap.setBuildingsEnabled(false);
                } else {
                    item.setChecked(true);
                    Toast.makeText(this, R.string.filter_2, Toast.LENGTH_SHORT).show();
                    theMap.setBuildingsEnabled(true);
                }
                return true;

            case R.id.parked_icon:

                if(markerSelected) {
                    if(isSFParkLocation) {

                        if(parkingList.get(parkingSpot).getParkedHere()) { // if parked, unpark
                            toast("No longer parked");
                            if(parkingList.get(parkingSpot).isFavorite()) {
                                userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.star)); // favorite icon
                            } else if (parkingList.get(parkingSpot).isUserDefined()) {
                                userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car)); // user defined
                            } else {
                                if ((parkingList.get(parkingSpot).getTheftProbability()) > 1) {
                                    userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.crossbones)); // high theft
                                } else {
                                    userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.parked)); // regular parking location
                                }
                            }
                        } else {
                            toast("Parked");
                            userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_parked));
                        }

                        locationFactory.toggleParkedHere(parkingList.get(parkingSpot));
                        parkingList = locationFactory.updateDataFromDatabase(parkingList);
                        userMarkers.get(parkingSpot).setSnippet("Parking location: " + parkingSpot + "\n" + parkingList.get(parkingSpot).toString());
                        userMarkers.get(parkingSpot).showInfoWindow();

                    } else {

                        if(parked) {
                            parked = false;
                            toast("No longer parked");
                            userMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
                        } else {
                            parked = true;
                            toast("Parked");
                            userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_parked));
                        }

                    }
                } else {
                    toast("Select a marker to park");
                }

                return true;

            // favorite
            case R.id.favorite:

                if(markerSelected) {
                    if(isSFParkLocation) {

                        if(parkingList.get(parkingSpot).isFavorite()) { // if favorite, unfavorite
                            if(parkingList.get(parkingSpot).getParkedHere()) { // if parked
                                userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.parked)); // favorite icon
                            } else if (parkingList.get(parkingSpot).isUserDefined()) {
                                userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car)); // user defined
                            } else {
                                if ((parkingList.get(parkingSpot).getTheftProbability()) > 1) {
                                    userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.crossbones)); // high theft
                                } else {
                                    userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.parking)); // regular parking location
                                }
                            }
                        } else {
                            userMarkers.get(parkingSpot).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.star));
                        }

                        locationFactory.toggleFavorite(parkingList.get(parkingSpot));
                        parkingList = locationFactory.updateDataFromDatabase(parkingList);
                        userMarkers.get(parkingSpot).setSnippet("Parking location: " + parkingSpot + "\n" + parkingList.get(parkingSpot).toString());
                        userMarkers.get(parkingSpot).showInfoWindow();

                    } else {

                        toast("not sfpark location");

                    }
                } else {
                    toast("Select a marker to favorite");
                }

                return true;

            // layers (radio buttons)
            case R.id.layersMenu_1:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    toast("Normal View");
                }
                return true;
            case R.id.layersMenu_2:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    theMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    toast("Satellite View");
                }
                return true;
            case R.id.layersMenu_3:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    theMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    toast("Terrain View");
                }
                return true;
            case R.id.layersMenu_4:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    toast("Hybrid View");
                }
                return true;

            // settings (new activity)
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(intent); // starting settings activity
                return true;

            default:
                return super.onOptionsItemSelected(item);
        } // end switch
    } // end onOptionsItemSelected

    ////////////////////////////////////// END ACTION BAR /////////////////////////////////////////


    ///////////////////////////////////// NAVIGATION DRAWER ///////////////////////////////////////

    /**
     * Sets values for navigation drawer
     */
    public void setNavigationDrawer() {
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
                this,                   // host Activity
                mDrawerLayout,          // DrawerLayout object
                R.string.drawer_open,   // "open drawer" description for accessibility
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
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /**
     * changes configuration of application upon user input
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
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
                if (position != previousPosition)
                    theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 1:
                if (position != previousPosition)
                    theMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 2:
                if (position != previousPosition)
                    theMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case 3:
                if (position != previousPosition)
                    theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 4:
                for (int i = 0; i < locationFactory.getFavorites().size(); i++) {
                    theMap.addMarker(new MarkerOptions()
                            .position(locationFactory.getFavorites().get(i).getCoords())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.star))
                            .title(locationFactory.getFavorites().get(i).getName())
                            .snippet(locationFactory.getFavorites().get(i).toString()));
                }
                break;
            case 5:
                for (int i = 0; i < locationFactory.getUserDefinedLocations().size(); i++) {
                    theMap.addMarker(new MarkerOptions()
                            .position(locationFactory.getUserDefinedLocations().get(i).getCoords())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))
                            .title(locationFactory.getUserDefinedLocations().get(i).getName())
                            .snippet(locationFactory.getUserDefinedLocations().get(i).toString()));
                }
                break;
            case 6:
                for (int i = 0; i < locationFactory.getParkedLocations().size(); i++) {
                    theMap.addMarker(new MarkerOptions()
                            .position(locationFactory.getParkedLocations().get(i).getCoords())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.parked))
                            .title(locationFactory.getParkedLocations().get(i).getName())
                            .snippet(locationFactory.getParkedLocations().get(i).toString()));
                }
                break;
            case 7:
                theMap.clear();
                updatePlaces();
                break;
            default:
        }
        previousPosition = position; // keeps track of position so item can't be selected twice
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    /////////////////////////////////// END NAVIGATION DRAWER /////////////////////////////////////


    //////////////////////////////////////// CONNECTION ///////////////////////////////////////////

    /**
     * Checks whether or not we are connected to location services
     * so that we can use the Google Maps API
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected to location services", Toast.LENGTH_SHORT).show();
    }

    /* implementing the location listener */
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

    /**
     * returns the result of whether or not there is a connection to the Google API client
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //  Log.i(TAG, "GoogleApiClient connection has failed");
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
    ///////////////////////////////////// END CONNECTION ///////////////////////////////////////////

} // end MainActivity