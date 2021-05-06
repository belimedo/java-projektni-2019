package controller.simulator;

import controller.config.ConfigReader;
import controller.config.RadarReader;
import controller.watcher.IModifiable;
import controller.watcher.Watcher;
import model.airspaceMap.Airspace;
import model.flyingObjects.FlyingObject;
import model.flyingObjects.airplanes.*;
import model.flyingObjects.drone.Drone;
import model.flyingObjects.helicopters.*;
import model.util.Direction;
import simulation.TestSimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Simulator extends Thread implements IModifiable {

    private static Logger logger;
    private static FileHandler fileHandler;

    static {

        File currentFolder=new File(".");
        File eventsFolder=new File(currentFolder,RadarReader.getRadarReaderInstance().getEventsFolderPath());
        File crashFolder=new File(currentFolder, ConfigReader.getConfigReaderInstance().getCrashPath());
        if(!crashFolder.exists())
            crashFolder.mkdir();
        else {
            File[] oldFiles=crashFolder.listFiles();
            for (File f:oldFiles)
                f.delete();
        }
        if(!eventsFolder.exists())  //If doesn't exist, make new directory
            eventsFolder.mkdir();
        else {
            File[] oldFiles=eventsFolder.listFiles();
            for (File f:oldFiles)
                f.delete();
        }
        logger=Logger.getLogger(Simulator.class.getName());
        try {
            fileHandler=new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath()+ File.separator+ RadarReader.class.getSimpleName()+".log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean generateFighters=false;
    private Object locker=new Object();

    public Simulator(){ }

    @Override
    public void run() {
        /*
         * This will initialize watcher for enemy aircraft, then it will call method defendAirspace that will
         * sent two fighter airplanes to destroy enemy.
         */
        Watcher watcher;
        try {
            watcher=new Watcher(this,RadarReader.getRadarReaderInstance().getEventsFolderPath(),
                    this.getClass().getDeclaredMethod("defendAirspace", String.class));
        } catch (NoSuchMethodException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
            return;
        }
        watcher.setDaemon(true);
        watcher.start();

        while(true && !TestSimulation.isOver) {
            synchronized (ConfigReader.getConfigReaderInstance().getPropertiesFilePath()) {
                if (ConfigReader.getConfigReaderInstance().isAlliedAircraft()) {
                    generateAlliedAircraft();
                } else if (ConfigReader.getConfigReaderInstance().isEnemyAircraft()) {
                    generateEnemyAircraft();
                } else if (!ConfigReader.getConfigReaderInstance().isFlightBan())
                    generateRandomAircraft();
            }
            try{
                sleep((long)Math.ceil(ConfigReader.getConfigReaderInstance().getCreationTime()*1000));
            }
            catch (InterruptedException ex) {
                logger.log(Level.SEVERE,ex.getMessage(),ex);
            }
        }
    }



    /*
     * This method will generate allied aircraft to pursue enemy aircraft, and information about enemy aircraft will
     * get from text file that Radar stored.
     */
    public synchronized void defendAirspace(String filePath) {
        if (generateFighters) {

            try (BufferedReader reader = new BufferedReader(new FileReader(RadarReader.getRadarReaderInstance().getEventsFolderPath() + File.separator + filePath))) {
                String enemyAircraft = reader.readLine();
                String[] characteristics = enemyAircraft.split("#");

                int enemyX = Integer.parseInt(characteristics[1].trim());
                int enemyY = Integer.parseInt(characteristics[2].trim());
                Direction enemyDirection = Direction.parseDirection(characteristics[3].trim());
                int enemyAltitude = Integer.parseInt(characteristics[4].trim());

                FighterAirplane f1 = null;
                FighterAirplane f2 = null;

                if (enemyX == 0) {
                    // Fighter f1 is going to go left from enemy, and f2 right
                    switch (enemyDirection) {
                        case LEFT:
                            f1 = generateAlliedFighterAirplane(0, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(1, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            break;
                        case RIGHT:
                            f1 = generateAlliedFighterAirplane(0, 0, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(1, 0, enemyDirection, enemyAltitude);
                            break;
                        case DOWN:
                            if (enemyY - 1 >= 0) {
                                f1 = generateAlliedFighterAirplane(0, enemyY - 1, enemyDirection, enemyAltitude);
                            } else
                                f1 = generateAlliedFighterAirplane(0, 0, enemyDirection, enemyAltitude);
                            if (enemyY + 1 < Airspace.getAirspaceInstance().getWidth())
                                f2 = generateAlliedFighterAirplane(0, enemyY + 1, enemyDirection, enemyAltitude); // drugi uvijek ide desno!
                            else
                                f2 = generateAlliedFighterAirplane(0, enemyY, enemyDirection, enemyAltitude); //prvi uvijek ide lijevo!
                            break;
                    }
                } else if (enemyX == Airspace.getAirspaceInstance().getHeight() - 1) {
                    switch (enemyDirection) {
                        case LEFT:
                            f1 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 2, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            break;
                        case RIGHT:
                            f1 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, 0, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 2, 0, enemyDirection, enemyAltitude);
                            break;
                        case UP:
                            if (enemyY - 1 >= 0)
                                f1 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, enemyY - 1, enemyDirection, enemyAltitude); //prvi uvijek ide lijevo!
                            else
                                f1 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, 0, enemyDirection, enemyAltitude); //prvi uvijek ide lijevo!
                            if (enemyY + 1 < Airspace.getAirspaceInstance().getWidth())
                                f2 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, enemyY + 1, enemyDirection, enemyAltitude); // drugi uvijek ide desno!
                            else
                                f2 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, enemyY, enemyDirection, enemyAltitude); //prvi uvijek ide lijevo!
                    }
                } else if (enemyY == 0) {
                    // Fighter f1 is always up, and fighter f2
                    switch (enemyDirection) {
                        case UP:
                            f1 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, 0, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, 1, enemyDirection, enemyAltitude);
                            break;
                        case DOWN:
                            f1 = generateAlliedFighterAirplane(0, 0, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(0, 1, enemyDirection, enemyAltitude);
                            break;
                        case RIGHT:
                            if (enemyX - 1 >= 0)
                                f1 = generateAlliedFighterAirplane(enemyX - 1, 0, enemyDirection, enemyAltitude);
                            else
                                f1 = generateAlliedFighterAirplane(enemyX, 0, enemyDirection, enemyAltitude);
                            if (enemyX + 1 < Airspace.getAirspaceInstance().getHeight())
                                f2 = generateAlliedFighterAirplane(enemyX + 1, 0, enemyDirection, enemyAltitude);
                            else
                                f2 = generateAlliedFighterAirplane(enemyX, 0, enemyDirection, enemyAltitude);
                            break;
                    }
                } else if (enemyY == Airspace.getAirspaceInstance().getWidth() - 1) {
                    switch (enemyDirection) {
                        case UP:
                            f1 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, Airspace.getAirspaceInstance().getWidth() - 2, enemyDirection, enemyAltitude);
                            break;
                        case DOWN:
                            f1 = generateAlliedFighterAirplane(0, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(0, Airspace.getAirspaceInstance().getWidth() - 2, enemyDirection, enemyAltitude);
                            break;
                        case LEFT:
                            if (enemyX - 1 >= 0)
                                f1 = generateAlliedFighterAirplane(enemyX - 1, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            else
                                f1 = generateAlliedFighterAirplane(enemyX, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            if (enemyX + 1 < Airspace.getAirspaceInstance().getHeight())
                                f2 = generateAlliedFighterAirplane(enemyX + 1, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            else
                                f2 = generateAlliedFighterAirplane(enemyX, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            break;
                    }
                } else {
                    switch (enemyDirection) {
                        case LEFT:
                            f1 = generateAlliedFighterAirplane(enemyX - 1, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(enemyX + 1, Airspace.getAirspaceInstance().getWidth() - 1, enemyDirection, enemyAltitude);
                            break;
                        case RIGHT:
                            f1 = generateAlliedFighterAirplane(enemyX - 1, 0, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(enemyX + 1, 0, enemyDirection, enemyAltitude);
                            break;
                        case UP:
                            f1 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, enemyY - 1, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(Airspace.getAirspaceInstance().getHeight() - 1, enemyY + 1, enemyDirection, enemyAltitude);
                            break;
                        case DOWN:
                            f1 = generateAlliedFighterAirplane(0, enemyY - 1, enemyDirection, enemyAltitude);
                            f2 = generateAlliedFighterAirplane(0, enemyY + 1, enemyDirection, enemyAltitude);
                            break;
                    }
                }
                try {
                    Thread.sleep((long) Math.ceil(ConfigReader.getConfigReaderInstance().getCreationTime() * 1000));
                } catch (InterruptedException ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
                synchronized (locker) {
                    generateFighters = false;
                }
                f1.start();
                f2.start();
            } catch (NullPointerException ex) {
                logger.log(Level.SEVERE, "Something went wrong with allied airplanes!\n" + ex.getMessage(), ex);
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Simulator couldn't find file with enemy aircraft description.\n" + ex.getMessage(), ex);
            }
        }
    }

    private synchronized void generateEnemyAircraft() {

        FighterAirplane enemyAircraft=new FighterAirplane();
        enemyAircraft.setEnemy(true);
        setAircraftPosition(enemyAircraft);
        ConfigReader.setFlightBanProperty(true);
        ConfigReader.setEnemyAircraftProperty(false);
        synchronized (locker) {
            generateFighters = true;
        }
        enemyAircraft.start();
    }

    private FighterAirplane generateAlliedFighterAirplane(int posX,int posY,Direction direction,int altitude) {

        FighterAirplane fa=new FighterAirplane();
        fa.setStartX(posX);
        fa.setStartY(posY);
        fa.setDirection(direction);
        fa.setAltitude(altitude);
        return fa;
    }

    private synchronized void generateAlliedAircraft() {

        Random rand=new Random();
        FlyingObject fo;
        switch (rand.nextInt(2)) {
            case 0:
                fo=new FighterAirplane();
                break;
            default:
                fo=new BomberAirplane();
        }
        setAircraftPosition(fo);
        ConfigReader.setAlliedAircraftProperty(false);
        fo.start();
    }

    private synchronized void generateRandomAircraft() {

        Random rand=new Random();
        int currentType =rand.nextInt(6);
        FlyingObject fo;
        switch (currentType) {
            case 0:
                fo=new Drone();
                break;
            case 1:
                fo=new FirefighterHelicopter();
                break;
            case 2:
                fo=new PassengerHelicopter();
                break;
            case 3:
                fo=new TransportingHelicopter();
                break;
            case 4:
                fo=new TransportingAirplane();
                break;
            case 5:
                fo=new PassengerAirplane();
                break;
            default:
                fo=new FirefightingAirplane();
        }
        setAircraftPosition(fo);
        fo.start();
    }

    private void setAircraftPosition(FlyingObject fo) {

        Random rand=new Random();
        if(fo.getDirection()== Direction.LEFT) {
            fo.setStartY(Airspace.getAirspaceInstance().getWidth()-1);
            fo.setStartX(rand.nextInt(Airspace.getAirspaceInstance().getHeight()));
        }
        if(fo.getDirection()==Direction.RIGHT) {
            fo.setStartY(0);
            fo.setStartX(rand.nextInt(Airspace.getAirspaceInstance().getHeight()));
        }
        if(fo.getDirection()==Direction.UP) {
            fo.setStartX(Airspace.getAirspaceInstance().getHeight()-1);
            fo.setStartY(rand.nextInt(Airspace.getAirspaceInstance().getWidth()));
        }
        if(fo.getDirection()==Direction.DOWN) {
            fo.setStartX(0);
            fo.setStartY(rand.nextInt(Airspace.getAirspaceInstance().getWidth()));
        }
    }
}
