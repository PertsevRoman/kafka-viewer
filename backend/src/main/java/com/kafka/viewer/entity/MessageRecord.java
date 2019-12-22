package com.kafka.viewer.entity;

import lombok.Data;
import org.apache.avro.specific.SpecificRecordBase;

@Data
public class MessageRecord implements Comparable {
    private SpecificRecordBase record;
    private String type;
    private Long timestamp;
    private Integer partition;
    private Long index;

    @Override
    public int compareTo(Object o) {
        if (o instanceof MessageRecord) {
            return Long.compare(this.getIndex(), ((MessageRecord) o).getIndex());
        }
        
        throw new IllegalArgumentException("Cannot compare MessageRecord w/ something else");
    }
}
