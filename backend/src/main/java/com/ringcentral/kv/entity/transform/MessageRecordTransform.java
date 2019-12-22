package com.ringcentral.kv.entity.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringcentral.kv.entity.MessageRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import java.util.List;

/**
 * @author Roman Pertsev <roman.pertsev@nordigy.ru>
 */
public class MessageRecordTransform {
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        objectMapper.enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS);
    }
    
    /**
     * Get record type
     * @param specificRecordBase Record base
     *
     * @return AVRO schema type or unknown type
     */
    private static String getRecordType(SpecificRecordBase specificRecordBase) {
        try {
            return specificRecordBase.getClass().getName();
        } catch (Exception e) {
            return "Unknown Type!";
        }
    }

    /**
     * 
     * @param record
     * @return
     */
    public static MessageRecord toMessageRecord(ConsumerRecord<?, SpecificRecordBase> record) {
        SpecificRecordBase specificRecordBase = record.value();

        String recordType = getRecordType(specificRecordBase);
        int partition = record.partition();
        long timestamp = record.timestamp();

        MessageRecord messageRecord = new MessageRecord();

        messageRecord.setRecord(specificRecordBase);
        messageRecord.setType(recordType);
        messageRecord.setPartition(partition);
        messageRecord.setTimestamp(timestamp);

        return messageRecord;
    }

    /**
     * To WS message
     * @param records
     * @return
     * @throws JsonProcessingException
     */
    public static WebSocketMessage<String> toWebsocketMessage(List<MessageRecord> records) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(records);
        return new TextMessage(payload);
    }
}
