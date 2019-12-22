package com.ringcentral.kv.entity.response;

import com.ringcentral.kv.entity.BundledSchema;
import lombok.Data;

import java.util.List;

@Data
public class BundledSchemaResponse {
    private List<BundledSchema> bundledSchemas;
}
