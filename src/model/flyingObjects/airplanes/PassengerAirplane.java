package model.flyingObjects.airplanes;

import javafx.scene.paint.Paint;
import model.interfaces.IPassenger;

public class PassengerAirplane extends Airplane implements IPassenger {

    private int seatCapacity;

    public PassengerAirplane() {
        super();
        markingText="P-PA";
        markingPaint= Paint.valueOf("orange");
    }

    public PassengerAirplane(String model) {
        super(model);markingText="P-PA";
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
