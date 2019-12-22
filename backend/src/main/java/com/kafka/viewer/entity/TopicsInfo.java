package com.kafka.viewer.entity;

import lombok.Data;

import java.util.Set;

@Data
public class TopicsInfo {
    private Set<String> names;
}
