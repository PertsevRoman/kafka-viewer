package com.kafka.viewer.generator;

import com.kafka.viewer.avro.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 */
class OrderGeneratorTest {

    private Stream<Order> ordersStream;

    // Records count
    private Long count = 100L;

    // ID boundary
    private Long idMin = 0L;
    private Long idMax = 800L;

    // Timestamp boundary
    private Long timestampMin = 10000L;
    private Long timestampMax = 50000L;

    @BeforeEach
    void setUp() {
        OrderGenerator orderGenerator = new OrderGenerator();

        Properties generatorProperties = new Properties();

        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.ID_MIN, String.valueOf(idMin));
        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.ID_MAX, String.valueOf(idMax));

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MIN, String.valueOf(timestampMin));
        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MAX, String.valueOf(timestampMax));

        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.COUNT, String.valueOf(count));

        ordersStream = orderGenerator.generateWith(generatorProperties);
    }

    @Test
    @DisplayName("Order generation count test")
    void orderGenerationCountTest() {
        final long ordersCount = ordersStream.count();

        assertThat(ordersCount).isEqualTo(count);
    }

    @Test
    @DisplayName("Order generation ID test")
    void orderGenerationIdTest() {
        ordersStream
                .map(Order::getId)
                .forEach(id -> assertThat(id).isBetween(idMin, idMax));
    }

    @Test
    @DisplayName("Order generation timestamp test")
    void orderGenerationTimestampTest() {
        ordersStream
                .map(Order::getTimestamp)
                .forEach(timestamp -> assertThat(timestamp).isBetween(timestampMin, timestampMax));
    }
}