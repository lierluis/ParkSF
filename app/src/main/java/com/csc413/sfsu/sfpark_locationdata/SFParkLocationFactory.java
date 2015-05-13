package com.csc413.sfsu.sfpark_locationdata;

import com.csc413.sfsu.csc413_parking.MainActivity;
import com.csc413.sfsu.sf_vehicle_crime.SFCrimeHandler;
import com.csc413.sfsu.sfpark_simplified.SFParkQuery;
import com.csc413.sfsu.sfpark_simplified.SFParkXMLResponse;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * The SFParkLocationFactory class is responsible for retrieving and storing location data from the
 * SFParkSimplified API.
 *
 * Currently the SFParkLocationFactory should be the only point of entry into the
 * LocationDatabaseHandler class. If the timesSearched field of
 * ParkingLocation objects are set manually and added to the database manually, the database will
 * not properly remove the least searched locations.
 *
 */
public class SFParkLocationFactory
{
    private LocationDatabaseHandler db;
    private MainActivity context;

    public SFParkLocationFactory(MainActivity context){
        this.db=new LocationDatabaseHandler(context);
        this.context=context;
    }

    /**
     * Retrieves all parking locations from SFPark within the specified radius of the origin as an
     * array list of discrete ParkingLocation objects.
     *
     * This method will also add or update all locations found to an internal database.
     *
     * If the internal database reaches capacity, the least searched locations are deleted.
     *
     * @param origin Center of search for parking locations.
     * @param radius radius to search for parking locations in miles.
     * @return list of ParkingLocation objects within the radius of the origin. This list will
     * include user defined locations.
     */
    public List<ParkingLocation> getParkingLocations(LatLng origin, double radius){
        List <ParkingLocation> locationList=new ArrayList<ParkingLocation>();
        List<LatLng>existingOrigins=this.db.isWithinToleranceOfOrigins(origin);
        if(existingOrigins.isEmpty()) {
            SFParkQuery query = new SFParkQuery();
            query.setLatitude(origin.latitude);
            query.setLongitude(origin.longitude);
            query.setRadius(radius);
            query.setUnitOfMeasurement("MILE");

            SFParkXMLResponse response = new SFParkXMLResponse();
            boolean success = response.populate(query);


            String status = response.status();
            if (success) {
                String message = response.message();
                int numRecords = response.numRecords();

                double crimeRadius=.05;
                int startYear=2011;
                int count=100;
                int offset=0;
                double crimeProb=0.0;

                SFCrimeHandler crimeHandler = new SFCrimeHandler(); /* Initialize empty handler */
                success = crimeHandler.generateReports(origin, crimeRadius, startYear, count, offset);
                /* Retrieve report data on a successful query */
                if (success) {
                    crimeProb=((double)crimeHandler.numReports()/13.3109243697);
                }


                for (int i = 0; i < numRecords; i++) {
                    LatLng coords = new LatLng(
                            response.avl(i).loc().latitude(0), response.avl(i).loc().longitude(0));
                    String name = response.avl(i).name();
                    boolean hasOnStreetParking = (response.avl(i).type().equals("ON")) ? true : false;
                    String desc = response.avl(i).desc();
                    int ospid = response.avl(i).ospid();
                    int bfid = response.avl(i).bfid();
                    boolean isFavorite = false;
                    int timesSearched = 1;
                    boolean parkedHere = false;
                    boolean isUserDefined = false;

                    ParkingLocation loc = new ParkingLocation(origin, radius, hasOnStreetParking, name,
                            desc, ospid, bfid, coords, isFavorite, timesSearched, parkedHere,
                            isUserDefined, crimeProb);

                    this.db.addLocation(loc);

                    if (loc.hasOnStreetParking()) {
                        locationList.add(db.getLocationFromBFID(bfid));
                    } else {
                        locationList.add(db.getLocationFromOSPID(ospid));
                    }


                }


            }
        }

        else{
            System.out.println("Found locations using database.");
            locationList.addAll(this.db.getLocsFromOrigin(existingOrigins));
        }

        List<ParkingLocation> udl=db.getUserDefinedWithinRadius(origin, radius);

        if(!udl.isEmpty()){
            locationList.addAll(udl);
        }


        return locationList;
    }

