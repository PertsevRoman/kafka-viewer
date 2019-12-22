package com.ringcentral.kv.entity;

import lombok.Data;

@Data
public class Partition {
    private Integer index;
    private Long messageCount;
}
