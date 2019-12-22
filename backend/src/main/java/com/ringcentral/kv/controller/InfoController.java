package com.ringcentral.kv.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/info")
public class InfoController {
    @Value("${avro.schemes.version}")
    private String avroVersion;

    public String getAvroVersion() {
        return avroVersion;
    }
}
