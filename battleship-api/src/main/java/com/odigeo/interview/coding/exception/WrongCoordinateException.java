package com.odigeo.interview.coding.exception;

public class WrongCoordinateException extends BattleshipException {

    public WrongCoordinateException(String coordinate) {
        super(String.format("Coordinate %s is wrong", coordinate));
    }

}
