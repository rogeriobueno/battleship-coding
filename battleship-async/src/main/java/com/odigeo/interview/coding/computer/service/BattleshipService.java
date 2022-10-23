package com.odigeo.interview.coding.computer.service;

import com.odigeo.interview.coding.computer.util.BattleshipClientCommandBuilder;
import com.odigeo.interview.coding.domain.contract.GameFireResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class BattleshipService {
    @Autowired
    private BattleshipClient client;

    @Autowired
    private CoordinateComputerServiceImpl coordinateComputerServiceImpl;

    public void joinGame(String gameId) {
        client.joinGame(gameId, BattleshipClientCommandBuilder.buildGameJoinCommand());
        log.info("[gameId={}] Computer joined the game", gameId);
    }

    public void deployShips(String gameId) {
        client.deployShips(gameId, BattleshipClientCommandBuilder.buildDeployShipsCommand());
        log.info("[gameId={}] Computer deployed its ships", gameId);
    }

    public void fire(String gameId) {
        final String coordinate = thinkWhereToFire();
        GameFireResponse gameFireResponse = client.fire(gameId, BattleshipClientCommandBuilder.buildGameFireCommand(coordinate));
        log.info("[gameId={}] Computer {} the ship on {}", gameId, gameFireResponse.getFireOutcome(), coordinate);
        if (gameFireResponse.isGameWon()) {
            log.info("[gameId={}] Computer WON the game", gameId);
        }
    }

    private String thinkWhereToFire() {
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            log.error("Error in thinkToFire Robot");
        }
        return coordinateComputerServiceImpl.randomCoordinate();
    }

}
