package com.ringcentral.kv.service;

import com.ringcentral.kv.entity.MessageRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ringcentral.kv.entity.transform.MessageRecordTransform.toMessageRecord;
import static com.ringcentral.kv.entity.transform.MessageRecordTransform.toWebsocketMessage;

@Component
public class KafkaWsPuller {
    private static final Logger log = LoggerFactory.getLogger(KafkaWsPuller.class);
    
    @Autowired
    private KafkaClusterService kafkaClusterService;

    @Value("${poll.timeout}")
    private Long pollTimeout;
    
    private Map<String, ExecutorService> consumerMap = new ConcurrentHashMap<>();

    /**
     * 
     */
    private static String UINT_REGEX = "^[1-9]+[0-9]*$";
    
    public List<TopicPartition> getPartitions(Integer clusterIndex, String topicName, String partition) {
        assert partition.equals("all") || partition.matches(UINT_REGEX);

        if (partition.equals("all")) {
            String clusterHost = kafkaClusterService.getClusterHost(clusterIndex);
    
            KafkaConsumer<String, SpecificRecordBase> consumer = kafkaClusterService.createConsumer(clusterHost);

            List<TopicPartition> topicPartitions = kafkaClusterService.getTopicPartitions(topicName, consumer);
            
            consumer.close();
            
            return topicPartitions;
        } else {
            TopicPartition topicPartition = new TopicPartition(topicName, Integer.parseInt(partition));

            return Collections.singletonList(topicPartition);
        }
    }

    public void assignKafkaPull(WebSocketSession webSocketSession, Map<String, String> queryMap) {
        String webSocketId = webSocketSession.getId();

        String clusterIndex = queryMap.get("clusterIndex");
        assert !StringUtils.isEmpty(clusterIndex) && clusterIndex.matches(UINT_REGEX) :
                "You should specify Kafka cluster index";

        String topicName = queryMap.get("topicName");
        assert !StringUtils.isEmpty(topicName) : "You should specify topic name";
        
        String offset = queryMap.get("offset");
        assert !StringUtils.isEmpty(offset) &&
                offset.matches(UINT_REGEX) : "You should specify offset";

        String partition = queryMap.get("partition");
        assert !StringUtils.isEmpty(partition) : "You should specify partition";
        
        String clusterHost = kafkaClusterService.getClusterHost(Integer.parseInt(clusterIndex));
        assert clusterHost != null : "Cluster host not found";

        String timestamp = queryMap.get("timestamp");
        assert !StringUtils.isEmpty(timestamp) &&
                offset.matches(UINT_REGEX) : "You should specify timestamp offset";

        KafkaConsumer<String, SpecificRecordBase> consumer = kafkaClusterService.createConsumer(clusterHost);
        List<TopicPartition> partitions = getPartitions(Integer.parseInt(clusterIndex), topicName, partition);

        consumer.assign(partitions);

        long parsedTimestamp = Long.parseLong(timestamp);
        if (parsedTimestamp > 0) {
            Map<TopicPartition, Long> offsetTimestamps = partitions
                    .stream()
                    .collect(Collectors.toMap(Function.identity(), t -> parsedTimestamp));

            Map<TopicPartition, OffsetAndTimestamp> measuredOffsets = consumer.offsetsForTimes(offsetTimestamps);

            // seek to end if we can't find timestamp offset
            List<TopicPartition> endSeekPartitions = new ArrayList<>(5);
            measuredOffsets
                    .forEach((topicPartition, offsetAndTimestamp) -> {
                        if (offsetAndTimestamp != null) {
                            long timeOffset = offsetAndTimestamp.offset();
                            consumer.seek(topicPartition, timeOffset);
                        } else {
                            endSeekPartitions.add(topicPartition);
                        }
                    });

            consumer.seekToEnd(endSeekPartitions);
        } else {
            consumer.seekToBeginning(partitions);
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        consumerMap.put(webSocketId, executorService);
        
        executorService.submit(() -> {
            while (!executorService.isShutdown()) {
                ConsumerRecords<String, SpecificRecordBase> pollResult =
                        consumer.poll(Duration.ofMillis(pollTimeout));

                if (pollResult.isEmpty()) {
                    continue;
                }

                Iterable<ConsumerRecord<String, SpecificRecordBase>> records =
                        pollResult.records(topicName);

                List<MessageRecord> messageRecords = new ArrayList<>(10);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    MessageRecord messageRecord = toMessageRecord(record);
                    messageRecord.setIndex(0L);

                    messageRecords.add(messageRecord);
                }
                
                if (messageRecords.size() > 0) {
                    log.info("{} records fetched", messageRecords.size());
                    
                    synchronized (webSocketSession) {
                        try {
                            webSocketSession.sendMessage(toWebsocketMessage(messageRecords));
                        } catch (IOException e) {
                            log.error("Can't send ws message", e);
                        }
                    }
                }
            }
            
            log.info("Close WebSocket pulling interaction: {}", webSocketId);

            consumer.close();
        });
    }
    
    public void closeKafkaPull(WebSocketSession webSocketSession) {
        String webSocketId = webSocketSession.getId();

        ExecutorService consumerTaskExecutor = consumerMap.remove(webSocketId);
        
        if (consumerTaskExecutor == null) {
            log.warn("Consumer executor not found for WS session {}", webSocketId);
            return;
        }

        consumerTaskExecutor.shutdown();

        try {
            // wait for 5 seconds
            consumerTaskExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
