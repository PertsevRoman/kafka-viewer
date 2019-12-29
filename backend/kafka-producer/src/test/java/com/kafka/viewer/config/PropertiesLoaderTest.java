package com.kafka.viewer.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class PropertiesLoaderTest {
    
    @Test
    @DisplayName("Bootstrap server test")
    public void shouldReturnLocalhostBootstrap() {
        assertThatCode(() -> {
            String kafkaBootstrapServer = PropertiesLoader.getProperty(ProducerProperty.KAFKA_BOOTSTRAP_SERVER);

            assertThat(kafkaBootstrapServer).isEqualTo("localhost:9092");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Bootstrap server test")
    public void shouldReturnTopic() {
        assertThatCode(() -> {
            String topicName = PropertiesLoader.getProperty(ProducerProperty.KAFKA_TOPIC);

            assertThat(topicName).isEqualTo("test-topic");
        }).doesNotThrowAnyException();
    }
}
