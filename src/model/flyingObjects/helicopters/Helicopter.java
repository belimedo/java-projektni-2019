package model.flyingObjects.helicopters;

import model.flyingObjects.Aircraft;

public abstract class Helicopter extends Aircraft {

    protected Helicopter() {
        super();
    }

    protected Helicopter(String model) {
        super(model);
    }

}
