package com.csc413.sfsu.csc413_parking;

import com.csc413.sfsu.sfpark_simplified.SFParkQuery;
import com.csc413.sfsu.sfpark_simplified.SFParkXMLResponse;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * The SFParkLocationFactory class is responsible for retrieving and storing location data from the SFParkSimplified API.
 */
public class SFParkLocationFactory
{
    private LocationDatabaseHandler db;


    /**
     * Retrieves all parking locations from SFPark within the specified radius of the origin as a list of discrete ParkingLocation objects
     * @param origin Center of search for parking locations.
     * @param radius radius to search for parking locations.
     * @return list of ParkingLocation objects within the radius of the origin.
     */
    public List<ParkingLocation> getParkingLocations(LatLng origin, double radius){
        SFParkQuery query = new SFParkQuery();
        query.setLatitude(origin.latitude);
        query.setLongitude(origin.longitude);
        query.setRadius(radius);
        query.setUnitOfMeasurement("MILE");

        SFParkXMLResponse response = new SFParkXMLResponse();
        boolean success = response.populate(query);
        List <ParkingLocation> locationList=new ArrayList<ParkingLocation>();

        String status = response.status();
        if (success) {
            String message = response.message();
            int numRecords = response.numRecords();
            for (int i = 0; i < numRecords; i++) {
                LatLng coords=new LatLng(response.avl(i).loc().latitude(0), response.avl(i).loc().longitude(0));
                String name=response.avl(i).name();
                boolean hasOnStreetParking=(response.avl(i).type().equals("ON")) ? true : false;
                String desc=response.avl(i).desc();
                int ospid=response.avl(i).ospid();
                int bfid=response.avl(i).bfid();
                boolean isFavorite=false;
                int timesSearched=0;
                locationList.add(new ParkingLocation(origin, radius, hasOnStreetParking, name, desc, ospid, bfid, coords, isFavorite, timesSearched));
            }
        }

        return locationList;

    }




}
