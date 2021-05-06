package model.flyingObjects.airplanes;

import javafx.scene.paint.Paint;
import model.interfaces.IFirefighter;

public class FirefightingAirplane extends Airplane implements IFirefighter {

    private int waterCapacity;

    public FirefightingAirplane() {
        super();
        markingText="F-FA";
        markingPaint= Paint.valueOf("red");
    }

    public FirefightingAirplane(String model) {
        super(model);
        markingText="F-FA";
        markingPaint= Paint.valueOf("red");
    }

    @Override
    public int getWaterCapacity() {
        return waterCapacity;
    }

    @Override
    public void setWaterCapacity(int waterCapacity) {
        this.waterCapacity=waterCapacity;
    }
}
