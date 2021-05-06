package model.flyingObjects;

import model.interfaces.IAircraftMovement;
import model.personnel.Person;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Aircraft extends FlyingObject implements IAircraftMovement {

    /*
    * To ensure unique id for every aircraft, we have used AtomicInteger as counter  and then gave its value to instance
    * variable aircraftId within creation, ensuring that every aircraft will have unique Id and that it will be thread safe operation
    */
    private static final AtomicInteger count = new AtomicInteger(0);

    private String model;
    private final int aircraftId;
    private Map<String,String> characteristics=null;
    private List<Person> crew = null;

    protected Aircraft(){
        super();
        aircraftId=count.incrementAndGet();
    }

    protected Aircraft(String model) {
        super();
        aircraftId=count.incrementAndGet();
        this.model=model;
    }

    public String getModel(){
        return model;
    }

    public void setModel(String model){
        this.model=model;
    }

    public int getAircaftId(){
        return aircraftId;
    }

    public Map<String, String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Map<String, String> characteristics) {
        this.characteristics = characteristics;
    }

    public List<Person> getCrew() {
        return crew;
    }

    public void setCrew(List<Person> crew) {
        this.crew = crew;
    }
}
