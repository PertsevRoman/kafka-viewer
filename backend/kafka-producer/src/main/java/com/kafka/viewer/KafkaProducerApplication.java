package com.kafka.viewer;

import com.kafka.viewer.config.PropertiesLoader;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class KafkaProducerApplication 
{
    public static void main( String[] args ) {
        final Properties properties = PropertiesLoader.getProperties();
        
        int i = 0;

//        try (KafkaProducer<Long, Customer> producer = new KafkaProducer<>(properties)) {
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
