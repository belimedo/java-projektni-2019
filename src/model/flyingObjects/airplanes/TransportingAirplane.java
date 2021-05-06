package model.flyingObjects.airplanes;

import javafx.scene.paint.Paint;
import model.interfaces.ITransport;

public class TransportingAirplane extends Airplane implements ITransport {

    private int maxCargoWeight;

    public TransportingAirplane() {
        super();
        markingText="T-TA";
        markingPaint= Paint.valueOf("darkblue");
    }

    public TransportingAirplane(String model) {
        super(model);
        markingText="T-TA";
        markingPaint= Paint.valueOf("darkblue");
    }


    @Override
    public int getMaxCargoWeight() {
        return maxCargoWeight;
    }

    @Override
    public void setMaxCargoWeight(int maxCargoWeight) {
        this.maxCargoWeight=maxCargoWeight;
    }
}
