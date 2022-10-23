package com.odigeo.interview.coding.controller;

import com.odigeo.interview.coding.domain.contract.*;
import com.odigeo.interview.coding.domain.model.Game;
import com.odigeo.interview.coding.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/games")
public class GameController {

    @Autowired
    private GameService service;

    public GameController() {
    }

    @RequestMapping(value = "/new")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation()
    public ResponseEntity<GameResponse> newGame(@RequestBody GameStartCommand gameStartCommand) {
        Game game = service.newGame(gameStartCommand);
        return new ResponseEntity<>(new GameResponse(game.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{gameId}/join")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation()
    public ResponseEntity<Void> joinGame(@PathVariable("gameId") String gameId, @RequestBody GameJoinCommand gameJoinCommand) {
        service.joinGame(gameId, gameJoinCommand);
        return ResponseEntity.ok().build();
    }


    @RequestMapping(value = "/{gameId}/fields/ships/deploy")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation()
    public ResponseEntity<Void> deployShips(@PathVariable("gameId") String gameId, @RequestBody DeployShipsCommand deployShipsCommand) {
        service.deployShips(gameId, deployShipsCommand);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{gameId}/fields/fire")
    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation()
    public ResponseEntity<GameFireResponse> fire(@PathVariable("gameId") String gameId, @RequestBody GameFireCommand gameFireCommand) {
        GameFireResponse fire = service.fire(gameId, gameFireCommand);
        return ResponseEntity.ok(fire);
    }

}
