package com.odigeo.interview.coding.service;

import com.odigeo.interview.coding.domain.model.Cell;
import com.odigeo.interview.coding.domain.model.ship.Ship;

import java.util.List;

public interface FieldService {
    Boolean allShipsSunk(Cell[][] field);

    Boolean isShipSunk(Cell[][] field, Ship ship);

    Cell[][] buildField(List<Ship> shipsDeployment);

    }
