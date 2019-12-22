package com.ringcentral.kv.service;

import com.ringcentral.kv.entity.Partition;
import com.ringcentral.kv.entity.TopicInfo;
import com.ringcentral.kv.entity.TopicsInfo;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TopicService {
    
    @Value("${poll.timeout}")
    private Long pollTimeout;

    @Autowired
    private KafkaClusterService kafkaClusterService;

    /**
     * Fetch all topics info
     * 
     * @param clusterIndex Cluster index
     * 
     * @return Topics info
     */
    public synchronized TopicsInfo getTopicsInfo(Integer clusterIndex) {
        String clusterHost = kafkaClusterService.getClusterHost(clusterIndex);

        Consumer<String, SpecificRecordBase> consumer = kafkaClusterService.createConsumer(clusterHost);

        Map<String, List<PartitionInfo>> topics = kafkaClusterService.getTopics(consumer, this);
        
        assert topics != null : "Topics doesn't fetched";

        Set<String> names = topics.keySet();

        TopicsInfo topicsInfo = new TopicsInfo();

        topicsInfo.setNames(names);

        return topicsInfo;
    }

    /**
     * Get topics info
     * 
     * @param clusterIndex Kafka cluster index
     * @param topicName Topic name
     *                  
     * @return Topic info
     */
    public TopicInfo getTopicInfo(Integer clusterIndex, String topicName) {
        String clusterHost = kafkaClusterService.getClusterHost(clusterIndex);
        
        TopicInfo topicInfo = new TopicInfo();

        Consumer<String, SpecificRecordBase> consumer = kafkaClusterService.createConsumer(clusterHost);

        List<TopicPartition> topicPartitions = kafkaClusterService.getTopicPartitions(topicName, consumer);
        
        assert topicPartitions != null : "Partitions doesn't fetched";

        consumer.assign(topicPartitions);

        List<Partition> partitionInfos = kafkaClusterService.getPartitionInfos(consumer, topicPartitions);

        topicInfo.setPartitions(partitionInfos);

        consumer.close();

        return topicInfo;
    }
}
