package com.odigeo.interview.coding.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Coordinate {

    private String value;
    private int column;
    private int row;
}
