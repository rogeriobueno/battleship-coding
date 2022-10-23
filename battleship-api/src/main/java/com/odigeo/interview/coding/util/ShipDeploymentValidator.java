package com.odigeo.interview.coding.util;

import com.odigeo.interview.coding.domain.model.Coordinate;
import com.odigeo.interview.coding.domain.model.ship.Ship;
import com.odigeo.interview.coding.domain.model.ship.ShipType;
import com.odigeo.interview.coding.domain.util.GameConfiguration;
import com.odigeo.interview.coding.exception.ShipDeploymentException;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class ShipDeploymentValidator {

    private static boolean coordinateIsOutOfGrid(Coordinate coordinate) {
        return coordinate.getRow() < 0 ||
                coordinate.getColumn() < 0 ||
                coordinate.getRow() >= GameConfiguration.FIELD_HEIGHT ||
                coordinate.getColumn() >= GameConfiguration.FIELD_WIDTH;
    }

    public void validate(List<Ship> ships) {
        numberOfDeployedShips(ships);
        duplicatedDeployment(ships);
        shipsOverlap(ships);
        ships.forEach(ship -> {
            shipLengthIsWrong(ship);
            shipIsOutOfGrid(ship);
            shipIsNotContiguous(ship);
        });
    }

    private void duplicatedDeployment(List<Ship> ships) {
        List<String> deployedTypes = new ArrayList<>();
        for (Ship ship: ships) {
            String shipType = ship.getShipType().getShipTypeName();
            if (deployedTypes.contains(shipType)) {
                throw new ShipDeploymentException(shipType, ship.getCoordinates().stream().map(Coordinate::getValue).collect(toList()));
            }
            deployedTypes.add(shipType);
        }
    }

    private void numberOfDeployedShips(List<Ship> shipDeployments) {
        int requiredNumberOfDeployments = ShipType.values().length;
        int providedDeploymentsNumber = shipDeployments.size();
        if (providedDeploymentsNumber != requiredNumberOfDeployments) {
            throw new ShipDeploymentException(String.format("Wrong number of deployed ships: expected %d, got %d", requiredNumberOfDeployments, providedDeploymentsNumber));
        }
    }

    private void shipLengthIsWrong(Ship ship) {
        // Use a set to account for overlapping coordinates
        boolean isLengthWrong = new HashSet<>(ship.getCoordinates()).size() != ship.getShipType().getShipLength();
        if (isLengthWrong) {
            throw new ShipDeploymentException(ship.getShipType().getShipTypeName(), ship.getCoordinates().stream().map(Coordinate::getValue).collect(toList()));
        }
    }

    private void shipIsOutOfGrid(Ship ship) {
        boolean isShipOutOfGrid = ship.getCoordinates().stream().anyMatch(ShipDeploymentValidator::coordinateIsOutOfGrid);
        if (isShipOutOfGrid) {
            throw new ShipDeploymentException(ship.getShipType().getShipTypeName(), ship.getCoordinates().stream().map(Coordinate::getValue).collect(toList()));
        }
    }

    private void shipIsNotContiguous(Ship ship) {
        boolean shipIsNotContiguous = !isHorizontal(ship) && !isVertical(ship);
        if (shipIsNotContiguous) {
            throw new ShipDeploymentException(ship.getShipType().getShipTypeName(), ship.getCoordinates().stream().map(Coordinate::getValue).collect(toList()));
        }
    }

    private boolean isHorizontal(Ship ship) {
        List<Coordinate> coordinates = ship.getCoordinates();
        int firstRow = coordinates.get(0).getRow();
        return coordinates.stream().allMatch(c -> c.getRow() == firstRow);
    }

    private boolean isVertical(Ship ship) {
        List<Coordinate> coordinates = ship.getCoordinates();
        int firstColumn = coordinates.get(0).getColumn();
        return coordinates.stream().allMatch(c -> c.getColumn() == firstColumn);
    }

    private void shipsOverlap(Collection<Ship> deployedShips) {
        List<Coordinate> allCoordinates = deployedShips.stream()
                .flatMap(ship -> ship.getCoordinates().stream())
                .collect(toList());
        Set<Coordinate> reducedCoordinates = new HashSet<>(allCoordinates);
        boolean isOverlapping = allCoordinates.size() != reducedCoordinates.size();
        if (isOverlapping) {
            throw new ShipDeploymentException("Deployed ships overlap - they cannot overlap!");
        }
    }
}
