package model.flyingObjects.helicopters;

import javafx.scene.paint.Paint;
import model.interfaces.IPassenger;

public class PassengerHelicopter extends Helicopter implements IPassenger {

    private int seatCapacity;

    public PassengerHelicopter(){

        super();
        markingText="P-PH";
        markingPaint= Paint.valueOf("orange");
    }

    public PassengerHelicopter(String model) {

        super(model);
        markingText="P-PH";
        markingPaint= Paint.valueOf("orange");
    }

    @Override
    public int getSeatCapacity() {
        return seatCapacity;
    }

    @Override
    public void setSeatCapacity(int seatCapacity) {
        this.seatCapacity=seatCapacity;
    }
}
