package simulation;

import controller.backup.Backup;
import controller.config.ConfigReader;
import controller.radar.Radar;
import controller.simulator.Simulator;

import view.MapController;


public class TestSimulation {

    private static ConfigReader configReaderInstance=ConfigReader.getConfigReaderInstance();

    public static boolean isOver=false;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Pocinje simulacija:");
        MapController mc=new MapController();
        Simulator simulator =new Simulator();
        Radar radar=new Radar();
        Backup backup=new Backup();
        simulator.start();
        radar.start();
        backup.start();
        mc.startStage();
        simulator.join();
        radar.join();
    }
}
