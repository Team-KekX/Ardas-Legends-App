package com.ardaslegends.presentation.api.response.claimbuild.coordinate;

import com.ardaslegends.domain.Coordinate;

public record CoordinateResponse(
        Integer x,
        Integer y,
        Integer z
) {

    public CoordinateResponse(Coordinate coordinate) {
        this(coordinate.getX(), coordinate.getY(), coordinate.getZ());
    }
}
