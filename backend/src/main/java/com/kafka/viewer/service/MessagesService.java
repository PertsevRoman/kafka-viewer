package com.kafka.viewer.service;

import com.kafka.viewer.entity.MessageRecord;
import com.kafka.viewer.entity.Partition;
import com.kafka.viewer.entity.RecordsPool;
import com.kafka.viewer.entity.Direction;
import com.kafka.viewer.entity.transform.MessageRecordTransform;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
@Component
public class MessagesService {

    @Value("${poll.timeout}")
    private Long pollTimeout;

    @Autowired
    KafkaClusterService kafkaClusterService;

    /**
     * Get topic messages with offset
     *
     * @param clusterIndex Cluster index
     * @param topicName Topic name
     * @param partition Partition index
     * @param offset Start offset
     * @param count Messages count
     * @param direction Fetching direction
     * @return Records pool
     */
    public RecordsPool getMessages(Integer clusterIndex,
                                   String topicName,
                                   Integer partition,
                                   Long offset,
                                   Integer count,
                                   Direction direction) {

        TopicPartition topicPartition = new TopicPartition(topicName, partition);

        String clusterHost = kafkaClusterService.getClusterHost(clusterIndex);

        Consumer<String, SpecificRecordBase> consumer = kafkaClusterService.createConsumer(clusterHost, count);

        List<TopicPartition> topicPartitions = Collections.singletonList(topicPartition);

        consumer.close();

        return fetchMessages(topicName, offset, count, direction, topicPartitions, clusterHost);
    }

    /**
     * Return all messages from topic
     * @param clusterIndex Cluster index
     * @param topicName Topic name
     * @param offset Basic offset
     * @param count Count
     * @param direction Direction
     *
     * @return Recorr
     */
    public RecordsPool getAllTopicMessages(Integer clusterIndex,
                                           String topicName,
                                           Long offset,
                                           Integer count,
                                           Direction direction) {

        String clusterHost = kafkaClusterService.getClusterHost(clusterIndex);

        Consumer<String, SpecificRecordBase> consumer = kafkaClusterService.createConsumer(clusterHost);

        List<TopicPartition> topicPartitions = kafkaClusterService.getTopicPartitions(topicName, consumer);

        consumer.close();

        return fetchMessages(topicName, offset, count, direction, topicPartitions, clusterHost);
    }

    /**
     * Get record type
     * @param specificRecordBase Record base
     *
     * @return AVRO schema type or unknown type
     */
    private String getRecordType(SpecificRecordBase specificRecordBase) {
        try {
            return specificRecordBase.getClass().getName();
        } catch (Exception e) {
            return "Unknown Type!";
        }
    }

    /**
     * Fetch messages from defined partitions
     * @param topicName Topic name
     * @param offset Offset
     * @param count Count
     * @param direction Direction
     * @param topicPartitions Topic partition
     * @param clusterHost Cluster host
     * @return
     */
    private RecordsPool fetchMessages(String topicName,
                                      Long offset,
                                      Integer count,
                                      Direction direction,
                                      List<TopicPartition> topicPartitions,
                                      String clusterHost) {

        RecordsPool recordsPool = new RecordsPool();

        Consumer<String, SpecificRecordBase> consumer = kafkaClusterService.createConsumer(clusterHost);

        consumer.assign(topicPartitions);

        List<Partition> partitionInfos = kafkaClusterService.getPartitionInfos(consumer, topicPartitions);

        long allMessagesCount = partitionInfos
                .stream()
                .mapToLong(Partition::getMessageCount)
                .sum();

        consumer.seekToBeginning(Collections.emptySet());

        assert (int) allMessagesCount < Integer.MAX_VALUE : "Messages count more than INTEGER maximum";

        // extract records
        List<ConsumerRecord<String, SpecificRecordBase>> rawRecords = new ArrayList<>((int) allMessagesCount);

        while (rawRecords.size() <= count) {
            ConsumerRecords<String, SpecificRecordBase> pollResult =
                    consumer.poll(Duration.ofMillis(pollTimeout));

            if (pollResult.isEmpty()) {
                break;
            }

            Iterable<ConsumerRecord<String, SpecificRecordBase>> records = pollResult.records(topicName);

            for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                rawRecords.add(record);
            }
        }

        // close consumer immediately after using
        consumer.close();

        // use external index
        AtomicLong index = new AtomicLong(0L);
        Stream<MessageRecord> messageRecordStream = rawRecords
                .stream()
                .map(MessageRecordTransform::toMessageRecord)
                .sorted(Comparator.comparing(MessageRecord::getTimestamp))
                .peek(messageRecord -> messageRecord.setIndex(index.getAndIncrement()));

        // Reverse direction
        boolean isDesc = direction.equals(Direction.DESC);

        if (isDesc) {
            messageRecordStream = messageRecordStream
                    .sorted(Collections.reverseOrder());
        }

        List<MessageRecord> messageRecordList = messageRecordStream
                .collect(Collectors.toList());
        
        if (messageRecordList.size() > 0) {
            Long lastTimestamp = isDesc ? messageRecordList.get(0).getTimestamp() :
                    messageRecordList.get(messageRecordList.size() - 1).getTimestamp();

            recordsPool.setLastTimestamp(lastTimestamp + 1L);
        }

        recordsPool.setRecordList(messageRecordList);
        recordsPool.setCount(count);
        recordsPool.setOffset(offset);

        return recordsPool;
    }
}
