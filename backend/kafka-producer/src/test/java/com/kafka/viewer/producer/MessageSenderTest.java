package com.kafka.viewer.producer;

import com.kafka.viewer.avro.Order;
import com.kafka.viewer.generator.OrderGenerator;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test message sending
 */
class MessageSenderTest {

    private final LongSerializer longSerializer = new LongSerializer();

    private final AvroSerializer<Order> avroSerializer = new AvroSerializer<>();

    private final AvroRecordsGenerator<Order> avroRecordsGenerator =
            new AvroRecordsGenerator<>(Order.class);

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

    private MessageSender<Order> messageSender  = new MessageSender<>();

    private List<ProducerRecord<Long, Order>> history;

    private String topicName = "orders-topic";

    @BeforeEach
    void setUp() throws NoSuchMethodException, NoSuchAlgorithmException,
            IllegalAccessException, InvocationTargetException {
        OrderGenerator orderGenerator = new OrderGenerator();

        Properties generatorProperties = new Properties();

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.ID_MIN, String.valueOf(idMin));

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MIN, String.valueOf(timestampMin));

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MAX, String.valueOf(timestampMax));

        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.COUNT,
                String.valueOf(count));

        ordersStream = orderGenerator
                .generateWith(generatorProperties);

        producerRecordStream = avroRecordsGenerator.recordStream(ordersStream, topicName);

        producer = new MockProducer<>(
                false,
                null,
                longSerializer,
                avroSerializer
        );

        messageSender.send(producerRecordStream, producer);

        history = producer.history();
    }

    @Test
    @DisplayName("Producer send check")
    void producerSendCheck() {
        final long historySize = history.size();

        assertThat(historySize)
                .isEqualTo(count);
    }

    @Test
    @DisplayName("Producer content check")
    void producerContentCheck() {
        history
                .stream()
                .map(ProducerRecord::topic)
                .forEach(recordTopic -> assertThat(recordTopic)
                    .isEqualTo(topicName));
    }
}