package com.odigeo.interview.coding.computer.service;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CoordinateComputerServiceImpl {

    private final Random random = new Random();

    public String randomCoordinate() {
        int row = random.nextInt(10) + 1;
        char column = (char) (65 + random.nextInt(10));
        return column + "" + row;
    }
}
