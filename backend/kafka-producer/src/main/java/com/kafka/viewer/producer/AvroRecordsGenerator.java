package com.kafka.viewer.producer;

import com.kafka.viewer.config.ProducerProperty;
import com.kafka.viewer.config.PropertiesLoader;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 */
public class AvroRecordsGenerator {

    public Stream<? extends ProducerRecord<Long, SpecificRecordBase>>
            recordStream(Stream<? extends SpecificRecordBase> dataStream, final String topicName) {

        AtomicLong index = new AtomicLong(1L);

        final Properties properties = PropertiesLoader.getProperties();

        final String headerKey = properties.getProperty(ProducerProperty.HEADER_KEY);

        return dataStream
                .map(record -> {
                    final Schema schema = record.getSchema();

                    try {
                    final byte[] fingerprint = SchemaNormalization
                            .parsingFingerprint("CRC-64-AVRO", schema);

                    Header fingerprintHeader = new RecordHeader(headerKey, fingerprint);

                    return new ProducerRecord<Long, SpecificRecordBase>(topicName, null,
                            index.getAndIncrement(), record,
                            Collections.singletonList(fingerprintHeader));

                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    return null;
                });
    }
}
