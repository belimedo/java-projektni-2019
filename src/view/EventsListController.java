package view;

import controller.config.ConfigReader;
import controller.config.RadarReader;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EventsListController extends Application {

    private static Logger logger;
    private static FileHandler fileHandler;

    static {

        logger = Logger.getLogger(CrashListController.class.getName());
        try {
            fileHandler = new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath() + File.separator + CrashListController.class.getSimpleName() +".log", false);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        }catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public TableView<EventsWriting> eventsTable;
    @FXML
    public TableColumn<EventsWriting,String> aircraftTypeColumn;
    @FXML
    public TableColumn<EventsWriting,String> directionColumn;
    @FXML
    public TableColumn<EventsWriting,String> posXColumn;
    @FXML
    public TableColumn<EventsWriting,String> posYColumn;
    @FXML
    public TableColumn<EventsWriting,String> altitudeColumn;

    private static List<EventsWriting> eventsList=new ArrayList<>();

    protected class EventsWriting {

        public EventsWriting(String aircraftType,String posX,String posY,String direction,String altitude) {

            this.aircraftType=aircraftType;
            this.direction=direction;
            this.posX=posX;
            this.posY=posY;
            this.altitude=altitude;
        }

        private String aircraftType;
        private String direction;
        private String posX;
        private String posY;

        public String getAircraftType() {
            return aircraftType;
        }

        public void setAircraftType(String aircraftType) {
            this.aircraftType = aircraftType;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getPosX() {
            return posX;
        }

        public void setPosX(String posX) {
            this.posX = posX;
        }

        public String getPosY() {
            return posY;
        }

        public void setPosY(String posY) {
            this.posY = posY;
        }

        public String getAltitude() {
            return altitude;
        }

        public void setAltitude(String altitude) {
            this.altitude = altitude;
        }

        private String altitude;
    }

    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("EventsListView.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("Events list");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished( event -> primaryStage.close() );
            delay.play();
            primaryStage.show();
        }catch (IOException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }


    @FXML
    public void initialize() {

        posXColumn.setCellValueFactory(new PropertyValueFactory<>("posX"));
        posYColumn.setCellValueFactory(new PropertyValueFactory<>("posY"));
        altitudeColumn.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        aircraftTypeColumn.setCellValueFactory(new PropertyValueFactory<>("aircraftType"));
        directionColumn.setCellValueFactory(new PropertyValueFactory<>("direction"));

        for(EventsWriting e : eventsList) {
            eventsTable.getItems().add(e);
        }
    }

    public void readAllFiles() {

        eventsList.clear();	//Remove old files

        File folder = new File(RadarReader.getRadarReaderInstance().getEventsFolderPath());
        File[] listOfFiles = folder.listFiles();
        String line;
        String[] characteristics;
        if(listOfFiles.length>0) {
            for (File file : listOfFiles) {
                try(BufferedReader input = new BufferedReader(new FileReader(file.getPath()))) {
                    while((line=input.readLine())!=null) {
                        characteristics=line.split("#");
                        eventsList.add(new EventsWriting(characteristics[0],characteristics[1],characteristics[2],characteristics[3],characteristics[4]));
                    }
                }
                catch(IOException ex) {
                    logger.log(Level.SEVERE,ex.getMessage(),ex);
                }
            }
        }
    }
}