    /**
     * Adds a user defined location to the database. The only notable information in User defined
     * location is the coords data field which is set to the parameter passed to this method.
     * @param udl The location of the user defined location to be added to the database.
     */
    public ParkingLocation addUserDefinedLocation(LatLng udl){
        LatLng origin=udl;
        double radius=.25;
        String name="Past parking location.";
        boolean hasOnStreetParking=true;
        String desc="A user defined parking location.";
        int ospid=-1;
        int bfid=-1;
        boolean isFavorite=false;
        int timesSearched=1;
        boolean parkedHere=false;
        boolean isUserDefined=true;

        double crimeProb=0.0;
        double crimeRadius=.05;
        int startYear=2011;
        int count=100;
        int offset=0;


        SFCrimeHandler crimeHandler = new SFCrimeHandler(); /* Initialize empty handler */
        boolean success = crimeHandler.generateReports(origin, crimeRadius, startYear, count, offset);
                /* Retrieve report data on a successful query */
        if (success) {
            crimeProb=(double)crimeHandler.numReports()/13.3109243697;
        }

        ParkingLocation loc=new ParkingLocation(origin, radius, hasOnStreetParking, name,
                desc, ospid, bfid, udl, isFavorite, timesSearched, parkedHere,
                isUserDefined, crimeProb);
        db.addLocation(loc);
        return loc;
    }

    /**
     * Toggles the isFavorite field of the database to be the opposite of its current state.
     *
     * Note that this method will not update any data other than the isFavorite field, even if data
     * in the parameter location is different than in the database. This is to prevent unintended
     * side effects.
     *
     * The opposite of the current value of isFavorite in the database will
     * ALWAYS be used, regardless of the parameter values. It is a good idea for the user of this
     * method to store the return value, to keep the data updated.
     *
     * @param location The location to toggle the isFavorite field.
     * @return The updated location object data after toggling the favorite field. If the location
     * did not exist in the database, the return value will be null.
     */
    public ParkingLocation toggleFavorite(ParkingLocation location){
        if(db.getNumFav()>10){ //too many favorites in db.
            return null;
        }
        //Get database location data first, so as not to overwrite values other than the isFavorite
        if(location.hasOnStreetParking()){
            location=db.getLocationFromBFID(location.getBfid());
        }
        else{
            location=db.getLocationFromOSPID(location.getOspid());
        }

        if(location==null){ //location did not exist in database. Return location unchanged.
            return null;
        }

        location.setIsFavorite(!location.isFavorite());
        db.updateLocation(location);

        return location;

    }


    /**
     * Toggles the parkedHere field of the database to be the opposite of its current state.
     *
     * Note that this method will not update any data other than the isFavorite field, even if data
     * in the parameter location is different than in the database. This is to prevent unintended
     * side effects.
     *
     * The opposite of the current value of isFavorite in the database will
     * ALWAYS be used, regardless of the parameter values. It is a good idea for the user of this
     * method to store the return value, to keep the data updated. If the return value is null this
     * means the location no longer exists in the database. In this case, it is a good idea to call
     * accessor methods for updated lists of locations that currently exist in the database.
     *
     * @param location The location to toggle the parkedHere field.
     * @return The updated location object data after toggling the parkedHere field. If the location
     * did not exist in the database, the return value will be null.
     */
    public ParkingLocation toggleParkedHere(ParkingLocation location){
        if(db.getNumParkedHere()>20){ //delete parked here location to make room
            db.parkedHereToDelete.setParkedHere(false);
            db.updateLocation(db.parkedHereToDelete);
            db.updateparkedHereCount();
        }

        //Get database location data first, so as not to overwrite values other than the parkedHere
        if(location.hasOnStreetParking()){
            location=db.getLocationFromBFID(location.getBfid());
        }
        else{
            location=db.getLocationFromOSPID(location.getOspid());
        }

        if(location==null){ //location did not exist in database. Return location unchanged.
            return null;
        }

        location.setParkedHere(!location.getParkedHere());
        db.updateLocation(location);

        return location;
    }


