package com.kafka.viewer.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PropertiesLoaderTest {

    private static Properties properties;

    @BeforeAll
    static void beforeAll() {
        properties = PropertiesLoader.getProperties();
    }

    @Test
    @DisplayName("Bootstrap server test")
    public void shouldReturnLocalhostBootstrap() {
        assertThatCode(() -> {
            String kafkaBootstrapServer = properties.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG);

            assertThat(kafkaBootstrapServer).isEqualTo("localhost:9092");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Bootstrap server test")
    public void shouldReturnTopic() {
        assertThatCode(() -> {
            String topicName = properties.getProperty(ProducerProperty.KAFKA_TOPIC);

            assertThat(topicName).isEqualTo("test-topic");
        }).doesNotThrowAnyException();
    }
}
