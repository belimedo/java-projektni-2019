package controller.config;

import controller.watcher.IModifiable;
import controller.watcher.Watcher;

import java.io.*;
import java.util.Properties;
import java.util.logging.*;

// This is going to singleton class
public class ConfigReader implements IModifiable {

    private static ConfigReader configReaderInstance;
    private final static String loggingFolderPath="logging"; // Default logging path;
    private static Logger logger;
    private static Handler fileHandler;

    static {
        /*
        * This is made in static block to ensure we have logging folder created, and after that it will never be called,
        * but that folder will be used by every other class that has logging
        */
        File currentFolder=new File(".");
        File loggingFolder=new File(currentFolder,loggingFolderPath);
        if(!loggingFolder.exists())
            loggingFolder.mkdir();
        logger = Logger.getLogger(ConfigReader.class.getName());
        try {
            fileHandler=new FileHandler(loggingFolder.getPath()+File.separator+ConfigReader.class.getSimpleName()+".log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /*
     * creationTime is double, because that number will be multiplied with 1000 and rounded to ciel value, then casted
     * to long for sleep period
     */
    private double creationTime;
    private int width;
    private int height;
    private int numberOfAltitudes;
    private int rocketRange;
    private int backupPeriod;
    private boolean alliedAircraft;
    private boolean enemyAircraft;
    private boolean flightBan;

    private final String propertiesFilePath = "properties" + File.separator +"config.properties"; // Default properties path
    private String loggingPath=loggingFolderPath;
    private String crashPath;
    private String backupPath;

    private ConfigReader() {

        readProperties();
        checkProperties();

        // This watcher watches over any changes in config.properties file, then calls updateProperties method
        Watcher configWatcher = null;
        try {
            configWatcher = new Watcher(this, (new File(propertiesFilePath)).getParentFile().getAbsolutePath(),
                    this.getClass().getDeclaredMethod("updateProperties", String.class));
        } catch (NoSuchMethodException | SecurityException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        configWatcher.setDaemon(true);
        configWatcher.start();
    }

    public synchronized void updateProperties(String fileName) {
        //If it is some other file in that folder that has changed don't do anything (radar.config)
        if(!"config.properties".equals(fileName)) {
            return;
        }
        readProperties();
        checkProperties();
    }

    public synchronized static ConfigReader getConfigReaderInstance() {

        if(configReaderInstance==null)
            configReaderInstance=new ConfigReader();
        return configReaderInstance;
    }

    private void readProperties() {

        synchronized (propertiesFilePath) {
            File configFile = new File(propertiesFilePath);
            try (BufferedInputStream read = new BufferedInputStream(new FileInputStream(configFile))) {
                Properties prop = new Properties();
                prop.load(read);
                creationTime = Double.parseDouble(prop.getProperty("creation_interval"));
                width = Integer.parseInt(prop.getProperty("map_width"));
                height = Integer.parseInt(prop.getProperty("map_height"));
                numberOfAltitudes = Integer.parseInt(prop.getProperty("altitude_range"));
                rocketRange = Integer.parseInt(prop.getProperty("rocket_range"));
                backupPeriod = Integer.parseInt(prop.getProperty("backup_period"));
                alliedAircraft = Boolean.parseBoolean(prop.getProperty("allied_aircraft"));
                enemyAircraft = Boolean.parseBoolean(prop.getProperty("enemy_aircraft"));
                flightBan = Boolean.parseBoolean(prop.getProperty("flight_ban"));
                crashPath = prop.getProperty("crash_path");
                backupPath = prop.getProperty("backup_path");
            } catch (NumberFormatException ex) {
                logger.log(Level.SEVERE, "Check your config.properties file.\n" + ex.getMessage(), ex);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            return;
        }
    }

    private void checkProperties() {

        synchronized (propertiesFilePath) {
            if (creationTime < 0 || creationTime > 5) {
                creationTime = 2.5;
                logger.log(Level.WARNING, "Creation interval is invalid! It has been set to default value of 2.5 seconds. ");
            }
            if (width < 1) {
                width = 20;
                logger.log(Level.WARNING, "Width is invalid! It has been set to default value of 20. ");
            }
            if (height < 1) {
                height = 20;
                logger.log(Level.WARNING, "Height is invalid! It has been set to default value of 20. ");
            }
            if (numberOfAltitudes < 1) {
                numberOfAltitudes = 4;
                logger.log(Level.WARNING, "Number of altitudes is invalid! It has been set to default value of 4. ");
            }
            if (rocketRange < 1) { // If it is bigger than width or height it doesn't matter because rocket will fly through our airspace
                rocketRange = 20;
                logger.log(Level.WARNING, "Rocket range is invalid! It has been set to default value of 20. ");
            }
            if (backupPeriod < 1) {
                backupPeriod = 60;
                logger.log(Level.WARNING, "Backup period is invalid! It has been set to default value of 60 seconds. ");
            }
        }

    }

    public static void setFlightBanProperty(boolean value) {

        synchronized (ConfigReader.getConfigReaderInstance().getPropertiesFilePath()) {
            if (value == configReaderInstance.flightBan)
                return;
            configReaderInstance.flightBan = value;
            Properties prop = new Properties();
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(configReaderInstance.propertiesFilePath))) {
                prop.load(in);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(configReaderInstance.propertiesFilePath))) {
                prop.setProperty("flight_ban", Boolean.toString(value));
                prop.store(out, null);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public static  void setAlliedAircraftProperty(boolean value) {
        synchronized (ConfigReader.getConfigReaderInstance().getPropertiesFilePath()) {
            if (value == configReaderInstance.alliedAircraft)
                return;
            configReaderInstance.alliedAircraft = value;
            Properties prop = new Properties();
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(configReaderInstance.propertiesFilePath))) {
                prop.load(in);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(configReaderInstance.propertiesFilePath))) {
                prop.setProperty("allied_aircraft", Boolean.toString(value));
                prop.store(out, null);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public static void setEnemyAircraftProperty(boolean value) {
        synchronized (ConfigReader.getConfigReaderInstance().getPropertiesFilePath()) {
            if (value == configReaderInstance.enemyAircraft)
                return;
            configReaderInstance.enemyAircraft = value;
            Properties prop = new Properties();
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(configReaderInstance.propertiesFilePath))) {
                prop.load(in);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(configReaderInstance.propertiesFilePath))) {
                prop.setProperty("enemy_aircraft", Boolean.toString(value));
                prop.store(out, null);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public double getCreationTime() {
        return creationTime;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNumberOfAltitudes() {
        return numberOfAltitudes;
    }

    public int getRocketRange() {
        return rocketRange;
    }

    public int getBackupPeriod() {
        return backupPeriod;
    }

    public boolean isAlliedAircraft() {
        return alliedAircraft;
    }
    // We made setters for boolean values private, because they should be changed only within specified method
    private void setAlliedAircraft(boolean value) {
        alliedAircraft=value;
    }

    public boolean isEnemyAircraft() {
        return enemyAircraft;
    }

    private void setEnemyAircraft(boolean value) {
        enemyAircraft=value;
    }

    public boolean isFlightBan() {
        return flightBan;
    }

    private void setFlightBan(boolean value) {
        flightBan=value;
    }

    public String getLoggingPath() {
        return loggingPath;
    }

    public String getCrashPath() {
        return crashPath;
    }

    public String getPropertiesFilePath() {
        return propertiesFilePath;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }
}
