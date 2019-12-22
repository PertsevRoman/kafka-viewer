package com.kafka.viewer.entity;

public enum Direction {
    ASC("asc"),
    DESC("desc");

    private final String value;

    Direction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }
}
