package com.csc413.sfsu.sfpark_locationdata;

import com.google.android.gms.maps.model.LatLng;

/**
 * The ParkingLocation class is intended to store data about parking locations returned from the
 * SFParkSimplified API. ParkingLocation objects are associated with an origin location and will be
 * within a radius measurement of that origin. The data of ParkingLocation objects is populated
 * using the SFParkLocationFactory class as an entry point. ParkingLocation objects should not be
 * created manually, as this may cause unintended side effects with other DataBase classes.
 *
 * Note that once created, ParkingLocation objects are static, and do not provide mutators on
 * internal data members, except as protected methods for classes in the same package.
 * If the need arises to change data on a ParkingLocation object, the methods within the
 * SFParkLocationFactory class should be utilized.
 *
 * @author Devin Clary
 */
public class ParkingLocation {
    /**Represents the center from which the SFPark query was performed that resulted in this
     * Parking Location Data.*/
    private LatLng originLocation;
    /**The radius of search from the originLocation from which the SFPark query was performed that
     * resulted in this Parking Location Data.*/
    private Double radiusFromOrigin;
    /**Whether this location has on street parking. Corresponds to the <TYPE> tag from SFPark
     * responses. The ON value corresponds to true, and the OFF value corresponds to false.*/
    private boolean hasOnStreetParking;
    /**The name of this location. Corresponds to the <NAME> tag from SFPark responses. */
    private String name;
    /**The description of this parking location. This will only contain data for off street parking.
     *Corresponds to the <DESC> tag from SFPark responses. Set to "" if nonexistent for this
     * location. */
    private String desc;
    /**Unique SFMTA ID for off street parking. Corresponds to the <OSPID> tag from SFPark responses.
     *  Set to 0 if nonexistent for this location. */
    private int ospid;
    /**Unique SFMTA ID for on street, block facing parking. Corresponds to the <BFID> tag from
     * SFPark responses. Set to 0 if nonexistent for this location. */
    private int bfid;
    /**The coordinates of this parking location. Corresponds to the <LOC> tag from SFPark responses.
     *  Note: the SFPark database will usually have two points (start and end) for on street
     *  parking. In this case, only the first point will be saved.*/
    private LatLng coords;
    /**Whether this location has been specified as a favorite by the user.*/
    private boolean isFavorite;
    /** The number of times this location has been the subject of a search by the user.*/
    private int timesSearched;
    /**Whether the user has parked at this location.*/
    private boolean parkedHere;
    /** Whether this location was saved manually by the user. If true, most fields will be null */
    private boolean isUserDefined;
    /** The approximate probability of a parking theft occurring at this location as a percentage.*/
    private double theftProbability;

    /**
     * Note that calling the empty constructor may have unintended side effects of blank or null data.
     */
    ParkingLocation(){
        this.originLocation=new LatLng(0.0,0.0);
        this.radiusFromOrigin=0.0;
        this.hasOnStreetParking=false;
        name="";
        desc="";
        ospid=0;
        bfid=0;
        coords=new LatLng(0.0,0.0);
        this.isUserDefined=true;
        this.theftProbability=50.0;
    }

    /**
     *
     * @param origin The original coordinates originally sent to SFPark from which this parking
     *               location was a result.
     * @param radiusFromOrigin The radius from the origin that SFPark used to find this location.
     * @param hasOnStreetParking Corresponds to the <TYPE> tag from SFPark. ON value corresponds to
     *                           true, and OFF value corresponds to false.
     * @param name Corresponds to the <NAME> tag from SFPark. Describes this parking location.
     *             Set to "" if this value doesn't exist.
     * @param desc Corresponds to the <DESC> tag from SFPark. Usually contains the address of the
     *             location. Set to "" if this value doesn't exist.
     * @param ospid Corresponds to the <OSPID> tag from SFPark. This will only exist for off street
     *              parking locations. Set to 0 if this value doesn't exist.
     * @param bfid Corresponds to the <BFID> tag from SFPark. This will only exist for on street
     *             parking lcoations. Set to 0 if this value doesn't exist.
     * @param coords The coordinates of this parking locations. For on street locations that contain
     *               two sets of coordinates, the first should be used.
     */
    ParkingLocation(LatLng origin, Double radiusFromOrigin, boolean hasOnStreetParking, String name,
                    String desc, int ospid, int bfid, LatLng coords, boolean isFavorite,
                    int timesSearched, boolean parkedHere, boolean isUserDefined, double theftProbability){
        this.originLocation=origin;
        this.radiusFromOrigin=radiusFromOrigin;
        this.hasOnStreetParking=hasOnStreetParking;
        this.name=name;
        this.desc=desc;
        this.ospid=ospid;
        this.bfid=bfid;
        this.coords=coords;
        this.isFavorite=isFavorite;
        this.timesSearched=timesSearched;
        this.parkedHere=parkedHere;
        this.isUserDefined=isUserDefined;
        this.theftProbability=theftProbability;
    }

    //Accessors

    /**
     *The origin location represents the original point from which parking locations were searched
     * for.
     * @return The origin location from which this parking location is near.
     */
    public LatLng getOriginLocation(){
        return this.originLocation;
    }

    /**
     * The radius represents the query's distance from the origin that resulted in this location's
     * generation.
     * @return The radius from the origin from which this parking location is near.
     */
    public double getRadiusFromOrigin(){
        return this.radiusFromOrigin;
    }

