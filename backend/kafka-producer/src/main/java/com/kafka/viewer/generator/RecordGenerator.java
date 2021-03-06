package com.kafka.viewer.generator;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.Properties;
import java.util.stream.Stream;

/**
 */
public interface RecordGenerator {

    class GeneratorProperty {
        public static final String ID_MIN = "generator.id.min";
        public static final String COUNT = "generator.count";
    }

    Stream<? extends SpecificRecordBase> generateWith(Properties properties);
}
