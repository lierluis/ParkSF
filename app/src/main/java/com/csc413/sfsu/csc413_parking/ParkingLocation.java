package com.csc413.sfsu.csc413_parking;

import com.google.android.gms.maps.model.LatLng;

/**
 * The ParkingLocation class is intended to store data about parking locations returned from the SFParkSimplified API.
 * ParkingLocation objects are associated with an origin location and will be within a radius measurement of that origin.
 * Note that once created, ParkingLocation objects are static, and do not provide mutators on internal data members.
 * If the need arises to change data on a ParkingLocation object, a new object must be created.
 * @author Devin Clary
 */
public class ParkingLocation {
    //Private
    /**Represents the center from which the SFPark query was performed that resulted in this Parking Location Data.*/
    private LatLng originLocation;
    /**The radius of search from the originLocation from which the SFPark query was performed that resulted in this Parking Location Data.*/
    private Double radiusFromOrigin;
    /**Whether this location has on street parking. Corresponds to the <TYPE> tag from SFPark responses. The ON value corresponds to true, and the OFF value corresponds to false.*/
    private boolean hasOnStreetParking;
    /**The name of this location. Corresponds to the <NAME> tag from SFPark responses. */
    private static String name;
    /**The description of this parking location. This will only contain data for off street parking. Corresponds to the <DESC> tag from SFPark responses. Set to "" if nonexistent for this location. */
    private static String desc;
    /**Unique SFMTA ID for off street parking. Corresponds to the <OSPID> tag from SFPark responses. Set to 0 if nonexistent for this location. */
    private static int ospid;
    /**Unique SFMTA ID for on street, block facing parking. Corresponds to the <BFID> tag from SFPark responses. Set to 0 if nonexistent for this location. */
    private static int bfid;
    /**The coordinates of this parking location. Corresponds to the <LOC> tag from SFPark responses. Note: the SFPark database will usually have two points (start and end) for on street parking. In this case, only the first point will be saved.*/
    private static LatLng coords;

    //Constructors

    /**
     * Constructor
     * Note that calling the empty constructor may have unintended side effects of blank or null data.
     */
    ParkingLocation(){
        this.originLocation=new LatLng(0.0,0.0);
        this.radiusFromOrigin=0.0;
        this.hasOnStreetParking=false;
        this.name="";
        this.desc="";
        this.ospid=0;
        this.bfid=0;
        this.coords=new LatLng(0.0,0.0);
    }

    /**
     *
     * @param origin The original coordinates originally sent to SFPark from which this parking location was a result.
     * @param radiusFromOrigin The radius from the origin that SFPark used to find this location.
     * @param hasOnStreetParking Corresponds to the <TYPE> tag from SFPark. ON value corresponds to true, and OFF value corresponds to false.
     * @param name Corresponds to the <NAME> tag from SFPark. Describes this parking location. Set to "" if this value doesn't exist.
     * @param desc Corresponds to the <DESC> tag from SFPark. Usually contains the address of the location. Set to "" if this value doesn't exist.
     * @param ospid Corresponds to the <OSPID> tag from SFPark. This will only exist for off street parking locations. Set to 0 if this value doesn't exist.
     * @param bfid Corresponds to the <BFID> tag from SFPark. This will only exist for on street parking lcoations. Set to 0 if this value doesn't exist.
     * @param coords The coordinates of this parking locations. For on street locations that contain two sets of coordinates, the first should be used.
     */
    ParkingLocation(LatLng origin, Double radiusFromOrigin, boolean hasOnStreetParking, String name, String desc, int ospid, int bfid, LatLng coords){
        this.originLocation=origin;
        this.radiusFromOrigin=radiusFromOrigin;
        this.hasOnStreetParking=hasOnStreetParking;
        this.name=name;
        this.desc=desc;
        this.ospid=ospid;
        this.bfid=bfid;
        this.coords=coords;
    }

    //Accessors

    /**
     *
     * @return The origin location from which this parking location is near.
     */
    LatLng getOriginLocation(){
        return this.originLocation;
    }

    /**
     *
     * @return The radius from the origin from which this parking location is near.
     */
    double getRadiusFromOrigin(){
        return this.radiusFromOrigin;
    }

    /**
     *
     * @return The value of hasOnStreetParking. True if on street, false otherwise.
     */
    boolean hasOnStreetParking(){
        return this.hasOnStreetParking;
    }

    /**
     *
     * @return The name of this location if it exists. Null otherwise.
     */
    String getName(){
        return this.name;
    }

    /**
     *
     * @return The description of this location, if it exists. Null otherwise.
     */
    String getDesc(){
        return this.desc;
    }

    /**
     *
     * @return The SFMTA off street parking ID, if it exists. 0 otherwise.
     */
    int getOspid(){
        return this.ospid;
    }

    /**
     *
     * @return The SFMTA on street parking ID, if it exists. 0 otherwise.
     */
    int getBfid(){
        return this.bfid;
    }

    /**
     *
     * @return The coordinates of this parking location.
     */
    LatLng getCoords(){
        return this.coords;
    }





}
