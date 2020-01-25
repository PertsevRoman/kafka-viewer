package com.kafka.viewer;

import com.kafka.viewer.config.ProducerProperty;
import com.kafka.viewer.config.PropertiesLoader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class KafkaProducerApplication
{
    public static void main( String[] args ) {
        final Properties properties = PropertiesLoader.getProperties();

        final String generatorClass = properties.getProperty(ProducerProperty.GENERATOR_CLASS);

        try (KafkaProducer<Long, SpecificRecordBase> producer = new KafkaProducer<>(properties)) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
