//package com.csc413.sfsu.csc413_parking;
//
//import android.os.Bundle;
//import com.csc413.sfsu.sf_vehicle_crime.*;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import android.view.View;
//import android.widget.EditText;
//
//
//
//
///**
// * Created by jraxa on 5/6/15.
// */
//
//
//public class CrimeSettings extends MainActivity {
//
//    SFCrimeHandler crimeHandler = new SFCrimeHandler(); /* Initialize empty handler */
//    boolean success = crimeHandler.generateReports(null, -1, -1, -1, -1); /* Generate reports with all default values */
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.crime);
//    }
//
//
//    /* Takes user to crime data settings for configurations */
//    public void  parkingSafteyStart(){
//        setContentView(R.layout.crime);
//
//    }
//
//
//    public void btnDone(View v) throws EmptyResponseException {
//
//        EditText radius = (EditText) findViewById(R.id.radius);
//        EditText reports = (EditText) findViewById(R.id.reports);
//        EditText offset = (EditText) findViewById(R.id.offset);
//        EditText year = (EditText) findViewById(R.id.earliestYear);
//
//
//        int radius0 = Integer.parseInt(String.valueOf(radius.getText()));
//        int reports0 = Integer.parseInt(String.valueOf(reports.getText()));
//        int offset0 = Integer.parseInt(String.valueOf(offset.getText()));
//        int year0 = Integer.parseInt(String.valueOf(year.getText()));
//
//        parkingSaftey(radius0, reports0, offset0, year0);
//
//
//    }
//
//
//
//    /*
//* onClick function 'DONE' - takes in the user input
//* and transfers to API handler
//* */
//
//    public void parkingSaftey(int radius, int reports, int offset,int year) throws EmptyResponseException {
//
//        if (success) {
//    /* Iterate through reports returned */
//
//            for (int i = 0; i < reports; i++) {
//        /* Print the location and date of each, for instance */
//                System.out.println("Report #: " + (i+1));
//                System.out.println("Date: " + crimeHandler.date(i));
//                System.out.println("Location: " + crimeHandler.location(i));
//
//
//
//                theMap.addMarker(new MarkerOptions()
//                        .position(crimeHandler.location(i))
//                        .title("Report #: " + (i+1))
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                        .snippet("Date: " + crimeHandler.date(i)));
//
//            }
//        }
//
//        LatLng origin = new LatLng(37.728271, -122.433385); /* Create a new LatLng object to pass to the handler */
//
//        int theRadius = radius;
//        int theYear = year;
//        int theReports = reports;
//        int theOffset = offset;
//
//        crimeHandler.setTimeout(30); /* Increase the number of seconds before timeout from 20 to 30 */
//
///* Generate a new report list with the new parameters */
//        success = crimeHandler.generateReports(origin, theRadius, theYear, theReports, theOffset);
//
///* Retrieve report data on a successful query */
//        if (success) {
//    /* The number of reports returned will be at most "count", but may be fewer given narrowed parameter values */
//            System.out.println("Number of reports: " + crimeHandler.numReports());
//        }
//        finish();
//
//        crimeHandler = new SFCrimeHandler(); /* Reset the handler */
//    }
//
//    public void endCrimeSettings(){
//        setContentView(R.layout.activity_main);
//
//    }
//
//}
