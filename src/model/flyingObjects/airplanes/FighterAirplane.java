package model.flyingObjects.airplanes;

import controller.crash.Crash;
import controller.config.ConfigReader;
import javafx.scene.paint.Paint;
import model.airspaceMap.Airspace;
import model.flyingObjects.Aircraft;
import model.flyingObjects.FlyingObject;
import model.interfaces.IMilitary;
import model.util.Direction;

import java.util.Iterator;

public class FighterAirplane extends Airplane implements IMilitary {

    private boolean armed=true;
    private boolean enemy=false;

    public FighterAirplane() {

        super();
        markingText="M-FA";
        if(enemy)
            markingPaint= Paint.valueOf("darkred");
        else
            markingPaint=Paint.valueOf("darkgreen");
    }

    public FighterAirplane(String model) {

        super(model);
        markingText="M-FA";
        if(enemy)
            markingPaint= Paint.valueOf("darkred");
        else
            markingPaint=Paint.valueOf("darkgreen");
    }

    /*
     * In here we will override fly method, so that fighterAirplane searches for enemy aircraft while it is moving,
     * so in addition of crash,we will have destruction of enemy aircraft
     */
    @Override
    public void fly(FlyingObject fo) {

        synchronized (fo) {
            searchEnemyForDestruction(fo);

            if (fo.getDirection() == Direction.LEFT && fo.getPositionY() == 0) {
                fo.setFlying(false);
                Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].removeAircraftFromTheField(fo);
                if (fo instanceof IMilitary && ((IMilitary) fo).isEnemy()) {
                    Airspace.getAirspaceInstance().getEnemiesOnMap().remove(fo);
                    if (Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty())
                        ConfigReader.setFlightBanProperty(false);
                }
                return;
            }
            if (fo.getDirection() == Direction.RIGHT && fo.getPositionY() == Airspace.getAirspaceInstance().getWidth() - 1) {
                fo.setFlying(false);
                Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][Airspace.getAirspaceInstance().getWidth() - 1].removeAircraftFromTheField(fo);
                if (fo instanceof IMilitary && ((IMilitary) fo).isEnemy()) {
                    Airspace.getAirspaceInstance().getEnemiesOnMap().remove(fo);
                    if (Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty())
                        ConfigReader.setFlightBanProperty(false);
                }
                return;
            }
            if (fo.getDirection() == Direction.UP && fo.getPositionX() == 0) {
                fo.setFlying(false);
                Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].removeAircraftFromTheField(fo);
                if (fo instanceof IMilitary && ((IMilitary) fo).isEnemy()) {
                    Airspace.getAirspaceInstance().getEnemiesOnMap().remove(fo);
                    if (Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty())
                        ConfigReader.setFlightBanProperty(false);
                }
                return;
            }
            if (fo.getDirection() == Direction.DOWN && fo.getPositionX() == Airspace.getAirspaceInstance().getHeight() - 1) {
                fo.setFlying(false);
                Airspace.getAirspaceInstance().getMap()[Airspace.getAirspaceInstance().getHeight() - 1][fo.getPositionY()].removeAircraftFromTheField(fo);
                if (fo instanceof IMilitary && ((IMilitary) fo).isEnemy()) {
                    Airspace.getAirspaceInstance().getEnemiesOnMap().remove(fo);
                    if (Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty())
                        ConfigReader.setFlightBanProperty(false);
                }
                return;
            }

            int posX = fo.getPositionX();
            int posY = fo.getPositionY();

            if (!fo.isFlying()) {
                Airspace.getAirspaceInstance().getMap()[posX][posY].removeAircraftFromTheField(fo);
                return;
            }
            // Flight ban doesn't affect FighterAirplane
            if (fo.moveToNextField(fo)) {
                Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].addAircraftToTheField(fo);
                Airspace.getAirspaceInstance().getMap()[posX][posY].removeAircraftFromTheField(fo);
            } else {
                synchronized (Airspace.getAirspaceInstance().getMap()) {
                    Iterator<FlyingObject> iterator = Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].getAircraftsOnField().iterator();
                    FlyingObject temp = null;
                    while (iterator.hasNext()) {
                        if ((temp = iterator.next()).getAltitude() == fo.getAltitude() && !temp.equals(fo)) {
                            Crash.writeCrash("Crash happened between two aircrafts.", Long.toString(System.currentTimeMillis()), fo.getPositionX(), fo.getPositionY(), fo, temp);
                            temp.setFlying(false);
                            fo.setFlying(false);
                            Airspace.getAirspaceInstance().getEnemiesOnMap().remove(temp);
                            Airspace.getAirspaceInstance().getEnemiesOnMap().remove(fo);

                            if (Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty()) //If there aren't any enemy aircraft, remove flight ban
                                ConfigReader.setFlightBanProperty(false);

                            iterator.remove();
                            Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY()].removeAircraftFromTheField(fo);
                            Airspace.getAirspaceInstance().getMap()[posX][posY].removeAircraftFromTheField(fo);
                        }
                    }
                }
            }
        }

    }



    private synchronized void searchEnemyForDestruction(FlyingObject fo) {
        // First to make sure that this isn't enemy aircraft
        if (fo instanceof IMilitary && !((IMilitary) fo).isEnemy()) {
            synchronized (Airspace.getAirspaceInstance().getMap()) {
                FlyingObject temp;
                boolean destroyed = false;
                // This is if enemy aircraft is below our aircraft and we are not in lowest row
                if (fo.getPositionX() + 1 < Airspace.getAirspaceInstance().getHeight() &&
                        Airspace.getAirspaceInstance().getMap()[fo.getPositionX() + 1][fo.getPositionY()].getAircraftsOnField().stream().anyMatch(aircraft ->
                                (aircraft instanceof IMilitary && ((IMilitary) aircraft).isEnemy()) && aircraft.getAltitude() == fo.getAltitude())) {
                    Iterator<FlyingObject> iterator = Airspace.getAirspaceInstance().getMap()[fo.getPositionX() + 1][fo.getPositionY()].getAircraftsOnField().iterator();
                    while (iterator.hasNext() && !destroyed) {
                        if ((temp = iterator.next()) instanceof IMilitary && ((IMilitary) temp).isEnemy()) {
                            Airspace.getAirspaceInstance().getMap()[fo.getPositionX() + 1][fo.getPositionY()].removeAircraftFromTheField(temp);
                            temp.setFlying(false);
                            Airspace.getAirspaceInstance().getEnemiesOnMap().remove(temp);
                            destroyed = true;
                            if(Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty())
                                ConfigReader.setFlightBanProperty(false);
                        }
                    }
                }
                // If enemy aircraft is above us
                else if (fo.getPositionX() - 1 >= 0 &&
                        Airspace.getAirspaceInstance().getMap()[fo.getPositionX() - 1][fo.getPositionY()].getAircraftsOnField().stream().anyMatch(aircraft ->
                                (aircraft instanceof IMilitary && ((IMilitary) aircraft).isEnemy()) && aircraft.getAltitude() == fo.getAltitude())) {
                    Iterator<FlyingObject> iterator = Airspace.getAirspaceInstance().getMap()[fo.getPositionX() - 1][fo.getPositionY()].getAircraftsOnField().iterator();
                    while (iterator.hasNext() && !destroyed) {
                        if ((temp = iterator.next()) instanceof IMilitary && ((IMilitary) temp).isEnemy()) {
                            Airspace.getAirspaceInstance().getMap()[fo.getPositionX() - 1][fo.getPositionY()].removeAircraftFromTheField(temp);
                            temp.setFlying(false);
                            Airspace.getAirspaceInstance().getEnemiesOnMap().remove(temp);
                            destroyed = true;
                            if(Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty())
                                ConfigReader.setFlightBanProperty(false);
                        }
                    }
                }
                // If enemy aircraft is right from us
                else if (fo.getPositionY() + 1 < Airspace.getAirspaceInstance().getWidth() &&
                        Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY() + 1].getAircraftsOnField().stream().anyMatch(aircraft ->
                                (aircraft instanceof IMilitary && ((IMilitary) aircraft).isEnemy()) && aircraft.getAltitude() == fo.getAltitude())) {
                    Iterator<FlyingObject> iterator = Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY() + 1].getAircraftsOnField().iterator();
                    while (iterator.hasNext() && !destroyed) {
                        if ((temp = iterator.next()) instanceof IMilitary && ((IMilitary) temp).isEnemy()) {
                            Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY() + 1].removeAircraftFromTheField(temp);
                            temp.setFlying(false);
                            Airspace.getAirspaceInstance().getEnemiesOnMap().remove(temp);
                            destroyed = true;
                            if(Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty())
                                ConfigReader.setFlightBanProperty(false);
                        }
                    }
                }
                // If enemy aircraft is left from us
                else if (fo.getPositionY() - 1 >=0 &&
                        Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY() - 1].getAircraftsOnField().stream().anyMatch(aircraft ->
                                (aircraft instanceof IMilitary && ((IMilitary) aircraft).isEnemy()) && aircraft.getAltitude() == fo.getAltitude())) {
                    Iterator<FlyingObject> iterator = Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY() - 1].getAircraftsOnField().iterator();
                    while (iterator.hasNext() && !destroyed) {
                        if ((temp = iterator.next()) instanceof IMilitary && ((IMilitary) temp).isEnemy()) {
                            Airspace.getAirspaceInstance().getMap()[fo.getPositionX()][fo.getPositionY() - 1].removeAircraftFromTheField(temp);
                            temp.setFlying(false);
                            Airspace.getAirspaceInstance().getEnemiesOnMap().remove(temp);
                            destroyed = true;
                            if(Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty())
                                ConfigReader.setFlightBanProperty(false);
                        }
                    }
                }
            }
        }
    }

    public void attackArialTarget() {
        System.out.println("Atacking arial target... ");
    }

    @Override
    public boolean isArmed() {
        return armed;
    }

    @Override
    public void setArmed(boolean armed) {
        this.armed=armed;
    }

    @Override
    public boolean isEnemy() {
        return enemy;
    }

    @Override
    public void setEnemy(boolean enemy) {
        this.enemy=enemy;
        markingPaint= Paint.valueOf("darkred");
    }
}
