package com.kafka.viewer;

import com.kafka.viewer.config.ProducerProperty;
import com.kafka.viewer.config.PropertiesLoader;
import com.kafka.viewer.generator.RecordGenerator;
import com.kafka.viewer.generator.loader.GeneratorLoader;
import com.kafka.viewer.producer.AvroRecordsGenerator;
import com.kafka.viewer.producer.MessageSender;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.stream.Stream;

/**
 * Hello world!
 *
 */
public class KafkaProducerApplication
{
    public static void main( String[] args ) {
        final Properties properties = PropertiesLoader.getProperties();

        final String generatorClass = properties.getProperty(ProducerProperty.GENERATOR_CLASS);
        final String topicName = properties.getProperty(ProducerProperty.KAFKA_TOPIC);

        try (KafkaProducer<Long, SpecificRecordBase> producer = new KafkaProducer<>(properties)) {
            final RecordGenerator recordGenerator = GeneratorLoader.loadGenerator(generatorClass);

            final Stream<? extends SpecificRecordBase> recordsStream =
                    recordGenerator.generateWith(properties);

            AvroRecordsGenerator generator = new AvroRecordsGenerator();

            final Stream<? extends ProducerRecord<Long, SpecificRecordBase>> stream =
                    generator.recordStream(recordsStream, topicName);

            MessageSender.send(stream, producer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
