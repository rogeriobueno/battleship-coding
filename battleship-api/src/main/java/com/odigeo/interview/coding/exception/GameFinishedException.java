package com.odigeo.interview.coding.exception;

public class GameFinishedException extends BattleshipException {

    public GameFinishedException(String playedId) {
        super(String.format("The winner is %s", playedId));
    }

}
