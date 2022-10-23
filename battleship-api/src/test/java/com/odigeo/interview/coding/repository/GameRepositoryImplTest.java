package com.odigeo.interview.coding.repository;

import com.odigeo.interview.coding.domain.model.Game;
import com.odigeo.interview.coding.mapper.GameMapper;
import com.odigeo.interview.coding.repository.impl.GameRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Random;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class GameRepositoryImplTest {

    @Spy
    private GameMapper mapper = Mappers.getMapper(GameMapper.class);

    @InjectMocks
    private GameRepositoryImpl gameRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testSaveGame() {
        Game myGame = buildNewGame();
        gameRepository.saveOrUpdateGame(myGame);
        Assertions.assertTrue(gameRepository.getGames().size() > 0);
        Assertions.assertTrue(gameRepository.getGame(myGame.getId()).isPresent());
    }

    @Test
    public void testUpdateGame() {
        Game myGame = buildNewGame();
        gameRepository.saveOrUpdateGame(myGame);
        Assertions.assertNotNull(gameRepository.getGame(myGame.getId()));

        Game finishedGame = gameRepository.getGame(myGame.getId()).get();
        finishedGame.setWinner("P1");
        finishedGame.setFinishedAt(Instant.now());
        gameRepository.saveOrUpdateGame(finishedGame);
        Assertions.assertTrue(gameRepository.getGame(myGame.getId()).isPresent());
        Assertions.assertTrue(gameRepository.getGame(myGame.getId()).get().isFinished());
    }


    @Test
    public void testGetGame() {
        Game myGame = buildNewGame();
        gameRepository.saveOrUpdateGame(myGame);
        Assertions.assertTrue(gameRepository.getGames().size() > 0);
        Assertions.assertTrue(gameRepository.getGame(myGame.getId()).isPresent());
    }


    private Game buildNewGame() {
        Game newGame = new Game();
        newGame.setId(String.valueOf(new Random().nextInt()));
        newGame.setCreatedAt(Instant.now());
        newGame.setPlayerOneId("P1");
        newGame.setVsComputer(true);
        newGame.setPlayerTurn(1);
        return newGame;
    }
}
