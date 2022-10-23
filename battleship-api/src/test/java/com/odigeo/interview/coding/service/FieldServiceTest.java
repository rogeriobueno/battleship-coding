package com.odigeo.interview.coding.service;

import com.odigeo.interview.coding.domain.model.Cell;
import com.odigeo.interview.coding.domain.model.Coordinate;
import com.odigeo.interview.coding.domain.model.ship.Ship;
import com.odigeo.interview.coding.service.impl.CoordinateServiceImpl;
import com.odigeo.interview.coding.service.impl.FieldServiceImpl;
import com.odigeo.interview.coding.util.ShipDeploymentBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class FieldServiceTest {

    @InjectMocks
    private final FieldService fieldService = new FieldServiceImpl();
    @Mock
    private CoordinateService coordinateService = new CoordinateServiceImpl();
    private List<Ship> shipsDeployment;
    private Cell[][] field;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        shipsDeployment = ShipDeploymentBuilder.buildValidDeployment();
        field = fieldService.buildField(shipsDeployment);
    }

    @Test
    public void testShipSunkPartialHit() {
        List<Coordinate> coordinates = shipsDeployment.get(0).getCoordinates();
        field[coordinates.get(0).getRow()][coordinates.get(0).getColumn()].hit();
        Boolean isShipSunk = fieldService.isShipSunk(field, shipsDeployment.get(0));
        Assertions.assertFalse(isShipSunk);
    }

    @Test
    public void testShipSunkNoHit() {
        List<Coordinate> coordinates = shipsDeployment.get(0).getCoordinates();
        boolean isShipSunk = fieldService.isShipSunk(field, shipsDeployment.get(0));
        Assertions.assertFalse(isShipSunk);
    }

    @Test
    public void testBuildFieldShipsDeployment() {
        shipsDeployment.forEach(ship -> {
            List<Coordinate> coordinates = ship.getCoordinates();
            coordinates.forEach(coordinate -> {
                Cell cell = field[coordinate.getRow()][coordinate.getColumn()];
                Assertions.assertNotNull(cell);
                Assertions.assertFalse(cell.isWater());
                Assertions.assertFalse(cell.isHit());
                Assertions.assertEquals(cell.getShip().getShipType(), ship.getShipType());
            });
        });
    }

    @Test
    public void testBuildWater() {
        Set<Coordinate> shipsCoordinates = shipsDeployment.stream()
                .map(Ship::getCoordinates)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        int height = field.length;
        int width = field[0].length;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                String coordinate = ((char) (65 + col)) + "" + (row + 1);
                if(!shipsCoordinates.contains(new Coordinate(coordinate, col, row))) {
                    boolean isWater = field[row][col].isWater();
                    Assertions.assertTrue(isWater);
                }
            }
        }
    }

}