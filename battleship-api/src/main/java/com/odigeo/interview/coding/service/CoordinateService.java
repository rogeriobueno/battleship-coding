package com.odigeo.interview.coding.service;

import com.odigeo.interview.coding.domain.model.Coordinate;

public interface CoordinateService {
    Coordinate decodeCoordinate(String coordinate);
}
