package com.kafka.viewer.entity;

import lombok.Data;

@Data
public class S3Schema {
    private String key;
    private String schema;
}
