package com.odigeo.interview.coding.service.impl;

import com.odigeo.interview.coding.domain.contract.*;
import com.odigeo.interview.coding.domain.event.GameCreatedEvent;
import com.odigeo.interview.coding.domain.event.GameFireEvent;
import com.odigeo.interview.coding.domain.model.Cell;
import com.odigeo.interview.coding.domain.model.Coordinate;
import com.odigeo.interview.coding.domain.model.Game;
import com.odigeo.interview.coding.domain.model.ship.Ship;
import com.odigeo.interview.coding.domain.model.ship.ShipType;
import com.odigeo.interview.coding.exception.*;
import com.odigeo.interview.coding.repository.GameRepository;
import com.odigeo.interview.coding.service.CoordinateService;
import com.odigeo.interview.coding.service.GameService;
import com.odigeo.interview.coding.service.KafkaProducerService;
import com.odigeo.interview.coding.util.ShipDeploymentValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@Primary
public class GameServiceImpl implements GameService {
    @Autowired
    private CoordinateService coordinateService;

    @Autowired
    private FieldServiceImpl fieldService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private GameRepository repository;

    @Autowired
    private ShipDeploymentValidator shipDeploymentValidator;

    @Override
    public Game newGame(GameStartCommand command) {
        Game game = new Game();
        game.setId(UUID.randomUUID().toString());
        game.setPlayerOneId(command.getPlayerId());
        game.setVsComputer(command.isVsComputer());
        if (command.isVsComputer()) {
            kafkaProducerService.publish(new GameCreatedEvent(game.getId()));
        }
        game.setCreatedAt(Instant.now());
        game.setPlayerTurn(1);
        repository.saveOrUpdateGame(game);
        log.info("New game created {}", game.getId());
        return game;
    }

    @Override
    public void joinGame(String gameId, GameJoinCommand command) {
        Game game = repository.getGame(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.getPlayerTwoId() != null) {
            throw new GameJoinException("Another player is already playing this game");
        }

        game.setPlayerTwoId(command.getPlayerId());
        repository.saveOrUpdateGame(game);
    }

    @Override
    public void deployShips(String gameId, DeployShipsCommand command) {
        Game game = repository.getGame(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.playerReady(command.getPlayerId())) {
            throw new ShipsAlreadyDeployedException(command.getPlayerId());
        }
        List<Ship> shipsDeployment = mapShipsDeployment(command.getShipsDeploy());
        shipDeploymentValidator.validate(shipsDeployment);
        Cell[][] playerField = fieldService.buildField(shipsDeployment);
        game.setPlayerField(command.getPlayerId(), playerField);

        if (game.playersReady()) {
            game.setStartedAt(Instant.now());
        }

        repository.saveOrUpdateGame(game);
    }

    @Override
    public List<Ship> mapShipsDeployment(List<DeployShipsCommand.ShipDeployment> shipDeployments) {
        List<Ship> ships = new ArrayList<>();
        for (DeployShipsCommand.ShipDeployment shipDeployment : shipDeployments) {
            try {
                Ship ship = ShipType.getByTypeName(shipDeployment.getShipType()).newInstance();
                ship.setCoordinates(shipDeployment.getCoordinates().stream()
                        .map(coordinate -> coordinateService.decodeCoordinate(coordinate))
                        .collect(Collectors.toList()));
                ships.add(ship);
            } catch (Exception e) {
                throw new ShipDeploymentException(shipDeployment.getShipType(), shipDeployment.getCoordinates(), e);
            }
        }
        return ships;
    }
    @Override
    public GameFireResponse fire(String gameId, GameFireCommand command) {
        Game game = repository.getGame(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.isFinished()) {
            throw new GameFinishedException(game.getWinner());
        }

        if (!game.playersReady()) {
            throw new GameStartException("Players not ready");
        }

        if (!game.isPlayerTurn(command.getPlayerId())) {
            if (game.isVsComputer() && game.isPlayerTurn(1)) {
                // Ping the computer to avoid rare deadlocks
                kafkaProducerService.publish(new GameFireEvent(game.getId()));
            }
            throw new NotYourTurnException(command.getPlayerId());
        }

        Cell[][] field = game.getOpponentField(command.getPlayerId());
        Coordinate coordinate = coordinateService.decodeCoordinate(command.getCoordinate());
        Cell cell = field[coordinate.getRow()][coordinate.getColumn()];
        GameFireResponse response;
        if (cell.isWater()) {
            cell.hit();
            response = new GameFireResponse(GameFireResponse.FireOutcome.MISS);
        } else {
            cell.hit();
            Ship ship = cell.getShip();
            if (fieldService.isShipSunk(field, ship)) {
                response = new GameFireResponse(GameFireResponse.FireOutcome.SUNK);
                if (fieldService.allShipsSunk(field)) {
                    response.setGameWon(true);
                    game.setWinner(command.getPlayerId());
                    game.setFinishedAt(Instant.now());
                }
            } else {
                response = new GameFireResponse(GameFireResponse.FireOutcome.HIT);
            }
        }

        if (game.isVsComputer() && game.isPlayerTurn(1)) {
            kafkaProducerService.publish(new GameFireEvent(game.getId()));
        }

        game.setNextPlayerTurn();
        repository.saveOrUpdateGame(game);
        return response;
    }

}
