package com.odigeo.interview.coding.domain.contract;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class DeployShipsCommand {

    private String playerId;
    private List<ShipDeployment> shipsDeploy;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ShipDeployment {

        private String shipType;
        private List<String> coordinates;

        public ShipDeployment(String shipType, String... coordinates) {
            this.shipType = shipType;
            this.coordinates = Arrays.asList(coordinates);
        }
    }

}
