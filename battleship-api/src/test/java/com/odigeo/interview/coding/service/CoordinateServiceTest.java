package com.odigeo.interview.coding.service;


import com.odigeo.interview.coding.domain.model.Coordinate;
import com.odigeo.interview.coding.exception.WrongCoordinateException;
import com.odigeo.interview.coding.service.impl.CoordinateServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CoordinateServiceTest {

    @InjectMocks
    private final CoordinateService coordinateService = new CoordinateServiceImpl();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testDecodeCoordinateA1() {
        Coordinate coordinate = coordinateService.decodeCoordinate("A1");
        Assertions.assertEquals(coordinate, new Coordinate("A1", 0, 0));
    }

    @Test()
    public void testDecodeWrongCoordinatePattern() {
        Exception exception = Assertions.assertThrows(WrongCoordinateException.class, () -> {
            Coordinate coordinate = coordinateService.decodeCoordinate("AB");
        });
    }

}