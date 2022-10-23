package com.odigeo.interview.coding.util;

import com.odigeo.interview.coding.domain.model.ship.Ship;
import com.odigeo.interview.coding.exception.ShipDeploymentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ShipDeploymentValidatorTest {
    @InjectMocks
    private ShipDeploymentValidator shipDeploymentValidator;

    @BeforeAll
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testValidateWithValidDeployment() {
        shipDeploymentValidator.validate(ShipDeploymentBuilder.buildValidDeployment());
    }

    @Test
    public void testValidateWithDuplicatedShipType() {
        List<Ship> shipsDeployment = ShipDeploymentBuilder.buildDuplicatedShipsDeployment();
        Exception exception = Assertions.assertThrows(ShipDeploymentException.class,
                () -> shipDeploymentValidator.validate(shipsDeployment),
                "Ship Destroyer with coordinates [A1, B1] is not deployed correctly on the field");
    }

    @Test
    public void testValidateWithIncorrectShipsNumberDeployed() {
        List<Ship> shipsDeployment = ShipDeploymentBuilder.buildWrongNumberOfShipsDeployment();
        Exception exception = Assertions.assertThrows(ShipDeploymentException.class,
                () -> shipDeploymentValidator.validate(shipsDeployment),
                "Wrong number of deployed ships: expected 5, got 4");
    }

    @Test
    public void testValidateWithOverlappingShips() {
        List<Ship> shipsDeployment = ShipDeploymentBuilder.buildOverlappingShipsDeployment();
        Exception exception = Assertions.assertThrows(ShipDeploymentException.class,
                () -> shipDeploymentValidator.validate(shipsDeployment));
    }

    @Test
    public void testValidateWithWrongShipLength() {
        List<Ship> shipsDeployment = ShipDeploymentBuilder.buildWrongShipLengthDeployment();
        Exception exception = Assertions.assertThrows(ShipDeploymentException.class,
                () -> shipDeploymentValidator.validate(shipsDeployment));
    }

    @Test
    public void testValidateWithWrongShipOutOfGrid() {
        List<Ship> shipsDeployment = ShipDeploymentBuilder.buildShipOutOfGridDeployment();
        Exception exception = Assertions.assertThrows(ShipDeploymentException.class,
                () -> shipDeploymentValidator.validate(shipsDeployment));
    }

}
