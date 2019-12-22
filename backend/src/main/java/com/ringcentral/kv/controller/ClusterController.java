package com.ringcentral.kv.controller;

import com.ringcentral.kv.entity.ClusterInfo;
import com.ringcentral.kv.service.KafkaClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/clusters")
public class ClusterController {

    @Autowired
    private KafkaClusterService kafkaClusterService;

    @GetMapping("/info")
    public Set<ClusterInfo> getClusters() {
        return kafkaClusterService.getClusters();
    }
}
