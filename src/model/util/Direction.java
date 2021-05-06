package model.util;

import java.util.Random;

public enum Direction {
    LEFT,
    UP,
    RIGHT,
    DOWN;

    public static Direction randomDirection() {
        Random rand=new Random();
        return values()[rand.nextInt(values().length)];
    }

    public static Direction parseDirection(String direction) {
        if("LEFT".equals(direction))
            return LEFT;
        if("UP".equals(direction))
            return UP;
        if("RIGHT".equals(direction))
            return RIGHT;
        if("DOWN".equals(direction))
            return DOWN;
        return null;
    }

    @Override
    public String toString() {
        switch (this){
            case UP:
                return "UP";
            case LEFT:
                return "LEFT";
            case RIGHT:
                return "RIGHT";
            case DOWN:
                return "DOWN";
            default:
                return null;
        }
    }


}
