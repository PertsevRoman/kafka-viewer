package com.kafka.viewer.entity;

import lombok.Data;

@Data(staticConstructor = "of")
public class PairTuple<A, B> {
    private final A left;
    private final B right;
}
