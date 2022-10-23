package com.odigeo.interview.coding.service;


import com.odigeo.interview.coding.computer.service.CoordinateComputerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CoordinateServiceTest {

    @InjectMocks
    private CoordinateComputerServiceImpl coordinateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRandomCoordinate() {
        String coordinate = coordinateService.randomCoordinate();
        assertNotNull(coordinate);
        assertTrue(coordinate.matches("^([A-Z])(\\d+)$"));
    }

}