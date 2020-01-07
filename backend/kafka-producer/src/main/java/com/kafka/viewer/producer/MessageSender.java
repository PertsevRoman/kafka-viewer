package com.kafka.viewer.producer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.stream.Stream;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
public class MessageSender <T extends SpecificRecordBase> {
    public void send(Stream<ProducerRecord<Long, T>> messagesStream, Producer<Long, T> producer) {
        messagesStream.forEach(producer::send);
    }
}
