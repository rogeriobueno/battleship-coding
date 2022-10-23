package com.odigeo.interview.coding.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.odigeo.interview.coding.domain.model.ship.Ship;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Cell {

    private Ship ship;
    private boolean hit;

    public Cell(Ship ship) {
        this.ship = ship;
    }

    @JsonIgnore
    public boolean isWater() {
        return ship == null;
    }

    @JsonIgnore
    public void hit() {
        setHit(true);
    }
}
