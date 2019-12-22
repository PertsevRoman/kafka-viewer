package com.kafka.viewer.entity.response;

import com.kafka.viewer.entity.S3Schema;
import lombok.Data;

import java.util.List;

@Data
public class S3SchemaResponse {
    private List<S3Schema> s3Schemas;
}
