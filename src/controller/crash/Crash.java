package controller.crash;

import controller.config.ConfigReader;
import model.flyingObjects.FlyingObject;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;

public class Crash implements Serializable {

    private static String saveFolderPath;
    private static AtomicInteger counter=new AtomicInteger(0);
    private static Logger logger;
    private static Handler fileHandler;

    static {

        saveFolderPath=ConfigReader.getConfigReaderInstance().getCrashPath();
        logger=Logger.getLogger(Crash.class.getName());
        try {
            fileHandler=new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath()+ File.separator +Crash.class.getSimpleName()+".log");
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private String description;
    private String time;
    private int positionX;
    private int positionY;
    private String position;
    private final int crashNumber;
    private FlyingObject fo1;
    private FlyingObject fo2;
    // Things added for GUI
    private int altitude;
    private String fo1name;
    private String fo2name;

    public Crash(String description, String time, int positionX, int positionY, FlyingObject fo1, FlyingObject fo2) {
        this.description=description;
        this.time= time;
        this.positionX=positionX;
        this.positionY=positionY;
        this.position=Integer.toString(positionX)+" - "+ Integer.toString(positionY);
        crashNumber=counter.incrementAndGet();
        this.fo1=fo1;
        this.fo2=fo2;
        this.altitude=fo1.getAltitude();
        this.fo1name=fo1.getClass().getSimpleName();
        this.fo2name=fo2.getClass().getSimpleName();
    }


    /*
    * This method is called when crash occures, it is called with parameters description that gives short description over
    * what happened, time as a String so caller must call Long.toString(System.currentTimeInMillis()), and a position as a
    * two integer values that we convert to String value.
     */
    public synchronized static void writeCrash(String description,String time, int positionX, int positionY, FlyingObject fo1, FlyingObject fo2) {
        Crash crash=new Crash(description,time,positionX,positionY,fo1,fo2);
        String fileName= "Crash - " + Integer.toString(crash.crashNumber)+".ser";
        // To optimise performances we have wrapped FileOutputStream with BufferedOutputStream
        try(ObjectOutputStream oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(saveFolderPath + File.separator + fileName)))) {
            oos.writeObject(crash);
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public synchronized static Crash readCrash(String filePath) {

            try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath)))) {
                Thread.sleep(100);//NullPointerException without this if there is still NullPointerException, we need to put another sleep above for 100 millis
                return (Crash) ois.readObject();
            } catch (ClassNotFoundException| InterruptedException | IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
            return null;
    }

    @Override
    public String toString() {
        return description+"\nCrash No. "+ crashNumber + " happened  between " + fo1.getClass().getSimpleName() +
                " and " + fo2.getClass().getSimpleName()+ " at position "+ position+ " on time:"+time;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getCrashNumber() {
        return crashNumber;
    }

    public FlyingObject getFo1() {
        return fo1;
    }

    public void setFo1(FlyingObject fo1) {
        this.fo1 = fo1;
    }

    public FlyingObject getFo2() {
        return fo2;
    }

    public void setFo2(FlyingObject fo2) {
        this.fo2 = fo2;
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

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public String getFo1name() {
        return fo1name;
    }

    public void setFo1name(String fo1name) {
        this.fo1name = fo1name;
    }

    public String getFo2name() {
        return fo2name;
    }

    public void setFo2name(String fo2name) {
        this.fo2name = fo2name;
    }


}
