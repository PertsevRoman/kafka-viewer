package com.kafka.viewer.entity.response;

import com.kafka.viewer.entity.BundledSchema;
import lombok.Data;

import java.util.List;

@Data
public class BundledSchemaResponse {
    private List<BundledSchema> bundledSchemas;
}
