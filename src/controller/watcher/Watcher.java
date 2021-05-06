package controller.watcher;

import controller.config.ConfigReader;
import simulation.TestSimulation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Watcher extends Thread {

    private static Logger logger;
    private static FileHandler fileHandler;

    static {
        logger=Logger.getLogger(Watcher.class.getName());
        try {
            fileHandler=new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath()+ File.separator+Watcher.class.getSimpleName()+".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private IModifiable modifiableObject;
    private String filePath;
    private Method updateMethod;

    public Watcher(IModifiable modifiableObject,String filePath,Method updateMethod) {
        this.modifiableObject=modifiableObject;
        this.filePath=filePath;
        this.updateMethod=updateMethod;
    }

    @Override
    public void run(){
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(filePath);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
            while(true && !TestSimulation.isOver) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException ex) {
                    logger.log(Level.WARNING, ex.getMessage(), ex);
                    return;
                }
                try {
                    sleep(50);
                }
                catch (InterruptedException ex) {
                    logger.log(Level.SEVERE,ex.getMessage(),ex);
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    String fileName = ev.context().toString().trim();
                    if ((kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) &&
                            (fileName.endsWith(".properties") || fileName.endsWith(".txt") || fileName.endsWith(".ser")))
                        updateMethod.invoke(modifiableObject, fileName);
                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }

        }
        catch (InvocationTargetException | IllegalAccessException | IOException  ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }

}
