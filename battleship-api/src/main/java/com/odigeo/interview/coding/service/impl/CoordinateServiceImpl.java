package com.odigeo.interview.coding.service.impl;

import com.odigeo.interview.coding.domain.model.Coordinate;
import com.odigeo.interview.coding.exception.WrongCoordinateException;
import com.odigeo.interview.coding.service.CoordinateService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Primary
public class CoordinateServiceImpl implements CoordinateService {

    private static final String COORDINATE_REGEX = "^([A-Z])(\\d+)$";
    private static final Pattern PATTERN = Pattern.compile(COORDINATE_REGEX);

    @Override
    public Coordinate decodeCoordinate(String coordinate) {
        Matcher matcher = PATTERN.matcher(coordinate);

        if(!matcher.matches()) {
            throw new WrongCoordinateException(coordinate);
        }

        String column = matcher.group(1);
        String row = matcher.group(2);

        int columnIndex = decodeColumn(column.charAt(0));
        int rowIndex = decodeRow(row);
        return new Coordinate(coordinate, columnIndex, rowIndex);
    }

    private int decodeColumn(char c) {
        return c - 'A';
    }

    private int decodeRow(String value) {
        return Integer.parseInt(value) - 1;
    }

}