    /**
     * Updates the list of locations with the current data stored in the database.
     * This method should be called whenever the data in the database may have changed, especially
     * if the locations in the list are being accessed through more than one entry point.
     *
     * Note that if a location in the parameter list does not exist in the database, the original
     * location object parameters will be returned unchanged. It is advisable to only maintain
     * lists of locations for short periods of time, unless the locations are set as favorites or
     * parkedHere.
     *
     *
     * @param locs The array list of ParkingLocations to update
     * @return An array list of ParkingLocations equal to the parameter list, but with updated data.
     *
     */
    public List<ParkingLocation> updateDataFromDatabase(List<ParkingLocation> locs){
        List<ParkingLocation>updatedList=new ArrayList<ParkingLocation>();
        for(int i=0; i<locs.size(); i++){
            ParkingLocation locToUpdate=locs.get(i);
            ParkingLocation updatedLocation;
            if(locToUpdate.hasOnStreetParking()){
                updatedLocation=db.getLocationFromBFID(locToUpdate.getBfid());
            }
            else{
                updatedLocation=db.getLocationFromOSPID(locToUpdate.getOspid());
            }

            if(updatedLocation==null){ //didn't exist in Database. Return location unchanged.
                updatedLocation=locToUpdate;
            }
            updatedList.add(updatedLocation);
        }

        return updatedList;
    }

    /**
     * Retrieves all items in the database that are favorites.
     *
     * @return An array list of ParkingLocation objects with isFavorite set to true from the
     * database.
     */
    public List<ParkingLocation> getFavorites(){
        return this.db.getFavorites();
    }


    /**
     * Retrieves all items in the database that are parked locations.
     *
     * @return An array list of ParkingLocation objects with parkedHere set to true from the
     * database.
     */
    public List<ParkingLocation> getParkedLocations(){
        return this.db.getParkedLocations();
    }

    /**
     * Retrieves all items in the database that are parked user defined locations.
     *
     * @return An array list of ParkingLocation objects with the userDefined location field set
     * to true.
     */
    public List<ParkingLocation> getUserDefinedLocations(){ return this.db.getUserDefinedLocations();}

    /**
     * Retrieves the number of entries in the location database
     * @return An integer value of the number of items in the database.
     */
    public int getLocationCount(){ return this.db.getLocationsCount();}


    /**
     * A debugging method, not for use in a production environment for security reasons.
     * Prints the values of all locations in the database to the console.
     */
    public void printAllDB(){

        System.out.println("---------------Current Database Contents---------------");
        List<ParkingLocation> l=db.getAllLocations();
        if (db.getLocationsCount()==0){
            System.out.println("    The parking location database is empty.");
        }
        else{
            System.out.println("    There are "+db.getLocationsCount()+" entries in the database.");
            for(int i=0; i<db.getLocationsCount(); i++){
                System.out.println("Entry "+(i+1)+": ");
                ParkingLocation p=l.get(i);
                System.out.println(p.toString());
                System.out.println("----------");
            }
        }

    }


    /**
     * A debugging method, not for use in a production environment for security reasons.
     * Prints the values of all favorite locations in the database to the console.
     */
    public void printFav(){

        System.out.println("---------------Current Favorites---------------");
        List<ParkingLocation> l=this.getFavorites();
        if (l.size()==0){
            System.out.println("    There are no favorites.");
        }
        else{
            System.out.println("    There are "+l.size()+" favorites.");
            for(int i=0; i<l.size(); i++){
                System.out.println("Entry "+(i+1)+": ");
                ParkingLocation p=l.get(i);
                System.out.println(p.toString());
                System.out.println("----------");
            }
        }

    }


    /**
     * A debugging method, not for use in a production environment for security reasons.
     * Prints the values of all parkedHere locations in the database to the console.
     */

    public void printParkedHere(){

        System.out.println("---------------Current Parked Here locations---------------");
        List<ParkingLocation> l=this.getParkedLocations();
        if (l.size()==0){
            System.out.println("    There are no parked here locations.");
        }
        else{
            System.out.println("    There are "+l.size()+" parked locations.");
            for(int i=0; i<l.size(); i++){
                System.out.println("Entry "+(i+1)+": ");
                ParkingLocation p=l.get(i);
                System.out.println(p.toString());
                System.out.println("----------");
            }
        }

    }


    /**
     * A debugging method, not for use in a production environment for security reasons.
     * For running test code to validate consistency of data.
     */
    public void testDB(){


    }







}
