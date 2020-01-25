package com.kafka.viewer.producer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.stream.Stream;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
public class MessageSender {

    public static void send(Stream<? extends ProducerRecord<Long, SpecificRecordBase>> stream,
                            Producer<Long, SpecificRecordBase> producer) {
        stream.forEach(producer::send);
    }
}
