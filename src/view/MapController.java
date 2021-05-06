package view;

import controller.crash.Crash;
import controller.config.ConfigReader;
import controller.config.RadarReader;
import controller.watcher.IModifiable;
import controller.watcher.Watcher;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import javafx.util.Duration;
import javafx.util.Pair;
import model.airspaceMap.Airspace;
import model.flyingObjects.FlyingObject;
import model.flyingObjects.airplanes.*;
import model.flyingObjects.drone.Drone;
import model.flyingObjects.helicopters.FirefighterHelicopter;
import model.flyingObjects.helicopters.PassengerHelicopter;
import model.flyingObjects.helicopters.TransportingHelicopter;
import model.flyingObjects.rocket.HailProtectionRocket;
import model.flyingObjects.rocket.MilitaryRocket;
import simulation.TestSimulation;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class MapController extends Application implements IModifiable {

    private static Logger logger;
    private static FileHandler fileHandler;

    static {
        logger = Logger.getLogger(MapController.class.getName());

        try {
            fileHandler = new FileHandler(ConfigReader.getConfigReaderInstance().getLoggingPath() + File.separator + "MapController.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        }catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private GridPane mapGridPane;
    @FXML
    private Button generateEnemyButton;
    @FXML
    private Button flightBan;
    @FXML
    private Button showAllEvents;
    @FXML
    private Button showAllCrashes;
    @FXML
    private Label eventsLabel1;
    @FXML
    private Label eventsLabel2;
    @FXML
    private Label eventsLabel3;

    private ConcurrentHashMap<Pair<Integer,Integer>, FlyingObject> objectsOnMap=new ConcurrentHashMap<>();
    private String textForLabel2;
    private String textForLabel3;
    private boolean firstTimeLabel;

    private final String disableFlightBan="Disable flight ban";
    private final String enableFlightBan="Enable flight ban";
    private final String crashInfoLabel="Crash info";


    public MapController(){}

    public void startStage()
     {
        launch();
     }

    @Override
    public void start(Stage primaryStage) {

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("MapView.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Radar");
            primaryStage.setOnCloseRequest(e->{
                e.consume();
                System.out.println("Application is ending, please wait for few moments...");
                TestSimulation.isOver=true;
                primaryStage.close();
            });
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    @FXML
    private void initialize() {
        for (int j = 0; j < ConfigReader.getConfigReaderInstance().getWidth(); j++) {
            for (int i = 0; i < ConfigReader.getConfigReaderInstance().getHeight(); i++) {
                Label lbl = new Label("");
                lbl.setPrefWidth(35);
                lbl.setPrefHeight(35);
                lbl.setAlignment(Pos.BASELINE_CENTER);
                lbl.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
                lbl.setStyle("-fx-background-color: lightgray;");
                mapGridPane.add(lbl, j, i);
            }
        }

        updateFlightBanButton(null);
        eventsLabel1.setText(String.format("%30s %20s %20s %20s %20s","Enemy aircraft:","Position X:","Position Y:","Direction:","Altitude:"));
        firstTimeLabel=true;

        Watcher mapWatcher;
        try {
            mapWatcher=new Watcher(this,
                    RadarReader.getRadarReaderInstance().getMapFolderPath(),
                    this.getClass().getDeclaredMethod("mapUpdate", String.class));
        } catch (NoSuchMethodException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
            return;
        }
        mapWatcher.setDaemon(true);
        mapWatcher.start();

        Watcher eventsWatcher;
        try {
            eventsWatcher = new Watcher(this,
                    RadarReader.getRadarReaderInstance().getEventsFolderPath(),
                    this.getClass().getDeclaredMethod("eventsUpdate",String.class));
        } catch (NoSuchMethodException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
            return;
        }
        eventsWatcher.setDaemon(true);
        eventsWatcher.start();

        Watcher crashWatcher;
        try {
            crashWatcher = new Watcher(this,
                    ConfigReader.getConfigReaderInstance().getCrashPath(),
                    this.getClass().getDeclaredMethod("crashPopUp", String.class));
        } catch (NoSuchMethodException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
            return;
        }
        crashWatcher.setDaemon(true);
        crashWatcher.start();


        // This watcher is added just for GUI purposes, to make it look nicer
        Watcher buttonWatcher = null;
        try {
            buttonWatcher = new Watcher( this,
                    new File(ConfigReader.getConfigReaderInstance().getPropertiesFilePath()).getParentFile().getPath(),
                    this.getClass().getDeclaredMethod("updateFlightBanButton", String.class));
        } catch (NoSuchMethodException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
        buttonWatcher.setDaemon(true);
        buttonWatcher.start();
    }


     @FXML
     private void toggleFlyingBan(ActionEvent event) {
         ConfigReader.setFlightBanProperty(!ConfigReader.getConfigReaderInstance().isFlightBan());
         updateFlightBanButton(null);
     }

     @FXML
     private void toggleEnemyAircraft(ActionEvent event) {
        ConfigReader.setEnemyAircraftProperty(true);
        generateEnemyButton.setDisable(true);
         Timer timer = new Timer();
         timer.schedule(
                 new TimerTask() {
                     @Override
                     public void run()
                     {
                         Platform.runLater(() ->
                         {
                             generateEnemyButton.setDisable(false);
                         });
                         timer.cancel();
                     }
                 }, (long)Math.ceil(ConfigReader.getConfigReaderInstance().getCreationTime()*1000) //This will make button inaccessible
         );

     }

     @FXML
     private void toggleCrashList(ActionEvent event) {
         CrashListController controller = new CrashListController();
         Timer timer = new Timer();
         timer.schedule(new TimerTask() {
             @Override
             public void run()
             {
                 // call time consuming method on separate thread
                 controller.readAllFiles();

                 Platform.runLater(() ->
                 {
                     Stage stage = new Stage();
                     try {
                         controller.start(stage);
                     } catch (Exception e1) {
                         logger.log(Level.SEVERE, e1.getMessage(), e1);
                     }
                 });
                 timer.cancel();
             }
         }, 0);
     }

     @FXML
     private void toggleEventsList(ActionEvent event) {
        EventsListController controller = new EventsListController();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run()
            {

                controller.readAllFiles();

                Platform.runLater(() ->
                {
                    Stage stage = new Stage();
                    try {
                        controller.start(stage);
                    } catch (Exception e1) {
                        logger.log(Level.SEVERE, e1.getMessage(), e1);
                    }
                });
                timer.cancel();
            }
        }, 0);
    }

     @FXML
     public void updateFlightBanButton(String fileName) {
         Timer timer = new Timer();
         timer.schedule(
                 new TimerTask() {
                     @Override
                     public void run()
                     {
                         Platform.runLater(() ->
                         {
                             if(ConfigReader.getConfigReaderInstance().isFlightBan()) {
                                 flightBan.setText(disableFlightBan);
                             }
                             else if(!ConfigReader.getConfigReaderInstance().isFlightBan() && Airspace.getAirspaceInstance().getEnemiesOnMap().isEmpty()) {
                                 flightBan.setText(enableFlightBan);
                             }
                         });
                         timer.cancel();
                     }
                 }, 0
         );
     }

     public synchronized void eventsUpdate(String fileName) {
         Timer timer = new Timer();
         timer.schedule(
                 new TimerTask() {
                     @Override
                     public void run() {
                         readEventsFile(fileName);
                         Platform.runLater(() ->
                         {
                             updateLabels();
                         });
                         timer.cancel();
                     }
                 }, 0
         );
     }

     @FXML
     public synchronized void crashPopUp(String fileName) {

        Crash crash = Crash.readCrash(ConfigReader.getConfigReaderInstance().getCrashPath()+File.separator+fileName);

        Label infoLabel= new Label();
        Label crashInfo= new Label();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() ->
                {
                    VBox root = new VBox();

                    root.getChildren().addAll(infoLabel,crashInfo);
                    root.setAlignment(Pos.CENTER);
                    root.setSpacing(20);
                    root.setPadding(new Insets(10,10,10,10));
                    root.setBackground(new Background(new BackgroundFill(Paint.valueOf("darkorange"), null, null)));

                    infoLabel.setText(crashInfoLabel);
                    crashInfo.setText(crash.toString());
                    Stage stage = new Stage();
                    stage.setTitle("Crash " + crash.getCrashNumber()+ " info");
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    PauseTransition delay = new PauseTransition(Duration.seconds(3));
                    delay.setOnFinished( event -> stage.close() );
                    delay.play();
                    stage.show();

                });
                timer.cancel();
            }
            }, 400);
     }

     @FXML
     private synchronized void updateGridpane() {

         Pair<Integer, Integer> position;
         String labelContent;
         Paint labelColor = null;

         for (int i = 0; i < ConfigReader.getConfigReaderInstance().getHeight(); i++) {
             for (int j = 0; j < ConfigReader.getConfigReaderInstance().getWidth(); j++) {

                 position = new Pair<>(i, j);
                 Label lbl = (Label) mapGridPane.getChildrenUnmodifiable().get(j * ConfigReader.getConfigReaderInstance().getHeight() + i + 1);

                 if (objectsOnMap.containsKey(position)) {
                     labelContent = objectsOnMap.get(position).getMarkingText();
                     labelColor = objectsOnMap.get(position).getMarkingPaint();
                     objectsOnMap.remove(position);
                 } else {
                     labelContent = " ";
                     labelColor = Paint.valueOf("lightgray");
                 }
                 lbl.setText(labelContent);
                 lbl.setTextFill(labelColor);
             }
         }
     }

     public synchronized void mapUpdate(String fileName) {

         Timer timer = new Timer();
         timer.schedule(
                 new TimerTask() {
                     @Override
                     public void run() {
                         loadMap(RadarReader.getRadarReaderInstance().getMapFolderPath() + File.separator + RadarReader.getRadarReaderInstance().getMapFileName());    //This is not on UI thread
                         Platform.runLater(() ->
                         {
                             updateGridpane();
                         });
                         timer.cancel();
                     }
                 }, 400
         );
     }
     // This method will read from Map.txt file where Radar saves our Flying Objects from the map
     private void loadMap(String fileName) {

         objectsOnMap.clear();
         synchronized (RadarReader.getRadarReaderInstance().getMapFilePath()) {
             try (BufferedReader reader = new BufferedReader(new FileReader(RadarReader.getRadarReaderInstance().getMapFilePath()))) {
                 String line;
                 String[] characteristics;
                 FlyingObject fo = new FirefightingAirplane();
                 Pair<Integer, Integer> position;
                 while ((line = reader.readLine()) != null) {
                     characteristics = line.split("#");
                     position = new Pair<>(Integer.parseInt(characteristics[1].trim()), Integer.parseInt(characteristics[2].trim()));
                     if (BomberAirplane.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new BomberAirplane();
                     }
                     if (FighterAirplane.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new FighterAirplane();
                         if (characteristics.length == 6)
                             ((FighterAirplane) fo).setEnemy(true);
                     }
                     if (FirefightingAirplane.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new FirefightingAirplane();
                     }
                     if (PassengerAirplane.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new PassengerAirplane();
                     }
                     if (TransportingAirplane.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new TransportingAirplane();
                     }
                     if (Drone.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new Drone();
                     }
                     if (FirefighterHelicopter.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new FirefighterHelicopter();
                     }
                     if (PassengerHelicopter.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new PassengerHelicopter();
                     }
                     if (TransportingHelicopter.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new TransportingHelicopter();
                     }
                     if (HailProtectionRocket.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new HailProtectionRocket();
                     }
                     if (MilitaryRocket.class.getSimpleName().equals(characteristics[0].trim())) {
                         fo = new MilitaryRocket();
                     }
                     objectsOnMap.put(position, fo);
                 }
             } catch (IOException ex) {
                 logger.log(Level.SEVERE, "Problems with loading map!\n" + ex.getMessage(), ex);
             }
         }
     }

     // This method reads newest event (enemy aircraft that got into our airspace) and updates labels
     private void readEventsFile(String fileName) {

         try (BufferedReader reader = new BufferedReader(new FileReader(RadarReader.getRadarReaderInstance().getEventsFolderPath() + File.separator + fileName))) {
             String line = reader.readLine();
             if (firstTimeLabel) {
                 textForLabel2 = formatAircraft(line);
                 textForLabel3 = "";
                 firstTimeLabel = false;
             } else {
                 textForLabel3 = textForLabel2;
                 textForLabel2 = formatAircraft(line);
             }
         } catch (IOException ex) {
             logger.log(Level.SEVERE, ex.getMessage(), ex);
         }
     }


    @FXML
    private void updateLabels() {
        eventsLabel2.setText(textForLabel2);
        eventsLabel3.setText(textForLabel3);
    }

    private String formatAircraft(String line) {
        String characteristics[]=line.split("#");
        return String.format("%30s %20s %20s %20s %20s",characteristics[0],characteristics[1],characteristics[2],characteristics[3],characteristics[4]);
    }

}
