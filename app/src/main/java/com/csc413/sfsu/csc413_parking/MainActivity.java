package com.csc413.sfsu.csc413_parking;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.csc413.sfsu.sfpark_locationdata.ParkingLocation;
import com.csc413.sfsu.sfpark_locationdata.SFParkLocationFactory;
import com.csc413.sfsu.sfpark_simplified.SFParkQuery;
import com.csc413.sfsu.sfpark_simplified.SFParkXMLResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private SFParkQuery query;
    private SFParkXMLResponse response;
    private List<Marker> activeMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//
//        this.deleteDatabase("locations");
//        SFParkLocationFactory fact=new SFParkLocationFactory(this);
//        locationFactory.testDB();
//        fact.printAllDB();

    }

    @Override
    public void onMapReady (GoogleMap map) {
        this.map = map;
        LatLng sanFrancisco = new LatLng(37.7750, -122.4183);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sanFrancisco, 14));
        //map.setOnMapClickListener(new MapListener(map));

        query=new SFParkQuery();
        response = new SFParkXMLResponse();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String msg = "Latitude: " + latLng.latitude + "\nLongitude: " + latLng.longitude;

//                double radius=.1;
//                int startYear=2011;
//                int count=100;
//                int offset=0;
//
//                SFCrimeHandler crimeHandler = new SFCrimeHandler(); /* Initialize empty handler */
//                boolean success = crimeHandler.generateReports(latLng, radius, startYear, count, offset);
//                /* Retrieve report data on a successful query */
//                if (success) {
//                /* The number of reports returned will be at most "count", but may be fewer given narrowed parameter values */
//                    System.out.println("Number of reports: " + crimeHandler.numReports());
//                }

                SFParkLocationFactory locationFactory=new SFParkLocationFactory(MainActivity.this);
                List <ParkingLocation> parkingList=new ArrayList<ParkingLocation>();
                parkingList=locationFactory.getParkingLocations(latLng,.15);
                System.out.print("Done. ");
                System.out.println("Number of locations in database: "+locationFactory.getLocationCount());
                System.out.println(parkingList.size()+" number of locations within tap range.");



//
//                query.setLongitude(latLng.longitude);
//                query.setLatitude(latLng.latitude);
//                if (response.populate(query)) {
//                    msg = "Status: " + response.status();
//                    msg += "\nMessage: " + response.message();
//                    if (response.numRecords() > 0) {
//                        for (int i = 0; i < response.avl(0).pts(); i++) {
//                            msg += "\nLocation " + (i+1) + ": ("
//                                    + response.avl(0).loc().longitude(i)
//                                    + ", "
//                                    + response.avl(0).loc().latitude(i)
//                                    + ")";
//                        }
//                    }
//                }
//                else
//                    msg = "failed to populate: " + response.status();
//
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
