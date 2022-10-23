package com.odigeo.interview.coding.service;

import com.odigeo.interview.coding.domain.contract.*;
import com.odigeo.interview.coding.domain.model.Game;
import com.odigeo.interview.coding.domain.model.ship.Ship;

import java.util.List;

public interface GameService {
    Game newGame(GameStartCommand command);

    void joinGame(String gameId, GameJoinCommand command);

    void deployShips(String gameId, DeployShipsCommand command);

    List<Ship> mapShipsDeployment(List<DeployShipsCommand.ShipDeployment> shipDeployments);
    GameFireResponse fire(String gameId, GameFireCommand command);
}
