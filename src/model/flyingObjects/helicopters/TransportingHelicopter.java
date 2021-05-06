package model.flyingObjects.helicopters;

import javafx.scene.paint.Paint;
import model.interfaces.ITransport;

public class TransportingHelicopter extends Helicopter implements ITransport {

    int maxCargoWeight;

    public TransportingHelicopter(){
        super();
        markingText="T-TH";
        markingPaint= Paint.valueOf("darkblue");
    }

    public TransportingHelicopter(String model) {
        super(model);
        markingText="T-TH";
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
