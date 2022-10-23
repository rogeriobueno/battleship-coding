package com.odigeo.interview.coding.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odigeo.interview.coding.computer.listener.KafkaMDB;
import com.odigeo.interview.coding.computer.service.BattleshipService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
public class KafkaMDBTest {

    @SuppressWarnings("rawtypes")
    @Mock
    private static ConsumerRecord consumerRecord;
    @Mock
    private BattleshipService battleshipService;
    @Spy
    private ObjectMapper mapper;
    @InjectMocks
    private KafkaMDB kafkaMDB;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(consumerRecord.value()).thenReturn("{\"gameId\":\"3238eef4-5e2c-4add-accb-4ca9514d5aa2\"}");
    }

    @Test
    public void testOnGameNew() throws JsonProcessingException {
        kafkaMDB.onGameNew(consumerRecord);
        verify(battleshipService, times(1)).joinGame("3238eef4-5e2c-4add-accb-4ca9514d5aa2");
        verify(battleshipService, times(1)).deployShips("3238eef4-5e2c-4add-accb-4ca9514d5aa2");
    }

    @Test
    public void testOnGameFieldFire() throws JsonProcessingException {
        kafkaMDB.onGameFieldFire(consumerRecord);
        verify(battleshipService, times(1)).fire("3238eef4-5e2c-4add-accb-4ca9514d5aa2");
    }

}