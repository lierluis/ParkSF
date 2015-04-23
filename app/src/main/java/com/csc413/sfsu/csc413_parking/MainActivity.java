package com.csc413.sfsu.csc413_parking;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.view.*;
import android.widget.Toast;
import android.widget.RelativeLayout;

/**
 * Author: Luis Estrada
 *  Class: CSC413
 */
public class MainActivity extends ActionBarActivity
        implements OnMapReadyCallback {

    /**
     * This is where the activity is initialized
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Called when the map is ready to be used
     * @param map
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        // North & East are positive, South & West are negative
        LatLng sanFrancisco = new LatLng(37.7833, -122.4167);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sanFrancisco, 14));

        map.addMarker(new MarkerOptions()
                .title("San Francisco")
                .snippet("That one city")
                .position(sanFrancisco));

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /*
                String latString = latLng.latitude + "";
                Double lat = Double.parseDouble(latString);
                String lonString = latLng.longitude + "";
                Double lon = Double.parseDouble(lonString);
                String coordinates = latString + ", " + lonString;
                */
                String coordinates = "Latitude: " + latLng.latitude + "\nLongitude: " + latLng.longitude;
                Toast.makeText(getBaseContext(), coordinates, Toast.LENGTH_SHORT).show();
            }
        });
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

        // handle action bar item clicks
        switch(item.getItemId()) {
            case R.id.search_icon:
                Toast.makeText(getBaseContext(), "Search", Toast.LENGTH_LONG).show();
                return true;
            case R.id.layers_icon:
                Toast.makeText(getBaseContext(), "Layers", Toast.LENGTH_LONG).show();
                return true;
            case R.id.parked_icon:
                Toast.makeText(getBaseContext(), "Parked", Toast.LENGTH_LONG).show();
                return true;

            case R.id.menu_option1:
                if(item.isChecked()) {
                    // do nothing, because user shouldn't be allowed to click twice on an option
                } else {
                    item.setCheckable(true);
                    item.setChecked(false);
                    Toast.makeText(getBaseContext(), "You chose option 1", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_option2:
                if(item.isChecked()) {
                    // do nothing
                } else {
                    item.setCheckable(true);
                    item.setChecked(false);
                    Toast.makeText(getBaseContext(), "You chose option 2", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_option3:
                if(item.isChecked()) {
                    // do nothing
                } else {
                    item.setCheckable(true);
                    item.setChecked(false);
                    Toast.makeText(getBaseContext(), "You chose option 3", Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        } // end switch
    } // end onOptionsItemSelected
} // end MainActivity