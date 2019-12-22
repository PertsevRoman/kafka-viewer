package com.ringcentral.kv.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.ringcentral.kv.entity.PairTuple;
import com.ringcentral.kv.serder.AvroSchemaUtil;
import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Profile("s3")
public class S3RepositoryService {
    @Autowired
    private AmazonS3 s3Client;

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(S3RepositoryService.class);


    @PostConstruct
    public void init() {
        log.info("Init S3 storage");
    }

    /**
     * Initialize repository schemas
     */
    synchronized void initRepoSchemas() {
        log.info("Update S3 AVRO schemes");

        try {
            s3Client.headBucket(new HeadBucketRequest(bucketName));

            Set<String> schemaFingerprints = Stream.of(AvroSchemaUtil
                    .getBundledSchemas())
                    .map(AvroSchemaUtil::generateFingerprint)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            ObjectListing objectListing = s3Client.listObjects(bucketName);

            Map<String, Schema> schemaMap = objectListing
                    .getObjectSummaries()
                    .stream()
                    .filter(s3ObjectSummary -> s3ObjectSummary.getKey().endsWith(".avsc"))
                    .map(s3ObjectSummary -> {
                        String key = s3ObjectSummary.getKey();

                        S3Object object = s3Client.getObject(bucketName, key);

                        S3ObjectInputStream objectContent = object.getObjectContent();
                        Schema.Parser parser = new Schema.Parser();

                        String schemaDoc = null;

                        try {
                            schemaDoc = StreamUtils.copyToString(objectContent, StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (schemaDoc != null) {
                            Schema schema = parser.parse(schemaDoc);
                            return PairTuple.of(key, schema);
                        }

                        return PairTuple.of(key, Schema.create(Schema.Type.NULL));
                    })
                    .filter(stringSchemaPairTuple -> !stringSchemaPairTuple
                            .getRight().getType().equals(Schema.Type.NULL))
                    .filter(stringSchemaPairTuple -> !schemaFingerprints
                            .contains(AvroSchemaUtil.generateFingerprint(stringSchemaPairTuple.getRight())))
                    .collect(Collectors.toMap(PairTuple::getLeft, PairTuple::getRight));

            // TODO find more clean way
            AvroSchemaUtil.setRepositorySchemas(schemaMap);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refresh schemas
     */
    @Async
    public void refresh() {
        initRepoSchemas();
    }
}
