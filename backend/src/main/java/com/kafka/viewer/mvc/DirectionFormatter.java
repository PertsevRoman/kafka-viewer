package com.kafka.viewer.mvc;

import com.kafka.viewer.entity.Direction;
import org.springframework.core.convert.converter.Converter;

public class DirectionFormatter implements Converter<String, Direction> {
    @Override
    public Direction convert(String source) {
        try {
            return Direction.valueOf(source.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