    /**
     * Parking locations with on street parking will exhibit a value of true.
     * @return The value of hasOnStreetParking. True if on street, false otherwise.
     */
    public boolean hasOnStreetParking(){
        return this.hasOnStreetParking;
    }

    /**
     * The name of the parking lcoation is as string, and will sometimes be an address.
     * @return The name of this location if it exists. Null otherwise.
     */
    public String getName(){
        return this.name;
    }

    /**
     * The description of the parking location will be supplementary to the name field, and may not
     * exist.
     * @return The description of this location, if it exists. Null otherwise.
     */
    public String getDesc(){
        return this.desc;
    }

    /**
     * The OSPID field is a unique parking ID for off street parking locations.
     * @return The SFMTA off street parking ID, if it exists. 0 otherwise.
     */
    public int getOspid(){
        return this.ospid;
    }

    /**
     * The BFID field is a unique SFMTA on street parking ID.
     * @return The SFMTA on street parking ID, if it exists. 0 otherwise.
     */
    public int getBfid(){
        return this.bfid;
    }

    /**
     *  The coordinates of this parking location.
     * @return The coordinates of this parking location.
     */
    public LatLng getCoords(){
        return this.coords;
    }

    /**
     * Whether this location has been favorited by the user.
     * @return Whether this location is a favorite by the user or not.
     */
    public boolean isFavorite(){
        return this.isFavorite;
    }

    /**
     * The number of times this location has been the result of a search query by this application.
     * @return The number of times this location has been searched.
     */
    public int getTimesSearched(){
        return this.timesSearched;
    }

    /**
     * Whether the user has parked at this location.
     * @return A boolean for whether the user has parked at this location.
     */
    public boolean getParkedHere() { return this.parkedHere; }

    /**
     * Whether this location is user defined.
     * @return A boolean for whether this is a user defined location.
     */
    public boolean isUserDefined() { return this.isUserDefined; }

    /**
     * Gets the probability of a theft occurring at this location.
     * @return An integer representing the percentage probability of a theft occurring.
     */
    public double getTheftProbability() { return this.theftProbability; }


    /**
     * Sets the location's isFavorite field.
     *
     * Note: This method should only be accessible by the SFParkLocationFactory and
     * LocationDatabaseHandler classes-hence this method has an access modifier of protected.
     * DO NOT SET THIS FIELD FROM ANY OTHER CLASS.
     * Use the SFParkLocationFactory class to set this field to prevent unintended side-effects.
     *
     * @param isFavorite The boolean value to set on the isFavorite data field.
     */
    protected void setIsFavorite(boolean isFavorite){
        this.isFavorite=isFavorite;
    }

    /**
     * Sets the location's parkedHere field.
     *
     * Note: This method should only be accessible by the SFParkLocationFactory and
     * LocationDatabaseHandler classes-hence this method has an access modifier of protected.
     * DO NOT SET THIS FIELD FROM ANY OTHER CLASS.
     * Use the SFParkLocationFactory class to set this field to prevent unintended side-effects.
     *
     * @param parkedHere The boolean value to set on the parkedHere data field.
     */
    protected void setParkedHere(boolean parkedHere){
        this.parkedHere=parkedHere;
    }

    /**
     * Sets the times searched field of this location-A data field used by the ParkingLocation
     * database.
     *
     * Note: This method should only be accessible by the SFParkLocationFactory and
     * LocationDatabaseHandler classes-hence this method has an access modifier of protected.
     * DO NOT SET THIS FIELD FROM ANY OTHER CLASS.
     * Use the SFParkLocationFactory class to set this field to prevent unintended side-effects.
     * @param timesSearched
     */
    protected void setTimesSearched(int timesSearched){
        this.timesSearched=timesSearched;
    }

    /*
    public String toString(){
        return "\t (Times Searched: "+getTimesSearched()+") "+"Name: "+getName()+"\t Desc: "+getDesc()
                +"\n\t Location: "+getCoords()+" \tOrigin: "+this.originLocation.toString()
                +" \n\t\t Radius: "+this.radiusFromOrigin+" \tHas Street Parking: "
                +this.hasOnStreetParking+" \tOSPID: "+getOspid()+" \tBFID: "+getBfid()
                +" \tIsFavorite: "+isFavorite()+" Parked Here: "+this.parkedHere
                +" User Defined: "+this.isUserDefined+" Theft probability: "+this.theftProbability+"%";
    }
    */

    public String toString(){
        return "Times Searched: " + getTimesSearched()+ "\n"
                //+ "Name: " + getName()+ "\n"
                //+ "Desc: " + getDesc() + "\n"
                + "Location: " + getCoords()+ "\n"
                + "Origin: " + this.originLocation.toString() + "\n"
                + "Radius: " + this.radiusFromOrigin+" miles\n"
                + "Has Street Parking: " + this.hasOnStreetParking+ "\n"
//                + "OSPID: " + getOspid()+ "\n"
//                + "BFID: " + getBfid() + "\n"
                + "IsFavorite: " + isFavorite() + "\n"
                + "Parked Here: " + this.parkedHere + "\n"
                + "User Defined: " + this.isUserDefined + "\n"
                //+ "Theft probability: " + this.theftProbability;
                + "Theft probability: " + ((int)(this.theftProbability * 100)) / 100.0;
    }





}
