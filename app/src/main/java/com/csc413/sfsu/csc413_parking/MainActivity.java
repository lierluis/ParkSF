package com.csc413.sfsu.csc413_parking;

import com.csc413.sfsu.sfpark_simplified.*;
import com.csc413.sfsu.sf_vehicle_crime.*;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.maps.*;

import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.*;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private SFParkQuery query;
    private SFParkXMLResponse response;
    private SFCrimeHandler crimeHandler;
    private boolean parkingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        query = new SFParkQuery();
        query.setLocation(37.792275, -122.397089);
        query.setRadius(0.5);
        query.setUnitOfMeasurement("miles");
        response = new SFParkXMLResponse();

        crimeHandler = new SFCrimeHandler();
        parkingInfo = true;
    }

    @Override
    public void onMapReady (GoogleMap map) {
        this.map = map;
        LatLng sanFrancisco = new LatLng(37.7750, -122.4183);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sanFrancisco, 14));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                query.setLocation(latLng);
                String msg = "";
                if (parkingInfo) {
                    if (response.populate(query)) {
                        System.out.println("Num Records: " + response.numRecords());
                        for (int i = 0; i < response.numRecords(); i++) {
                            String output = "Location #" + i;
                            output += "\nName: " + response.avl(i).name();
                            output += "\nLocation: " + response.avl(i).loc();
                            System.out.println(output);
                        }
                        msg = "Status: " + response.status();
                        msg += "\nMessage: " + response.message();
                        if (response.numRecords() > 0) {
                            for (int i = 0; i < response.avl(0).pts(); i++) {
                                msg += "\nLocation " + (i + 1) + ": ("
                                        + response.avl(0).loc().latitude(i)
                                        + ", "
                                        + response.avl(0).loc().longitude(i)
                                        + ")";
                            }
                        }
                    } else
                        msg = "failed to populate: " + response.status();
                }
                else {
                    if (crimeHandler.generateReports(latLng, 0.1, -1, 100, 0)) {
                        msg = "Status: " + crimeHandler.status();
                        msg += "\nReports: " + crimeHandler.numReports();
                        try {
                            msg += "\nLocation: " + "(" + crimeHandler.location(0).latitude + ", "
                                    + crimeHandler.location(0).longitude + ")";
                        } catch (Exception e) {
                            msg += "\nNo location data";
                        }
                        try {
                            msg += "\nDate: " + crimeHandler.date(0);
                        } catch (Exception e) {
                            msg += "\nNo date";
                        }
                    }
                }

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

    public void onClick_setAsParking (View view) {
        TextView textView = (TextView)findViewById(R.id.my_text_view);
        parkingInfo = true;
        textView.setText("Parking Data");
    }

    public void onClick_setAsCrime (View view) {
        TextView textView = (TextView)findViewById(R.id.my_text_view);
        parkingInfo = false;
        textView.setText("Crime Data");
    }
}
