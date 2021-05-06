package model.flyingObjects;

import controller.config.ConfigReader;
import javafx.scene.paint.Paint;
import model.airspaceMap.Airspace;
import model.interfaces.IMovement;
import model.util.Direction;
import simulation.TestSimulation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.*;

public abstract class FlyingObject extends Thread implements IMovement, Serializable {

    private static Logger logger;
    private static Handler fileHandler;

    static {
        logger=Logger.getLogger(FlyingObject.class.getName());
        try {
            fileHandler=new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath()+ File.separator +FlyingObject.class.getSimpleName()+".log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private int altitude;
    private long speed;
    private int positionX;
    private int positionY;
    private int startX;
    private int startY;
    private Direction direction;
    private boolean flying;

    /* Variables needed for GUI */
    protected String markingText;
    protected transient Paint markingPaint;

    /* In constructor, we ensure that every flying object has random speed(that is in given range), random altitude and random direction*/
    protected FlyingObject() {

        Random rand=new Random();
        speed=1000+rand.nextInt(2001);
        altitude=rand.nextInt(ConfigReader.getConfigReaderInstance().getNumberOfAltitudes());
        direction=Direction.randomDirection();
    }

    @Override
    public boolean equals(Object obj) {

        FlyingObject other=(FlyingObject)obj;
        if(other.getPositionX()==this.getPositionX() && other.getPositionY()==this.getPositionY() && other.getAltitude()== this.getAltitude()
        && other.getMarkingText()==this.markingText && other.getStartX()==this.startX && other.getStartY()==this.startY && other.getSpeed()==this.speed)
            return true;
        else
            return false;
    }

    // Method that makes everything fly
    @Override
    public void run(){
        Airspace airspace= Airspace.getAirspaceInstance();
        positionX=startX;
        positionY=startY;
        flying=true;
        Airspace.getAirspaceInstance().getMap()[positionX][positionY].addAircraftToTheField(this);

        // While object is flying and while is in range of matrix
        while(flying && !TestSimulation.isOver) {
            try {
                sleep(speed);
                fly(this);
            }
            catch (InterruptedException ex) {
                logger.log(Level.SEVERE, ex.getMessage(),ex);
            }
        }

        Airspace.getAirspaceInstance().getMap()[positionX][positionY].removeAircraftFromTheField(this);
    }

    @Override
    public String toString(){
        return String.format("%s#%s#%s#%s#%s",this.getClass().getSimpleName(),
                Integer.toString(positionX),Integer.toString(positionY), direction,Integer.toString(altitude));
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }


    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        this.flying = flying;
    }

    public String getMarkingText() {
        return markingText;
    }

    public void setMarkingText(String markingText) {
        this.markingText = markingText;
    }

    public Paint getMarkingPaint() {
        return markingPaint;
    }

    public void setMarkingPaint(Paint markingPaint) {
        this.markingPaint = markingPaint;
    }
}
