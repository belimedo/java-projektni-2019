package model.flyingObjects.airplanes;

import model.flyingObjects.Aircraft;

public abstract class Airplane extends Aircraft {

    protected Airplane(){
        super();
    }

    protected Airplane(String model){
        super(model);
    }
}
