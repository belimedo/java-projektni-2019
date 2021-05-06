package view;

import controller.crash.Crash;
import controller.config.ConfigReader;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CrashListController extends Application {

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
    public TableView<Crash> crashTable;
    @FXML
    public TableColumn<Crash,String> crashIdColumn;
    @FXML
    public TableColumn<Crash,String> firstAircraftColumn;
    @FXML
    public TableColumn<Crash,String> secondAircraftColumn;
    @FXML
    public TableColumn<Crash,String> posXColumn;
    @FXML
    public TableColumn<Crash,String> posYColumn;
    @FXML
    public TableColumn<Crash,String> altitudeColumn;

    private static List<Crash> crashList=new ArrayList<>();

    public CrashListController(){}


    @Override
    public void start(Stage primaryStage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("CrashListView.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("Crash List");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }
        catch (IOException ex) {
            logger.log(Level.SEVERE,ex.getMessage(),ex);
        }
    }

    @FXML
    public void initialize() {

        posXColumn.setCellValueFactory(new PropertyValueFactory<>("positionX"));
        posYColumn.setCellValueFactory(new PropertyValueFactory<>("positionY"));
        altitudeColumn.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        firstAircraftColumn.setCellValueFactory(new PropertyValueFactory<>("fo1name"));
        secondAircraftColumn.setCellValueFactory(new PropertyValueFactory<>("fo2name"));
        crashIdColumn.setCellValueFactory(new PropertyValueFactory<>("crashNumber"));

        for(Crash c : crashList) {
            crashTable.getItems().add(c);
        }
    }

    public void readAllFiles() {

        crashList.clear();	//Remove old files

        File folder = new File(ConfigReader.getConfigReaderInstance().getCrashPath());
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles.length>0) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    crashList.add(Crash.readCrash(file.getPath()));
                }
            }
        }
    }

}
