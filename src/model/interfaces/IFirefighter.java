package model.interfaces;

public interface IFirefighter {

    int getWaterCapacity();
    void setWaterCapacity(int waterCapacity);

    default void fireExtinguishing() {
        System.out.println("Fire extinguishing... ");
    }



}
