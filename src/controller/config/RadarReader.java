package controller.config;

import controller.watcher.IModifiable;
import controller.watcher.Watcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RadarReader implements IModifiable {

    private static RadarReader radarReaderInstance;
    private static Logger logger;
    private static FileHandler fileHandler;

    static {
        logger = Logger.getLogger(ConfigReader.class.getName());
        try {
            fileHandler=new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath()+File.separator+RadarReader.class.getSimpleName()+".log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private double scanTime;
    private String mapFileName;
    private String mapFolderPath;
    private String eventsFolderPath;
    private String mapFilePath;
    private final String radarPropertiesFilePath="properties"+File.separator+"radar.properties"; //default radar.properties path

    private RadarReader() {

        readProperties();
        checkProperties();

        Watcher radarWatcher = null;
        try {
            radarWatcher = new Watcher(this, (new File(ConfigReader.getConfigReaderInstance().getPropertiesFilePath())).getParentFile().getAbsolutePath(),
                    this.getClass().getDeclaredMethod("updateRadarProperties", String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        radarWatcher.setDaemon(true);
        radarWatcher.start();

    }

    public static synchronized RadarReader getRadarReaderInstance() {
        if(radarReaderInstance==null)
            return radarReaderInstance=new RadarReader();
        return radarReaderInstance;
    }

    private synchronized void readProperties() {

        try(BufferedInputStream read=new BufferedInputStream(new FileInputStream(radarPropertiesFilePath))) {
            Properties prop=new Properties();
            prop.load(read);
            scanTime=Double.parseDouble(prop.getProperty("scan_time"));
            mapFileName=prop.getProperty("save_file");
            mapFolderPath=prop.getProperty("save_file_path");
            eventsFolderPath=prop.getProperty("intruder_events_path");
            mapFilePath=mapFolderPath+File.separator+mapFileName;
        }
        catch (NumberFormatException ex) {
            logger.log(Level.SEVERE,"Check your radar.properties file.\n"+ex.getMessage(),ex);
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }

    public synchronized void updateRadarProperties(String fileName) {

        if(!"radar.properties".equals(fileName))
            return;
        readProperties();
        checkProperties();
    }

    private synchronized void checkProperties() {
        if(scanTime<0.2) {
            scanTime=1;
            logger.log(Level.WARNING,"Scan time is invalid! It has been set to default value of 0.2 seconds. ");
        }
    }

    public double getScanTime() {
        return scanTime;
    }

    public String getMapFileName() {
        return mapFileName;
    }

    public String getMapFolderPath() {
        return mapFolderPath;
    }

    public String getEventsFolderPath() {
        return eventsFolderPath;
    }

    public String getMapFilePath() {
        return mapFilePath;
    }

    public void setMapFilePath(String mapFilePath) {
        this.mapFilePath = mapFilePath;
    }
}
