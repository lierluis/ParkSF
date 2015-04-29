package com.csc413.sfsu.csc413_parking;



import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.view.*;
import android.widget.Toast;
import android.widget.RelativeLayout;



import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;


import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import com.csc413.sfsu.csc413_parking_data.*;


/**
 * Author: Luis Estrada + UI Team (Jonathan Raxa & Ishwari)
 *  Class: CSC413
 */


public class MainActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap theMap;
    private LocationManager locMan;
    private Marker userMarker;
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private TextView mLocationView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(serevicesOK()){
            setContentView(R.layout.activity_main);

            if (initMap()){
                Toast.makeText(this,"Ready to park!", Toast.LENGTH_SHORT).show();
                mLocationView = new TextView(this);


            } else{
                Toast.makeText(this,"Map Unavailable!", Toast.LENGTH_SHORT).show();

            }

        } else {
            setContentView(R.layout.activity_main);
        }

        theMap.setMyLocationEnabled(true);
        theMap.setIndoorEnabled(false);
        theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        theMap.getUiSettings().setZoomControlsEnabled(true);

        updatePlaces();

    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this,"Connected to location services", Toast.LENGTH_SHORT).show();

    }

    /*
    * Implementing the location listener
    * */
    @Override
    public void onConnectionSuspended(int i) {
        // Log.i(TAG, "GoogleApiClient connection has been suspend");
        Toast.makeText(this,"Connected to location services", Toast.LENGTH_SHORT).show();
        LocationRequest request = LocationRequest.create();

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(60000); //every 5 seconds
        request.setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //  Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        // mLocationView.setText("Location received: " + location.toString());
        String msg = "Location: " + location.getLatitude() + "," + location.getLongitude();
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();

    }

    public boolean serevicesOK(){
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /*
    * initialze the map object
    * Checks
    * no return*/
    private boolean initMap() {
        if (theMap == null) {
            SupportMapFragment mapFrag =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            theMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
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
                    TextView tvLocality = (TextView)v.findViewById(R.id.tv_locality);
                    TextView tvLat = (TextView)v.findViewById(R.id.tv_lat);
                    TextView tvLng = (TextView)v.findViewById(R.id.tv_lng);
                    TextView tvSnippet = (TextView)v.findViewById(R.id.tv_snippet);

                    // gets latitude and longitude
                    LatLng ll = marker.getPosition();

                    tvLocality.setText(marker.getTitle());
                    tvLat.setText("Latitude: "+ll.latitude);
                    tvLng.setText("Longitude: "+ll.longitude);
                    tvSnippet.setText(marker.getSnippet());

                    return v;

                }
            });
        }
        return(theMap != null);
    }


    private void gotoLocation(double lat, double lng, float zoom) {

        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,zoom);
        theMap.moveCamera(update);
    }

    private void updatePlaces(){
        //update location
        locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double lat = lastLoc.getLatitude();
        double lng = lastLoc.getLongitude();

        LatLng lastLatLng = new LatLng(lat, lng);

        if(userMarker!=null) userMarker.remove();

        userMarker = theMap.addMarker(new MarkerOptions()

                .position(lastLatLng)
                .title("Parking Location")
                .snippet("You are here"));
        userMarker.setDraggable(true);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(lastLatLng,18);
        theMap.moveCamera(update);
        theMap.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng), 3000, null);
    }


    // test method
    public void findParking(){
        

    }



    /**
     * Initialize the contents of the Activity's standard options menu
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
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RelativeLayout main_view = (RelativeLayout) findViewById(R.id.derp);
        
            int id = item.getItemId();


            if(id==R.id.layersMenu_1){
                theMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            }

            if(id==R.id.layersMenu_2){
                theMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            }

            if(id==R.id.layersMenu_3){
                theMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            }
            if(id==R.id.layersMenu_4){
                theMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            }


        switch(item.getItemId()) {
            case R.id.search_icon:
                Toast.makeText(getBaseContext(), "Search", Toast.LENGTH_LONG).show();
                return true;
            case R.id.filter_icon:
                Toast.makeText(getBaseContext(), "Filter", Toast.LENGTH_LONG).show();
                return true;
            case R.id.parked_icon:
                if(item.isChecked()) {
                    item.setChecked(false);
                    userMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
                    Toast.makeText(getBaseContext(), "No longer parked", Toast.LENGTH_LONG).show();
                } else {
                    updatePlaces();
                    item.setChecked(true);
                    userMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car_parked));
                    Toast.makeText(getBaseContext(), "Parked", Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.favorite:
                if(item.isChecked()) { // if checked & user clicks on it
                    item.setChecked(false);
                    Toast.makeText(getBaseContext(), "Removed from favorites", Toast.LENGTH_LONG).show();
                } else {
                    item.setChecked(true);
                    Toast.makeText(getBaseContext(), "Added to favorites", Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        } // end switch
    } // end onOptionsItemSelected
} // end MainActivity