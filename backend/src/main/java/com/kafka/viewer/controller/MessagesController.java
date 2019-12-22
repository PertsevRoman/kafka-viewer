package com.kafka.viewer.controller;

import com.kafka.viewer.entity.Direction;
import com.kafka.viewer.entity.RecordsPool;
import com.kafka.viewer.service.MessagesService;
import com.kafka.viewer.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
@RestController
@RequestMapping("/api/v1/cluster")
public class MessagesController {
    @Autowired
    private MessagesService messagesService;

    /**
     *
     * @see TopicService
     *
     * @param topicName
     * @param partition
     * @param offset
     * @param count
     * @return
     */
    @GetMapping(
            value = "/{clusterIndex}/topics/{topicName}/messages/partition/{partition}/offset/{offset}/count/{count}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public RecordsPool messages(
            @PathVariable Integer clusterIndex,
            @PathVariable String topicName,
            @PathVariable Integer partition,
            @PathVariable Long offset,
            @PathVariable Integer count,
            @QueryParam("direction") Direction direction
    ) {
        return messagesService.getMessages(clusterIndex, topicName, partition, offset, count, direction);
    }

    @GetMapping(
            value = "/{clusterIndex}/topics/{topicName}/messages/partition/all/offset/{offset}/count/{count}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public RecordsPool messages(
            @PathVariable Integer clusterIndex,
            @PathVariable String topicName,
            @PathVariable Long offset,
            @PathVariable Integer count,
            @QueryParam("direction") Direction direction
    ) {
        return messagesService.getAllTopicMessages(clusterIndex, topicName, offset, count, direction);
    }
}
