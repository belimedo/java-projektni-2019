package model.flyingObjects.drone;

import javafx.scene.paint.Paint;
import model.flyingObjects.Aircraft;

public class Drone extends Aircraft {

    public Drone() {

        super();
        markingText="D-D";
        markingPaint= Paint.valueOf("black");
    }

    public Drone(String model) {

        super(model);
        markingText="D-D";
        markingPaint= Paint.valueOf("black");
    }

    public void recordTerrain() {
        System.out.println("Recording the terrain... ");
    }

}
