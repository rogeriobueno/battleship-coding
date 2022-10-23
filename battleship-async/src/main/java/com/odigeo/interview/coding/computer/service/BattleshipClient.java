package com.odigeo.interview.coding.computer.service;

import com.odigeo.interview.coding.domain.contract.DeployShipsCommand;
import com.odigeo.interview.coding.domain.contract.GameFireCommand;
import com.odigeo.interview.coding.domain.contract.GameFireResponse;
import com.odigeo.interview.coding.domain.contract.GameJoinCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "battleship-client", url = "${game.server_feign}/battleship-service/api")
public interface BattleshipClient {

    @RequestMapping(method = RequestMethod.POST, value = "/games/{gameId}/join", consumes = "application/json")
    void joinGame(@PathVariable("gameId") String gameId, GameJoinCommand gameJoinCommand);

    @RequestMapping(method = RequestMethod.POST, value = "/games/{gameId}/fields/ships/deploy", consumes = "application/json")
    void deployShips(@PathVariable("gameId") String gameId, DeployShipsCommand deployShipsCommand);

    @RequestMapping(method = RequestMethod.POST, value = "/games/{gameId}/fields/fire", consumes = "application/json")
    GameFireResponse fire(@PathVariable("gameId") String gameId, GameFireCommand gameFireCommand);
}
