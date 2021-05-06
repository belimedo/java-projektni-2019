package model.interfaces;

public interface IMilitary {

    boolean isArmed();
    void setArmed(boolean armed);

    boolean isEnemy();
    void setEnemy(boolean enemy);

    default void attackGroundTarget(){
        System.out.println("Attacking ground target... ");
    }


}
