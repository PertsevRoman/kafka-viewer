package com.kafka.viewer.producer;

import com.kafka.viewer.avro.Order;
import com.kafka.viewer.generator.OrderGenerator;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
class AvroRecordsGeneratorTest {
    private AvroRecordsGenerator<Order> producer;

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

    @BeforeEach
    void setUp() throws NoSuchMethodException,
            NoSuchAlgorithmException, IllegalAccessException, InvocationTargetException {

        OrderGenerator orderGenerator = new OrderGenerator();

        Properties generatorProperties = new Properties();

        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.ID_MIN, String.valueOf(idMin));

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MIN, String.valueOf(timestampMin));
        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MAX, String.valueOf(timestampMax));

        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.COUNT, String.valueOf(count));

        ordersStream = orderGenerator.generateWith(generatorProperties);

        producer = new AvroRecordsGenerator<>(Order.class);

        producerRecordStream = producer.recordStream(ordersStream, "orders-topic");
    }

    @Test
    @DisplayName("Producer count test")
    void producerCountTest() {
        final long recordsCount = producerRecordStream.count();

        assertThat(recordsCount).isEqualTo(count);
    }

    @Test
    @DisplayName("Headers test")
    void headersTest() {
        producerRecordStream
                .forEach(record -> assertThat(
                        record.headers().lastHeader("AVRO-SCHEMA-HASH"))
                            .isNotNull());
    }
}