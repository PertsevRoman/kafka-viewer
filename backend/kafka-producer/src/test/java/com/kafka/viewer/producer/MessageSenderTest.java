package com.kafka.viewer.producer;

import com.kafka.viewer.avro.Order;
import com.kafka.viewer.generator.OrderGenerator;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
class MessageSenderTest {

    private AvroRecordsGenerator<Order> avroRecordsGenerator;

    // Records count
    private Long count = 100L;

    // ID boundary
    private Long idMin = 0L;

    // Timestamp boundary
    private Long timestampMin = 10000L;
    private Long timestampMax = 50000L;

    // Orders stream
    private Stream<Order> ordersStream;

    private Stream<ProducerRecord<Long, Order>> producerRecordStream;

    private MockProducer<Long, Order> producer;

    private MessageSender<Order> messageSender;

    @BeforeEach
    void setUp() throws NoSuchMethodException, NoSuchAlgorithmException,
            IllegalAccessException, InvocationTargetException {
        OrderGenerator orderGenerator = new OrderGenerator();

        Properties generatorProperties = new Properties();

        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.ID_MIN, String.valueOf(idMin));

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MIN, String.valueOf(timestampMin));
        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MAX, String.valueOf(timestampMax));

        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.COUNT, String.valueOf(count));

        ordersStream = orderGenerator.generateWith(generatorProperties);

        avroRecordsGenerator = new AvroRecordsGenerator<>(Order.class);

        producerRecordStream = avroRecordsGenerator.recordStream(ordersStream, "orders-topic");

        producer = new MockProducer<>();

        messageSender = new MessageSender<>();

        messageSender.send(producerRecordStream, producer);
    }

    @Test
    void producerCheck() {
    }
}