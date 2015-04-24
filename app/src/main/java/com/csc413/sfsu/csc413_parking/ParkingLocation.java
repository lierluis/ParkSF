package com.csc413.sfsu.csc413_parking;

import com.google.android.gms.maps.model.LatLng;

/**
 * The ParkingLocation class is intended to store data about parking locations returned from the SFParkSimplified API.
 * ParkingLocation objects are associated with an origin location and will be within a radius measurement of that origin.
 * @author Devin Clary
 */
public class ParkingLocation {
    /**Represents the center from which the SFPark query was performed that resulted in this Parking Location Data.*/
    private LatLng originLocation;
    /**The radius of search from the originLocation from which the SFPark query was performed that resulted in this Parking Location Data.*/
    private Double radiusFromOrigin;
    /**Whether this location has off street parking. Corresponds to the <TYPE> tag from SFPark responses.*/
    private boolean hasOffStreetParking;
    /**The name of this location. Corresponds to the <NAME> tag from SFPark responses. */
    private String name;
    /**The description of this parking location. This will only contain data for off street parking. Corresponds to the <DESC> tag from SFPark responses. */
    private String desc;
    /**Unique SFMTA ID for off street parking. Corresponds to the <OSPID> tag from SFPark responses.*/
    private int ospid;
    /**Unique SFMTA ID for on street, block facing parking. Corresponds to the <BFID> tag from SFPark responses. */
    private int bfid;
    /**The coordinates of this parking location. Corresponds to the <LOC> tag from SFPark responses. Note: the SFPark database will usually have two points (start and end) for on street parking. In this case, only the first point will be saved */
    private LatLng coords;

}
