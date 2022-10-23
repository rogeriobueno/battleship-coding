package com.odigeo.interview.coding.domain.contract;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GameFireResponse {

    private FireOutcome fireOutcome;
    private boolean gameWon;

    public GameFireResponse(FireOutcome fireOutcome) {
        this.fireOutcome = fireOutcome;
    }

    public enum FireOutcome {
        MISS, HIT, SUNK
    }

}
