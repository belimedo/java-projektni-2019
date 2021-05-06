package model.airspaceMap;

import model.flyingObjects.FlyingObject;

import java.util.HashSet;
import java.util.Set;

public class Field {

    private Set<FlyingObject> aircrafts;

    public Field() {
        aircrafts=new HashSet<>();
    }

    public synchronized boolean addAircraftToTheField(FlyingObject aircraft) {
        for(FlyingObject fo: aircrafts){
            if(fo.getAltitude()==aircraft.getAltitude())
                return false;
        }
            aircrafts.add(aircraft);
            return true;
    }


    // This method removes aircarft from the field, it is synchronized
    public synchronized boolean removeAircraftFromTheField(FlyingObject aircraft) {
        return aircrafts.remove(aircraft);
    }

    public synchronized Set<FlyingObject> getAircraftsOnField() {
        return aircrafts;
    }

}
