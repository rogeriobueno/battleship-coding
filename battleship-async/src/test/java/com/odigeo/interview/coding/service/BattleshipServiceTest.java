package com.odigeo.interview.coding.service;

import com.odigeo.interview.coding.computer.service.BattleshipClient;
import com.odigeo.interview.coding.computer.service.BattleshipService;
import com.odigeo.interview.coding.computer.service.CoordinateComputerServiceImpl;
import com.odigeo.interview.coding.domain.contract.GameFireResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BattleshipServiceTest {

    @Mock
    private GameFireResponse gameFireResponse;
    @Mock
    private BattleshipClient battleshipClient;
    @Spy
    private CoordinateComputerServiceImpl coordinateComputerService;
    @InjectMocks
    private BattleshipService battleshipService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testJoinGame() {
        battleshipService.joinGame("12345");
        verify(battleshipClient, times(1)).joinGame(any(), any());
    }

    @Test
    public void testDeployShips() {
        battleshipService.deployShips("12345");
        verify(battleshipClient, times(1)).deployShips(any(), any());
    }

    @Test
    public void testFire() {
        when(gameFireResponse.isGameWon()).thenReturn(true);
        when(battleshipClient.fire(any(), any())).thenReturn(gameFireResponse);
        battleshipService.fire("12345");
        verify(battleshipClient, times(1)).fire(any(), any());
    }
}