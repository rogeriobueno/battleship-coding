package com.odigeo.interview.coding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    private String id;
    private String playerOneId;
    private String playerTwoId;
    private boolean vsComputer;
    private Integer playerTurn;
    private Cell[][] playerOneField;
    private Cell[][] playerTwoField;
    private Instant createdAt;
    private Instant startedAt;
    private Instant finishedAt;
    private String winner;


    public void setPlayerField(String playerId, Cell[][] playerField) {
        if (playerId.equals(getPlayerOneId())) {
            setPlayerOneField(playerField);
        } else if (playerId.equals(getPlayerTwoId())) {
            setPlayerTwoField(playerField);
        } else {
            throw new IllegalArgumentException(String.format("Player %s does not exist in the game.", playerId));
        }
    }

    public Cell[][] getPlayerField(String playerId) {
        if (playerId.equals(getPlayerOneId())) {
            return getPlayerOneField();
        } else if (playerId.equals(getPlayerTwoId())) {
            return getPlayerTwoField();
        } else {
            throw new IllegalArgumentException(String.format("Player %s does not exist in the game.", playerId));
        }
    }

    public Cell[][] getOpponentField(String playerId) {
        if (playerId.equals(getPlayerOneId())) {
            return getPlayerTwoField();
        } else if (playerId.equals(getPlayerTwoId())) {
            return getPlayerOneField();
        } else {
            throw new IllegalArgumentException(String.format("Player %s does not exist in the game.", playerId));
        }
    }

    public void setNextPlayerTurn() {
        setPlayerTurn((getPlayerTurn() % 2) + 1);
    }

    public boolean isPlayerTurn(String playerId) {
        if (playerId.equals(getPlayerOneId())) {
            return isPlayerTurn(1);
        } else if (playerId.equals(getPlayerTwoId())) {
            return isPlayerTurn(2);
        } else {
            throw new IllegalArgumentException(String.format("Player %s does not exist in the game.", playerId));
        }
    }

    public boolean isPlayerTurn(int playerNumber) {
        return getPlayerTurn() != null && getPlayerTurn() == playerNumber;
    }

    public boolean isFinished() {
        return getFinishedAt() != null;
    }

    public boolean playersReady() {
        return getPlayerOneId() != null
                && getPlayerTwoId() != null
                && getPlayerOneField() != null
                && getPlayerTwoField() != null;
    }

    public boolean playerReady(String playerId) {
        return getPlayerField(playerId) != null;
    }
}
