package com.kafka.viewer;

import com.kafka.viewer.avro.Customer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class KafkaProducerApplication 
{
    public static void main( String[] args ) {
        Properties properties = new Properties();

        try (KafkaProducer<Long, Customer> producer = new KafkaProducer<>(properties)) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
