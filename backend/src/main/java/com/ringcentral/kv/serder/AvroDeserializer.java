package com.ringcentral.kv.serder;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.ExtendedDeserializer;

import java.io.IOException;
import java.util.Map;

public class AvroDeserializer<T extends SpecificRecordBase> implements ExtendedDeserializer<T> {
    /**
     * Extract generic data
     * @param bytes
     * @return
     */
    private T getGenericData(byte[] bytes) {
        GenericDatumReader<T> genericDatumReader = new GenericDatumReader<>();
        BinaryDecoder binaryDdr = DecoderFactory.get().binaryDecoder(bytes, null);

        try {
            return genericDatumReader.read(null, binaryDdr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract specified data
     * @param messageBinaryData Message data
     * @param avroChecksum AVRO hash header
     * @param classPath Specified schema class path
     * @return
     */
    private T getSpecificSchemaData(byte[] messageBinaryData, byte[] avroChecksum, String classPath) {
        Schema schema = AvroSchemaUtil.matchBundledSchema(avroChecksum, classPath);

        if(schema != null) {
            return decodeMessage(messageBinaryData, schema);
        }

        return null;
    }

    /**
     *
     * @param messageBinaryData Message data
     * @param avroChecksum AVRO hash header
     * @return
     */
    private T getSpecificSchemaData(byte[] messageBinaryData, byte[] avroChecksum) {
        Schema schema = AvroSchemaUtil.matchBundledSchema(avroChecksum);

        if(schema != null) {
            return decodeMessage(messageBinaryData, schema);
        }

        return null;
    }

    /**
     *
     * @param messageBinaryData
     * @param avroChecksum
     * @return
     */
    private T getRepositorySchemaData(byte[] messageBinaryData, byte[] avroChecksum) {
        Schema schema = AvroSchemaUtil.matchRepoSchema(avroChecksum);

        if (schema != null) {
            return decodeMessage(messageBinaryData, schema);
        }

        return null;
    }

    private T decodeMessage(byte[] messageBinaryData, Schema schema) {
        BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(messageBinaryData, null);

        SpecificDatumReader<T> reader
                = new SpecificDatumReader<>(schema);
        try {
            T message = reader.read(null, binaryDecoder);
            return message;
        } catch (ClassCastException | IOException | SerializationException | AvroRuntimeException e) {
            return null;
        }
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] messageBinaryData) {

        // extract generic data TODO check that it makes sense
        T parsedMessage = getGenericData(messageBinaryData);

        if (parsedMessage != null) {
            return parsedMessage;
        }

        // avro hash header
        Header avroSchemaHash = headers
                .lastHeader("AVRO-SCHEMA-HASH");

        if (avroSchemaHash == null) {
            return null;
        }

        byte[] avroChecksum = avroSchemaHash
                .value();

        // avro class name
        Header schemaTypeHeader = headers
                .lastHeader("AVRO-SCHEMA-TYPE");

        // extract data from bundled schemas
        if (schemaTypeHeader != null) {
            String className = new String(schemaTypeHeader.value());
            parsedMessage = getSpecificSchemaData(messageBinaryData, avroChecksum, className);
        } else {
            parsedMessage = getSpecificSchemaData(messageBinaryData, avroChecksum);
        }

        if (parsedMessage != null) {
            return parsedMessage;
        }

        // extract data from s3 repositories
        parsedMessage = getRepositorySchemaData(messageBinaryData, avroChecksum);

        return parsedMessage;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) { }

    @Override
    public T deserialize(String topic, byte[] data) {
        return null;
    }

    @Override
    public void close() { }
}
