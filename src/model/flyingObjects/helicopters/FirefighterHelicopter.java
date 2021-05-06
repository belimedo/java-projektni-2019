package model.flyingObjects.helicopters;

import javafx.scene.paint.Paint;
import model.interfaces.IFirefighter;

public class FirefighterHelicopter extends Helicopter implements IFirefighter {

    private int waterCapacity;

    public FirefighterHelicopter() {

        super();
        markingText="F-FH";
        markingPaint= Paint.valueOf("red");
    }

    public FirefighterHelicopter(String model) {

        super(model);
        markingText="F-FH";
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
