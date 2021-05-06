package model.airspaceMap;

import controller.config.ConfigReader;
import model.flyingObjects.FlyingObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

public class Airspace {

    private static Airspace airspaceInstance;
    private static Logger logger;
    private static Handler fileHandler;

    static {
        logger=Logger.getLogger(Airspace.class.getName());
        try {
            fileHandler=new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath()+ File.separator +Airspace.class.getSimpleName()+".log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Field[][] map;
    private int width;
    private int height;


    private Set<FlyingObject> enemiesOnMap=new HashSet<>();



    private Airspace() {
        width=ConfigReader.getConfigReaderInstance().getWidth();
        height=ConfigReader.getConfigReaderInstance().getHeight();
        // Map initialization
        map=new Field[height][width];
        for(int i=0;i<height;i++) {
            for(int j=0;j<width;j++)
                map[i][j]=new Field();
        }

    }


    public static synchronized Airspace getAirspaceInstance() {
        if (airspaceInstance==null)
            return airspaceInstance=new Airspace();
        else
            return airspaceInstance;
    }

    public Field[][] getMap() {
        return map;
    }

    public void setMap(Field[][] map) {
        this.map = map;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }



    public synchronized Set<FlyingObject> getEnemiesOnMap() {
        return enemiesOnMap;
    }

    public void setEnemiesOnMap(Set<FlyingObject> enemiesOnMap) {
        this.enemiesOnMap = enemiesOnMap;
    }


}
