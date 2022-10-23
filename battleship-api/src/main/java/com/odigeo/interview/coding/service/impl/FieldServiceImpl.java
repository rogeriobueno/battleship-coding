package com.odigeo.interview.coding.service.impl;


import com.odigeo.interview.coding.domain.model.Cell;
import com.odigeo.interview.coding.domain.model.ship.Ship;
import com.odigeo.interview.coding.domain.util.GameConfiguration;
import com.odigeo.interview.coding.service.CoordinateService;
import com.odigeo.interview.coding.service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Primary
public class FieldServiceImpl implements FieldService {

    @Autowired
    private CoordinateService coordinateService;


    @Override
    public Boolean allShipsSunk(Cell[][] field) {
        return Arrays.stream(field).flatMap(Arrays::stream)
                .filter(cell -> !cell.isWater() && !cell.isHit()).findFirst().isEmpty();
    }

    @Override
    public Boolean isShipSunk(Cell[][] field, Ship ship) {
        return ship.getCoordinates()
                .parallelStream()
                .allMatch(shipCoordinate -> field[shipCoordinate.getRow()][shipCoordinate.getColumn()].isHit());
    }

    @Override
    public Cell[][] buildField(List<Ship> shipsDeployment) {
        Cell[][] field = buildWater();
        deployShips(field, shipsDeployment);
        return field;
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

    private void deployShips(Cell[][] field, List<Ship> ships) {
        ships.forEach(ship ->
                ship.getCoordinates().forEach(coordinate ->
                        field[coordinate.getRow()][coordinate.getColumn()] = new Cell(ship)
                )
        );
    }

}
