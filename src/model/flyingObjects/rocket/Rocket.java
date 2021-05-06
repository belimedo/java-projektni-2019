package model.flyingObjects.rocket;

import controller.config.ConfigReader;
import model.airspaceMap.Airspace;
import model.flyingObjects.FlyingObject;
import model.interfaces.IRocketMovement;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public abstract class Rocket extends FlyingObject implements IRocketMovement {

    private static Logger logger;
    private static Handler fileHandler;

    static {

        logger= Logger.getLogger(Airspace.class.getName());
        try {
            fileHandler=new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath() + File.separator +Airspace.class.getSimpleName()+".log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    // Range in field sections
    private int range;

    protected Rocket() {
        super();
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range=range;
    }

    @Override
    public void run() {

        while(range>0 && this.isFlying()) {
            try {
                sleep(this.getSpeed());
                fly(this);
                range--; //Every rocket has a range, so we need to decrement it to ensure it explodes somewhere
            }
            catch (InterruptedException ex) {
                logger.log(Level.SEVERE,ex.getMessage(),ex);
            }
        }
    }

}
