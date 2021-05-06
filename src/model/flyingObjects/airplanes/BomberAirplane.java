package model.flyingObjects.airplanes;

import javafx.scene.paint.Paint;
import model.interfaces.IMilitary;

public class BomberAirplane extends Airplane implements IMilitary {

    private boolean armed=true;
    private boolean enemy=false;

    public BomberAirplane() {

        super();
        markingText="M-BA";
        markingPaint= Paint.valueOf("darkgreen");
    }

    public BomberAirplane(String model) {

        super(model);
        markingText="M-BA";
        markingPaint= Paint.valueOf("darkgreen");
    }


    @Override
    public boolean isArmed() {
        return armed;
    }

    @Override
    public void setArmed(boolean armed) {
        this.armed=armed;
    }

    @Override
    public boolean isEnemy() {
        return enemy;
    }

    @Override
    public void setEnemy(boolean enemy) {
        this.enemy=enemy;
    }


}
