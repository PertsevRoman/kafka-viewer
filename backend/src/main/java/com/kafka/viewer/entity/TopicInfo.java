package com.kafka.viewer.entity;

import lombok.Data;

import java.util.List;

@Data
public class TopicInfo {
    private List<Partition> partitions;
}
