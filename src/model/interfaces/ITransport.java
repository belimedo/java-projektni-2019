package model.interfaces;

public interface ITransport {

    int getMaxCargoWeight();
    void setMaxCargoWeight(int maxCargoWeight);

    default void transportingCargo() {
        System.out.println("Transporting cargo... ");
    }

}
