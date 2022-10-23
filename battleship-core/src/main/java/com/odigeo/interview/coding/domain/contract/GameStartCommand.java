package com.odigeo.interview.coding.domain.contract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameStartCommand {

    private String playerId;
    private boolean vsComputer = true;

}
