package com.ringcentral.kv.controller;

import com.ringcentral.kv.entity.TopicInfo;
import com.ringcentral.kv.entity.TopicsInfo;
import com.ringcentral.kv.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 *
 */
@RestController
@RequestMapping("/api/v1/cluster")
public class TopicController {
    @Autowired
    private TopicService topicService;

    /**
     *
     * @see TopicService
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping(value = "/{clusterIndex}/topics/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public TopicsInfo getTopicsInfo(@PathVariable Integer clusterIndex) {
        return topicService.getTopicsInfo(clusterIndex);
    }

    /**
     * @see TopicService
     *
     * @param topicName
     * @return
     */
    @GetMapping(value = "/{clusterIndex}/topics/{topicName}/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public TopicInfo getTopicInfo(@PathVariable String topicName,
                                  @PathVariable Integer clusterIndex) {
        return topicService.getTopicInfo(clusterIndex, topicName);
    }

    @ExceptionHandler({ Exception.class })
    public void handleException(Exception e) {
        e.printStackTrace();
    }
}
