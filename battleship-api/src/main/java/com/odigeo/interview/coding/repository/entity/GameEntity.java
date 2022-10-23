package com.odigeo.interview.coding.repository.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class GameEntity {

    private String id;
    private String playerOneId;
    private String playerTwoId;
    private Boolean vsComputer;
    private Integer playerTurn;
    private String playerOneField;
    private String playerTwoField;
    private Instant createdAt;
    private Instant startedAt;
    private Instant finishedAt;
    private String winner;

}
