package model.interfaces;

import controller.config.ConfigReader;
import controller.crash.Crash;
import model.airspaceMap.Airspace;
import model.flyingObjects.FlyingObject;
import model.util.Direction;

import java.util.Calendar;
import java.util.Iterator;

public interface IRocketMovement extends IMovement {

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
            // Flight ban doesn't affect rockets so we don't have that part here
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
                            Airspace.getAirspaceInstance().getMap()[temp.getPositionX()][temp.getPositionY()].removeAircraftFromTheField(temp);
                            Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].removeAircraftFromTheField(fo);
                            Airspace.getAirspaceInstance().getMap()[posX][posY].removeAircraftFromTheField(fo);
                        }
                    }
                }
            }
        }
    }

}


