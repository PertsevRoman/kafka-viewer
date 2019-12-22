package com.kafka.viewer.serder;

import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;
import org.apache.avro.specific.SpecificRecordBase;

import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class AvroSchemaUtil {
    private static final String FP_NAME = "CRC-64-AVRO";

    /**
     * Repository schemas map
     *
     * @implNote I don't wanna use simple list because I don't wanna to lose S3 key info
     */
    private static Map<String, Schema> repositorySchemas = new ConcurrentHashMap<>();

    /**
     * Bundled schemas
     */
    private static Schema[] bundledSchemas = { };

    private static Schema getSchemaFromClasspath(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return ((SpecificRecordBase) Class.forName(className).newInstance()).getSchema();
    }

    public static String generateFingerprint(Schema schema) {
        try {
            return generateFingerprint(SchemaNormalization.parsingFingerprint(FP_NAME, schema));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String generateFingerprint(byte[] avroFingerprint) {
        return DatatypeConverter.printHexBinary(avroFingerprint);
    }

    /**
     * Set repository schemas
     * @param schemas
     */
    public static void setRepositorySchemas(Map<String, Schema> schemas) {
        repositorySchemas.clear();
        for (Map.Entry<String, Schema> stringSchemaEntry : schemas.entrySet()) {
            repositorySchemas.put(stringSchemaEntry.getKey(), stringSchemaEntry.getValue());
        }
    }

    /**
     * Get repository schemas
     * @return
     */
    public static Map<String, Schema> getRepositorySchemas() {
        return repositorySchemas;
    }

    /**
     * Get bundled schemas
     * @return
     */
    public static Schema[] getBundledSchemas() {
        return bundledSchemas;
    }

    /**
     * Match bundled schema
     * @param avroChecksum
     * @return
     */
    static Schema matchBundledSchema(byte[] avroChecksum) {
        return matchSchema( avroChecksum, bundledSchemas );
    }

    static Schema matchBundledSchema(byte[] avroChecksum, String schemaClasspath) {
        try {
            Schema[] schemas = new Schema[1];
            schemas[0] = getSchemaFromClasspath(schemaClasspath);
            return matchSchema( avroChecksum, Stream.concat(
                    Arrays.stream(bundledSchemas),
                    Arrays.stream(schemas)
            ).toArray(Schema[]::new) );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Matches repository schema
     * @param avroChecksum
     * @return
     */
    static Schema matchRepoSchema(byte[] avroChecksum) {
        return matchSchema(avroChecksum, getRepositorySchemas().values());
    }

    /**
     * Schema matching based on checksum
     * @param avroChecksum
     * @param schemas
     * @return
     */
    private static Schema matchSchema(byte[] avroChecksum, Collection<Schema> schemas) {
        return schemas.stream().filter(schema -> {
            try {
                return Arrays.equals(avroChecksum,
                        SchemaNormalization.parsingFingerprint(FP_NAME, schema));
            } catch (NoSuchAlgorithmException e) {
                return false;
            }
        }).findFirst().orElse(null);
    }

    /**
     * Schema matching based on checksum
     * @param avroChecksum
     * @param schemas
     * @return
     */
    private static Schema matchSchema(byte[] avroChecksum, Schema... schemas) {
        return matchSchema(avroChecksum, Arrays.asList(schemas));
    }
}
