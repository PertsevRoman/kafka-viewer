package com.kafka.viewer.entity;

import lombok.Data;

import java.util.List;

/**
 * Records chunk w/ offset and total count
 * 
 * @apiNote Record list size and count field value could be not the same
 * 
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
@Data
public class RecordsPool {
    private List<MessageRecord> recordList;
    private Long offset;
    private Integer count;
    private Long lastTimestamp = 0L;
}
