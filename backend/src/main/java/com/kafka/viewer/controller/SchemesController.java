package com.kafka.viewer.controller;

import com.kafka.viewer.entity.BundledSchema;
import com.kafka.viewer.entity.S3Schema;
import com.kafka.viewer.entity.response.BundledSchemaResponse;
import com.kafka.viewer.entity.response.S3SchemaResponse;
import com.kafka.viewer.s3.S3RepositoryService;
import com.kafka.viewer.serder.AvroSchemaUtil;
import com.kafka.viewer.service.LocalSchemasService;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/schemes")
@Profile("s3")
public class SchemesController {

    @Autowired
    private S3RepositoryService s3RepositoryService;
    
    @Autowired
    private LocalSchemasService localSchemasService;

    @GetMapping("/status")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean enabled() {
        return true;
    }

    @GetMapping("/bundled")
    public BundledSchemaResponse getBundledSchemas() {
        List<BundledSchema> schemas = Stream
                .of(AvroSchemaUtil.getBundledSchemas())
                .map(schema -> {
                    BundledSchema bundledSchema = new BundledSchema();
                    bundledSchema.setSchema(schema.toString());
                    bundledSchema.setClName(schema.getName());
                    return bundledSchema;
                })
                .collect(Collectors.toList());

        BundledSchemaResponse bundledSchemaResponse = new BundledSchemaResponse();
        bundledSchemaResponse.setBundledSchemas(schemas);

        return bundledSchemaResponse;
    }
    
    @PostMapping("/upload")
    public void uploadSchema(@RequestParam("file") MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException {
        localSchemasService.saveFile(multipartFile);
    }

    @GetMapping("/s3")
    public S3SchemaResponse getS3Schemas() {
        List<S3Schema> schemas = AvroSchemaUtil
                .getRepositorySchemas()
                .entrySet()
                .stream()
                .map(stringSchemaEntry -> {
                    String key = stringSchemaEntry.getKey();
                    Schema schema = stringSchemaEntry.getValue();
                    String schemaBody = schema.toString();

                    S3Schema s3Schema = new S3Schema();

                    s3Schema.setKey(key);
                    s3Schema.setSchema(schemaBody);

                    return s3Schema;
                }).collect(Collectors.toList());

        S3SchemaResponse s3SchemaResponse = new S3SchemaResponse();
        s3SchemaResponse.setS3Schemas(schemas);

        return s3SchemaResponse;
    }

    @PostMapping("/refresh")
    public void runUpdate() {
        s3RepositoryService.refresh();
    }
}
