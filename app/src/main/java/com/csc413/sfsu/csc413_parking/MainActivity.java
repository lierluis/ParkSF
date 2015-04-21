package com.csc413.sfsu.csc413_parking;

import com.csc413.sfsu.sfpark_simplified.*;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.*;
import android.widget.Toast;
import com.google.android.gms.maps.model.*;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private SFParkQuery query;
    private SFParkXMLResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        query = new SFParkQuery();
        query.addParameter("lat", "37.792275");
        query.addParameter("long", "-122.397089");
        query.addParameter("radius", "0.5");
        query.addParameter("uom", "mile");
        query.addParameter("response", "xml");
        response = new SFParkXMLResponse();

    }

    @Override
    public void onMapReady (GoogleMap map) {
        this.map = map;
        LatLng sanFrancisco = new LatLng(37.7750, -122.4183);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sanFrancisco, 14));
        //map.setOnMapClickListener(new MapListener(map));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //String msg = "Latitude: " + latLng.latitude + "\nLongitude: " + latLng.longitude;
                query.addOrUpdateParameter("long", latLng.longitude + "");
                query.addOrUpdateParameter("lat", latLng.latitude + "");
                String msg;
                if (response.populateResponse(query)) {
                    msg = "Status: " + response.status();
                    msg += "\nMessage: " + response.message();
                    if (response.numRecords() > 0) {
                        for (int i = 0; i < response.avl(0).pts(); i++) {
                            msg += "\nLocation " + (i+1) + ": ("
                                    + response.avl(0).loc().longitude(i)
                                    + ","
                                    + response.avl(0).loc().latitude(i)
                                    + ")";
                        }
                    }
                }
                else
                    msg = "failed to populate: " + response.status();

                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
