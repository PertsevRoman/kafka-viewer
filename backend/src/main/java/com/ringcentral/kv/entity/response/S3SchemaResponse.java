package com.ringcentral.kv.entity.response;

import com.ringcentral.kv.entity.S3Schema;
import lombok.Data;

import java.util.List;

@Data
public class S3SchemaResponse {
    private List<S3Schema> s3Schemas;
}
