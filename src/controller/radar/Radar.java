package controller.radar;

import controller.config.ConfigReader;
import controller.config.RadarReader;
import model.airspaceMap.Airspace;
import model.flyingObjects.FlyingObject;
import model.interfaces.IMilitary;
import simulation.TestSimulation;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Radar extends Thread {

    private static Logger logger;
    private static FileHandler fileHandler;

    static {
        logger=Logger.getLogger(Radar.class.getName());
        try{
            fileHandler=new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath()+ File.separator+Radar.class.getSimpleName()+".log",true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Radar(){}

    @Override
    public void run() {

        while(true && !TestSimulation.isOver) {
            synchronized (RadarReader.getRadarReaderInstance().getMapFilePath()) {
                try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(RadarReader.getRadarReaderInstance().getMapFilePath())))) {
                    synchronized (Airspace.getAirspaceInstance().getMap()) {

                        for (int i = 0; i < Airspace.getAirspaceInstance().getHeight(); i++) {
                            for (int j = 0; j < Airspace.getAirspaceInstance().getWidth(); j++) {

                                Airspace.getAirspaceInstance().getMap()[i][j].getAircraftsOnField().forEach(fo -> {
                                    if (fo instanceof IMilitary && ((IMilitary) fo).isEnemy() && fo.isFlying() && !Airspace.getAirspaceInstance().getEnemiesOnMap().contains(fo)) {
                                        writeEnemy(fo);
                                        Airspace.getAirspaceInstance().getEnemiesOnMap().add(fo);
                                    }
                                    else if (fo.isFlying() && !Airspace.getAirspaceInstance().getEnemiesOnMap().contains(fo))
                                        writer.println(fo);
                                    else if (fo.isFlying() && Airspace.getAirspaceInstance().getEnemiesOnMap().contains(fo))
                                        writer.println(fo + "#E");
                                });
                            }
                        }
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            try{
                sleep((long)Math.ceil(RadarReader.getRadarReaderInstance().getScanTime()*1000));
            }
            catch (InterruptedException ex) {
                logger.log(Level.SEVERE,ex.getMessage(),ex);
            }
        }
    }

    private synchronized void writeEnemy(FlyingObject fo) {

        File enemyFile = new File(RadarReader.getRadarReaderInstance().getEventsFolderPath() + File.separator +
                new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(Calendar.getInstance().getTime()) + ".txt");
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(enemyFile)))) {
            writer.println(fo);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
