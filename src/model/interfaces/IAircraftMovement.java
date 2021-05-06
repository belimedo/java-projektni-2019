package model.interfaces;

import controller.crash.Crash;
import controller.config.ConfigReader;
import model.airspaceMap.Airspace;
import model.flyingObjects.FlyingObject;
import model.util.Direction;

import java.util.Calendar;
import java.util.Iterator;


public interface IAircraftMovement extends IMovement {

    @Override
    default void fly(FlyingObject fo) {

            synchronized (fo) {

                if (fo.getDirection() == Direction.LEFT && fo.getPositionY() == 0) {
                    fo.setFlying(false);
                    Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].removeAircraftFromTheField(fo);
                    return;
                }
                if (fo.getDirection() == Direction.RIGHT && fo.getPositionY() == Airspace.getAirspaceInstance().getWidth() - 1) {
                    fo.setFlying(false);
                    Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][Airspace.getAirspaceInstance().getWidth() - 1].removeAircraftFromTheField(fo);
                    return;
                }
                if (fo.getDirection() == Direction.UP && fo.getPositionX() == 0) {
                    fo.setFlying(false);
                    Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].removeAircraftFromTheField(fo);
                    return;
                }
                if (fo.getDirection() == Direction.DOWN && fo.getPositionX() == Airspace.getAirspaceInstance().getHeight() - 1) {
                    fo.setFlying(false);
                    Airspace.getAirspaceInstance().getMap()[Airspace.getAirspaceInstance().getHeight() - 1][fo.getPositionY()].removeAircraftFromTheField(fo);
                    return;
                }

                int posX = fo.getPositionX();
                int posY = fo.getPositionY();
                if (!fo.isFlying()) {
                    Airspace.getAirspaceInstance().getMap()[posX][posY].removeAircraftFromTheField(fo);
                    return;
                }
                if (ConfigReader.getConfigReaderInstance().isFlightBan())
                    fo.setDirection(flightBanRoute(fo)); // Direction is set here because this method is synchronized
                if (fo.moveToNextField(fo)) {
                    Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].addAircraftToTheField(fo);
                    Airspace.getAirspaceInstance().getMap()[posX][posY].removeAircraftFromTheField(fo);
                }
                // If we can't move to the next field, we have crashed, so we need to save that crash
                else {
                    synchronized (Airspace.getAirspaceInstance().getMap()) {

                        Iterator<FlyingObject> iterator = Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].getAircraftsOnField().iterator();
                        FlyingObject temp = null;
                        while (iterator.hasNext()) {
                            if ((temp = iterator.next()).getAltitude() == fo.getAltitude() && !temp.equals(fo)) {
                                Crash.writeCrash("Crash happened between two aircrafts.", Calendar.getInstance().getTime().toString(), fo.getPositionX(), fo.getPositionY(), fo, temp);
                                temp.setFlying(false);
                                fo.setFlying(false);
                                Airspace.getAirspaceInstance().getEnemiesOnMap().remove(temp);
                                Airspace.getAirspaceInstance().getEnemiesOnMap().remove(fo);
                                iterator.remove();
                                Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].removeAircraftFromTheField(fo);
                                Airspace.getAirspaceInstance().getMap()[posX][posY].removeAircraftFromTheField(fo);
                            }
                        }
                    }
                }
            }
        }

    // This method determines which route is fastest to get out when Flight ban is on
    default Direction flightBanRoute(FlyingObject fo) {

        Direction flightBanDirection=Direction.UP;
        Airspace airspace=Airspace.getAirspaceInstance();
        int width=airspace.getWidth()-1;
        int height=airspace.getHeight()-1;
        int posX=fo.getPositionX();
        int posY=fo.getPositionY();
        int min=posX;
        if(posY<min) {
            min=posY;
            flightBanDirection=Direction.LEFT;
        }
        if(height-posX<min) {
            min = height - posX;
            flightBanDirection=Direction.DOWN;
        }
        if(width-posY<min) {
            flightBanDirection = Direction.RIGHT;
        }
        // This is to make sure that we don't turn in the same field but rather have turning maneuver
        if((fo.getDirection()==Direction.UP && flightBanDirection==Direction.DOWN) || (fo.getDirection()==Direction.DOWN && flightBanDirection==Direction.UP))
            return Direction.RIGHT;
        if((fo.getDirection()==Direction.LEFT && flightBanDirection==Direction.RIGHT) || (fo.getDirection()==Direction.RIGHT && flightBanDirection==Direction.LEFT))
            return Direction.UP;
        return flightBanDirection;
    }

}
