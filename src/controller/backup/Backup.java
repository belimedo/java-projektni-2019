package controller.backup;

import controller.config.ConfigReader;
import controller.config.RadarReader;
import simulation.TestSimulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Backup extends Thread
{
    private static Logger logger;
    private static Handler fileHandler;

    static {
        File currentFolder=new File(".");
        File backupFolder=new File(currentFolder+File.separator+ConfigReader.getConfigReaderInstance().getBackupPath());
        if(!backupFolder.exists())
            backupFolder.mkdir();
        logger = Logger.getLogger(Backup.class.getName());
        try {
            fileHandler = new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath() + File.separator + "Backup.log", false);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while(true && !TestSimulation.isOver) {

            try {
                sleep(ConfigReader.getConfigReaderInstance().getBackupPeriod()*1000);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            makeBackup();
        }
    }

    /**
     * Make backup file. File name is current time. Output location is set by config.properties.
     */
    private synchronized void makeBackup()
    {
        ArrayList<File> foldersToBackup = new ArrayList<>();
        foldersToBackup.add(new File(ConfigReader.getConfigReaderInstance().getCrashPath()));
        foldersToBackup.add(new File(ConfigReader.getConfigReaderInstance().getLoggingPath()));
        foldersToBackup.add(new File(RadarReader.getRadarReaderInstance().getEventsFolderPath()));
        foldersToBackup.add(new File(RadarReader.getRadarReaderInstance().getMapFolderPath()));

        List<File> fileList = new ArrayList<>();
        for (File folder : foldersToBackup)
        {
            getAllFiles(folder, fileList);
        }
        writeZipFile(fileList);
    }

    private void getAllFiles(File dir, List<File> fileList)
    {
        File[] files = dir.listFiles();
        if(files == null) {
            return;
        }
        for (File file : files) {
            if(file.getName().endsWith(".txt")) {
                fileList.add(file);
            }
            if (file.isDirectory()) {
                getAllFiles(file, fileList);
            }
        }
    }

    private void writeZipFile(List<File> fileList)
    {
        try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(ConfigReader.getConfigReaderInstance().getBackupPath() + File.separator +
                new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime()) + ".zip")))
        {
            for (File file : fileList)
            {
                if (!file.isDirectory())
                {
                    //  only zip files, not directories
                    addToZip(file, zos);
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void addToZip(File file, ZipOutputStream zos) throws IOException
    {
        try (FileInputStream fis = new FileInputStream(file))
        {
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
            zos.closeEntry();
        }
    }

}
