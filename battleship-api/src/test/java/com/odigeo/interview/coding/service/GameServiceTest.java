package com.odigeo.interview.coding.service;

import com.odigeo.interview.coding.domain.contract.*;
import com.odigeo.interview.coding.domain.event.GameCreatedEvent;
import com.odigeo.interview.coding.domain.event.GameFireEvent;
import com.odigeo.interview.coding.domain.model.Cell;
import com.odigeo.interview.coding.domain.model.Coordinate;
import com.odigeo.interview.coding.domain.model.Game;
import com.odigeo.interview.coding.domain.model.ship.Ship;
import com.odigeo.interview.coding.domain.model.ship.ShipType;
import com.odigeo.interview.coding.domain.util.GameConfiguration;
import com.odigeo.interview.coding.exception.*;
import com.odigeo.interview.coding.repository.impl.GameRepositoryImpl;
import com.odigeo.interview.coding.service.impl.CoordinateServiceImpl;
import com.odigeo.interview.coding.service.impl.FieldServiceImpl;
import com.odigeo.interview.coding.service.impl.GameServiceImpl;
import com.odigeo.interview.coding.service.impl.KafkaProducerServiceImpl;
import com.odigeo.interview.coding.util.ShipDeploymentBuilder;
import com.odigeo.interview.coding.util.ShipDeploymentValidator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private CoordinateServiceImpl coordinateService;
    @Mock
    private FieldServiceImpl fieldService;

    @Mock
    private ShipDeploymentValidator shipDeploymentValidator;
    @Mock
    private KafkaProducerServiceImpl kafkaProducerServiceImpl;
    @Mock
    private GameRepositoryImpl gameRepository;
    @Mock
    private Game game;

    @InjectMocks
    private GameServiceImpl gameService;

    @BeforeAll
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    public void tearDown() {
        reset(kafkaProducerServiceImpl);
        reset(gameRepository);
    }

    @Test
    public void testNewGameVsComputer() {
        GameStartCommand command = new GameStartCommand();
        command.setPlayerId("player1");
        command.setVsComputer(true);
        Game newGame = gameService.newGame(command);
        assertNotNull(newGame);
        assertNotNull(newGame.getId());
        assertEquals(newGame.getPlayerOneId(), command.getPlayerId());
        assertEquals(newGame.isVsComputer(), command.isVsComputer());
        verify(kafkaProducerServiceImpl, times(1)).publish(any(GameCreatedEvent.class));
        verify(gameRepository, times(1)).saveOrUpdateGame(any(Game.class));
    }

    @Test
    public void testNewGameVsHumanPlayer() {
        GameStartCommand command = new GameStartCommand();
        command.setPlayerId("player1");
        command.setVsComputer(false);
        Game newGame = gameService.newGame(command);
        assertNotNull(newGame);
        assertNotNull(newGame.getId());
        assertEquals(newGame.getPlayerOneId(), command.getPlayerId());
        assertEquals(newGame.isVsComputer(), command.isVsComputer());
        verify(kafkaProducerServiceImpl, never()).publish(any(GameCreatedEvent.class));
        verify(gameRepository, times(1)).saveOrUpdateGame(any(Game.class));
    }

    @Test
    public void testJoinGame() {
        lenient().when(game.getId()).thenReturn("12345");
        when(game.getPlayerTwoId()).thenReturn(null);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameJoinCommand command = new GameJoinCommand();
        command.setPlayerId("player2");
        gameService.joinGame("12345", command);
        verify(gameRepository, times(1)).saveOrUpdateGame(game);
    }

    @Test()
    public void testJoinFullGame() {
        lenient().when(game.getId()).thenReturn("12345");
        when(game.getPlayerTwoId()).thenReturn("anotherPlayer");
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameJoinCommand command = new GameJoinCommand();
        command.setPlayerId("player2");
        Exception exception = Assertions.assertThrows(GameJoinException.class,
                () -> gameService.joinGame("12345", command),
                "Another player is already playing this game");

    }

    @Test
    public void testDeployShips() {
        lenient().when(game.getId()).thenReturn("12345");
        lenient().when(game.getPlayerOneId()).thenReturn("player1");
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        DeployShipsCommand command = new DeployShipsCommand();
        command.setPlayerId("player1");
        command.setShipsDeploy(ShipDeploymentBuilder.buildShipsDeployment());
        gameService.deployShips("12345", command);
        verify(gameRepository, times(1)).saveOrUpdateGame(game);
    }

    @Test()
    public void testDeployShipsWithIncorrectShipType() {
        lenient().when(game.getId()).thenReturn("12345");
        lenient().when(game.getPlayerOneId()).thenReturn("player1");
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        DeployShipsCommand command = new DeployShipsCommand();
        command.setPlayerId("player1");
        List<DeployShipsCommand.ShipDeployment> shipsDeployment = ShipDeploymentBuilder.buildShipsDeployment();
        shipsDeployment.remove(shipsDeployment.size() - 1);
        shipsDeployment.add(new DeployShipsCommand.ShipDeployment("YellowSubmarine", "A1", "B1"));
        command.setShipsDeploy(shipsDeployment);
        Exception exception = Assertions.assertThrows(ShipDeploymentException.class, () ->
                gameService.deployShips("12345", command));
    }

    @Test
    public void testDeployShipsAndPlayersReady() {
        lenient().when(game.getId()).thenReturn("12345");
        when(game.playerReady(any())).thenReturn(false);
        when(game.playersReady()).thenReturn(true);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        DeployShipsCommand command = new DeployShipsCommand();
        command.setPlayerId("player1");
        command.setShipsDeploy(ShipDeploymentBuilder.buildShipsDeployment());
        gameService.deployShips("12345", command);
        verify(gameRepository, times(1)).saveOrUpdateGame(game);
    }

    @Test()
    public void testDeployShipsAlreadyDeployed() {
        lenient().when(game.getId()).thenReturn("12345");
        when(game.playerReady(any())).thenReturn(true);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        DeployShipsCommand command = new DeployShipsCommand();
        command.setPlayerId("player1");
        command.setShipsDeploy(ShipDeploymentBuilder.buildShipsDeployment());
        Exception exception = Assertions.assertThrows(ShipsAlreadyDeployedException.class, () ->
                gameService.deployShips("12345", command), "player1's ships are already deployed");

    }

    @Test()
    public void testFireWhenGameIsAlreadyFinished() {
        lenient().when(game.getId()).thenReturn("12345");
        when(game.getWinner()).thenReturn("player2");
        when(game.isFinished()).thenReturn(true);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameFireCommand command = new GameFireCommand();
        command.setPlayerId("player1");
        command.setCoordinate("A1");
        Exception exception = Assertions.assertThrows(GameFinishedException.class, () ->
                gameService.fire("12345", command), "The winner is player2");
        verify(gameRepository, never()).saveOrUpdateGame(game);
    }

    @Test()
    public void testFireWhenPlayersNotReady() {
        lenient().when(game.getId()).thenReturn("12345");
        when(game.isFinished()).thenReturn(false);
        when(game.playersReady()).thenReturn(false);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameFireCommand command = new GameFireCommand();
        command.setPlayerId("player1");
        command.setCoordinate("A1");
        Exception exception = Assertions.assertThrows(GameStartException.class, () ->
                gameService.fire("12345", command), "Players not ready");
        verify(gameRepository, never()).saveOrUpdateGame(game);
    }

    @Test()
    public void testFireWhenIsNotPlayerTurn() {
        lenient().when(game.getId()).thenReturn("12345");
        when(game.isFinished()).thenReturn(false);
        when(game.playersReady()).thenReturn(true);
        when(game.isPlayerTurn(any(String.class))).thenReturn(false);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameFireCommand command = new GameFireCommand();
        command.setPlayerId("player1");
        command.setCoordinate("A1");
        Exception exception = Assertions.assertThrows(NotYourTurnException.class, () ->
                gameService.fire("12345", command), "player1 is not your turn");
        verify(gameRepository, never()).saveOrUpdateGame(game);
    }

    @Test()
    public void testFireWhenIsNotPlayerTurnAndNeedToPingTheComputer() {
        when(game.getId()).thenReturn("12345");
        when(game.isFinished()).thenReturn(false);
        when(game.playersReady()).thenReturn(true);
        when(game.isPlayerTurn(any(String.class))).thenReturn(false);
        when(game.isVsComputer()).thenReturn(true);
        when(game.isPlayerTurn(1)).thenReturn(true);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameFireCommand command = new GameFireCommand();
        command.setPlayerId("player1");
        command.setCoordinate("A1");
        Exception exception = Assertions.assertThrows(NotYourTurnException.class, () ->
                gameService.fire("12345", command), "player1 is not your turn");
        verify(kafkaProducerServiceImpl, times(1)).publish(any(GameFireEvent.class));
        verify(gameRepository, never()).saveOrUpdateGame(game);
    }

    @Test
    public void testFireHit() {
        when(coordinateService.decodeCoordinate(any())).thenCallRealMethod();
        when(fieldService.isShipSunk(any(), any(Ship.class))).thenCallRealMethod();
//        when(fieldService.allShipsSunk(any())).thenCallRealMethod();


        final String[] gridCoordinate = new String[]{"A1", "A2"};
        Cell[][] field = buildFieldWithShip("Destroyer", gridCoordinate);
        when(game.getId()).thenReturn("12345");
        when(game.isVsComputer()).thenReturn(true);
        when(game.isFinished()).thenReturn(false);
        when(game.playersReady()).thenReturn(true);
        when(game.isPlayerTurn(any(String.class))).thenReturn(true);
        when(game.isPlayerTurn(1)).thenReturn(true);
        when(game.getOpponentField(any())).thenReturn(field);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameFireCommand command = new GameFireCommand();
        command.setPlayerId("player1");
        command.setCoordinate(gridCoordinate[0]);
        GameFireResponse fireResponse = gameService.fire("12345", command);
        assertNotNull(fireResponse);
        assertEquals(fireResponse.getFireOutcome(), GameFireResponse.FireOutcome.HIT);
        verify(kafkaProducerServiceImpl, times(1)).publish(any(GameFireEvent.class));
        verify(gameRepository, times(1)).saveOrUpdateGame(game);
    }

    @Test
    public void testFireSunkAndGameWon() {
        final String[] gridCoordinate = new String[]{"A1"};

        when(coordinateService.decodeCoordinate(any())).thenCallRealMethod();
        when(fieldService.isShipSunk(any(), any(Ship.class))).thenCallRealMethod();
        when(fieldService.allShipsSunk(any())).thenCallRealMethod();

        Cell[][] field = buildFieldWithShip("Destroyer", gridCoordinate);
        when(game.getId()).thenReturn("12345");
        when(game.isVsComputer()).thenReturn(true);
        when(game.isFinished()).thenReturn(false);
        when(game.playersReady()).thenReturn(true);
        when(game.isPlayerTurn(any(String.class))).thenReturn(true);
        when(game.isPlayerTurn(1)).thenReturn(true);
        when(game.getOpponentField(any())).thenReturn(field);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameFireCommand command = new GameFireCommand();
        command.setPlayerId("player1");
        command.setCoordinate(gridCoordinate[0]);
        GameFireResponse fireResponse = gameService.fire("12345", command);
        assertNotNull(fireResponse);
        assertTrue(fireResponse.isGameWon());
        assertEquals(GameFireResponse.FireOutcome.SUNK, fireResponse.getFireOutcome());
        verify(kafkaProducerServiceImpl, times(1)).publish(any(GameFireEvent.class));
        verify(gameRepository, times(1)).saveOrUpdateGame(game);
    }

    @Test
    public void testFireMiss() {
        final String[] gridCoordinate = new String[]{"A1"};
        when(coordinateService.decodeCoordinate(any())).thenCallRealMethod();
        Cell[][] field = buildFieldWithShip("Destroyer", gridCoordinate);
        when(game.getId()).thenReturn("12345");
        when(game.isVsComputer()).thenReturn(true);
        when(game.isFinished()).thenReturn(false);
        when(game.playersReady()).thenReturn(true);
        when(game.isPlayerTurn(any(String.class))).thenReturn(true);
        when(game.isPlayerTurn(1)).thenReturn(true);
        when(game.getOpponentField(any())).thenReturn(field);
        when(gameRepository.getGame(any())).thenReturn(Optional.of(game));
        GameFireCommand command = new GameFireCommand();
        command.setPlayerId("player1");
        command.setCoordinate("B6");
        GameFireResponse fireResponse = gameService.fire("12345", command);
        assertNotNull(fireResponse);
        assertEquals(fireResponse.getFireOutcome(), GameFireResponse.FireOutcome.MISS);
        verify(kafkaProducerServiceImpl, times(1)).publish(any(GameFireEvent.class));
        verify(gameRepository, times(1)).saveOrUpdateGame(game);
    }

    private Cell[][] buildFieldWithShip(String shipType, String... gridCoordinates) {
        Cell[][] field = buildWater();
        deployShip(field, shipType, gridCoordinates);
        return field;
    }

    private void deployShip(Cell[][] field, String shipType, String[] gridCoordinates) {
        Ship ship = ShipType.getByTypeName(shipType).newInstance();
        List<Coordinate> coordinates = Arrays.stream(gridCoordinates)
                .map(s -> coordinateService.decodeCoordinate(s))
                .collect(Collectors.toList());
        coordinates.forEach(coordinate -> {
            ship.getCoordinates().add(coordinate);
            field[coordinate.getRow()][coordinate.getColumn()] = new Cell(ship);
        });
    }

    private Cell[][] buildWater() {
        Cell[][] field = new Cell[GameConfiguration.FIELD_HEIGHT][GameConfiguration.FIELD_WIDTH];
        for (int row = 0; row < GameConfiguration.FIELD_HEIGHT; row++) {
            for (int col = 0; col < GameConfiguration.FIELD_WIDTH; col++) {
                field[row][col] = new Cell();
            }
        }
        return field;
    }

}
