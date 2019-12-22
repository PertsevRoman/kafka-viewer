package com.ringcentral.kv.service;

import com.ringcentral.kv.entity.ClusterInfo;
import com.ringcentral.kv.entity.Partition;
import com.ringcentral.kv.serder.AvroDeserializer;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class KafkaClusterService {
    private static final Logger log = LoggerFactory.getLogger(KafkaClusterService.class);
    
    @Value("${app.kafka.bootstrap-address}")
    private String bootstrapAddress;
    
    private Set<ClusterInfo> clusterList;

    @Value("${fetch.topics.exclude}")
    private String fetchTopicsExclude;

    @Value("${app.kafka.group-id}")
    private String groupId;
    
    @PostConstruct
    public void initLogging() {
        log.info("Start Kafka cluster service, group - {}", getGroupId());
        log.info("[{}] topics will be excluded", fetchTopicsExclude);
    }

    @PostConstruct
    public void init() {
        String[] rawServerList = bootstrapAddress.split(";");

        clusterList = IntStream
                .range(0, rawServerList.length)
                .mapToObj(index -> {
                    ClusterInfo clusterInfo = new ClusterInfo();

                    clusterInfo.setHost(rawServerList[index]);
                    clusterInfo.setIndex(index);

                    return clusterInfo;
                })
                .collect(Collectors.toSet());
    }
    
    public Set<ClusterInfo> getClusters() {
        return clusterList;
    }

    public String getClusterHost(Integer clusterIndex) {
        return this.getClusters()
                .stream()
                .filter(clusterInfo -> clusterInfo.getIndex().equals(clusterIndex))
                .findFirst()
                .map(ClusterInfo::getHost)
                .orElse(null);
    }

    /**
     * Get consumer topics
     * 
     * @param consumer
     * @param topicService
     * @return
     */
    public Map<String, List<PartitionInfo>> getTopics(Consumer<String, SpecificRecordBase> consumer, TopicService topicService) {
        return consumer
                .listTopics()
                .entrySet()
                .stream()
                .filter(topicEntry -> !topicEntry.getKey().startsWith("__")
                        && !excludeTopics().contains(topicEntry.getKey())
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get topic partitions
     * 
     * @param topicName Topic name
     * @param consumer Consumer
     *                 
     * @return Topic partitions list
     */
    public synchronized List<TopicPartition> getTopicPartitions(String topicName, Consumer<String, SpecificRecordBase> consumer) {
        return consumer.partitionsFor(topicName)
                .stream()
                .map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition()))
                .collect(Collectors.toList());
    }
    
    public String getGroupId() {
        return groupId;
    }

    /**
     * Topics to exclude
     * 
     * @return Topics exclude list
     */
    private Set<String> excludeTopics() {
        return new HashSet<>(Arrays.asList(fetchTopicsExclude.split(";")));
    }

    /**
     * Make basic consumer config
     * @param clusterHost Kafka cluster servers
     * 
     * @return Config map
     */
    public Map<String, Object> makeBasicConfig(String clusterHost) {
        final Map<String, Object> configs = new HashMap<>();

        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, clusterHost);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, getGroupId());
        configs.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializer.class);
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return configs;
    }

    /**
     * Create basic consumer w/ poll limitation 
     *
     * @param clusterHost Cluster host
     * @param count Pull messages count
     * 
     * @return Messages consumer
     */
    KafkaConsumer<String, SpecificRecordBase> createConsumer(String clusterHost, Integer count) {
        final Map<String, Object> configs = makeBasicConfig(clusterHost);

        if (count != null && count > 0) {
            configs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, count);
        }

        return new KafkaConsumer<>(configs, null, null);
    }

    /**
     * Create basic consumer
     * 
     * @param clusterHost Create basic consumer
     * 
     * @return Messages consumer
     */
    public KafkaConsumer<String, SpecificRecordBase> createConsumer(String clusterHost) {
        final Map<String, Object> configs = makeBasicConfig(clusterHost);

        return new KafkaConsumer<>(configs, null, null);
    }

    /**
     * Prepare partitions info list
     * 
     * @param consumer Kafka consumer
     * @param partitions Partitions list
     * 
     * @return List of partitions index and messages count
     */
    public List<Partition> getPartitionInfos(Consumer<String, SpecificRecordBase> consumer, List<TopicPartition> partitions) {
        assert consumer.assignment().size() > 0 : "Consumer partitions doesn't assigned";

        return partitions.stream().map(topicPartition -> {
            Partition part = new Partition();

            part.setIndex(topicPartition.partition());
            part.setMessageCount(0L);

            return part;
        }).collect(Collectors.toList());
    }
}
