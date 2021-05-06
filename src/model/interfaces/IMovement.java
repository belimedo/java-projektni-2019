package model.interfaces;

import model.airspaceMap.Airspace;
import model.flyingObjects.FlyingObject;


public interface IMovement {
    /*
     * This is an abstract method that is used for specifics of flying of different objects
     */
    void fly(FlyingObject fo);

    /*
     * This method gives us information if our flying object can move to the next field on the map. It is universal method for
     * both rockets and aircrafts, and it is used in implementation of fly method that is overrided in specific interfaces. It
     * needs to be synchronized. This method will change position of aircraft.
     */
    default boolean moveToNextField(FlyingObject fo) {

        synchronized (fo) {
            synchronized (Airspace.getAirspaceInstance().getMap()) {
                switch (fo.getDirection()) {
                    // By moving left we are actually moving on X axis going from highest index to the zero
                    case LEFT:
                        if (fo.getPositionY()- 1 >= 0) {
                            fo.setPositionY(fo.getPositionY() - 1);
                            // If there exists another aircraft on that altitude it will crash and it wont move to that field
                            return !Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].getAircraftsOnField().stream().anyMatch(f -> f.getAltitude() == fo.getAltitude());
                        } else
                            return false;
                        // By moving up we are actually moving on Y axis going from highest index to the zero
                    case UP:
                        if (fo.getPositionX()- 1 >=  0) {
                            fo.setPositionX(fo.getPositionX() - 1);
                            return !Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].getAircraftsOnField().stream().anyMatch(f -> f.getAltitude() == fo.getAltitude());
                        } else
                            return false;
                        // By moving right we are actually moving on X axis going from zero index to the highest
                    case RIGHT:
                        if (fo.getPositionY() + 1 < Airspace.getAirspaceInstance().getWidth()) {
                            fo.setPositionY(fo.getPositionY() + 1);
                            return !Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].getAircraftsOnField().stream().anyMatch(f -> f.getAltitude() == fo.getAltitude());
                        } else
                            return false;
                        // By moving down we are actually moving on Y axis going from zero index to the highest
                    case DOWN:
                        if (fo.getPositionX() + 1 < Airspace.getAirspaceInstance().getHeight()) {
                            fo.setPositionX(fo.getPositionX() + 1);
                            return !Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].getAircraftsOnField().stream().anyMatch(f -> f.getAltitude() == fo.getAltitude());
                        } else
                            return false;
                    default:
                        return false;
                }
            }
        }
    }


}
