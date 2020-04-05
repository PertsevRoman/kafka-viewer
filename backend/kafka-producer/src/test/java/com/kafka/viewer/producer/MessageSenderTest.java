package com.kafka.viewer.producer;

import com.kafka.viewer.generator.OrderGenerator;
import com.kafka.viewer.generator.RecordGenerator;
import com.kafka.viewer.generator.loader.GeneratorLoader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test message sending
 */
class MessageSenderTest {

    private final LongSerializer longSerializer = new LongSerializer();

    private final AvroSerializer<SpecificRecordBase> avroSerializer =
            new AvroSerializer<>();

    // Records count
    private Long count = 100L;

    // ID boundary
    private Long idMin = 0L;

    // Timestamp boundary
    private Long timestampMin = 10000L;
    private Long timestampMax = 50000L;

    // Orders stream
    private Stream<? extends SpecificRecordBase> ordersStream;


    private MockProducer<Long, SpecificRecordBase> producer;

    private List<ProducerRecord<Long, SpecificRecordBase>> history;

    private String topicName = "orders-topic";

    private String generatorClass;
    private RecordGenerator avroRecordsGenerator;

    private Stream<? extends ProducerRecord<Long, SpecificRecordBase>> producerRecordStream;

    @BeforeEach
    void setUp() throws IllegalAccessException, InvocationTargetException,
            InstantiationException, ClassNotFoundException {

        generatorClass = "com.kafka.viewer.generator.OrderGenerator";

        avroRecordsGenerator =
                GeneratorLoader.loadGenerator(generatorClass);

        Properties generatorProperties = new Properties();

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.ID_MIN, String.valueOf(idMin));

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MIN, String.valueOf(timestampMin));

        generatorProperties.setProperty(OrderGenerator
                .OrderGeneratorProperty.TIMESTAMP_MAX, String.valueOf(timestampMax));

        generatorProperties.setProperty(OrderGenerator.OrderGeneratorProperty.COUNT,
                String.valueOf(count));

        ordersStream = avroRecordsGenerator.generateWith(generatorProperties);

        producer = new MockProducer<>(
                false,
                null,
                longSerializer,
                avroSerializer
        );

        AvroRecordsGenerator generator = new AvroRecordsGenerator();
        producerRecordStream = generator.recordStream(ordersStream, topicName);

        MessageSender.send(producerRecordStream, producer);

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